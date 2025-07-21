#!/bin/bash
# common.sh

# 📦 .env 파일에서 환경변수를 export하는 공통 함수
load_env() {
  if [ -z "$ENV_FILE" ]; then
    echo "❌ ENV_FILE 환경변수가 지정되지 않았습니다."
    exit 1
  fi

  echo "📦 [$ENV_FILE] 환경변수 export 중..."
  if [ -f "$ENV_FILE" ]; then
    # 빈 줄, 주석(#) 제거 후 export
    export $(grep -v '^#' "$ENV_FILE" | grep -v '^$' | xargs)
  else
    echo "⚠️ 환경 파일이 존재하지 않습니다: $ENV_FILE"
    exit 1
  fi
}
