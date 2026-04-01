node {
  // Prefer Jenkins-managed Maven; fall back to system mvn
  try {
    def mvnHome = tool name: 'maven3', type: 'maven'
    env.PATH = "${mvnHome}/bin:${env.PATH}"
  } catch(Exception e) {
    echo "Warning: Jenkins Maven tool 'maven3' not found, using system mvn if present"
  }

  boolean failed = false

  try {
    stage('Init') {
      echo "Pipeline loaded from ${env.BRANCH_NAME ?: 'unknown branch'} commit ${env.GIT_COMMIT ?: 'unknown'}"
    }

    stage('Checkout') {
      checkout([
        $class: 'GitSCM',
        branches: [[name: env.BRANCH_NAME ?: '*/main']],
        userRemoteConfigs: [[url: 'https://github.com/ducanhdhtb/Playwright-Java-POM-Framework.git']]
      ])
    }

    stage('Install Browsers') {
      sh 'mvn -B -DskipTests exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"'
    }

    stage('Test') {
      sh 'mvn -B clean test'
    }

    stage('Post') {
      junit 'target/surefire-reports/*.xml'
      archiveArtifacts artifacts: 'target/allure-results/**', fingerprint: true
    }
  } catch (err) {
    failed = true
    currentBuild.result = 'FAILURE'
    mail to: 'ducanhdhtb@gmail.com',
         subject: "FAILURE ${env.JOB_NAME} #${env.BUILD_NUMBER}",
         body: "Build failed.\nLink: ${env.BUILD_URL}\nSee console for details."
    throw err
  } finally {
    if (!failed) {
      mail to: 'ducanhdhtb@gmail.com',
           subject: "SUCCESS ${env.JOB_NAME} #${env.BUILD_NUMBER}",
           body: "Build passed.\nLink: ${env.BUILD_URL}"
    }
  }
}
