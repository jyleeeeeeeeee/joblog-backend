#!/bin/bash

#echo "✅ Docker 이미지 빌드 시작"
#./gradlew clean build
#
## Docker 이미지 및 컨테이너 이름
#IMAGE_NAME=joblog-backend
#CONTAINER_NAME=joblog-container
#
#echo "✅ Docker 이미지 빌드 시작"
#docker build -t $IMAGE_NAME .
#
#echo "🛑 기존 컨테이너 종료 및 삭제"
#docker stop $CONTAINER_NAME 2>/dev/null || true
#docker rm $CONTAINER_NAME 2>/dev/null || true
#
#echo "🚀 새 컨테이너 실행"
#docker run -d \
#  --name $CONTAINER_NAME \
#  --network joblog-backend_default \
#  -p 8080:8080 $IMAGE_NAME


#!/bin/bash

echo "🧹 1. Spring Boot 전체 빌드"
./gradlew clean build

if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  exit 1
fi

echo "🐳 2. Docker Compose 재시작"
docker-compose down
docker-compose up -d --build

echo "✅ 배포 완료"
