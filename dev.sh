#!/bin/bash
set -e

# 📦 환경 변수 로드
#ENV_FILE=".env.dev"
# 절대 경로 지정
ENV_FILE="/var/jenkins_home/workspace/joblog/.env.dev"
COMPOSE_FILE="/var/jenkins_home/workspace/joblog/docker-compose.yml"

if [ -f "$ENV_FILE" ]; then
  echo "📄 환경 변수 로딩: $ENV_FILE"
  export $(grep -v '^#' "$ENV_FILE" | xargs)
else
  echo "❌ $ENV_FILE 파일이 존재하지 않습니다."
  exit 1
fi

# 서버 정보
REMOTE_USER=ubuntu
REMOTE_HOST=$REMOTE_HOST                            # .env.dev에서 불러옴
KEY_PATH="/var/jenkins_home/.ssh/joblog-key.pem"    # Jenkins 컨테이너 내 PEM 경로
KNOWN_HOSTS="/var/jenkins_home/.ssh/known_hosts"    # 명시적으로 지정
TARGET_DIR="/home/ubuntu/joblog"

echo "🔐 PEM 경로: $KEY_PATH"
ls -l "$KEY_PATH"

# 📂 파일 전송
echo "🚚 Dev 서버로 .env.dev, docker-compose.yml 전송 중..."
scp -o UserKnownHostsFile="$KNOWN_HOSTS" -i "$KEY_PATH" \
  "$ENV_FILE" \
  "$COMPOSE_FILE" \
  "$REMOTE_USER@$REMOTE_HOST:$TARGET_DIR/"

# 🚀 원격 서버에서 배포 수행
echo "🚀 Dev 서버에서 배포 실행..."
ssh -o UserKnownHostsFile="$KNOWN_HOSTS" -i "$KEY_PATH" "$REMOTE_USER@$REMOTE_HOST" <<EOF
  set -e
  cd $TARGET_DIR

  echo "🛑 기존 컨테이너 중지 중..."
  docker compose --env-file .env.dev down

  echo "🐳 최신 Docker 이미지 Pull..."
  docker compose --env-file .env.dev pull

  echo "🚀 Docker Compose 재시작..."
  docker compose --env-file .env.dev up -d

  echo "✅ Dev 서버 배포 완료!"
EOF
