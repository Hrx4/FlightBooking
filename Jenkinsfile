// pipeline {

// agent any

// stages {

//     stage('Build') {

//         steps {

//             sh 'mvn clean package -DskipTests'
//         }
//     }

//     stage('Docker Build') {

//         steps {

//             sh 'docker build -t booking-service ./BookingService'
//             sh 'docker build -t inventory-service ./InventoryService'
//             sh 'docker build -t payment-service ./PaymentService'
//         }
//     }

//     stage('Deploy') {

//         steps {

//             sh 'kubectl apply -f k8s/'
//         }
//     }
// }

// }


pipeline {

    agent any

    stages {

        stage('Check Kubernetes') {

            steps {

                sh 'kubectl get pods'
            }
        }
    }
}

// 33d521514a4944709bc8bb2abc468e80