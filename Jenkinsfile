pipeline {
    agent any
    tools {
        maven 'Maven 3.5.3'
    }
    stages {
        stage ('Build') {
            steps {
                withCredentials([usernamePassword(credentialsId: '382374a6-e94c-4872-aac4-c6bfafb93a8a', passwordVariable: 'DOCKERHUB_PASSWORD', usernameVariable: 'DOCKERHUB_USER')]) {
                    sh "mvn clean install -Ddockerhub.user=$DOCKERHUB_USER -Ddockerhub.password=$DOCKERHUB_PASSWORD"
                }
            }
        }
    }
    post {
        always {
            sh 'docker system prune -af'
        }
    }
}