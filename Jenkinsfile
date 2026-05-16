pipeline {
    agent any

    tools {
        maven 'maven3'
        jdk '17.0'
    }

    environment {
        SPRING_PROFILES_ACTIVE = "dev"
    }

    stages {

        stage('Verify Files') {
            steps {
                bat 'dir'
            }
        }

        stage('Build Model Module') {
            steps {
                dir('model') {
                    bat 'mvn clean install -DskipTests'
                }
            }
        }

        stage('Build Command Module') {
            steps {
                dir('command') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }
    }

    post {
        success {
            echo 'Build Successful'
        }

        failure {
            echo 'Pipeline Failed'
        }

        always {
            cleanWs()
        }
    }
}