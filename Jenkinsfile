pipeline {
    agent any
    tools {
        maven 'Maven 3.5.3'
    }
    stages {
        stage ('Build and publish container') {
            steps {
                withCredentials([usernamePassword(credentialsId: '64b31b93-70ed-40fc-bb16-00a225b585a4', passwordVariable: 'DOCKERHUB_PASSWORD', usernameVariable: 'DOCKERHUB_USER')]) {
                    sh "mvn clean install -Ddockerhub.user=$DOCKERHUB_USER -Ddockerhub.password=$DOCKERHUB_PASSWORD"
                }
            }
        }
    }
    post {
        always {
            sh 'docker system prune -f'
        }
        success {
                mail to: '8bitforms@gmail.com',
                     subject: "Successfull build: ${currentBuild.fullDisplayName}",
                     body: "Click the link for details ${env.BUILD_URL}"
        }
        unstable {
                mail to: '8bitforms@gmail.com',
                     subject: "Unstable Pipeline: ${currentBuild.fullDisplayName}",
                     body: "Click the link for details with ${env.BUILD_URL}"
        }
        failure {
                mail to: '8bitforms@gmail.com',
                     subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                     body: "Something is wrong with ${env.BUILD_URL}"
            }
    }
}