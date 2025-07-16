#!/bin/bash

echo "🧹 1. Spring Boot 전체 빌드 및 테스트 실행"
./gradlew clean build --no-build-cache --rerun-tasks

if [ $? -ne 0 ]; then
  echo "❌ 테스트 또는 빌드 실패. 배포 중단."
  exit 1
fi

echo "🧨 2. 기존 Docker 컨테이너 정리"
docker-compose down --remove-orphans
docker rm -f joblog-app joblog-mysql joblog-redis joblog-jenkins 2>/dev/null || true
docker container prune -f

echo "🐳 3. Docker Compose로 재시작"
docker-compose up -d --build

echo "✅ 배포 완료"
