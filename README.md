# 🚀 backEnd-template

백엔드 프로젝트를 빠르게 시작하기 위한 **인증 서버 템플릿 모음**입니다.
로그인/로그아웃/회원가입/토큰 재발급 흐름을 기본 탑재해 초기 세팅 시간을 줄이는 데 목적이 있습니다.

---

## 📌 프로젝트 목적
- 인증 기능이 포함된 Spring Boot 템플릿을 빠르게 재사용
- 공통 응답/예외 처리 규칙을 일관되게 적용
- 개발/운영 환경 분리 패턴을 실무형으로 연습

## 🧱 포함 템플릿
- `auth-backend-template-java/`
  - Stack: Spring Boot · Spring Security · JPA · PostgreSQL · JWT

## ⚡ Quick Start
```bash
cd auth-backend-template-java
mvn spring-boot:run
```

## 🔐 필수 환경변수
- `SPRING_PROFILES_ACTIVE`
- `PORT`
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_ACCESS_SECRET`, `JWT_REFRESH_SECRET`
- `CORS_ALLOWED_ORIGINS`

## 🧭 추천 사용 순서
1. 템플릿 폴더로 이동
2. `.env.example` 기준 환경변수 설정
3. DB 생성 및 연결 확인
4. 서버 실행 후 API 테스트

## 📚 참고
- 상세 엔드포인트/구성은 템플릿 내부 README 참고
