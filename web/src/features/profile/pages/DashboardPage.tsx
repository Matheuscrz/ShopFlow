import { LogOut, ShieldCheck, User } from "lucide-react";
import React from "react";
import { Button } from "../../../components/ui/Button";
import { useAuth } from "../../auth/hooks/useAuth";

export const DashboardPage: React.FC = () => {
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen bg-background text-text-main">
      <nav className="border-b border-border bg-surface px-6 py-4 flex justify-between items-center">
        <div className="flex items-center gap-2">
          <ShieldCheck className="w-6 h-6 text-primary" />
          <span className="font-bold text-lg">ShopFlow Painel</span>
        </div>
        <div className="flex items-center gap-4">
          <div className="text-sm text-right hidden sm:block">
            <div className="font-medium text-text-main">{user?.email}</div>
            <div className="text-text-muted text-xs capitalize">
              {user?.role?.toLowerCase() || "Operador"}
            </div>
          </div>
          <Button variant="secondary" onClick={logout} className="w-auto! py-2">
            <LogOut className="w-4 h-4" />
            <span className="hidden sm:inline">Sair</span>
          </Button>
        </div>
      </nav>

      <main className="max-w-5xl mx-auto p-6 mt-6">
        <div className="bg-surface border border-border rounded-xl p-6 shadow-sm">
          <h2 className="text-lg font-bold mb-4 flex items-center gap-2">
            <User className="w-5 h-5 text-primary" /> Dados da Sessão Ativa
          </h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm">
            <div className="p-4 rounded-lg bg-background border border-border">
              <span className="text-text-muted block text-xs mb-1">
                Identificador Único (UUID)
              </span>
              <span className="font-mono text-xs">{user?.id}</span>
            </div>
            <div className="p-4 rounded-lg bg-background border border-border">
              <span className="text-text-muted block text-xs mb-1">
                Permissão / Perfil
              </span>
              <span className="font-semibold text-primary">{user?.role}</span>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};
