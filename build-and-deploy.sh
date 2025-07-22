#!/bin/bash

# Jenkins Job에서 실행할 스크립트
# 위치: joblog-backend/build-and-deploy.sh

set -e  # 실패 시 즉시 종료
echo "📁 현재 디렉토리: $PWD"
cd "$(dirname "$0")"  # 👉 항상 joblog-backend 기준에서 실행되도록 보장
echo "📁 현재 디렉토리: $PWD"
ENV_FILE=".env.docker"

echo "📦 .env 환경변수 로드"
if [ ! -f "$ENV_FILE" ]; then
  echo "❌ 환경변수 파일 ($ENV_FILE) 이 존재하지 않습니다."
  exit 1
fi

export $(grep -v '^#' "$ENV_FILE" | xargs)

echo "🧪 Gradle 테스트 및 빌드 시작"
./gradlew clean build

echo "🧼 기존 컨테이너 종료"
docker-compose --env-file "$ENV_FILE" down

echo "🐳 앱 컨테이너 재빌드 및 실행"
docker-compose --env-file "$ENV_FILE" up -d --build

echo "✅ 배포 완료!"