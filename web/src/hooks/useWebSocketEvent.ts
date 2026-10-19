import { useEffect } from "react";
import { wsClient } from "../lib/websocket-client";
import type { WebSocketEventType, WebSocketListener } from "../types/websocket.types";

export function useWebSocketEvent<T = unknown>(
  type: WebSocketEventType,
  handler: WebSocketListener<T>,
): void {
  useEffect(() => {
    const unsubscribe = wsClient.subscribe<T>(type, handler);
    return () => {
      unsubscribe();
    };
  }, [type, handler]);
}