#!/bin/bash
# ✅ Jenkins 빌드 전에 실행: .env.dev를 Jenkins 컨테이너 workspace로 복사
echo "📦 .env.dev Jenkins 컨테이너로 복사"

docker cp .env.dev joblog-jenkins:/var/jenkins_home/workspace/joblog/.env.dev