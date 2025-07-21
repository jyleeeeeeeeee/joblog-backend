#!/bin/bash

echo "🐳 [local-build.sh] local 배포 환경 시작"

export ENV_FILE=.env.docker
export SPRING_PROFILES_ACTIVE=docker
echo "🧪 프로필 설정 : ${SPRING_PROFILES_ACTIVE}"
echo "🧼 [local-build.sh] 로컬 전체 초기화 및 컨테이너 재빌드 시작"

# 🔥 모든 컨테이너 및 네트워크 제거 (Jenkins 포함)
echo "🧹 모든 컨테이너 및 네트워크 제거"
docker-compose down --remove-orphans
docker rm -f $(docker ps -aq)

# 사용자 정의 네트워크 제거 (bridge, host 등 기본 제외)
docker network prune -f


# ✅ 전체 컨테이너 재생성 (Jenkins 포함)
echo "🐳 전체 컨테이너 재생성"
docker-compose --env-file "$ENV_FILE" up -d --build

# ⏳ Redis / MySQL 대기

# Redis가 올라올 때까지 대기
# Redis 우선 실행

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

# 🛠️ 빌드 실행

./gradlew clean build -x test
if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  exit 1
fi

echo "✅ 빌드 성공"

echo "🎉 로컬 전체 배포 완료"
