import axios, { AxiosInstance } from 'axios';
import { ChatResponse } from '../types';

export class ChatbotAPI {
  private api: AxiosInstance;
  private sessionId: string;

  constructor(baseURL: string, jwtToken: string, sessionId?: string) {
    this.api = axios.create({
      baseURL,
      headers: {
        'Authorization': `Bearer ${jwtToken}`,
        'Content-Type': 'application/json',
      },
    });
    
    this.sessionId = sessionId || this.generateSessionId();
  }

  async sendMessage(message: string): Promise<ChatResponse> {
    try {
      const response = await this.api.post('/api/chat/message', {
        message,
        sessionId: this.sessionId,
      });
      
      return response.data;
    } catch (error) {
      console.error('Error sending message:', error);
      throw new Error('Failed to send message to chatbot');
    }
  }

  async getAvailableIntents(): Promise<string[]> {
    try {
      const response = await this.api.get('/api/chat/intents');
      return response.data;
    } catch (error) {
      console.error('Error fetching intents:', error);
      throw new Error('Failed to fetch available intents');
    }
  }

  private generateSessionId(): string {
    return `session_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  getSessionId(): string {
    return this.sessionId;
  }
}