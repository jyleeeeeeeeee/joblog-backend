# 1. Java 17 경량 이미지
FROM openjdk:17-slim

WORKDIR /app

# 2. 실행할 JAR 파일 이름 설정 (plain 제외)
ARG JAR_FILE=build/libs/app.jar

# 3. 복사 (파일이 하나만 매칭된다는 전제 하에)
COPY ${JAR_FILE} app.jar

# 4. 실행/ㅏ
ENTRYPOINT ["java", "-jar", "/app.jar"]
