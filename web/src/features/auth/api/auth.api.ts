import { apiClient } from "../../../lib/api-client";
import { sanitizeText } from "../../../lib/sanitize";
import type { RegisterFormData } from "../schemas/register.schema";
import type { LoginFormData } from "../schemas/auth.schema";
import type { LoginResponse, UserSession } from "../types/auth.types";
import type {
  ForgotPasswordFormData,
  ResetPasswordFormData,
} from "../schemas/recovery.schema";

export const authApi = {
  async register(data: RegisterFormData): Promise<UserSession> {
    const response = await apiClient.post<UserSession>("/users/register", {
      name: sanitizeText(data.name),
      email: sanitizeText(data.email),
      cpf: data.cpf,
      phone: data.phone,
      password: data.password,
    });

    return response.data;
  },

  async login(credentials: LoginFormData): Promise<LoginResponse> {
    const payload = {
      email: sanitizeText(credentials.email),
      password: credentials.password,
    };

    const response = await apiClient.post<LoginResponse>(
      "/auth/login",
      payload,
    );
    return response.data;
  },

  async refresh(): Promise<LoginResponse> {
    const response = await apiClient.post<LoginResponse>("/auth/refresh");
    return response.data;
  },

  async logout(): Promise<void> {
    await apiClient.post("/auth/logout");
  },

  async getMe(): Promise<UserSession> {
    const response = await apiClient.get<UserSession>("/users/me");
    return response.data;
  },

  async forgotPassword(data: ForgotPasswordFormData): Promise<void> {
    await apiClient.post("/auth/forgot-password", {
      email: sanitizeText(data.email),
    });
  },

  async resetPassword(data: ResetPasswordFormData): Promise<void> {
    await apiClient.post("/auth/reset-password", {
      token: data.token,
      newPassword: data.newPassword,
    });
  },
};
