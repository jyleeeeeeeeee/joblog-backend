#!/bin/bash
set -e  # 에러 발생 시 즉시 중단

echo "📁 현재 디렉토리: $PWD"
cd "$(dirname "$0")"
echo "📁 이동 후 디렉토리: $PWD"

# ✅ Jenkins 환경 변수 또는 기본값
DOCKER_USERNAME=${DOCKER_USERNAME:-"jyleeeeeeeeee"}
REPO_NAME=${REPO_NAME:-"joblog"}
TAG=${TAG:-"latest"}

echo "👤 $DOCKER_USERNAME: $DOCKER_USERNAME"
echo "👤 $DOCKER_PASSWORD: $DOCKER_PASSWORD"
echo "📦 Repo: $REPO_NAME"
echo "🏷️ Tag: $TAG"

# 🔨 Gradle 빌드 (테스트 생략)
echo "🧹 Gradle clean & build 시작..."
./gradlew clean build -x test

# 🐳 Docker 이미지 빌드
echo "🐳 Docker 이미지 빌드 시작..."
IMAGE_TAG="$DOCKER_USERNAME/$REPO_NAME:$TAG"
docker build -t "$IMAGE_TAG" .

# 🔐 Docker Hub 로그인
echo "🔐 Docker Hub 로그인..."
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

# 🚀 Docker Hub 푸시
echo "🚀 Docker Hub 푸시..."
docker push "$IMAGE_TAG"

echo "✅ Docker 이미지 푸시 완료 → $IMAGE_TAG"

# 🎯 (선택 사항) 서버 Webhook 또는 SSH 재배포는 별도 처리
echo "🎉 배포 스크립트 완료"



