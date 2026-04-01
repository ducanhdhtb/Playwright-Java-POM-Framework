node {
  stage('Init') {
    echo "Pipeline loaded from ${env.BRANCH_NAME ?: 'unknown branch'} commit ${env.GIT_COMMIT ?: 'unknown'}"
  }

  stage('Checkout') {
    checkout scm
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
}
