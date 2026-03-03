import { Router } from 'express';
import bcrypt from 'bcrypt';
import { z } from 'zod';
import { prisma } from '../db.js';
import { requireAuth } from '../middleware/auth.js';
import { compareToken, hashToken, signAccessToken, signRefreshToken } from '../utils/tokens.js';

const signupSchema = z.object({
  email: z.string().email(),
  password: z.string().min(8),
  name: z.string().min(1).max(50).optional()
});

const loginSchema = z.object({
  email: z.string().email(),
  password: z.string().min(1)
});

const refreshSchema = z.object({
  refreshToken: z.string().min(1).optional()
});

const logoutSchema = z.object({
  refreshToken: z.string().min(1).optional()
});

export const authRouter = Router();

function pickRefreshToken(req: any, bodyToken?: string): string | null {
  return bodyToken ?? req.cookies?.refreshToken ?? null;
}

authRouter.post('/signup', async (req, res) => {
  const parsed = signupSchema.safeParse(req.body);
  if (!parsed.success) {
    return res.status(400).json({ message: 'Invalid input', errors: parsed.error.flatten() });
  }

  const { email, password, name } = parsed.data;

  const exists = await prisma.user.findUnique({ where: { email } });
  if (exists) {
    return res.status(409).json({ message: 'Email already in use' });
  }

  const passwordHash = await bcrypt.hash(password, 12);
  const user = await prisma.user.create({
    data: { email, passwordHash, name },
    select: { id: true, email: true, name: true }
  });

  return res.status(201).json({ user });
});

authRouter.post('/login', async (req, res) => {
  const parsed = loginSchema.safeParse(req.body);
  if (!parsed.success) {
    return res.status(400).json({ message: 'Invalid input' });
  }

  const { email, password } = parsed.data;

  const user = await prisma.user.findUnique({ where: { email } });
  if (!user) {
    return res.status(401).json({ message: 'Invalid credentials' });
  }

  const ok = await bcrypt.compare(password, user.passwordHash);
  if (!ok) {
    return res.status(401).json({ message: 'Invalid credentials' });
  }

  const accessToken = signAccessToken({ sub: user.id, email: user.email });
  const refreshToken = signRefreshToken(user.id);
  const refreshTokenHash = await hashToken(refreshToken);

  const expiresAt = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000);
  await prisma.session.create({
    data: {
      userId: user.id,
      refreshTokenHash,
      userAgent: req.headers['user-agent'] as string | undefined,
      ipAddress: req.ip,
      expiresAt
    }
  });

  res.cookie('refreshToken', refreshToken, {
    httpOnly: true,
    sameSite: 'lax',
    secure: false,
    path: '/auth'
  });

  return res.json({
    accessToken,
    user: { id: user.id, email: user.email, name: user.name }
  });
});

authRouter.post('/refresh', async (req, res) => {
  const parsed = refreshSchema.safeParse(req.body ?? {});
  if (!parsed.success) return res.status(400).json({ message: 'Invalid input' });

  const token = pickRefreshToken(req, parsed.data.refreshToken);
  if (!token) return res.status(401).json({ message: 'Missing refresh token' });

  const sessions = await prisma.session.findMany({
    where: {
      revokedAt: null,
      expiresAt: { gt: new Date() }
    },
    include: { user: true }
  });

  for (const session of sessions) {
    const matched = await compareToken(token, session.refreshTokenHash);
    if (!matched) continue;

    const accessToken = signAccessToken({ sub: session.user.id, email: session.user.email });
    return res.json({ accessToken });
  }

  return res.status(401).json({ message: 'Invalid refresh token' });
});

authRouter.post('/logout', async (req, res) => {
  const parsed = logoutSchema.safeParse(req.body ?? {});
  if (!parsed.success) return res.status(400).json({ message: 'Invalid input' });

  const token = pickRefreshToken(req, parsed.data.refreshToken);
  if (token) {
    const sessions = await prisma.session.findMany({ where: { revokedAt: null } });
    for (const session of sessions) {
      const matched = await compareToken(token, session.refreshTokenHash);
      if (!matched) continue;
      await prisma.session.update({
        where: { id: session.id },
        data: { revokedAt: new Date() }
      });
      break;
    }
  }

  res.clearCookie('refreshToken', { path: '/auth' });
  return res.status(204).send();
});

authRouter.get('/me', requireAuth, async (req, res) => {
  const user = await prisma.user.findUnique({
    where: { id: req.user!.id },
    select: { id: true, email: true, name: true, createdAt: true }
  });

  if (!user) return res.status(404).json({ message: 'User not found' });
  return res.json({ user });
});
