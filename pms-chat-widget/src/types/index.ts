export interface ChatMessage {
  id: string;
  message: string;
  timestamp: Date;
  isUser: boolean;
  intent?: string;
  entities?: Record<string, any>;
}

export interface ChatResponse {
  message: string;
  intent: string;
  entities: Record<string, any>;
  timestamp: string;
  sessionId: string;
  requiresAction: boolean;
  actionType?: string;
  actionData?: Record<string, any>;
}

export interface ChatbotConfig {
  apiUrl: string;
  jwtToken: string;
  sessionId?: string;
  position?: 'bottom-right' | 'bottom-left' | 'top-right' | 'top-left';
  theme?: 'light' | 'dark';
  primaryColor?: string;
  title?: string;
  subtitle?: string;
}

export interface ChatWidgetProps {
  config: ChatbotConfig;
  onMessage?: (message: ChatMessage) => void;
  onResponse?: (response: ChatResponse) => void;
  onError?: (error: Error) => void;
}