FROM jenkins/jenkins:lts

USER root

# JDK 17 설치
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get clean

# JAVA_HOME 환경 변수 등록
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

USER jenkins
