import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import { env } from '../env.js';
import type { JwtAccessPayload } from '../types.js';

export function signAccessToken(payload: JwtAccessPayload): string {
  return jwt.sign(payload, env.JWT_ACCESS_SECRET, {
    expiresIn: env.ACCESS_TOKEN_EXPIRES_IN
  });
}

export function signRefreshToken(userId: string): string {
  return jwt.sign({ sub: userId }, env.JWT_REFRESH_SECRET, {
    expiresIn: env.REFRESH_TOKEN_EXPIRES_IN
  });
}

export function verifyAccessToken(token: string): JwtAccessPayload {
  return jwt.verify(token, env.JWT_ACCESS_SECRET) as JwtAccessPayload;
}

export async function hashToken(token: string): Promise<string> {
  return bcrypt.hash(token, 10);
}

export async function compareToken(token: string, hash: string): Promise<boolean> {
  return bcrypt.compare(token, hash);
}
