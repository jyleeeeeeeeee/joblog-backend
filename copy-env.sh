#!/bin/bash
# ✅ Jenkins 빌드 전에 실행: .env.* 파일들을 Jenkins 컨테이너로 복사

ls
echo "📦 .env.dev → Jenkins 컨테이너로 복사"

docker cp ./env/.env.dev joblog-jenkins:/var/jenkins_home/workspace/joblog/.env.dev

echo "📦 .env.staging → Jenkins 컨테이너로 복사"
docker cp ./env/.env.staging joblog-jenkins:/var/jenkins_home/workspace/joblog/.env.staging

echo "📦 .env.prod → Jenkins 컨테이너로 복사"
docker cp ./env/.env.prod joblog-jenkins:/var/jenkins_home/workspace/joblog/.env.prod
