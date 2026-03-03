# Auth Backend Template (Express + TypeScript + Prisma)

로그인/로그아웃/회원가입이 바로 가능한 **백엔드 템플릿**입니다.

## 포함 API

- `POST /auth/signup` 회원가입
- `POST /auth/login` 로그인 (access token 발급 + refresh token 쿠키 저장)
- `POST /auth/refresh` access token 재발급
- `POST /auth/logout` 로그아웃 (refresh 세션 폐기)
- `GET /auth/me` 내 정보 조회 (Bearer access token 필요)
- `GET /health` 헬스체크

## 빠른 시작

```bash
cp .env.example .env
npm install
npx prisma migrate dev --name init
npm run dev
```

서버: `http://localhost:4000`

## 예시 요청

### 1) 회원가입

```bash
curl -X POST http://localhost:4000/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","name":"test"}'
```

### 2) 로그인

```bash
curl -i -X POST http://localhost:4000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

응답 body의 `accessToken`을 `Authorization: Bearer ...`로 사용하면 됩니다.

### 3) 내 정보

```bash
curl http://localhost:4000/auth/me \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

## 구조

```text
auth-backend-template/
  prisma/
    schema.prisma
  src/
    middleware/auth.ts
    routes/auth.ts
    utils/tokens.ts
    db.ts
    env.ts
    index.ts
```

## 확장 포인트

- 이메일 인증 / 비밀번호 재설정
- OAuth 소셜 로그인 (Google, Kakao 등)
- Redis 기반 refresh token 블랙리스트
- rate limit / brute-force 방지
