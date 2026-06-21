pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 15, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                echo 'Command service code checkout completed.'
            }
        }

        stage('Check Maven') {
            steps {
                bat 'mvn -v'
            }
        }

        stage('List Workspace Files') {
            steps {
                bat 'dir'
                bat 'dir command'
            }
        }

        stage('Build Command Service') {
            steps {
                dir('command') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'command/target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'Command service CI pipeline completed successfully.'
        }

        failure {
            echo 'Command service CI pipeline failed. Check console logs.'
        }
    }
}