##!/bin/bash
#
#echo "🐳 [docker-build.sh] Docker 배포 환경 시작"
#
#source ./common.sh
#
#load_env
#
## 컨테이너 제거 (Jenkins 제외)
#echo "🧹 Redis/MySQL/App 컨테이너 제거 (Jenkins 제외)"
##docker rm -f joblog-redis joblog-mysql joblog-app joblog-jenkins 2>/dev/null
##docker-compose --env-file .env.docker down --remove-orphans
#docker rm -f joblog-redis joblog-mysql joblog-app
#docker-compose --env-file .env.docker down --remove-orphans
#
#
#sleep 5
#
#
#wait_for_redis
#sleep 5
#wait_for_db
#sleep 5
##run_tests
#run_build
#sleep 5
#
## 8. App 컨테이너만 빌드 후 실행
#echo "🐳 App 컨테이너 배포"
#sleep 5
#docker-compose --env-file .env.docker up -d --build joblog-app
#sleep 5
#echo "🎉 Jenkins 빌드 배포 완료"


#!/bin/bash

echo "🐳 [docker-build.sh] Docker 배포 환경 시작"

source ./common.sh
load_env

# 🔥 모든 컨테이너 및 네트워크 제거
echo "🧹 모든 컨테이너 및 네트워크 제거"
docker-compose --env-file .env.docker down --remove-orphans

# ✅ 전체 서비스 한꺼번에 실행
echo "🐳 전체 컨테이너 재생성"
docker-compose --env-file .env.docker up -d --build

# Gradle 빌드
run_build

echo "🎉 Jenkins 빌드 배포 완료"
