#!/bin/bash
set -e

ENV_NAME="$1"
if [ -z "$ENV_NAME" ]; then
  echo "❌ ENV_NAME 인자가 없습니다."
  exit 1
fi

ENV_FILE="/var/jenkins_home/workspace/joblog/.env.${ENV_NAME}"
COMPOSE_FILE="/var/jenkins_home/workspace/joblog/docker-compose.yml"
KNOWN_HOSTS="/var/jenkins_home/.ssh/known_hosts"

# 📦 환경 변수 로드
if [ -f "$ENV_FILE" ]; then
  echo "📄 로딩: $ENV_FILE"
  export $(grep -v '^#' "$ENV_FILE" | xargs)
else
  echo "❌ $ENV_FILE 파일 없음"
  exit 1
fi

# 🔐 SSH Keyscan
if [ -n "$REMOTE_HOST" ]; then
  ssh-keyscan -H "$REMOTE_HOST" >> "$KNOWN_HOSTS" 2>/dev/null
else
  echo "❌ REMOTE_HOST 없음"
  exit 1
fi

# 📡 서버 접속 정보
REMOTE_USER=ubuntu
KEY_PATH="/var/jenkins_home/.ssh/joblog-key.pem"
TARGET_DIR="/home/ubuntu/joblog"

# 📤 전송
echo "🚚 .env.${ENV_NAME} 전송 중..."
scp -o UserKnownHostsFile="$KNOWN_HOSTS" -i "$KEY_PATH" \
  "$ENV_FILE" "$COMPOSE_FILE" \
  "$REMOTE_USER@$REMOTE_HOST:$TARGET_DIR/"

# 🚀 배포
ssh -o UserKnownHostsFile="$KNOWN_HOSTS" -i "$KEY_PATH" "$REMOTE_USER@$REMOTE_HOST" <<EOF
  set -e
  cd $TARGET_DIR
  rm -f .env && cp .env.${ENV_NAME} .env
  docker compose down
  docker compose pull
  docker compose up -d
EOF

echo "✅ ${ENV_NAME^^} 배포 완료"
