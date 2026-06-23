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
        stage('Debug Workspace') {
            steps {
                sh 'pwd'
                sh 'ls -la'
                sh 'ls -la sharedevents'
            }
        }
        stage('Build sharedevents') {
            steps {
                dir('sharedevents') {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }
        stage('Build Jars') {
            steps {
                dir('ApiGateway') {
                    sh 'mvn clean package -DskipTests'
                }

                dir('AuthService') {
                    sh 'mvn clean package -DskipTests'
                }

                dir('BookingService') {
                    sh 'mvn clean package -DskipTests -X'
                }

                dir('InventoryService') {
                    sh 'mvn clean package -DskipTests'
                }

                dir('PaymentService') {
                    sh 'mvn clean package -DskipTests'
                }

                dir('NotificationService') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Test Kubernetes') {
            steps {
                sh 'echo "Current Context:"'
                sh 'kubectl config current-context'

                sh 'echo "Cluster Nodes:"'
                sh 'kubectl get nodes'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker build -t hrx4/api-gateway:latest ./ApiGateway'
                sh 'docker build -t hrx4/auth-service:latest ./AuthService'
                sh 'docker build -t hrx4/booking-service:latest ./BookingService'
                sh 'docker build -t hrx4/inventory-service:latest ./InventoryService'
                sh 'docker build -t hrx4/payment-service:latest ./PaymentService'
                sh 'docker build -t hrx4/notification-service:latest ./NotificationService'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {

                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'

                    sh 'docker push hrx4/api-gateway:latest'
                    sh 'docker push hrx4/auth-service:latest'
                    sh 'docker push hrx4/booking-service:latest'
                    sh 'docker push hrx4/inventory-service:latest'
                    sh 'docker push hrx4/payment-service:latest'
                    sh 'docker push hrx4/notification-service:latest'

                    sh 'docker logout'
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh 'kubectl apply -f k8s/postgres/'
                sh 'kubectl apply -f k8s/kafka/'
                sh 'kubectl apply -f k8s/apigateway/'
                sh 'kubectl apply -f k8s/auth/'
                sh 'kubectl apply -f k8s/booking/'
                sh 'kubectl apply -f k8s/inventory/'
                sh 'kubectl apply -f k8s/payment/'
                sh 'kubectl apply -f k8s/notification/'

                sh 'kubectl rollout restart deployment api-gateway || true'
                sh 'kubectl rollout restart deployment auth-service || true'
                sh 'kubectl rollout restart deployment booking-service || true'
                sh 'kubectl rollout restart deployment inventory-service || true'
                sh 'kubectl rollout restart deployment payment-service || true'
                sh 'kubectl rollout restart deployment notification-service || true'
            }
        }

        stage('Verify Deployment') {
            steps {
                sh 'echo "Deployments:"'
                sh 'kubectl get deployments'

                sh 'echo "Pods:"'
                sh 'kubectl get pods -o wide'

                sh 'echo "Services:"'
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

        always {
            deleteDir()
        }
    }
}