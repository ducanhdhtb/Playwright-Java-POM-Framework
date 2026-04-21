def sendBuildEmail(String to, String subject, String body) {
  echo "[Notify] Email to=${to}, subject=${subject}"

  // Prefer Email Extension plugin if available; fall back to core Mailer step.
  try {
    emailext to: to, subject: subject, body: body, mimeType: 'text/plain'
    echo "[Notify] Sent via emailext"
    return
  } catch (Exception e1) {
    echo "[Notify] emailext not available/failed: ${e1.getClass().getName()}: ${e1.message}"
  }

  try {
    mail to: to, subject: subject, body: body
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
    string(name: 'PLAYWRIGHT_HEADLESS', defaultValue: 'true', description: "true/false. CI should be true.")
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

  def testGroups = coalesceStr(params.TEST_GROUPS, env.TEST_GROUPS, '')
  def excludedGroups = coalesceStr(params.EXCLUDED_GROUPS, env.EXCLUDED_GROUPS, '')
  def playwrightHeadless = coalesceStr(params.PLAYWRIGHT_HEADLESS, env.PLAYWRIGHT_HEADLESS, 'true')

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
      // returnStatus keeps the pipeline running so we still publish reports + send email.
      testExitCode = sh(
        script: "mvn -B clean test -Dplaywright.headless='${playwrightHeadless}' -Dtestng.suiteXmlFile='${testngSuite}' -Dtestng.groups='${testGroups}' -Dtestng.excludedGroups='${excludedGroups}'",
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
        junitSummary = junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
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

      def body =
        "Result: ${result}\n" +
        "Job: ${env.JOB_NAME}\n" +
        "Build: #${env.BUILD_NUMBER}\n" +
        "Branch: ${branchName}\n" +
        "Profile: ${profile}\n" +
        "URL: ${env.BUILD_URL}\n" +
        "TestNGSuite: ${testngSuite}\n" +
        "Groups: ${testGroups}\n" +
        (excludedGroups ? ("ExcludedGroups: ${excludedGroups}\n") : "") +
        (total != null ? ("Tests: total=${total}, passed=${passed}, failed=${failedCount}, skipped=${skipped}\n") : "") +
        "InstallExitCode: ${installExitCode}\n" +
        "TestExitCode: ${testExitCode}\n" +
        (allureUrl ? ("Allure: ${allureUrl}\n") : "") +
        (artifactsUrl ? ("Artifacts: ${artifactsUrl}\n") : "") +
        (failDetails ? ("\nDetails:\n" + failDetails) : "")

      sendBuildEmail(emailTo, subject, body)
    }

    if (failed) {
      error("Build failed\n${failDetails}".trim())
    }
  }
}
