#!/bin/bash
set -e

# 📦 전송할 파일들
FILES="joblog-key.pem .env.dev .env.staging .env.prod docker-compose.yml"

echo "🚚 로컬 → Jenkins EC2로 env 및 compose 파일 전송..."
scp -i "joblog-key.pem" $FILES "ubuntu@3.37.2.69:~/"

echo "✅ 파일 전송 완료"
