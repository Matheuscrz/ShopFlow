import { create } from "zustand";

export interface UserSession {
  id: string;
  email: string;
  role: string;
}

interface AuthState {
  accessToken: string | null;
  user: UserSession | null;
  isAuthenticated: boolean;
  setSession: (token: string, user: UserSession) => void;
  clearSession: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  user: null,
  isAuthenticated: false,
  setSession: (accessToken, user) =>
    set({ accessToken, user, isAuthenticated: true }),
  clearSession: () =>
    set({ accessToken: null, user: null, isAuthenticated: false }),
}));
