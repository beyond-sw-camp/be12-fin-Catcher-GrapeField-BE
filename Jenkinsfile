pipeline {
    agent none

    environment {
        DOCKER_USER = 'rekvv'
        IMAGE_NAME = 'grapefield_backend'
        IMAGE_TAG = "${new Date().format('yyyyMMdd')}-${BUILD_NUMBER}"
    }

    stages {
        stage('Git clone') {
            agent { label 'build' }
            steps {
                echo "Cloning Repository"
                git branch: 'main', url: 'https://github.com/beyond-sw-camp/be12-fin-Catcher-GrapeField-BE.git'
            }
        }

        stage('Gradle Build') {
            agent { label 'build' }
            steps {
                echo "Add Permission"
                sh 'chmod +x gradlew'
                echo "Build"
                sh './gradlew bootJar'
            }
        }

        stage('Docker Build') {
            agent { label 'build' }
            steps {
                script {
                    def fullImageName = "${DOCKER_USER}/${IMAGE_NAME}:${IMAGE_TAG}"
                    echo "Building Docker image: ${fullImageName}"
                    docker.build(fullImageName)
                }
            }
        }

        stage('Docker Push') {
            agent { label 'build' }
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

        stage('K8s Deploy') {
            agent { label 'deploy' }
            steps {
                script {
                    withEnv(['KUBECONFIG=/home/test/.kube/config']) {
                        sh """
                            # 이미지 태그 업데이트
                            sed -i 's/latest/${IMAGE_TAG}/g' k8s/backend-deployment.yml
                            
                            # 배포
                            kubectl apply -f k8s/backend-deployment.yml -n first
                            kubectl rollout status deployment/backend-deployment -n first
                        """
                    }
                }
            }
        }
    }
}
