node {

  // Maven setup
  try {
    def mvnHome = tool name: 'maven3', type: 'maven'
    env.PATH = "${mvnHome}/bin:${env.PATH}"
  } catch(Exception e) {
    echo "Warning: using system mvn"
  }

  boolean failed = false

  try {

    stage('Init') {
      echo "Pipeline from ${env.BRANCH_NAME ?: 'unknown'} - ${env.GIT_COMMIT ?: 'unknown'}"
    }

    stage('Checkout') {
      checkout([
        $class: 'GitSCM',
        branches: [[name: env.BRANCH_NAME ?: '*/main']],
        userRemoteConfigs: [[url: 'https://github.com/ducanhdhtb/Playwright-Java-POM-Framework.git']]
      ])
    }

    stage('Install Browsers') {
      sh '''
      mvn -B -DskipTests exec:java \
      -Dexec.mainClass=com.microsoft.playwright.CLI \
      -Dexec.args="install chromium"
      '''
    }

    stage('Test') {
      sh 'mvn -B clean test'
    }

  } catch (err) {
    failed = true
    currentBuild.result = 'FAILURE'
    echo "❌ Test failed"
  } finally {

    // 🔥 LUÔN chạy report dù pass hay fail
    stage('Report') {

      // JUnit để Jenkins hiểu pass/fail test detail
      junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

      // 🔥 Allure render UI (cái mày đang thiếu)
      allure([
        includeProperties: false,
        jdk: '',
        results: [[path: 'target/allure-results']]
      ])

      // (optional) vẫn giữ archive nếu muốn download raw file
      archiveArtifacts artifacts: 'target/allure-results/**', fingerprint: true
    }

    // 📧 Email
    if (failed) {
      mail to: 'ducanhdhtb@gmail.com',
           subject: "❌ FAILURE ${env.JOB_NAME} #${env.BUILD_NUMBER}",
           body: "Build failed\n${env.BUILD_URL}"
    } else {
      mail to: 'ducanhdhtb@gmail.com',
           subject: "✅ SUCCESS ${env.JOB_NAME} #${env.BUILD_NUMBER}",
           body: "Build passed\n${env.BUILD_URL}"
    }

    if (failed) {
      error("Build failed") // 🔥 ép Jenkins mark đỏ
    }
  }
}