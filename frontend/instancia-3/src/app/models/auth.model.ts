export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
}

export type AnalystRole = 'ANALYST' | 'ADMIN';

export interface CurrentAnalyst {
  id: number;
  name: string;
  email: string;
  role: AnalystRole;
}
