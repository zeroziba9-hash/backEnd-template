# Auth Backend Template Java (Spring Boot)

회원가입/로그인/로그아웃/토큰 재발급이 가능한 Java 백엔드 템플릿입니다.

## 스택
- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (jjwt)

## 보강 포인트 (koreanit-server-program docs 반영)
- 공통 응답 포맷: `ApiResponse<T>`
- 공통 에러 코드: `ErrorCode`
- 공통 예외: `ApiException`
- 전역 예외 처리: `GlobalExceptionHandler`
- 401/403 JSON 응답 통일
- dev/prod 프로필 분리 + CORS 환경변수화

## API
- `POST /auth/signup`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`
- `GET /auth/me`
- `GET /health`

`/auth/login` 응답 예시:
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "accessToken": "...",
    "refreshToken": "...",
    "user": { "id": "...", "email": "test@example.com", "name": "test" }
  },
  "code": null
}
```
(동시에 `refreshToken` 쿠키도 설정)

## 실행
```bash
cd auth-backend-template-java
# .env.example 참고해서 환경변수 설정
mvn spring-boot:run
```

## 환경 분리
- 기본 프로필: `dev`
- 운영 프로필: `prod`
- 운영에서는 필수 환경변수 주입 권장:
  - `SPRING_PROFILES_ACTIVE=prod`
  - `PORT`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
  - `JWT_ACCESS_SECRET`, `JWT_REFRESH_SECRET`
  - `CORS_ALLOWED_ORIGINS`

## 테스트 예시
```bash
curl -X POST http://localhost:8080/auth/signup -H "Content-Type: application/json" -d '{"email":"test@example.com","password":"password123","name":"test"}'

curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"email":"test@example.com","password":"password123"}'
```

## 참고
- 현재 템플릿은 단순화를 위해 dev에서 `ddl-auto: update` 사용
- 운영에서는 Flyway/Liquibase + stricter security 설정 권장
