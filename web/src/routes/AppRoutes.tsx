import React, { useEffect, useRef, useState } from "react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { authApi } from "../features/auth/api/auth.api";
import { LoginPage } from "../features/auth/pages/LoginPage";
import { RegisterPage } from "../features/auth/pages/RegisterPage";
import { ForgotPasswordPage } from "../features/auth/pages/ForgotPasswordPage";
import { ResetPasswordPage } from "../features/auth/pages/ResetPasswordPage";
import { DashboardPage } from "../features/profile/pages/DashboardPage";
import { ProfilePage } from "../features/profile/pages/ProfilePage";
import { useAuthStore } from "../stores/auth-store";
import { decodeUserFromToken } from "../lib/jwt";

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

const FullPageSpinner = () => (
  <div className="min-h-screen flex items-center justify-center bg-background">
    <span className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin" />
  </div>
);

export const AppRoutes: React.FC = () => {
  const [initializing, setInitializing] = useState(true);
  const sessionInitialized = useRef(false);
  const { setSession, clearSession } = useAuthStore();

  useEffect(() => {
    if (sessionInitialized.current) {
      return;
    }

    sessionInitialized.current = true;

    authApi
      .refresh()
      .then((data) => {
        const user = data.user ?? decodeUserFromToken(data.accessToken);
        setSession(data.accessToken, user);
      })
      .catch(() => clearSession())
      .finally(() => setInitializing(false));
  }, [setSession, clearSession]);

  if (initializing) {
    return <FullPageSpinner />;
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
          path="/register"
          element={
            <PublicRoute>
              <RegisterPage />
            </PublicRoute>
          }
        />

        <Route
          path="/forgot-password"
          element={
            <PublicRoute>
              <ForgotPasswordPage />
            </PublicRoute>
          }
        />

        <Route
          path="/reset-password"
          element={
            <PublicRoute>
              <ResetPasswordPage />
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

        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <ProfilePage />
            </ProtectedRoute>
          }
        />

        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
};
