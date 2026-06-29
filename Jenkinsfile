pipeline {
    agent any

    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'idev', 'prod'],
            description: 'Select the target environment for deployment'
        )
    }

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
                    bat "mvn clean package -DskipTests -Dspring.profiles.active=${params.ENVIRONMENT}"
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

        stage('Build Docker Image') {
            steps {
                dir('command') {
                    bat """
                        docker build -t healthcare-command-service:${BUILD_NUMBER} .
                        docker tag healthcare-command-service:${BUILD_NUMBER} healthcare-command-service:latest
                    """
                }
            }
            post {
                success { echo "✅ Image built: healthcare-command-service:${BUILD_NUMBER}" }
                failure { echo "❌ Docker build failed." }
            }
        }

        stage('List Docker Images') {
            steps {
                bat 'docker images healthcare-command-service'
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'command/target/*.jar', fingerprint: true
            }
        }

        stage('Deploy to Environment') {
            when {
                expression { params.ENVIRONMENT != 'dev' }
            }
            steps {
                script {
                    if (params.ENVIRONMENT == 'idev') {
                        dir('command') {
                            bat """
                                docker tag healthcare-command-service:${BUILD_NUMBER} healthcare-command-service:idev
                                docker save healthcare-command-service:idev | gzip > healthcare-command-service-idev.tar.gz
                            """
                            // Add your idev deployment commands here
                            // Example: bat 'scp healthcare-command-service-idev.tar.gz user@idev-server:/path/'
                            // Example: bat 'ssh user@idev-server "docker load < healthcare-command-service-idev.tar.gz"'
                            // Example: bat 'ssh user@idev-server "docker run -d -p 8081:8081 healthcare-command-service:idev"'
                            echo "Deploying to idev environment..."
                        }
                    } else if (params.ENVIRONMENT == 'prod') {
                        echo "Deploying to production environment..."
                        // Add production deployment steps
                    }
                }
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