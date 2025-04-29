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
                git branch: 'develop', url: 'https://github.com/beyond-sw-camp/be12-fin-Catcher-GrapeField-BE.git'
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
                            
                            # 배포 전 YAML 확인
                            echo "=== 배포할 YAML 파일 내용 ==="
                            cat k8s/backend-deployment.yml
                            
                            # 배포
                            kubectl apply -f k8s/backend-deployment.yml -n first
                            
                            # 리소스 이름 확인 및 상태 확인
                            DEPLOYMENT_NAME=\$(kubectl get deployment -n first -l app=backend -o jsonpath='{.items[0].metadata.name}')
                            echo "배포된 Deployment 이름: \$DEPLOYMENT_NAME"
                            
                            # 확인된 이름으로 롤아웃 상태 체크
                            kubectl rollout status deployment/\$DEPLOYMENT_NAME -n first
                        """
                    }
                }
            }
        }
    }
    post {
        success {
            echo "배포 성공: ${DOCKER_USER}/${IMAGE_NAME}:${IMAGE_TAG}"
        }
        failure {
            echo "배포 실패: 오류를 확인하세요."
        }
    }
}
