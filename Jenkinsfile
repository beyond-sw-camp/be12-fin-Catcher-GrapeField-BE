pipeline {
    agent none

    environment {
        DOCKER_USER = 'rekvv'
        IMAGE_NAME = 'grapefield_backend'
        IMAGE_TAG = "${new Date().format('yyyyMMdd')}-${BUILD_NUMBER}"
    }

    stages {
        stage('Git clone') {
            agent {
                label 'build'
            }
            steps {
                echo "Cloning Repository"
                git branch: 'main', url: 'https://github.com/beyond-sw-camp/be12-fin-Catcher-GrapeField-BE.git'
            }
        }

        stage('Gradle Build') {
            agent {
                label 'build'
            }
            steps {
                echo "Add Permission"
                sh 'chmod +x gradlew'

                echo "Build"
                sh './gradlew bootJar'
            }
        }

        stage('Docker Build') {
            agent {
                label 'build'
            }
            steps {
                script {
                    def fullImageName = "${DOCKER_USER}/${IMAGE_NAME}:${IMAGE_TAG}"
                    echo "Building Docker image: ${fullImageName}"

                    docker.build(fullImageName)
                }
            }
        }

        stage('Docker Push') {
            agent {
                label 'build'
            }
            steps {
                script {
                    def fullImageName = "${DOCKER_USER}/${IMAGE_NAME}:${IMAGE_TAG}"
                    echo "Pushing Docker image: ${fullImageName}"

                    docker.withRegistry('https://index.docker.io/v1/', 'DOCKER_HUB') {
                        docker.image(fullImageName).push()
                    }
                }
            }
        }

        stage('K8s Deploy via SSH') {
            agent {
                label 'deploy'
            }
            steps {
                script {
                    sshPublisher(
                        publishers: [
                            sshPublisherDesc(
                                configName: 'k8s',
                                verbose: true,
                                transfers: [
                                    sshTransfer(
                                        sourceFiles: 'k8s/backend-deployment.yml',
                                        remoteDirectory: '/home/test/k8s',
                                        execCommand: """
                                            echo "=== Replace version in YAML ==="
                                            sed -i 's/latest/${IMAGE_TAG}/g' /home/test/k8s/backend-deployment.yml

                                            echo "=== Deploy to Kubernetes ==="
                                            kubectl apply -f /home/test/k8s/backend-deployment.yml -n first

                                            echo "=== Rollout Status Check ==="
                                            kubectl rollout status deployment/backend-deployment -n first
                                        """
                                    )
                                ]
                            )
                        ]
                    )
                }
            }
        }
    }
}