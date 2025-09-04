import React from 'react';
import { ChatMessage as ChatMessageType } from '../types';

interface ChatMessageProps {
  message: ChatMessageType;
}

export const ChatMessage: React.FC<ChatMessageProps> = ({ message }) => {
  const formatTime = (date: Date) => {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div className={`message ${message.isUser ? 'message-user' : 'message-bot'}`}>
      <div className="text-sm">
        {message.message}
      </div>
      <div className={`text-xs mt-1 opacity-70 ${message.isUser ? 'text-right' : 'text-left'}`}>
        {formatTime(message.timestamp)}
      </div>
      {message.intent && (
        <div className="text-xs mt-1 opacity-50">
          Intent: {message.intent}
        </div>
      )}
    </div>
  );
};