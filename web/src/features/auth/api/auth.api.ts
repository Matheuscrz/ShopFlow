import { apiClient } from '../../../lib/api-client';
import { sanitizeText } from '../../../lib/sanitize';
import type { LoginFormData } from '../schemas/auth.schema';
import type { LoginResponse, UserSession } from '../types/auth.types';

export const authApi = {
  async login(credentials: LoginFormData): Promise<LoginResponse> {
    const payload = {
      email: sanitizeText(credentials.email),
      password: credentials.password,
    };

    const response = await apiClient.post<LoginResponse>('/auth/login', payload);
    return response.data;
  },

  async refresh(): Promise<LoginResponse> {
    // Cookie HttpOnly é enviado automaticamente pelo navegador, não é necessário enviar o token manualmente
    const response = await apiClient.post<LoginResponse>('/auth/refresh');
    return response.data;
  },

  async logout(): Promise<void> {
    // Invalida no banco/Redis e limpa cookie HttpOnly no backend
    await apiClient.post('/auth/logout');
  },

  async getMe(): Promise<UserSession> {
    const response = await apiClient.get<UserSession>('/users/me');
    return response.data;
  },
};