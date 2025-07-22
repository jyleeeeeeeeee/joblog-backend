#!/bin/bash
set -e

# Oracle 서버 정보
REMOTE_USER=ubuntu
REMOTE_HOST=138.2.35.116
KEY_PATH=/var/jenkins_home/.ssh/oracle-key.pem
TARGET_DIR=/home/ubuntu/joblog

echo "🚚 Oracle 서버에 .env 파일 전송"
scp -i "$KEY_PATH" .env.docker $REMOTE_USER@$REMOTE_HOST:$TARGET_DIR/

echo "🚀 Oracle 서버에 SSH 접속 후 배포 시작"
ssh -i "$KEY_PATH" $REMOTE_USER@$REMOTE_HOST <<EOF
  cd $TARGET_DIR

  echo "🔄 기존 컨테이너 중지"
  docker-compose down

  echo "🐳 최신 Docker 이미지 pull"
  docker-compose pull

  echo "🚀 컨테이너 재시작"
  docker-compose up -d

  echo "✅ 배포 완료!"
EOF
