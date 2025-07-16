#!/bin/bash

echo "🧹 1. Spring Boot 전체 빌드 시작..."
./gradlew clean build -x test

# 빌드 실패 시 종료
if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  exit 1
fi

echo "✅ 빌드 성공"

echo "🧨 2. 기존 Docker Compose 중지 및 정리"
docker-compose down

echo "🐳 3. Docker Compose로 전체 컨테이너 재실행"
docker-compose up -d --build

echo "🚀 4. Spring Boot + Redis + MySQL 컨테이너 실행 완료"