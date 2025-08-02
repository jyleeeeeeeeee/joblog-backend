#!/bin/bash
ENV_NAME=$(basename "$0" .sh)   # dev, staging, prod
SCRIPT_DIR=$(dirname "$0")

# 공통 배포 스크립트 실행
bash "$SCRIPT_DIR/common_deploy.sh" "$ENV_NAME"
