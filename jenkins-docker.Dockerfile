# 베이스 이미지: Jenkins + JDK 17
FROM jenkins/jenkins:lts-jdk17

# 루트 권한 설정
USER root

# 도구 설치
RUN apt-get update && \
    apt-get install -y docker.io docker-compose && \
    apt-get clean

# 시간대 설정
ENV JAVA_OPTS="-Duser.timezone=Asia/Seoul"
