#!/bin/bash

echo "🐳 [docker-build.sh] Jenkins 배포 환경 시작"

export ENV_FILE=.env.docker
export SPRING_PROFILES_ACTIVE=docker
echo "🧪 프로필 설정 : ${SPRING_PROFILES_ACTIVE}"
echo "🧼 Redis → MySQL → 빌드 → App 순 재배포 시작 (Jenkins 제외)"

# 🔥 기존 Redis / MySQL / App 컨테이너 제거 (Jenkins 제외)
echo "🧹 Redis / MySQL / App 컨테이너 및 네트워크 제거"
docker-compose -f docker-compose.yml \
               -f docker-compose.redis.yml \
               -f docker-compose.mysql.yml \
               -f docker-compose.app.yml \
               down --remove-orphans

# ✅ Redis 실행
echo "🚀 Redis 컨테이너 실행"
docker-compose -f docker-compose.yml \
               -f docker-compose.redis.yml \
               up -d --build joblog-redis

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

# 🧪 테스트 실행
echo "⏳ 테스트 실행"
./gradlew test
if [ $? -ne 0 ]; then
  echo "❌ 테스트 실패. 로그 출력:"
  ./gradlew test --info
  exit 1
fi
echo "✅ 테스트 성공"

# 🛠️ 빌드
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
               -f docker-compose.app.yml \
               up -d --build joblog-app

echo "🎉 Jenkins 자동 배포 완료 (Jenkins 컨테이너 제외)"
