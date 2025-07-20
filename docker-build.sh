#!/bin/bash

echo "🐳 [docker-build.sh] Docker 배포 환경 시작"

source ./common.sh

load_env

# 컨테이너 제거 (Jenkins 제외)
echo "🧹 Redis/MySQL/App 컨테이너 제거 (Jenkins 제외)"
#docker rm -f joblog-redis joblog-mysql joblog-app joblog-jenkins 2>/dev/null
docker-compose --env-file .env.docker down
wait_for_redis
wait_for_db
#run_tests
run_build

# 컨테이너 재실행
echo "🐳 Docker Compose 재실행 (Jenkins 제외)"
#docker-compose --env-file .env.docker up -d --build joblog-redis joblog-mysql joblog-app
docker-compose --env-file .env.docker up -d --build
echo "🎉 Jenkins 빌드 배포 완료"

