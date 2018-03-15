pipeline {
    agent any
    tools {
        maven 'Maven 3.5.3'
    }
    stages {
        stage ('Build') {
            steps {
                withCredentials([usernamePassword(credentialsId: '382374a6-e94c-4872-aac4-c6bfafb93a8a', passwordVariable: 'DOCKERHUB_PASSWORD', usernameVariable: 'DOCKERHUB_USER')]) {
                    sh 'echo $DOCKERHUB_USER  +  $DOCKERHUB_PASSWORD'
                    sh "mvn clean package -Ddockerhub_user=$DOCKERHUB_USER -Ddockerhub_password=$DOCKERHUB_PASSWORD"
                }
            }
        }
    }
}