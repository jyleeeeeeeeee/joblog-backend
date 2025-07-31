#!/bin/bash

set -e  # 에러 시 즉시 종료

# ✅ Jenkins 컨테이너 이름 (docker-compose.yml과 일치해야 함)
CONTAINER_NAME="jenkins"

# ✅ docker-compose.yml 위치로 이동
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# ✅ Jenkins 컨테이너 존재 여부 확인
if docker ps -a --format '{{.Names}}' | grep -q "^$CONTAINER_NAME$"; then
  # ▶ 컨테이너 실행 중인 경우
  if docker ps --format '{{.Names}}' | grep -q "^$CONTAINER_NAME$"; then
    echo "✅ Jenkins 컨테이너가 실행 중입니다: $CONTAINER_NAME"
    read -p "📝 Jenkins를 재시작하고 새로 빌드할까요? (y/n): " answer
    if [[ "$answer" == "y" || "$answer" == "Y" ]]; then
      echo "🧹 기존 Jenkins 컨테이너 중지 및 제거"
      docker compose down

      echo "🛠 캐시 없이 Jenkins 이미지 재빌드"
      docker compose build --no-cache

      echo "🚀 Jenkins 컨테이너 재실행"
      docker compose up -d
    else
      echo "⏩ Jenkins 컨테이너는 그대로 유지됩니다."
    fi
  else
    # ▶ 컨테이너 정지 상태
    echo "🔄 Jenkins 컨테이너가 정지 상태입니다. 강제 재시작합니다..."

    echo "🧹 기존 Jenkins 컨테이너 중지 및 제거"
    docker compose down

    echo "🛠 캐시 없이 Jenkins 이미지 재빌드"
    docker compose build --no-cache

    echo "🚀 Jenkins 컨테이너 재실행"
    docker compose up -d
  fi
else
  # ▶ 컨테이너 자체가 존재하지 않음
  echo "🚀 Jenkins 컨테이너가 존재하지 않습니다. 새로 생성 후 실행합니다..."
  docker compose up -d
fi

# ✅ 초기 비밀번호 출력 시도
echo ""
echo "🔑 Jenkins 초기 비밀번호:"
docker exec "$CONTAINER_NAME" cat /var/jenkins_home/secrets/initialAdminPassword 2>/dev/null || \
echo "⚠️ 비밀번호를 가져올 수 없습니다. 컨테이너가 아직 준비되지 않았거나 최초 실행이 아닐 수 있습니다."


#dev-mac
#dev-windows
