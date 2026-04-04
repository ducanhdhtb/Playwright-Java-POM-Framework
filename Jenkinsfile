node {

  // Config
  def repoUrl = 'https://github.com/ducanhdhtb/Playwright-Java-POM-Framework.git'
  def branchName = env.BRANCH_NAME ?: 'dev_jenkin'

  // Maven tool (fallback to system mvn)
  try {
    def mvnHome = tool name: 'maven3', type: 'maven'
    env.PATH = "${mvnHome}/bin:${env.PATH}"
  } catch(Exception e) {
    echo "⚠️ Maven tool 'maven3' not configured, using system mvn"
  }

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
