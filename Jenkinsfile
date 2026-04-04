node {

  // ====== CONFIG ======
  env.ENV = "staging"
  env.BROWSER = "chromium"
  def repoUrl = 'https://github.com/ducanhdhtb/Playwright-Java-POM-Framework.git'

  // ====== MAVEN SETUP ======
  try {
    def mvnHome = tool name: 'maven3', type: 'maven'
    env.PATH = "${mvnHome}/bin:${env.PATH}"
  } catch(Exception e) {
    echo "⚠️ Using system mvn"
  }

  boolean failed = false

  try {

    stage('Init') {
      echo "🚀 ENV: ${env.ENV}"
      echo "🌿 BRANCH: ${env.BRANCH_NAME ?: 'main'}"
    }

    stage('Checkout') {
      cleanWs()
      checkout([
        $class: 'GitSCM',
        branches: [[name: env.BRANCH_NAME ?: '*/main']],
        userRemoteConfigs: [[url: repoUrl]]
      ])
    }

    stage('Install Browsers') {
      sh '''
      mvn -B -DskipTests exec:java \
      -Dexec.mainClass=com.microsoft.playwright.CLI \
      -Dexec.args="install chromium"
      '''
    }

    stage('Run Test') {
      timeout(time: 30, unit: 'MINUTES') {
        retry(2) {
          sh 'mvn -B clean test'
        }
      }
    }

  } catch (err) {
    failed = true
    currentBuild.result = 'FAILURE'
    echo "❌ ERROR: ${err}"
  } finally {

    stage('Publish Report') {

      echo "📊 Publishing JUnit report..."
      junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

      echo "📈 Publishing Allure report..."
      allure([
        includeProperties: false,
        jdk: '',
        results: [[path: 'target/allure-results']]
      ])

      echo "📦 Archiving artifacts..."
      archiveArtifacts artifacts: 'target/allure-results/**', fingerprint: true
    }

    stage('Notify') {

      def status = failed ? "❌ FAILED" : "✅ SUCCESS"

      mail to: 'ducanhdhtb@gmail.com',
           subject: "${status} ${env.JOB_NAME} #${env.BUILD_NUMBER}",
           body: """
🔥 Build Result: ${status}

📦 Job: ${env.JOB_NAME}
🔢 Build: ${env.BUILD_NUMBER}
🌿 Branch: ${env.BRANCH_NAME}

🔗 Link: ${env.BUILD_URL}
📊 Allure: ${env.BUILD_URL}allure

      """
    }

    if (failed) {
      error("🚨 Build failed")
    } else {
      echo "🎉 Build success"
    }
  }
}