import { WifiOff } from "lucide-react";
import React, { useEffect, useState } from "react";

export const OfflineBanner: React.FC = () => {
  const [isOffline, setIsOffline] = useState(!navigator.onLine);

  useEffect(() => {
    const handleOnline = () => setIsOffline(false);
    const handleOffline = () => setIsOffline(true);

    window.addEventListener("online", handleOnline);
    window.addEventListener("offline", handleOffline);

    return () => {
      window.removeEventListener("online", handleOnline);
      window.removeEventListener("offline", handleOffline);
    };
  }, []);

  if (!isOffline) return null;

  return (
    <div
      role="status"
      aria-live="polite"
      className="bg-warning text-warning-foreground px-4 py-2 text-center text-xs sm:text-sm font-medium flex items-center justify-center gap-2 sticky top-0 z-50 transition-all shadow-md"
    >
      <WifiOff className="w-4 h-4 shrink-0" />
      <span>
        Conexão perdida. Algumas funcionalidades podem estar limitadas.
      </span>
    </div>
  );
};
