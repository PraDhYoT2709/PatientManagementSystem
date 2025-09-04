import React from 'react';
import { MessageCircle, X } from 'lucide-react';

interface ChatButtonProps {
  isOpen: boolean;
  onClick: () => void;
  className?: string;
}

export const ChatButton: React.FC<ChatButtonProps> = ({ 
  isOpen, 
  onClick, 
  className = '' 
}) => {
  return (
    <button
      onClick={onClick}
      className={`chat-button ${className} ${isOpen ? 'bg-red-600 hover:bg-red-700' : ''}`}
      title={isOpen ? 'Close chat' : 'Open chat'}
    >
      {isOpen ? (
        <X size={24} className="animate-fade-in" />
      ) : (
        <MessageCircle size={24} className="animate-bounce-in" />
      )}
    </button>
  );
};