#!/bin/bash

# 🔧 사용할 .env 파일 지정
ENV_FILE=".env.docker"

# 🔄 환경 변수 로드
export $(grep -v '^#' "$ENV_FILE" | xargs)
docker compose up -d --build
docker ps