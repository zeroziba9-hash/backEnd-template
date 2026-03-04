# 🚀 backEnd-template

백엔드 프로젝트를 빠르게 시작하기 위한 **인증 서버 템플릿 모음**입니다.  
로그인/로그아웃/회원가입/토큰 재발급 흐름을 기본 탑재해서, 초기 세팅 시간을 줄이는 데 목적이 있습니다.

---

## 📦 포함된 템플릿

## `auth-backend-template-java` (Java)

- **Stack**: Spring Boot · Spring Security · JPA · PostgreSQL · JWT
- **Path**: `auth-backend-template-java/`

### 제공 API

| 기능 | Method | Endpoint |
|---|---|---|
| 회원가입 | POST | `/auth/signup` |
| 로그인 | POST | `/auth/login` |
| 로그아웃 | POST | `/auth/logout` |
| 토큰 재발급 | POST | `/auth/refresh` |
| 내 정보 조회 | GET | `/auth/me` |
| 헬스체크 | GET | `/health` |

### 보강 사항

- 공통 응답 포맷: `ApiResponse`
- 공통 에러 코드: `ErrorCode`
- 전역 예외 처리: `GlobalExceptionHandler`
- 401/403 JSON 응답 포맷 통일
- `dev/prod` 환경 분리 + CORS 환경변수 기반 설정

---

## ⚡ Quick Start (Java 템플릿)

```bash
cd auth-backend-template-java
mvn spring-boot:run
```

### 필수 환경변수

- `SPRING_PROFILES_ACTIVE`
- `PORT`
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_ACCESS_SECRET`, `JWT_REFRESH_SECRET`
- `CORS_ALLOWED_ORIGINS`

> 자세한 값 예시는 `.env.example` 참고

---

## 🧭 추천 사용 순서

1. `auth-backend-template-java` 폴더로 이동
2. `.env.example` 기준으로 환경변수 설정
3. DB 생성 및 연결 확인
4. 서버 실행 (`mvn spring-boot:run`)
5. Postman/curl로 인증 API 동작 확인

---

## 🔐 운영 배포 체크리스트

운영 환경에서는 아래 항목을 반드시 적용하세요.

- [ ] 시크릿 환경변수 분리
- [ ] HTTPS + Secure Cookie 설정
- [ ] DB 마이그레이션 도입 (Flyway/Liquibase)
- [ ] 로깅/모니터링 구성

---

## 📚 참고

- 각 템플릿 폴더 내부 `README.md`에 상세 실행 방법이 있습니다.
