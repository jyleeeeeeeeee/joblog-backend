#!/bin/bash

echo "🐳 [docker-build.sh] Docker 배포 환경 시작"

# 1. .env.docker 로드
export $(grep -v '^#' .env.docker | xargs)

# 2. 기존 충돌 컨테이너 제거
echo "🧹 컨테이너 충돌 방지: 기존 컨테이너 강제 제거"
docker rm -f joblog-redis joblog-mysql joblog-jenkins joblog-app 2>/dev/null

# 3. Redis 선제 실행
echo "🚀 Redis 우선 실행"
docker-compose up -d joblog-redis

echo "⏳ Redis 준비 대기..."
for i in {1..10}; do
  docker exec joblog-redis redis-cli ping &> /dev/null && break
  echo "Redis 응답 대기 중... (${i}/10)"
  sleep 1
done

# 4. Redis 응답 확인
docker exec joblog-redis redis-cli ping &> /dev/null
if [ $? -ne 0 ]; then
  echo "❌ Redis가 정상적으로 실행되지 않았습니다. 배포 중단."
  docker logs joblog-redis
  exit 1
fi

# 5. 프로필 설정
export SPRING_PROFILES_ACTIVE=docker
echo "🧪 프로필 설정 : ${SPRING_PROFILES_ACTIVE}"

# 6. 테스트
./gradlew test
if [ $? -ne 0 ]; then
  echo "❌ 테스트 실패. 로그 출력:"
  ./gradlew test --info
  exit 1
fi
echo "✅ 테스트 성공"

# 7. 빌드
./gradlew clean build -x test
if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  exit 1
fi
echo "✅ 빌드 성공"

# 8. 전체 컨테이너 정리 + 실행
echo "🧨 기존 Docker Compose 전체 정리"
docker-compose --env-file .env.docker down

echo "🐳 Docker Compose 재실행 (빌드 포함)"
docker-compose --env-file .env.docker up -d --build joblog-redis joblog-mysql joblog-app joblog-jenkins

echo "🚀 배포 완료"