import React, { useEffect, useState } from "react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router";
import { authApi } from "../features/auth/api/auth.api";
import { LoginPage } from "../features/auth/pages/LoginPage";
import { DashboardPage } from "../features/profile/pages/DashboardPage";
import { useAuthStore } from "../stores/auth-store";

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
};

const PublicRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  return isAuthenticated ? (
    <Navigate to="/dashboard" replace />
  ) : (
    <>{children}</>
  );
};

export const AppRoutes: React.FC = () => {
  const [initializing, setInitializing] = useState(true);
  const { setSession, clearSession } = useAuthStore();

  useEffect(() => {
    // Ao abrir a aplicação, tenta restaurar a sessão via Cookie HttpOnly silenciosamente
    authApi
      .refresh()
      .then((data) => setSession(data.accessToken, data.user))
      .catch(() => clearSession())
      .finally(() => setInitializing(false));
  }, [setSession, clearSession]);

  if (initializing) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <span className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/login"
          element={
            <PublicRoute>
              <LoginPage />
            </PublicRoute>
          }
        />
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
};
