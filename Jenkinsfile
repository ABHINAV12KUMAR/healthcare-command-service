pipeline {
    agent any

    tools {
        maven 'maven3'
        jdk '17.0'
    }

    environment {
        APP_NAME = "healthcare-command-service"
        SPRING_PROFILES_ACTIVE = "dev"
    }

    stages {

        stage('Verify Files') {
            steps {
                bat 'dir'
            }
        }

        stage('Clean and Compile') {
            steps {
                bat 'mvn clean compile'
            }
        }

        stage('Run Unit Tests') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Build Jar') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }
    }

    post {
        success {
            echo "Application Build Successful"
        }

        failure {
            echo "Pipeline Failed"
        }

        always {
            cleanWs()
        }
    }
}