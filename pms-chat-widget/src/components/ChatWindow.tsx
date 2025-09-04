import React, { useState, useRef, useEffect } from 'react';
import { Send, X, RotateCcw } from 'lucide-react';
import { ChatMessage } from './ChatMessage';
import { ChatMessage as ChatMessageType } from '../types';

interface ChatWindowProps {
  messages: ChatMessageType[];
  isLoading: boolean;
  onSendMessage: (message: string) => void;
  onClose: () => void;
  onClearMessages: () => void;
  title?: string;
  subtitle?: string;
  messagesEndRef: React.RefObject<HTMLDivElement>;
}

export const ChatWindow: React.FC<ChatWindowProps> = ({
  messages,
  isLoading,
  onSendMessage,
  onClose,
  onClearMessages,
  title = 'PMS Assistant',
  subtitle = 'How can I help you today?',
  messagesEndRef,
}) => {
  const [inputValue, setInputValue] = useState('');
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (inputRef.current) {
      inputRef.current.focus();
    }
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (inputValue.trim() && !isLoading) {
      onSendMessage(inputValue.trim());
      setInputValue('');
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSubmit(e);
    }
  };

  return (
    <div className="chat-window animate-slide-up">
      <div className="chat-header">
        <div>
          <h3 className="font-semibold text-lg">{title}</h3>
          <p className="text-sm opacity-90">{subtitle}</p>
        </div>
        <div className="flex items-center space-x-2">
          <button
            onClick={onClearMessages}
            className="p-1 hover:bg-primary-700 rounded transition-colors duration-200"
            title="Clear messages"
          >
            <RotateCcw size={16} />
          </button>
          <button
            onClick={onClose}
            className="p-1 hover:bg-primary-700 rounded transition-colors duration-200"
            title="Close chat"
          >
            <X size={16} />
          </button>
        </div>
      </div>

      <div className="chat-messages">
        {messages.length === 0 ? (
          <div className="text-center text-gray-500 mt-8">
            <p>Start a conversation with the PMS Assistant!</p>
            <p className="text-sm mt-2">Try asking:</p>
            <ul className="text-sm mt-1 space-y-1">
              <li>• "Book an appointment"</li>
              <li>• "Find a cardiology doctor"</li>
              <li>• "Check my appointments"</li>
              <li>• "Help"</li>
            </ul>
          </div>
        ) : (
          messages.map((message) => (
            <ChatMessage key={message.id} message={message} />
          ))
        )}
        
        {isLoading && (
          <div className="message message-bot">
            <div className="flex items-center space-x-2">
              <div className="flex space-x-1">
                <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce"></div>
                <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0.1s' }}></div>
                <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></div>
              </div>
              <span className="text-sm text-gray-500">Assistant is typing...</span>
            </div>
          </div>
        )}
        
        <div ref={messagesEndRef} />
      </div>

      <div className="chat-input">
        <form onSubmit={handleSubmit} className="flex items-center">
          <input
            ref={inputRef}
            type="text"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Type your message..."
            className="input-field"
            disabled={isLoading}
          />
          <button
            type="submit"
            disabled={!inputValue.trim() || isLoading}
            className="send-button disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Send size={16} />
          </button>
        </form>
      </div>
    </div>
  );
};