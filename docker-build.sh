# 네트워크 상태 확인 함수
check_networks() {
  echo ""
  echo "📡 현재 Docker 네트워크 목록:"
  docker network ls
  echo ""
  echo "🔍 joblog_joblog 네트워크 상세:"
  docker network inspect joblog_joblog 2>/dev/null | grep -A 10 "\"Name\": \"joblog_joblog\"" || echo "❌ joblog_joblog 네트워크 없음"
  echo ""
}

echo "🐳 [docker-build.sh] Docker 배포 환경 시작"

export ENV_FILE=.env.docker
export SPRING_PROFILES_ACTIVE=docker
echo "🧪 프로필 설정 : ${SPRING_PROFILES_ACTIVE}"

# 초기 상태 확인
echo "📌 초기 네트워크 상태 확인"
check_networks

echo "🧼 Jenkins 제외 초기화 및 컨테이너 재빌드 시작"
echo "🧹 Redis / MySQL / App 컨테이너 강제 제거 (Jenkins 제외)"
docker rm -f joblog-redis joblog-mysql joblog-app 2>/dev/null

# 컨테이너 제거 후 네트워크 확인
check_networks

echo "🚀 Redis / MySQL / App 컨테이너 시작"
docker-compose --env-file .env.docker -p joblog up -d --build joblog-redis joblog-mysql joblog-app

# 컨테이너 생성 후 네트워크 확인
check_networks

# ... 중간 생략 (Redis, MySQL 준비 확인 부분 동일) ...

echo "✅ 빌드 성공"

echo "🚀 App 컨테이너 재시작"
docker-compose --env-file .env.docker -p joblog up -d --build joblog-app

# App 컨테이너 실행 후 네트워크 확인
check_networks

echo "🎉 Jenkins 빌드 배포 완료"
