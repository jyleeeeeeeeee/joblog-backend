# 📦 Jenkins 기반 CI/CD 자동화 문서

## 🎯 목적
- GitHub에 Push → Jenkins가 자동으로 테스트, 빌드, 배포까지 수행
- Redis 의존성이 있는 테스트 실패 방지
- 모든 컨테이너 환경에서 작동 보장

---


## 🔁 전체 자동화 플로우

```bash
[GitHub Push] 
   ↓ Webhook Trigger
[Jenkins]
   ↓
[build-and-up.sh 실행]
   ↓
1. Redis 컨테이너 선 실행
2. Redis 준비 상태 확인
3. Gradle 빌드 + 테스트
4. 실패 시 중단
5. 컨테이너 전체 중지
6. 전체 컨테이너 재빌드 및 실행
