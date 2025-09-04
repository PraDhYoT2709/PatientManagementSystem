import { useState, useCallback, useRef } from 'react';
import { ChatMessage, ChatResponse } from '../types';
import { ChatbotAPI } from '../utils/api';

export const useChatbot = (api: ChatbotAPI) => {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isOpen, setIsOpen] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = useCallback(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, []);

  const addMessage = useCallback((message: ChatMessage) => {
    setMessages(prev => [...prev, message]);
    setTimeout(scrollToBottom, 100);
  }, [scrollToBottom]);

  const sendMessage = useCallback(async (messageText: string) => {
    if (!messageText.trim()) return;

    const userMessage: ChatMessage = {
      id: `user_${Date.now()}`,
      message: messageText,
      timestamp: new Date(),
      isUser: true,
    };

    addMessage(userMessage);
    setIsLoading(true);

    try {
      const response: ChatResponse = await api.sendMessage(messageText);
      
      const botMessage: ChatMessage = {
        id: `bot_${Date.now()}`,
        message: response.message,
        timestamp: new Date(response.timestamp),
        isUser: false,
        intent: response.intent,
        entities: response.entities,
      };

      addMessage(botMessage);
    } catch (error) {
      const errorMessage: ChatMessage = {
        id: `error_${Date.now()}`,
        message: 'Sorry, I encountered an error. Please try again.',
        timestamp: new Date(),
        isUser: false,
      };

      addMessage(errorMessage);
    } finally {
      setIsLoading(false);
    }
  }, [api, addMessage]);

  const toggleChat = useCallback(() => {
    setIsOpen(prev => !prev);
  }, []);

  const clearMessages = useCallback(() => {
    setMessages([]);
  }, []);

  return {
    messages,
    isLoading,
    isOpen,
    sendMessage,
    toggleChat,
    clearMessages,
    messagesEndRef,
  };
};