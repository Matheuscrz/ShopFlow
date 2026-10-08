export interface UserSession {
  id: string;
  email: string;
  name: string;
  role: string;
}

export interface LoginResponse {
  accessToken: string;
  user: UserSession;
}

export interface ApiErrorResponse {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  retryAfterSeconds?: number;
}