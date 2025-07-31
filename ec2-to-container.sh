#!/bin/bash
set -e

# 🐳 Jenkins 컨테이너 이름
JENKINS_CONTAINER="joblog-jenkins"
TARGET_DIR="/var/jenkins_home/workspace/joblog"

# 📦 복사할 파일들
FILES=".env.dev .env.staging .env.prod docker-compose.yml"

echo "📦 Jenkins 컨테이너로 파일 복사 중..."
for file in $FILES; do
  docker cp "./$file" "joblog-jenkins:/var/jenkins_home/workspace/joblog"
done

echo "✅ Jenkins 컨테이너에 복사 완료"
