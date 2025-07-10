#!/bin/bash

# Docker 이미지 및 컨테이너 이름
IMAGE_NAME=joblog-backend
CONTAINER_NAME=joblog-container

echo "✅ Docker 이미지 빌드 시작"
docker build -t $IMAGE_NAME .

echo "🛑 기존 컨테이너 종료 및 삭제"
docker stop $CONTAINER_NAME 2>/dev/null || true
docker rm $CONTAINER_NAME 2>/dev/null || true

echo "🚀 새 컨테이너 실행"
docker run -d --name $CONTAINER_NAME -p 8080:8080 $IMAGE_NAME
