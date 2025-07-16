#!/bin/bash

echo "🧹 1. Spring Boot 전체 빌드"
./gradlew clean build

if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  exit 1
fi

echo "🧨 2. 기존 Docker 컨테이너 강제 정리"
docker-compose down --remove-orphans
docker container prune -f   # ⬅️ 충돌 방지

echo "🐳 3. Docker Compose로 재시작"
docker-compose up -d --build

echo "✅ 배포 완료"
