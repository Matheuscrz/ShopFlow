import type { UserSession } from "../stores/auth-store";

export function decodeUserFromToken(token: string): UserSession {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join(""),
    );
    const parsed = JSON.parse(jsonPayload);

    return {
      id: parsed.sub,
      email: parsed.email,
      name: parsed.name ?? "",
      role: parsed.role,
    };
  } catch (error) {
    throw new Error("Falha ao decodificar token de acesso: " + error, {
      cause: error,
    });
  }
}
