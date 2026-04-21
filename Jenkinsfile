def sendBuildEmail(String to, String subject, String bodyText, String bodyHtml = null) {
  echo "[Notify] Email to=${to}, subject=${subject}"

  // Prefer Email Extension plugin if available; fall back to core Mailer step.
  try {
    if (bodyHtml != null && bodyHtml.trim()) {
      emailext to: to, subject: subject, body: bodyHtml, mimeType: 'text/html'
    } else {
      emailext to: to, subject: subject, body: bodyText, mimeType: 'text/plain'
    }
    echo "[Notify] Sent via emailext"
    return
  } catch (Exception e1) {
    echo "[Notify] emailext not available/failed: ${e1.getClass().getName()}: ${e1.message}"
  }

  try {
    // core Mailer plugin doesn't consistently support HTML; send plain text.
    mail to: to, subject: subject, body: bodyText
    echo "[Notify] Sent via mail"
  } catch (Exception e2) {
    echo "[Notify] mail not available/failed: ${e2.getClass().getName()}: ${e2.message}"
  }
}

def coalesceStr(Object... values) {
  for (v in values) {
    if (v == null) continue
    def s = String.valueOf(v).trim()
    if (s) return s
  }
  return null
}

def scheduledProfile() {
  // Jenkins uses the controller timezone. Force a stable schedule reference (Vietnam time).
  def tz = TimeZone.getTimeZone('Asia/Ho_Chi_Minh')
  def cal = Calendar.getInstance(tz)
  cal.setTime(new Date())
  int hour = cal.get(Calendar.HOUR_OF_DAY)
  int dow = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon, ... 7=Sat

  // Suggested default schedule (AUTO):
  // - 01:00 Mon-Fri: API-only
  // - 02:00 Mon-Fri: Regression (exclude e2e)
  // - 03:00 Mon-Fri: E2E
  // - Other hours: Smoke
  boolean weekday = (dow >= Calendar.MONDAY && dow <= Calendar.FRIDAY)
  if (weekday && hour == 1) return 'API'
  if (weekday && hour == 2) return 'REGRESSION'
  if (weekday && hour == 3) return 'E2E'
  return 'SMOKE'
}

def suiteForProfile(String profile) {
  switch (profile?.trim()?.toUpperCase()) {
    case 'API': return 'testng-api.xml'
    case 'REGRESSION': return 'testng-regression.xml'
    case 'E2E': return 'testng-e2e.xml'
    case 'SMOKE': return 'testng-smoke.xml'
    default: return 'testng-smoke.xml'
  }
}

def parseAttrsFromTag(String tag) {
  def m = [:]
  if (!tag) return m
  // Extract key="value" pairs; good enough for TestNG XML.
  def re = (tag =~ /([A-Za-z0-9_:-]+)="([^"]*)"/)
  re.each { g ->
    m[g[1]] = g[2]
  }
  return m
}

def parseFailedTestsFromTestngResults(String xmlText) {
  def out = []
  if (!xmlText) return out

  // Avoid XmlSlurper to reduce Jenkins sandbox/script-approval friction.
  // Parse class blocks and then test-method tags inside them.
  def classBlocks = (xmlText =~ /(?s)<class\b[^>]*\bname="([^"]+)"[^>]*>(.*?)<\/class>/)
  classBlocks.each { g ->
    def className = g[1]
    def body = g[2]
    def methods = (body =~ /<test-method\b[^>]*>/)
    methods.each { mm ->
      def attrs = parseAttrsFromTag(mm[0])
      if (attrs['status'] == 'FAIL' && attrs['is-config'] != 'true') {
        def methodName = attrs['name']
        if (className && methodName) {
          out << [className: className, methodName: methodName]
        }
      }
    }
  }
  return out
}

// Hourly cron trigger. For per-profile cron with parameters, you'd need the "Parameterized Scheduler" plugin.
properties([
  disableConcurrentBuilds(),
  pipelineTriggers([cron('H * * * *')]),
  parameters([
    choice(
      name: 'RUN_PROFILE',
      choices: ['AUTO', 'SMOKE', 'REGRESSION', 'E2E', 'API'].join('\n'),
      description: "AUTO uses schedule (Asia/Ho_Chi_Minh): 01:00 API, 02:00 Regression, 03:00 E2E, else Smoke."
    ),
    string(name: 'BRANCH_NAME', defaultValue: 'dev_jenkin', description: 'Git branch to run'),
    string(name: 'EMAIL_TO', defaultValue: 'ducanhdhtb@gmail.com', description: 'Email recipients'),
    string(name: 'TESTNG_SUITE', defaultValue: '', description: "Override suite file (e.g. testng-smoke.xml). Blank = use RUN_PROFILE default."),
    string(name: 'TEST_GROUPS', defaultValue: '', description: "Optional override: TestNG groups (e.g. smoke). Usually leave blank when using suite files."),
    string(name: 'EXCLUDED_GROUPS', defaultValue: '', description: "Optional override: excluded groups."),
    string(name: 'PLAYWRIGHT_HEADLESS', defaultValue: 'true', description: "true/false. CI should be true."),
    string(name: 'PLAYWRIGHT_SLOWMO_MS', defaultValue: '0', description: "Playwright slow motion in ms (0 disables).")
  ])
])

node {

  // Config
  def repoUrl = 'https://github.com/ducanhdhtb/Playwright-Java-POM-Framework.git'
  def branchName = coalesceStr(params.BRANCH_NAME, env.BRANCH_NAME, 'dev_jenkin')
  def emailTo = coalesceStr(params.EMAIL_TO, env.EMAIL_TO, 'ducanhdhtb@gmail.com')
  def requestedProfile = coalesceStr(params.RUN_PROFILE, env.RUN_PROFILE, 'AUTO')

  def profile = (requestedProfile?.toUpperCase() == 'AUTO') ? scheduledProfile() : requestedProfile.toUpperCase()
  def suiteOverride = coalesceStr(params.TESTNG_SUITE, env.TESTNG_SUITE, '')
  def testngSuite = suiteOverride ? suiteOverride : suiteForProfile(profile)

  def testGroups = coalesceStr(params.TEST_GROUPS, env.TEST_GROUPS) ?: ''
  def excludedGroups = coalesceStr(params.EXCLUDED_GROUPS, env.EXCLUDED_GROUPS) ?: ''
  def playwrightHeadless = coalesceStr(params.PLAYWRIGHT_HEADLESS, env.PLAYWRIGHT_HEADLESS, 'true')
  def playwrightSlowMoMs = coalesceStr(params.PLAYWRIGHT_SLOWMO_MS, env.PLAYWRIGHT_SLOWMO_MS, '0')

  boolean needsBrowser = !(testngSuite?.toLowerCase()?.contains('api'))
  int installExitCode = 0
  int testExitCode = 0
  boolean failed = false
  String failDetails = ""
  def junitSummary = null

  // Maven tool (fallback to system mvn)
  try {
    def mvnHome = tool name: 'maven3', type: 'maven'
    env.PATH = "${mvnHome}/bin:${env.PATH}"
  } catch(Exception e) {
    echo "Warning: Maven tool 'maven3' not configured, using system mvn"
  }

  try {
    stage('Init') {
      echo "[Init] Repo: ${repoUrl}"
      echo "[Init] Branch: ${branchName}"
      echo "[Init] RUN_PROFILE(requested)=${requestedProfile} => profile=${profile}"
      echo "[Init] Suite: ${testngSuite}"
      echo "[Init] Groups override: '${testGroups}', Excluded override: '${excludedGroups}'"
      echo "[Init] Headless: ${playwrightHeadless}"
      echo "[Init] SlowMoMs: ${playwrightSlowMoMs}"
    }

    stage('Checkout') {
      echo "[Checkout] Start"
      deleteDir() // clean workspace (built-in, no extra plugin)
      checkout([$class: 'GitSCM',
        branches: [[name: "*/${branchName}"]],
        userRemoteConfigs: [[url: repoUrl]]
      ])
      echo "[Checkout] Done"
    }

    stage('Install Browsers') {
      if (!needsBrowser) {
        echo "[Install Browsers] Skipped (API-only run)"
        return
      }

      echo "[Install Browsers] Start"
      installExitCode = sh(
        script: '''
        mvn -B -DskipTests exec:java \
          -Dexec.mainClass=com.microsoft.playwright.CLI \
          -Dexec.args="install chromium"
        '''.stripIndent(),
        returnStatus: true
      )
      if (installExitCode != 0) {
        failed = true
        currentBuild.result = 'FAILURE'
        failDetails += "Install Browsers failed (exit code ${installExitCode})\n"
        echo "[Install Browsers] FAILED (exit code ${installExitCode})"
      } else {
        echo "[Install Browsers] Done"
      }
    }

    stage('Run Test') {
      echo "[Run Test] Start"
      def mvnArgs = []
      mvnArgs << "mvn -B clean test"
      mvnArgs << "-Dplaywright.headless='${playwrightHeadless}'"
      mvnArgs << "-Dplaywright.slowMoMs='${playwrightSlowMoMs}'"
      mvnArgs << "-Dtestng.suiteXmlFile='${testngSuite}'"
      if (testGroups?.trim()) {
        mvnArgs << "-Dtestng.groups='${testGroups}'"
      }
      if (excludedGroups?.trim()) {
        mvnArgs << "-Dtestng.excludedGroups='${excludedGroups}'"
      }

      def mvnCmd = mvnArgs.join(' ')
      echo "[Run Test] Command: ${mvnCmd}"

      // returnStatus keeps the pipeline running so we still publish reports + send email.
      testExitCode = sh(
        script: mvnCmd,
        returnStatus: true
      )
      if (testExitCode != 0) {
        failed = true
        currentBuild.result = 'FAILURE'
        failDetails += "Tests failed (exit code ${testExitCode})\n"
        echo "[Run Test] FAILED (exit code ${testExitCode})"
      } else {
        echo "[Run Test] PASSED"
      }
    }
  } catch (Exception e) {
    failed = true
    currentBuild.result = 'FAILURE'
    failDetails += "Unexpected pipeline error: ${e.getClass().getName()}: ${e.message}\n"
    echo "[Pipeline] Unexpected error: ${e.getClass().getName()}: ${e.message}"
  } finally {
    stage('Report') {
      echo "[Report] JUnit..."
      try {
        // Surefire generates JUnit-style XML under junitreports/ when running TestNG.
        junitSummary = junit allowEmptyResults: true, testResults: 'target/surefire-reports/junitreports/TEST-*.xml'
      } catch (Exception e) {
        echo "[Report] JUnit publish failed: ${e.getClass().getName()}: ${e.message}"
      }

      echo "[Report] Allure..."
      try {
        allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
      } catch (Exception e) {
        // Allure Jenkins plugin may not be installed; don't fail the build because of reporting.
        echo "[Report] Allure publish failed: ${e.getClass().getName()}: ${e.message}"
      }

      echo "[Report] Archive allure results..."
      try {
        archiveArtifacts artifacts: 'target/allure-results/**', fingerprint: true, allowEmptyArchive: true
      } catch (Exception e) {
        echo "[Report] Archive failed: ${e.getClass().getName()}: ${e.message}"
      }

      echo "[Report] Archive surefire reports..."
      try {
        archiveArtifacts artifacts: 'target/surefire-reports/**', fingerprint: true, allowEmptyArchive: true
      } catch (Exception e) {
        echo "[Report] Archive surefire reports failed: ${e.getClass().getName()}: ${e.message}"
      }

      echo "[Report] Archive traces/videos..."
      try {
        archiveArtifacts artifacts: 'traces/**,target/videos/**', fingerprint: true, allowEmptyArchive: true
      } catch (Exception e) {
        echo "[Report] Archive traces/videos failed: ${e.getClass().getName()}: ${e.message}"
      }

      echo "[Report] Archive failure snapshots..."
      try {
        archiveArtifacts artifacts: 'target/artifacts/**', fingerprint: true, allowEmptyArchive: true
      } catch (Exception e) {
        echo "[Report] Archive artifacts failed: ${e.getClass().getName()}: ${e.message}"
      }
    }

    stage('Notify') {
      def result = currentBuild.currentResult ?: (currentBuild.result ?: 'SUCCESS')
      def subject = "${result} ${env.JOB_NAME} #${env.BUILD_NUMBER}"

      def total = junitSummary?.totalCount
      def failedCount = junitSummary?.failCount
      def skipped = junitSummary?.skipCount
      def passed = (total != null && failedCount != null && skipped != null) ? (total - failedCount - skipped) : null

      def allureUrl = env.BUILD_URL ? "${env.BUILD_URL}allure/" : ""
      def artifactsUrl = env.BUILD_URL ? "${env.BUILD_URL}artifact/" : ""
      def surefireHtml = artifactsUrl ? (artifactsUrl + "target/surefire-reports/index.html") : ""

      // Parse failed tests for nicer email (best-effort).
      def failedTests = []
      try {
        def xmlPath = 'target/surefire-reports/testng-results.xml'
        if (fileExists(xmlPath)) {
          failedTests = parseFailedTestsFromTestngResults(readFile(xmlPath))
        }
      } catch (Exception e) {
        echo "[Notify] Failed test parse failed: ${e.getClass().getName()}: ${e.message}"
      }

      // List trace artifacts so recipients can download and open them in Playwright Trace Viewer.
      def traceFiles = []
      def traceLinks = ""
      try {
        def traces = sh(
          script: "ls -1 traces/*.zip 2>/dev/null | sort | tail -n 30",
          returnStdout: true
        ).trim()
        if (traces) {
          def lines = traces.split("\\r?\\n") as List
          traceFiles = lines
          def sb = new StringBuilder()
          sb.append("\\nTraces (download .zip):\\n")
          for (String f : lines) {
            // f is a relative path like traces/testName_123.zip
            if (artifactsUrl) {
              sb.append("- ").append(artifactsUrl).append(f).append("\\n")
            } else {
              sb.append("- ").append(f).append("\\n")
            }
          }
          traceLinks = sb.toString()
        }
      } catch (Exception e) {
        echo "[Notify] Trace list failed: ${e.getClass().getName()}: ${e.message}"
      }

      def bodyText =
        "Result: ${result}\n" +
        "Job: ${env.JOB_NAME}\n" +
        "Build: #${env.BUILD_NUMBER}\n" +
        "Branch: ${branchName}\n" +
        "Profile: ${profile}\n" +
        "URL: ${env.BUILD_URL}\n" +
        "TestNGSuite: ${testngSuite}\n" +
        "GroupsOverride: ${testGroups ?: '<from suite xml>'}\n" +
        (excludedGroups ? ("ExcludedGroups: ${excludedGroups}\n") : "") +
        (total != null ? ("Tests: total=${total}, passed=${passed}, failed=${failedCount}, skipped=${skipped}\n") : "") +
        "InstallExitCode: ${installExitCode}\n" +
        "TestExitCode: ${testExitCode}\n" +
        (allureUrl ? ("Allure: ${allureUrl}\n") : "") +
        (artifactsUrl ? ("Artifacts: ${artifactsUrl}\n") : "") +
        (surefireHtml ? ("SurefireReport: ${surefireHtml}\n") : "") +
        (failDetails ? ("\nDetails:\n" + failDetails) : "") +
        (traceLinks ?: "")

      def bodyHtml = null
      try {
        def sb = new StringBuilder()
        sb.append("<div style='font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Arial;'>")
        sb.append("<h2 style='margin:0 0 8px 0;'>").append(subject).append("</h2>")
        sb.append("<table cellpadding='6' cellspacing='0' style='border-collapse:collapse;border:1px solid #ddd;'>")
        def row = { k, v ->
          sb.append("<tr>")
          sb.append("<td style='border:1px solid #ddd;background:#f7f7f7;'><b>").append(k).append("</b></td>")
          sb.append("<td style='border:1px solid #ddd;'>").append(v ?: "").append("</td>")
          sb.append("</tr>")
        }
        row("Result", result)
        row("Job", env.JOB_NAME)
        row("Build", "#${env.BUILD_NUMBER}")
        row("Branch", branchName)
        row("Profile", profile)
        row("Suite", testngSuite)
        row("GroupsOverride", testGroups ?: "<from suite xml>")
        if (excludedGroups) row("ExcludedGroups", excludedGroups)
        if (total != null) row("Tests", "total=${total}, passed=${passed}, failed=${failedCount}, skipped=${skipped}")
        row("InstallExitCode", String.valueOf(installExitCode))
        row("TestExitCode", String.valueOf(testExitCode))
        if (env.BUILD_URL) row("Build URL", "<a href='${env.BUILD_URL}'>${env.BUILD_URL}</a>")
        if (allureUrl) row("Allure", "<a href='${allureUrl}'>open</a>")
        if (surefireHtml) row("Surefire", "<a href='${surefireHtml}'>open</a>")
        if (artifactsUrl) row("Artifacts", "<a href='${artifactsUrl}'>browse</a>")
        sb.append("</table>")

        if (failedTests && artifactsUrl) {
          sb.append("<h3 style='margin:16px 0 8px 0;'>Failed Tests</h3>")
          sb.append("<ul>")
          for (t in failedTests) {
            def base = (t.className + "_" + t.methodName).replaceAll("[^A-Za-z0-9_.-]", "_")
            def png = ""
            def html = ""
            def meta = ""
            try {
              png = sh(script: "ls -1 target/artifacts/failures/${base}_*.png 2>/dev/null | sort | tail -n 1", returnStdout: true).trim()
              html = sh(script: "ls -1 target/artifacts/failures/${base}_*.html 2>/dev/null | sort | tail -n 1", returnStdout: true).trim()
              meta = sh(script: "ls -1 target/artifacts/failures/${base}_*.txt 2>/dev/null | sort | tail -n 1", returnStdout: true).trim()
            } catch (Exception ignored) {
            }
            sb.append("<li><code>").append(t.className).append("#").append(t.methodName).append("</code>")
            if (png) sb.append(" - <a href='").append(artifactsUrl).append(png).append("'>screenshot</a>")
            if (html) sb.append(" - <a href='").append(artifactsUrl).append(html).append("'>dom.html</a>")
            if (meta) sb.append(" - <a href='").append(artifactsUrl).append(meta).append("'>meta</a>")
            if (png) {
              // Many email clients block remote images by default; link above is the reliable fallback.
              sb.append("<br/><img alt='screenshot' style='max-width:720px;border:1px solid #ddd;margin-top:6px;' src='")
                .append(artifactsUrl).append(png).append("'/>")
            }
            sb.append("</li>")
          }
          sb.append("</ul>")
        }

        if (failDetails) {
          sb.append("<h3 style='margin:16px 0 8px 0;'>Details</h3>")
          sb.append("<pre style='white-space:pre-wrap;background:#111;color:#eee;padding:10px;border-radius:6px;'>")
          sb.append(failDetails.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"))
          sb.append("</pre>")
        }

        if (traceFiles && artifactsUrl) {
          sb.append("<h3 style='margin:16px 0 8px 0;'>Traces</h3>")
          sb.append("<p>Download the trace .zip and open with <code>npx playwright show-trace file.zip</code>.</p>")
          sb.append("<ul>")
          for (String f : traceFiles) {
            sb.append("<li><a href='").append(artifactsUrl).append(f).append("'>").append(f).append("</a></li>")
          }
          sb.append("</ul>")
        }

        sb.append("</div>")
        bodyHtml = sb.toString()
      } catch (Exception e) {
        echo "[Notify] HTML body build failed: ${e.getClass().getName()}: ${e.message}"
      }

      sendBuildEmail(emailTo, subject, bodyText, bodyHtml)
    }

    if (failed) {
      error("Build failed\n${failDetails}".trim())
    }
  }
}
