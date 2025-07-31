#!/bin/bash
set -e
echo "📦 Jenkins 컨테이너로 파일 복사 중..."

docker exec joblog-jenkins rm -f /var/jenkins_home/workspace/joblog/.env.dev
docker exec joblog-jenkins rm -f /var/jenkins_home/workspace/joblog/.env.staging
docker exec joblog-jenkins rm -f /var/jenkins_home/workspace/joblog/.env.prod
docker cp ~/.env.dev joblog-jenkins:/var/jenkins_home/workspace/joblog/.env.dev
docker cp ~/.env.staging joblog-jenkins:/var/jenkins_home/workspace/joblog/.env.staging
docker cp ~/.env.prod joblog-jenkins:/var/jenkins_home/workspace/joblog/.env.prod

echo "✅ Jenkins 컨테이너에 복사 완료"
