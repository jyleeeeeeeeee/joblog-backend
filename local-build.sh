#!/bin/bash

echo "🧼 [local-build.sh] 로컬 전체 초기화 및 컨테이너 재빌드 시작"

source ./common.sh

load_env

# 모든 컨테이너 제거 (Jenkins 포함)
echo "🧹 모든 컨테이너 제거"
docker rm -f joblog-redis joblog-mysql joblog-app joblog-jenkins 2>/dev/null


wait_for_redis
wait_for_db
run_tests
run_build

# 전체 컨테이너 재실행
echo "🐳 Docker Compose 전체 재시작"
docker-compose --env-file .env.docker up -d --build joblog-redis joblog-mysql joblog-app joblog-jenkins
echo "🎉 로컬 전체 배포 완료"
