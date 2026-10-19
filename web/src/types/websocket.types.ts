export type WebSocketEventType =
  | "ORDER_STATUS_UPDATED"
  | "NOTIFICATION_RECEIVED";

export interface WebSocketMessage<T = unknown> {
  type: WebSocketEventType;
  payload: T;
  timestamp: string;
}

export type WebSocketListener<T = unknown> = (data: T) => void;
