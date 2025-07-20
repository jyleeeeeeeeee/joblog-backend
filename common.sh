#!/bin/bash

echo "📦 [common.sh] 공통 유틸 로딩"

# .env.docker 변수 로드
function load_env() {
  export $(grep -v '^#' .env.docker | xargs)
  echo "🔧 .env.docker 환경 변수 로드 완료"
}

# Redis가 올라올 때까지 대기
function wait_for_redis() {
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
}

# Gradle 테스트 실행
function run_tests() {
  export SPRING_PROFILES_ACTIVE=docker
  echo "🧪 프로필 설정 : ${SPRING_PROFILES_ACTIVE}"

  ./gradlew test
  if [ $? -ne 0 ]; then
    echo "❌ 테스트 실패. 로그 출력:"
    ./gradlew test --info
    exit 1
  fi
  echo "✅ 테스트 성공"
}

# Gradle 빌드 실행
function run_build() {
  ./gradlew clean build -x test
  if [ $? -ne 0 ]; then
    echo "❌ 빌드 실패. 배포 중단."
    exit 1
  fi
  echo "✅ 빌드 성공"
}
