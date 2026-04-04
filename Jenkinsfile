node {

  def repoUrl = 'https://github.com/ducanhdhtb/Playwright-Java-POM-Framework.git'

  stage('Init') {
    echo "Start"
  }

  stage('Checkout') {
    git repoUrl
  }

  stage('Install Browsers') {
    sh '''
    mvn -B -DskipTests exec:java \
    -Dexec.mainClass=com.microsoft.playwright.CLI \
    -Dexec.args="install chromium"
    '''
  }

  stage('Run Test') {
    sh 'mvn -B clean test'
  }

  stage('Report') {
    junit 'target/surefire-reports/*.xml'

    allure([
      results: [[path: 'target/allure-results']]
    ])
  }

  stage('Notify') {
    mail to: 'ducanhdhtb@gmail.com',
         subject: "Build ${currentBuild.currentResult}",
         body: "${env.BUILD_URL}"
  }
}