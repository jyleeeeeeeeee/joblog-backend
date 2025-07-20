#!/bin/bash

echo "🐳 [docker-build.sh] Docker 배포 환경 시작"

export ENV_FILE=.env.docker
export SPRING_PROFILES_ACTIVE=docker
echo "🧪 프로필 설정 : ${SPRING_PROFILES_ACTIVE}"

# 🔥 Redis / MySQL / App 컨테이너 및 네트워크만 제거 (Jenkins 제외)
echo "🧹 Redis / MySQL / App 컨테이너 및 네트워크 제거 (Jenkins 제외)"
docker-compose --env-file .env.docker down --remove-orphans
sleep 10

# ✅ Redis / MySQL 먼저 실행
echo "🚀 Redis / MySQL 컨테이너 시작"
docker-compose --env-file .env.docker up -d --build joblog-redis joblog-mysql

sleep 10
# ⏳ Redis / DB 준비 대기

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

# MySQL 우선 실행

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

# 🛠️ Gradle 빌드 (테스트 제외)

./gradlew clean build -x test
if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  exit 1
fi

echo "✅ 빌드 성공"

## ✅ App 컨테이너만 실행
#echo "🚀 App 컨테이너 실행"
#docker-compose --env-file .env.docker up -d --build joblog-app

echo "🎉 Jenkins 빌드 배포 완료"

