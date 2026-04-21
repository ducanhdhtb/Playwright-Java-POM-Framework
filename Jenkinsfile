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

node {

  // Config
  def repoUrl = 'https://github.com/ducanhdhtb/Playwright-Java-POM-Framework.git'
  def branchName = env.BRANCH_NAME ?: 'dev_jenkin'
  def emailTo = (env.EMAIL_TO?.trim()) ? env.EMAIL_TO.trim() : 'ducanhdhtb@gmail.com'
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
    }

    stage('Checkout') {
      echo "[Checkout] Start"
      deleteDir() // clean workspace (built-in, no extra plugin)
      checkout([$class: 'GitSCM',
        branches: [[name: "*/${branchName}"]],
        userRemoteConfigs: [[url: repoUrl]]
      ])
      // Ensure StepLoggerListener exists to avoid TestNG classpath error
      sh '''
      mkdir -p src/test/java/utils
      cat > src/test/java/utils/StepLoggerListener.java <<'EOF'
package utils;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
public class StepLoggerListener implements IInvokedMethodListener {
  @Override public void beforeInvocation(IInvokedMethod m, ITestResult r) {}
  @Override public void afterInvocation(IInvokedMethod m, ITestResult r) {}
}
EOF
      '''
      echo "[Checkout] Done"
    }

    stage('Install Browsers') {
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
      testExitCode = sh(script: 'mvn -B clean test', returnStatus: true)
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
        "URL: ${env.BUILD_URL}\n" +
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
