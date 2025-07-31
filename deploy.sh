#!/bin/bash
set -e

# 📦 환경 변수 로드
ENV_FILE=".env.dev"
if [ -f "$ENV_FILE" ]; then
  echo "📄 환경 변수 로딩: $ENV_FILE"
  export $(grep -v '^#' "$ENV_FILE" | xargs)
else
  echo "❌ $ENV_FILE 파일이 존재하지 않습니다."
  exit 1
fi

# 서버 정보
REMOTE_USER=ubuntu
REMOTE_HOST=$REMOTE_HOST
KEY_PATH=/var/jenkins_home/.ssh/joblog-key.pem
TARGET_DIR=/home/ubuntu/joblog

echo "🚚 서버에 .env 파일 전송"
scp -i "$KEY_PATH" "$ENV_FILE" $REMOTE_USER@$REMOTE_HOST:$TARGET_DIR/

echo "🚀 서버에 SSH 접속 후 배포 시작"
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
