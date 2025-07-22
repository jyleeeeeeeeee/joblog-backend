pipeline {
    agent any

    environment {
        SPRING_PROFILES_ACTIVE = 'docker'
    }

    tools {
    }

    stages {
        stage('Clean & Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
    }

    post {
        success {
            echo '✅ Build Success'
        }
        failure {
            echo '❌ Build Failed'
        }
    }
}
