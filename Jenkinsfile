pipeline {
    agent any

    environment {

        // =========================
        // APP CONFIG
        // =========================
        APP_NAME = "springboot-kafka-app"

        // =========================
        // GITHUB CONFIG
        // =========================
        GIT_REPO = "https://github.com/ABHINAV12KUMAR/healthcare-command-service.git"
        GIT_BRANCH = "main"

        // =========================
        // WINDOWS JAVA & MAVEN PATH
        // =========================
        JAVA_HOME = "C:\\Program Files\\Java\\jdk-17"
        MAVEN_HOME = "C:\\Program Files\\apache-maven-3.9.13"

        PATH = "${JAVA_HOME}\\bin;${MAVEN_HOME}\\bin;${env.PATH}"

        // =========================
        // SPRING PROFILE
        // =========================
        SPRING_PROFILES_ACTIVE = "dev"
    }

    tools {
        maven 'maven3'
        jdk '17.0'
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: "${GIT_BRANCH}",
                    url: "${GIT_REPO}"
            }
        }

        stage('Clean Project') {
            steps {
                bat 'mvn clean'
            }
        }

        stage('Compile Project') {
            steps {
                bat 'mvn compile'
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

        // =========================
        // OPTIONAL DOCKER STAGES
        // =========================

        // stage('Build Docker Image') {
        //     steps {
        //         bat "docker build -t %APP_NAME%:latest ."
        //     }
        // }

        // stage('Deploy Container') {
        //     steps {
        //         bat """
        //             docker stop %APP_NAME%
        //             docker rm %APP_NAME%
        //
        //             docker run -d ^
        //             --name %APP_NAME% ^
        //             -p 8080:8080 ^
        //             -e SPRING_PROFILES_ACTIVE=%SPRING_PROFILES_ACTIVE% ^
        //             %APP_NAME%:latest
        //         """
        //     }
        // }

    }

    post {

        success {
            echo "Application Deployment Successful"
        }

        failure {
            echo "Pipeline Failed"
        }

        always {
            cleanWs()
        }
    }
}