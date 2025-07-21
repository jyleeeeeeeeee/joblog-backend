#!/bin/bash

echo "🐳 [docker-build.sh] Jenkins 배포 환경 시작"

# 📌 환경파일 설정
export ENV_FILE=.env.docker
export SPRING_PROFILES_ACTIVE=docker

# ✅ 공통 함수 로드
source ./common.sh
load_env

echo "🧪 프로필 설정 : ${SPRING_PROFILES_ACTIVE}"
echo "🧼 Redis → MySQL → 빌드 → App 순 재배포 시작 (Jenkins 제외)"

# ✅ 컨테이너 제거
docker-compose -f docker-compose.yml \
               -f docker-compose.redis.yml \
               -f docker-compose.mysql.yml \
               -f docker-compose.app.yml \
               --env-file "$ENV_FILE" \
               down --remove-orphans

# ✅ 네트워크 제거 (경고 제거 및 설정 충돌 방지)
echo "🧹 Docker 네트워크 초기화 (joblog_joblog 제거)"
docker network rm joblog_joblog 2>/dev/null || true

# ✅ Redis 실행
docker-compose -f docker-compose.yml \
               -f docker-compose.redis.yml \
               up -d --build joblog-redis

# ✅ MySQL 실행
docker-compose -f docker-compose.yml \
               -f docker-compose.mysql.yml \
               --env-file "$ENV_FILE" \
               up -d --build joblog-mysql

# ✅ 빌드
./gradlew clean build -x test
if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  exit 1
fi
echo "✅ 빌드 성공"

# ✅ App 실행
docker-compose -f docker-compose.yml \
               -f docker-compose.redis.yml \
               -f docker-compose.mysql.yml \
               -f docker-compose.app.yml \
               --env-file "$ENV_FILE" \
               up -d --build joblog-app

echo "🎉 Jenkins 자동 배포 완료"
