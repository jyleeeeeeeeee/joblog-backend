#!/bin/bash
source ./common.sh

echo "🐳 [local-build.sh] local 배포 환경 시작"

export ENV_FILE=.env.docker
export $(grep -v '^#' "$ENV_FILE" | xargs)
echo "🧪 프로필 설정 : ${SPRING_PROFILES_ACTIVE}"

echo "🧼 [local-build.sh] 로컬 전체 초기화 및 컨테이너 재빌드 시작"

# 🔥 모든 컨테이너 및 네트워크 제거 (Jenkins 포함)
echo "🧹 모든 컨테이너 및 네트워크 제거"
docker-compose --env-file "$ENV_FILE" -f docker-compose.yml down

# ✅ 전체 컨테이너 재생성 (Jenkins 포함)
echo "🐳 전체 컨테이너 재생성"
docker-compose --env-file "$ENV_FILE" up -d --build

check_redis
check_mysql
build_no_test