#!/bin/bash
set -e
echo "📦 Jenkins 컨테이너로 파일 복사 중..."

docker exec -it joblog-jenkins mkdir -p /var/jenkins_home/.ssh

docker cp ~/joblog-key.pem joblog-jenkins:/var/jenkins_home/.ssh/joblog-key.pem
docker exec -it joblog-jenkins chmod 600 /var/jenkins_home/.ssh/joblog-key.pem

docker cp ~/.env.dev joblog-jenkins:/var/jenkins_home/workspace/joblog/.env.dev
docker cp ~/.env.staging joblog-jenkins:/var/jenkins_home/workspace/joblog/.env.staging
docker cp ~/.env.prod joblog-jenkins:/var/jenkins_home/workspace/joblog/.env.prod

echo "✅ Jenkins 컨테이너에 복사 완료"
