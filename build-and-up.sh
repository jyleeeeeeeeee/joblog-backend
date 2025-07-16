#!/bin/bash

echo "🚀 0. Redis 컨테이너 선제 실행 (빌드용)"
docker-compose up -d joblog-redis

echo "⏳ Redis 준비 대기..."
for i in {1..10}; do
  docker exec joblog-redis redis-cli ping &> /dev/null && break
  echo "Redis 응답 대기 중... (${i}/10)"
  sleep 1
done

echo "🧪 1. Spring Boot 전체 빌드 + 테스트 실행"
./gradlew clean build

# 빌드 실패 시 종료
if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  docker-compose down
  exit 1
fi

echo "✅ 빌드 성공"

echo "🧨 2. 기존 Docker Compose 중지 및 정리"
docker-compose down

echo "🐳 3. 전체 컨테이너 재실행 (App, MySQL, Redis, Jenkins)"
docker-compose up -d --build

echo "🎉 4. 모든 컨테이너 실행 완료"
