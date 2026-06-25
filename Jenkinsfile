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

        stage('SonarQube Analysis') {
             steps {
                 dir('command') {
                     withSonarQubeEnv('SonarQube') {
                         bat '''
                             mvn sonar:sonar ^
                             -Dsonar.projectKey=healthcare-command-service ^
                             -Dsonar.projectName=healthcare-command-service ^
                             -Dsonar.host.url=%SONAR_HOST_URL% ^
                             -Dsonar.login=%SONAR_AUTH_TOKEN%
                         '''
                     }
                 }
             }
         }

        stage('Quality Gate') {
             steps {
                 timeout(time: 3, unit: 'MINUTES') {
                     waitForQualityGate abortPipeline: true
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
            echo 'Command service CI + SonarQube pipeline completed successfully.'
        }

        failure {
            echo 'Command service pipeline failed. Check console logs.'
        }
    }
}