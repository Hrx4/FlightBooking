pipeline {
    agent any

    environment {
        DOCKER_USERNAME = 'hrx4'
        KUBECONFIG = credentials('kubeconfig')
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Hrx4/FlightBooking'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker build -t hrx4/auth-service:latest ./AuthService'
                sh 'docker build -t hrx4/booking-service:latest ./BookingService'
                sh 'docker build -t hrx4/inventory-service:latest ./InventoryService'
                sh 'docker build -t hrx4/payment-service:latest ./PaymentService'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh 'docker push hrx4/auth-service:latest'
                    sh 'docker push hrx4/booking-service:latest'
                    sh 'docker push hrx4/inventory-service:latest'
                    sh 'docker push hrx4/payment-service:latest'
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh 'kubectl apply -f k8s/postgres/'
                sh 'kubectl apply -f k8s/kafka/'
                sh 'kubectl apply -f k8s/auth/'
                sh 'kubectl apply -f k8s/booking/'
                sh 'kubectl apply -f k8s/inventory/'
                sh 'kubectl apply -f k8s/payment/'
            }
        }

        stage('Verify Deployment') {
            steps {
                sh 'kubectl get pods'
                sh 'kubectl get services'
            }
        }
    }

    post {
        success {
            echo '✅ Deployment successful!'
        }
        failure {
            echo '❌ Deployment failed! Check logs above.'
        }
    }
}