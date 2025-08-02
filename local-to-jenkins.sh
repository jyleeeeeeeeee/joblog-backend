#!/bin/bash
set -e

echo "🚚 로컬 → Jenkins EC2로 env 및 compose 파일 전송..."

# 🔹 1. 서버에 env 디렉토리 생성
ssh -i joblog-key.pem ubuntu@15.164.224.34 'mkdir -p ~/env'

# 🔹 2. PEM 키와 docker-compose.yml 전송
scp -i joblog-key.pem joblog-key.pem ubuntu@15.164.224.34:~/
scp -i joblog-key.pem docker-compose.yml ubuntu@15.164.224.34:~/

# 🔹 3. .env 파일들 env 디렉토리에 전송
scp -i joblog-key.pem ./env/.env.dev     ubuntu@15.164.224.34:~/env/.env.dev
scp -i joblog-key.pem ./env/.env.staging ubuntu@15.164.224.34:~/env/.env.staging
scp -i joblog-key.pem ./env/.env.prod    ubuntu@15.164.224.34:~/env/.env.prod

# 🔹 4. 서버 -> 컨테이너 파일 전송 스크립트 파일 전송
scp -i joblog-key.pem ec2-to-container.sh ubuntu@15.164.224.34:~/

echo "✅ 파일 전송 완료"
