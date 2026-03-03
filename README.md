# backEnd-template

백엔드 템플릿 모음 저장소입니다.  
로그인/로그아웃/회원가입 기반 인증 서버를 빠르게 시작할 수 있도록 구성했습니다.

## 포함된 템플릿

### 1) `auth-backend-template` (Node.js)
- **스택:** Express + TypeScript + Prisma + PostgreSQL + JWT
- **기능:**
  - 회원가입 `POST /auth/signup`
  - 로그인 `POST /auth/login`
  - 로그아웃 `POST /auth/logout`
  - 토큰 재발급 `POST /auth/refresh`
  - 내 정보 `GET /auth/me`

### 2) `auth-backend-template-java` (Java)
- **스택:** Spring Boot + Spring Security + JPA + PostgreSQL + JWT
- **기능:**
  - 회원가입 `POST /auth/signup`
  - 로그인 `POST /auth/login`
  - 로그아웃 `POST /auth/logout`
  - 토큰 재발급 `POST /auth/refresh`
  - 내 정보 `GET /auth/me`
  - 헬스체크 `GET /health`
- **보강사항:**
  - 공통 응답 포맷(`ApiResponse`)
  - 공통 에러 코드(`ErrorCode`) + 전역 예외 처리(`GlobalExceptionHandler`)
  - 401/403 JSON 응답 통일
  - dev/prod 환경 분리 및 CORS 환경변수 설정

---

## 빠른 시작 (Java 템플릿 기준)

```bash
cd auth-backend-template-java
mvn spring-boot:run
```

환경변수는 `.env.example` 참고:

- `SPRING_PROFILES_ACTIVE`
- `PORT`
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_ACCESS_SECRET`, `JWT_REFRESH_SECRET`
- `CORS_ALLOWED_ORIGINS`

---

## 추천 사용 순서

1. `auth-backend-template-java` 또는 `auth-backend-template` 중 하나 선택
2. `.env.example` 기반으로 환경변수 설정
3. DB 생성 및 연결 확인
4. 서버 실행
5. Postman/curl로 인증 API 테스트

---

## 참고

- 각 템플릿 폴더 내부 `README.md`에 상세 실행 방법이 있습니다.
- 운영 배포 시에는 반드시
  - 시크릿 환경변수 분리
  - HTTPS + secure cookie
  - DB 마이그레이션(Flyway/Liquibase)
  - 로깅/모니터링
  을 적용하세요.
