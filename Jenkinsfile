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
                bat 'dir command'
            }
        }

        stage('Clean and Compile') {
            steps {
                dir('command') {
                    bat 'mvn clean compile'
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                dir('command') {
                    bat 'mvn test'
                }
            }
        }

        stage('Build Jar') {
            steps {
                dir('command') {
                    bat 'mvn package -DskipTests'
                }
            }
        }
    }

    post {
        success {
            echo 'Application Build Successful'
        }

        failure {
            echo 'Pipeline Failed'
        }

        always {
            cleanWs()
        }
    }
}