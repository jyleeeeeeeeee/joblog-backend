#!/bin/bash

# Jenkins 컨테이너 실행 스크립트
cd "$(dirname "$0")"

CONTAINER_NAME="joblog-jenkins"

# Jenkins 컨테이너 실행 중인지 확인
if docker ps --format '{{.Names}}' | grep -q "$CONTAINER_NAME"; then
  echo "⚠️  현재 Jenkins 컨테이너가 실행 중입니다: $CONTAINER_NAME"
  read -p "정말 종료하고 초기화할까요? (y/n): " answer
  if [[ "$answer" != "y" && "$answer" != "Y" ]]; then
    echo "🚫 초기화 중단됨."
    exit 0
  fi
else
  echo "ℹ️  Jenkins 컨테이너가 실행 중이지 않습니다. 초기화만 진행합니다."
fi

echo "🧼 Jenkins 컨테이너 및 볼륨 삭제 중..."
docker-compose down -v

echo "✅ 완료: Jenkins 컨테이너 및 볼륨 초기화됨"
docker-compose up -d
# 최대 30초까지 대기하면서 비밀번호 생성될 때까지 기다리기
for i in {1..30}; do
  docker exec joblog-jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>/dev/null && break
  echo "⌛ 초기 비밀번호 대기 중... ($i)"
  sleep 1
done

echo "✅ Jenkins 웹 UI → http://localhost:9090"
echo "🔑 초기 비밀번호:"
docker exec joblog-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
