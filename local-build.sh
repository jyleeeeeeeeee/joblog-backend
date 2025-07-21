#!/bin/bash

echo "🐳 [docker-build.sh] Jenkins 배포 환경 시작"

# 📌 환경파일 설정
export ENV_FILE=.env.docker
export SPRING_PROFILES_ACTIVE=docker

# ✅ 공통 함수 로드
source ./common.sh
load_env

echo "🧪 프로필 설정 : ${SPRING_PROFILES_ACTIVE}"
echo "🧼 [local-build.sh] 로컬 Redis → MySQL → 빌드 → App 순 재배포 시작"

# 🔥 기존 컨테이너 및 네트워크 제거 (Jenkins 제외)
echo "🧹 Redis / MySQL / App 컨테이너 및 네트워크 제거 (Jenkins 제외)"
docker-compose -f docker-compose.yml \
               -f docker-compose.redis.yml \
               -f docker-compose.mysql.yml \
               -f docker-compose.app.yml \
               -f docker-compose.jenkins.yml \
               down --remove-orphans

# ✅ Redis 실행
echo "🚀 Redis 컨테이너 실행"
docker-compose -f docker-compose.yml \
               -f docker-compose.redis.yml \
               up -d --build joblog-redis

# Redis 준비 대기
echo "⏳ Redis 준비 대기..."
for i in {1..10}; do
  docker exec joblog-redis redis-cli ping &> /dev/null && break
  echo "Redis 응답 대기 중... (${i}/10)"
  sleep 1
done

docker exec joblog-redis redis-cli ping &> /dev/null
if [ $? -ne 0 ]; then
  echo "❌ Redis가 정상적으로 실행되지 않았습니다. 배포 중단."
  docker logs joblog-redis
  exit 1
fi
echo "✅ Redis 정상 응답 확인"

# ✅ MySQL 실행
echo "🚀 MySQL 컨테이너 실행"
docker-compose -f docker-compose.yml \
               -f docker-compose.mysql.yml \
               up -d --build joblog-mysql

# MySQL 준비 대기
echo "⏳ MySQL 준비 대기..."
for i in {1..10}; do
  docker exec joblog-mysql mysqladmin ping -h localhost &> /dev/null && break
  echo "MySQL 응답 대기 중... (${i}/10)"
  sleep 1
done

docker exec joblog-mysql mysqladmin ping -h localhost &> /dev/null
if [ $? -ne 0 ]; then
  echo "❌ MySQL가 정상적으로 실행되지 않았습니다. 배포 중단."
  docker logs joblog-mysql
  exit 1
fi
echo "✅ MySQL 정상 응답 확인"

echo "🔨 Gradle 빌드 시작"
./gradlew clean build -x test
if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  exit 1
fi
echo "✅ 빌드 성공"

# ✅ App 실행
echo "🚀 App 컨테이너 실행"
docker-compose -f docker-compose.yml \
               -f docker-compose.redis.yml \
               -f docker-compose.mysql.yml \
               -f docker-compose.app.yml \
               up -d --build joblog-app


echo "🎉 App 서비스까지 로컬 배포 완료"

# ✅ Jenkins 실행
echo "🔧 Jenkins 컨테이너 실행 (선택적)"
docker-compose -f docker-compose.yml \
               -f docker-compose.jenkins.yml \
               up -d --build joblog-jenkins

echo "🎉 로컬 전체 배포 완료 (Jenkins 포함)"
