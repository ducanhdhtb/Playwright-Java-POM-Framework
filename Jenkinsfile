pipeline {
  agent any

  tools {
    jdk 'jdk17'
    maven 'maven3'
  }

  options {
    timestamps()
    ansiColor('xterm')
  }

  environment {
    PLAYWRIGHT_BROWSERS_PATH = "${WORKSPACE}/.cache/ms-playwright"
    MAVEN_OPTS = "-Dmaven.test.failure.ignore=true"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Install Browsers') {
      steps {
        sh 'mvn -B -DskipTests exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"'
      }
    }

    stage('Test') {
      steps {
        sh 'mvn -B clean test'
      }
      post {
        always {
          junit 'target/surefire-reports/*.xml'
          archiveArtifacts artifacts: 'target/allure-results/**', fingerprint: true
        }
      }
    }

    stage('Allure Report') {
      steps {
        allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
      }
    }
  }
}
