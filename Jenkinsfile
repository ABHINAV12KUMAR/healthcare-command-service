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
        // JAVA & MAVEN
        // =========================
        JAVA_HOME = "/usr/lib/jvm/java-17-openjdk"
        MAVEN_HOME = "/opt/maven"

        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${env.PATH}"

        // =========================
        // MYSQL CONFIG
        // =========================
//         DB_URL = "jdbc:mysql://localhost:3306/orderdb"
//         DB_USERNAME = "root"
//         DB_PASSWORD = credentials('mysql-password')

        // =========================
        // KAFKA CONFIG
        // =========================
//         KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"
//         KAFKA_TOPIC = "order-topic"

        // =========================
        // SPRING PROFILE
        // =========================
        SPRING_PROFILES_ACTIVE = "dev"
    }

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
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
                sh 'mvn clean'
            }
        }

        stage('Compile Project') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }

//         stage('Build Jar') {
//             steps {
//                 sh 'mvn package -DskipTests'
//             }
//         }
//
//         stage('Build Docker Image') {
//             steps {
//                 sh """
//                     docker build -t ${APP_NAME}:latest .
//                 """
//             }
//         }

//         stage('Deploy Container') {
//             steps {
//                 sh """
//                     docker stop ${APP_NAME} || true
//                     docker rm ${APP_NAME} || true
//
//                     docker run -d \
//                     --name ${APP_NAME} \
//                     -p 8080:8080 \
//                     -e SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE} \
//                     -e DB_URL=${DB_URL} \
//                     -e DB_USERNAME=${DB_USERNAME} \
//                     -e DB_PASSWORD=${DB_PASSWORD} \
//                     -e KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS} \
//                     -e KAFKA_TOPIC=${KAFKA_TOPIC} \
//                     ${APP_NAME}:latest
//                 """
//             }
//         }
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