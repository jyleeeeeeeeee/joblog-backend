#!/bin/bash

echo "🐳 [docker-build.sh] Docker 배포 환경 시작"

# 1. .env.docker 로드 (프로필 설정 포함)
export $(grep -v '^#' .env.docker | xargs)

# 2. Redis 선제 실행
docker-compose up -d joblog-redis

echo "⏳ Redis 준비 대기..."
for i in {1..10}; do
  docker exec joblog-redis redis-cli ping &> /dev/null && break
  echo "Redis 응답 대기 중... (${i}/10)"
  sleep 1
done

# Redis 정상 응답 확인
docker exec joblog-redis redis-cli ping &> /dev/null
if [ $? -ne 0 ]; then
  echo "❌ Redis가 정상적으로 실행되지 않았습니다. 배포 중단."
  exit 1
fi

# 3. 빌드 및 테스트
echo "🧪 Spring Boot 빌드 + 테스트"
./gradlew clean build

if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  exit 1
fi

echo "✅ 빌드 성공"

# 4. 전체 컨테이너 재실행
echo "🧨 기존 Docker Compose 중지"
docker-compose down

echo "🐳 Docker Compose 재시작"
docker-compose up -d --build

echo "🚀 배포 완료"
