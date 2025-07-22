pipeline {
    agent any

    environment {
        SPRING_PROFILES_ACTIVE = 'docker'
    }

    tools {
        jdk 'jdk17' // Jenkins에 등록된 JDK 이름 (없으면 생략 가능)
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
