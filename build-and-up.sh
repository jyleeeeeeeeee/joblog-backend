#!/bin/bash

echo "🚀 0. Redis 컨테이너 선제 실행"
#!/bin/bash

export SPRING_PROFILES_ACTIVE=docker

docker-compose up -d joblog-redis

echo "⏳ Redis 준비 대기..."
for i in {1..10}; do
  if docker exec joblog-redis redis-cli ping | grep -q "PONG"; then
    echo "✅ Redis 준비 완료"
    break
  fi
  echo "⏳ Redis 응답 대기 중... (${i}/10)"
  sleep 1
done

# Redis가 준비되지 않으면 종료
if ! docker exec joblog-redis redis-cli ping | grep -q "PONG"; then
  echo "❌ Redis가 정상적으로 실행되지 않았습니다. 배포 중단."
  exit 1
fi

echo "🧪 1. Spring Boot 전체 빌드 + 테스트 실행"
./gradlew clean build

# 빌드 실패 시 종료
if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  exit 1
fi

echo "✅ 빌드 성공"

echo "🧨 2. 기존 Docker Compose 중지 및 정리"
docker-compose down

echo "🐳 3. Docker Compose로 전체 컨테이너 재실행"
docker-compose up -d --build

echo "🚀 4. Spring Boot + Redis + MySQL 컨테이너 실행 완료"
