import { env } from "../config/env";
import { useAuthStore } from "../stores/auth-store";
import type {
  WebSocketEventType,
  WebSocketListener,
  WebSocketMessage,
} from "../types/websocket.types";

class WebSocketClient {
  private socket: WebSocket | null = null;
  private listeners = new Map<WebSocketEventType, Set<WebSocketListener<any>>>();
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectInterval = 3000;
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  private heartbeatTimer: ReturnType<typeof setInterval> | null = null;
  private isExplicitlyClosed = false;

  public connect(): void {
    if (this.socket && (this.socket.readyState === WebSocket.OPEN || this.socket.readyState === WebSocket.CONNECTING)) {
      return;
    }

    const token = useAuthStore.getState().accessToken;
    if (!token) {
      return;
    }

    this.isExplicitlyClosed = false;
    const url = `${env.wsUrl}?token=${encodeURIComponent(token)}`;

    this.socket = new WebSocket(url);

    this.socket.onopen = () => {
      this.reconnectAttempts = 0;
      this.startHeartbeat();
    };

    this.socket.onmessage = (event: MessageEvent) => {
      try {
        const message: WebSocketMessage = JSON.parse(event.data);
        if (message.type) {
          const typeListeners = this.listeners.get(message.type);
          typeListeners?.forEach((listener) => listener(message.payload));
        }
      } catch (err) {
        console.error("Erro ao processar mensagem do WebSocket:", err);
      }
    };

    this.socket.onerror = (error) => {
      console.warn("Erro no WebSocket:", error);
    };

    this.socket.onclose = () => {
      this.stopHeartbeat();
      if (!this.isExplicitlyClosed) {
        this.scheduleReconnect();
      }
    };
  }

  public disconnect(): void {
    this.isExplicitlyClosed = true;
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    this.stopHeartbeat();
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  }

  public subscribe<T>(type: WebSocketEventType, listener: WebSocketListener<T>): () => void {
    if (!this.listeners.has(type)) {
      this.listeners.set(type, new Set());
    }
    this.listeners.get(type)!.add(listener);

    // Retorna função para cancelar subscrição automaticamente
    return () => {
      this.listeners.get(type)?.delete(listener);
    };
  }

  public send(type: string, payload: unknown): void {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify({ type, payload, timestamp: new Date().toISOString() }));
    } else {
      console.warn("WebSocket não está aberto para envio.");
    }
  }

  private scheduleReconnect(): void {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      const timeout = this.reconnectInterval * Math.pow(1.5, this.reconnectAttempts);
      this.reconnectTimer = setTimeout(() => {
        this.reconnectAttempts++;
        this.connect();
      }, timeout);
    }
  }

  private startHeartbeat(): void {
    this.stopHeartbeat();
    this.heartbeatTimer = setInterval(() => {
      if (this.socket && this.socket.readyState === WebSocket.OPEN) {
        this.socket.send(JSON.stringify({ type: "PING" }));
      }
    }, 30000);
  }

  private stopHeartbeat(): void {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer);
      this.heartbeatTimer = null;
    }
  }
}

export const wsClient = new WebSocketClient();