import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import cookieParser from 'cookie-parser';
import { env } from './env.js';
import { authRouter } from './routes/auth.js';

const app = express();

app.use(helmet());
app.use(cors({ origin: env.CORS_ORIGIN, credentials: true }));
app.use(express.json());
app.use(cookieParser());

app.get('/health', (_req, res) => {
  res.json({ ok: true });
});

app.use('/auth', authRouter);

app.use((err: any, _req: express.Request, res: express.Response, _next: express.NextFunction) => {
  console.error(err);
  res.status(500).json({ message: 'Internal server error' });
});

app.listen(env.PORT, () => {
  console.log(`Auth template server listening on :${env.PORT}`);
});
