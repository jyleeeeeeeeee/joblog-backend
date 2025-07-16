#!/bin/bash

echo "🧹 1. Spring Boot 전체 빌드"
./gradlew clean build

if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  exit 1
fi

echo "🐳 2. Docker Compose 재시작"
docker-compose down
docker-compose up -d --build

echo "✅ 배포 완료"
