import React from 'react';
import ReactDOM from 'react-dom/client';
import { ChatWidget } from './components/ChatWidget';
import { ChatbotConfig } from './types';
import './index.css';

// Global configuration interface
interface GlobalConfig {
  apiUrl: string;
  jwtToken: string;
  sessionId?: string;
  position?: 'bottom-right' | 'bottom-left' | 'top-right' | 'top-left';
  theme?: 'light' | 'dark';
  primaryColor?: string;
  title?: string;
  subtitle?: string;
}

// Global function to initialize the chat widget
declare global {
  interface Window {
    PmsChatWidget: {
      init: (config: GlobalConfig) => void;
      destroy: () => void;
    };
  }
}

let widgetContainer: HTMLDivElement | null = null;
let root: ReactDOM.Root | null = null;

const initChatWidget = (config: GlobalConfig) => {
  // Destroy existing widget if any
  destroyChatWidget();

  // Create container
  widgetContainer = document.createElement('div');
  widgetContainer.id = 'pms-chat-widget';
  document.body.appendChild(widgetContainer);

  // Create React root and render
  root = ReactDOM.createRoot(widgetContainer);

  const chatbotConfig: ChatbotConfig = {
    apiUrl: config.apiUrl,
    jwtToken: config.jwtToken,
    sessionId: config.sessionId,
    position: config.position || 'bottom-right',
    theme: config.theme || 'light',
    primaryColor: config.primaryColor,
    title: config.title || 'PMS Assistant',
    subtitle: config.subtitle || 'How can I help you today?',
  };

  root.render(
    <React.StrictMode>
      <ChatWidget
        config={chatbotConfig}
        onMessage={(message) => {
          console.log('New message:', message);
        }}
        onResponse={(response) => {
          console.log('Bot response:', response);
        }}
        onError={(error) => {
          console.error('Chatbot error:', error);
        }}
      />
    </React.StrictMode>
  );
};

const destroyChatWidget = () => {
  if (root) {
    root.unmount();
    root = null;
  }
  
  if (widgetContainer) {
    document.body.removeChild(widgetContainer);
    widgetContainer = null;
  }
};

// Expose global API
window.PmsChatWidget = {
  init: initChatWidget,
  destroy: destroyChatWidget,
};

// Auto-initialize if config is provided via data attributes
const autoInit = () => {
  const script = document.querySelector('script[data-pms-chat-widget]');
  if (script) {
    const config: GlobalConfig = {
      apiUrl: script.getAttribute('data-api-url') || 'http://localhost:8080',
      jwtToken: script.getAttribute('data-jwt-token') || '',
      sessionId: script.getAttribute('data-session-id') || undefined,
      position: (script.getAttribute('data-position') as any) || 'bottom-right',
      theme: (script.getAttribute('data-theme') as any) || 'light',
      primaryColor: script.getAttribute('data-primary-color') || undefined,
      title: script.getAttribute('data-title') || 'PMS Assistant',
      subtitle: script.getAttribute('data-subtitle') || 'How can I help you today?',
    };

    if (config.jwtToken) {
      initChatWidget(config);
    } else {
      console.warn('PMS Chat Widget: JWT token is required');
    }
  }
};

// Initialize when DOM is ready
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', autoInit);
} else {
  autoInit();
}

export { ChatWidget, ChatbotConfig };
export type { GlobalConfig };