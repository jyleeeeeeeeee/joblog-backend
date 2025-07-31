if docker ps -a --format '{{.Names}}' | grep -q "^$CONTAINER_NAME$"; then
  if docker ps --format '{{.Names}}' | grep -q "^$CONTAINER_NAME$"; then
    echo "✅ Jenkins 컨테이너가 이미 실행 중입니다: $CONTAINER_NAME"
  else
    echo "🔄 Jenkins 컨테이너가 정지 상태입니다. 다시 시작합니다..."
    docker start "$CONTAINER_NAME"
  fi
else
  echo "🚀 Jenkins 컨테이너가 존재하지 않습니다. 새로 생성 후 실행합니다..."
  docker-compose up -d

  echo ""
  echo "🔑 Jenkins 초기 비밀번호:"
  docker exec "$CONTAINER_NAME" cat /var/jenkins_home/secrets/initialAdminPassword 2>/dev/null || echo "⚠️ 비밀번호를 가져올 수 없습니다. 컨테이너가 아직 준비되지 않았거나 최초 실행이 아닐 수 있습니다."
fi

