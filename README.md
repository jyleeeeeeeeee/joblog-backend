## 🚀 CI/CD 자동화 (Jenkins + Docker + Gradle)

### 📌 개요
이 프로젝트는 Jenkins와 Docker를 활용하여 **자동 빌드 + 테스트 + 배포**를 수행합니다.  
Push 이벤트 발생 시 Jenkins가 아래 과정을 자동으로 실행합니다.

---

### ⚙️ 자동화 흐름 요약

1. Redis 컨테이너 선 실행
2. Redis 준비 상태 확인 (PING 응답 확인)
3. Gradle 빌드 및 테스트 수행 (`./gradlew clean build`)
4. 실패 시 즉시 중단, 성공 시 다음 단계로 진행
5. 기존 컨테이너 중지 및 삭제 (`docker-compose down`)
6. 모든 컨테이너 재빌드 및 실행 (`docker-compose up -d --build`)

---

### 🛠️ 주요 설정

#### ✅ build-and-up.sh
```bash
#!/bin/bash

echo "🚀 0. Redis 컨테이너 선제 실행"
docker-compose up -d joblog-redis

echo "⏳ Redis 준비 대기..."
for i in {1..10}; do
  docker exec joblog-redis redis-cli ping &> /dev/null && break
  echo "Redis 응답 대기 중... (${i}/10)"
  sleep 1
done

if ! docker exec joblog-redis redis-cli ping &> /dev/null; then
  echo "❌ Redis가 정상적으로 실행되지 않았습니다. 배포 중단."
  exit 1
fi

echo "🧪 1. Spring Boot 전체 빌드 + 테스트 실행"
./gradlew clean build

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

PR 보호 테스트용 문장 추가
