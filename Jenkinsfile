node {

  def repoUrl = 'https://github.com/ducanhdhtb/Playwright-Java-POM-Framework.git'

  // 🔥 FIX MAVEN
  def mvnHome = tool name: 'maven3', type: 'maven'
  env.PATH = "${mvnHome}/bin:${env.PATH}"

  stage('Init') {
    echo "Start"
  }

  stage('Checkout') {
    echo "[Checkout] Start"
    git branch: 'dev_jenkin', url: repoUrl
    // Ensure missing listener class exists to avoid TestNG error
    sh '''
    mkdir -p src/test/java/utils
    if [ ! -f src/test/java/utils/StepLoggerListener.java ]; then
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
      echo "[Checkout] Added stub StepLoggerListener.java"
    fi
    '''
    echo "[Checkout] Done"
  }

  stage('Install Browsers') {
    echo "[Install Browsers] Start"
    sh '''
    mvn -B -DskipTests exec:java \
    -Dexec.mainClass=com.microsoft.playwright.CLI \
    -Dexec.args="install chromium"
    '''
    echo "[Install Browsers] Done"
  }

  stage('Run Test') {
    echo "[Run Test] Start"
    sh 'mvn -B clean test'
    echo "[Run Test] Done"
  }

  stage('Report') {
    echo "[Report] JUnit..."
    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
    echo "[Report] Archive allure results..."
    archiveArtifacts artifacts: 'target/allure-results/**', fingerprint: true, allowEmptyArchive: true
    script {
      if (fileExists('target/allure-results')) {
        echo "[Report] Allure plugin not installed here; results archived only."
      } else {
        echo "[Report] No allure results found."
      }
    }
  }

  stage('Notify') {
    mail to: 'ducanhdhtb@gmail.com',
         subject: "Build ${currentBuild.currentResult}",
         body: "${env.BUILD_URL}"
  }
}
