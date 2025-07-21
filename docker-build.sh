##!/bin/bash
#
#echo "🐳 [docker-build.sh] docker 배포 환경 시작"
#
#export ENV_FILE=.env.docker
#export $(grep -v '^#' "$ENV_FILE" | xargs)
#echo "🧪 프로필 설정 : ${SPRING_PROFILES_ACTIVE}"
#
#echo "🧼 Jenkins 제외 초기화 및 컨테이너 재빌드 시작"
#
#echo "🧹 Redis / MySQL / App 컨테이너 강제 제거 (Jenkins 제외)"
#docker rm -f joblog-redis joblog-mysql joblog-app 2>/dev/null
#docker network rm joblog_joblog 2>/dev/null  # ✅ 추가 필요
#docker network ls
#sleep 5
##docker network rm joblog_default 2>/dev/null
#
#
#echo "🚀 Redis / MySQL / App 컨테이너 시작"
#docker-compose --env-file "$ENV_FILE" -p joblog up -d --build joblog-redis joblog-mysql
#
#docker network ls
#sleep 5
## ⏳ Redis / MySQL 대기
#echo "⏳ Redis 준비 대기..."
#for i in {1..10}; do
#  docker exec joblog-redis redis-cli ping &> /dev/null && break
#  echo "Redis 응답 대기 중... (${i}/10)"
#  sleep 1
#done
#
#docker exec joblog-redis redis-cli ping &> /dev/null
#if [ $? -ne 0 ]; then
#  echo "❌ Redis가 정상적으로 실행되지 않았습니다. 배포 중단."
#  docker logs joblog-redis
#  exit 1
#fi
#echo "✅ Redis 정상 응답 확인"
#
## MySQL 준비 대기
#echo "⏳ MySQL 준비 대기..."
#for i in {1..10}; do
#  docker exec joblog-mysql mysqladmin ping -h localhost &> /dev/null && break
#  echo "MySQL 응답 대기 중... (${i}/10)"
#  sleep 1
#done
#
#docker exec joblog-mysql mysqladmin ping -h localhost &> /dev/null
#if [ $? -ne 0 ]; then
#  echo "❌ MySQL가 정상적으로 실행되지 않았습니다. 배포 중단."
#  docker logs joblog-mysql
#  exit 1
#fi
#echo "✅ MySQL 정상 응답 확인"
#
## 테스트 실행
#echo "🧪 테스트 실행"
#./gradlew clean test
#if [ $? -ne 0 ]; then
#  echo "❌ 테스트 실패. 배포 중단"
#  exit 1
#fi
#echo "✅ 테스트 성공"
#
## 🛠️ 빌드 실행
#./gradlew clean build -x test
#if [ $? -ne 0 ]; then
#  echo "❌ 빌드 실패. 배포 중단."
#  exit 1
#fi
#echo "✅ 빌드 성공"
#
#
#echo "🚀App 컨테이너 시작"
#docker-compose --env-file "$ENV_FILE" -p joblog up -d --build joblog-app
#
#echo "🎉 Jenkins 빌드 배포 완료"
#
#!/bin/bash
source ./common.sh

echo "🐳 [local-build.sh] local 배포 환경 시작"

export ENV_FILE=.env.docker
export $(grep -v '^#' "$ENV_FILE" | xargs)
echo "🧪 프로필 설정 : ${SPRING_PROFILES_ACTIVE}"
echo "🔍 ENV_FILE: $ENV_FILE"
cat "$ENV_FILE"
echo "🧼 [local-build.sh] 로컬 전체 초기화 및 컨테이너 재빌드 시작"
docker ps
sleep 5
# 🔥 모든 컨테이너 및 네트워크 제거 (Jenkins 포함)
echo "🧹 모든 컨테이너 및 네트워크 제거"
docker rm -f joblog-redis joblog-mysql joblog-app 2>/dev/null
docker ps
sleep 5
# ✅ 전체 컨테이너 재생성 (Jenkins 포함)
echo "🐳 전체 컨테이너 재생성"
docker-compose --env-file "$ENV_FILE" up -d --build joblog-redis joblog-mysql joblog-app
#
docker ps
sleep 5
check_redis
check_mysql
build_no_test
