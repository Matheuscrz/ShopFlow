import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { useAuthStore } from '../stores/auth-store';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = useAuthStore.getState().accessToken;
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

let isRefreshing = false;
let failedQueue: Array<{
  resolve: (token: string) => void;
  reject: (error: unknown) => void;
}> = [];

const processQueue = (error: unknown, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else if (token) {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // Tratamento de Rate Limit com leitura defensiva de Retry-After (RFC 6585)
    if (error.response?.status === 429) {
      const retryAfter = error.response.headers['retry-after'] ?? '60';
      console.warn(`Taxa limite de requisições excedida. Aguarde ${retryAfter} segundos.`);
      return Promise.reject(error);
    }

    // Interceptação de 401 e renovação silenciosa de token
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (originalRequest.url?.includes('/auth/refresh') || originalRequest.url?.includes('/auth/login')) {
        useAuthStore.getState().clearSession();
        return Promise.reject(error);
      }

      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return apiClient(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const refreshResponse = await apiClient.post('/auth/refresh');
        const { accessToken, user } = refreshResponse.data;
        useAuthStore.getState().setSession(accessToken, user);

        processQueue(null, accessToken);
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return apiClient(originalRequest);
      } catch (refreshErr) {
        processQueue(refreshErr, null);
        useAuthStore.getState().clearSession();
        return Promise.reject(refreshErr);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);