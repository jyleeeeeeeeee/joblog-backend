#!/bin/bash

echo "🧪 1. Spring Boot 전체 빌드 + 테스트 실행"
./gradlew clean build

# 빌드 실패 시 종료
if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  exit 1
fi

echo "✅ 빌드 성공"

echo "🐳 2. Redis 컨테이너 실행"
docker-compose up -d joblog-redis

echo "⏳ Redis 준비 대기 중..."
for i in {1..10}; do
  docker exec joblog-redis redis-cli ping &> /dev/null
  if [ $? -eq 0 ]; then
    echo "✅ Redis 준비 완료"
    break
  fi
  echo "⌛ Redis 응답 대기 중... (${i}/10)"
  sleep 1
done

# Redis 준비 실패 시 종료
if [ $i -eq 10 ]; then
  echo "❌ Redis 준비 실패. 배포 중단."
  docker-compose down
  exit 1
fi

echo "🧨 3. 기존 Docker Compose 서비스 중지"
docker-compose down

echo "🚀 4. 전체 컨테이너 실행 (App, MySQL, Redis, Jenkins)"
docker-compose up -d --build

echo "🎉 5. 배포 완료"
