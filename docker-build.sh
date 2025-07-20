#!/bin/bash
echo "🐳 [docker-build.sh] Docker 배포 환경 시작"

# 1. .env.docker 로드
export $(grep -v '^#' .env.docker | xargs)

# 2. Redis 선제 실행
docker-compose up -d joblog-redis

echo "⏳ Redis 준비 대기..."
for i in {1..10}; do
  docker exec joblog-redis redis-cli ping &> /dev/null && break
  echo "Redis 응답 대기 중... (${i}/10)"
  sleep 1
done

# 3. Redis 정상 응답 확인
docker exec joblog-redis redis-cli ping &> /dev/null
if [ $? -ne 0 ]; then
  echo "❌ Redis가 정상적으로 실행되지 않았습니다. 배포 중단."
  docker logs joblog-redis
  exit 1
fi

# 4. 프로젝트 테스트
#!/bin/bash

# 환경 감지
if grep -qaE '(docker|kubepods)' /proc/1/cgroup 2>/dev/null; then
  echo "🐳 Docker 환경 감지됨 → docker-test 프로필 사용"
  export SPRING_PROFILES_ACTIVE=test
else
  echo "🖥️  로컬 환경 감지됨 → test 프로필 사용"
  export SPRING_PROFILES_ACTIVE=localtest
fi

echo "🧪 활성화된 프로필: ${SPRING_PROFILES_ACTIVE}"

# 테스트 실행
./gradlew test
echo "🧪 프로필 설정 : ${SPRING_PROFILES_ACTIVE}"
./gradlew test
if [ $? -ne 0 ]; then
  echo "❌ 테스트 실패. 로그 출력:"
  ./gradlew test --info
  exit 1
fi
echo "✅ 테스트 성공"

# 5. 프로젝트 빌드
export SPRING_PROFILES_ACTIVE=docker
echo "🧪 프로필 설정 : ${SPRING_PROFILES_ACTIVE}"

./gradlew clean build -x test
if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  exit 1
fi
echo "✅ 빌드 성공"

# 6. 전체 컨테이너 재실행
echo "🧨 기존 Docker Compose 중지"
docker-compose --env-file .env.docker down

echo "🐳 Docker Compose 재시작"
docker-compose --env-file .env.docker up -d --build

echo "🚀 배포 완료"
