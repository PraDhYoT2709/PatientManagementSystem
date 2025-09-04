import React, { useEffect, useState } from 'react';
import { ChatButton } from './ChatButton';
import { ChatWindow } from './ChatWindow';
import { useChatbot } from '../hooks/useChatbot';
import { ChatbotAPI } from '../utils/api';
import { ChatWidgetProps } from '../types';

export const ChatWidget: React.FC<ChatWidgetProps> = ({ 
  config, 
  onMessage, 
  onResponse, 
  onError 
}) => {
  const [api, setApi] = useState<ChatbotAPI | null>(null);

  useEffect(() => {
    try {
      const chatbotAPI = new ChatbotAPI(config.apiUrl, config.jwtToken, config.sessionId);
      setApi(chatbotAPI);
    } catch (error) {
      console.error('Failed to initialize chatbot API:', error);
      onError?.(error as Error);
    }
  }, [config.apiUrl, config.jwtToken, config.sessionId, onError]);

  const {
    messages,
    isLoading,
    isOpen,
    sendMessage,
    toggleChat,
    clearMessages,
    messagesEndRef,
  } = useChatbot(api!);

  const handleSendMessage = async (message: string) => {
    if (!api) return;

    try {
      await sendMessage(message);
    } catch (error) {
      console.error('Error sending message:', error);
      onError?.(error as Error);
    }
  };

  const getPositionClasses = () => {
    const baseClasses = 'chat-widget';
    
    switch (config.position) {
      case 'bottom-left':
        return `${baseClasses} bottom-4 left-4`;
      case 'top-right':
        return `${baseClasses} top-4 right-4`;
      case 'top-left':
        return `${baseClasses} top-4 left-4`;
      default:
        return `${baseClasses} bottom-4 right-4`;
    }
  };

  if (!api) {
    return null;
  }

  return (
    <div className={getPositionClasses()}>
      {isOpen && (
        <ChatWindow
          messages={messages}
          isLoading={isLoading}
          onSendMessage={handleSendMessage}
          onClose={toggleChat}
          onClearMessages={clearMessages}
          title={config.title}
          subtitle={config.subtitle}
          messagesEndRef={messagesEndRef}
        />
      )}
      <ChatButton
        isOpen={isOpen}
        onClick={toggleChat}
      />
    </div>
  );
};