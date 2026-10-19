import type { UserSession } from "../stores/auth-store";

export function decodeUserFromToken(token: string): UserSession {
  try {
    const [, base64Url] = token.split(".");
    if (!base64Url) {
      throw new Error("Token JWT sem payload");
    }

    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join(""),
    );
    const parsed = JSON.parse(jsonPayload);
    const roles = Array.isArray(parsed.roles)
      ? parsed.roles
      : parsed.role
        ? [parsed.role]
        : [];
    const role = roles
      .map((value: unknown) => String(value).replace(/^ROLE_/, ""))
      .find(Boolean);

    if (!parsed.sub || !parsed.email || !role) {
      throw new Error("Token JWT sem subject, e-mail ou role");
    }

    return {
      id: parsed.sub,
      email: parsed.email,
      name: parsed.name ?? "",
      role,
    };
  } catch (error) {
    throw new Error("Falha ao decodificar token de acesso: " + error, {
      cause: error,
    });
  }
}
