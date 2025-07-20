#!/bin/bash

echo "🐳 [docker-build.sh] Docker 배포 환경 시작"

source ./common.sh

load_env

# 컨테이너 제거 (Jenkins 제외)
echo "🧹 Redis/MySQL/App 컨테이너 제거 (Jenkins 제외)"
docker rm -f joblog-redis joblog-mysql joblog-app 2>/dev/null
# ✅ 네트워크 제거 추가
echo "🧹 Docker 네트워크 제거 (옵션 충돌 방지)"
docker network rm joblog_default 2>/dev/null

wait_for_redis
wait_for_db
run_tests
run_build

# 컨테이너 재실행
echo "🐳 Docker Compose 재실행 (Jenkins 제외)"
docker-compose --env-file .env.docker up -d --build joblog-redis joblog-mysql joblog-app

echo "🎉 Jenkins 빌드 배포 완료"
