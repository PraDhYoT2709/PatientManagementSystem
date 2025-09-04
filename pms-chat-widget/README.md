# PMS Chat Widget

A modern, responsive React chat widget for the Patient Management System (PMS). This floating chat widget provides seamless integration with the chatbot service and offers a beautiful, user-friendly interface for patient interactions.

## 🚀 Features

- **Floating Widget**: Non-intrusive floating chat button with smooth animations
- **Real-time Chat**: Instant messaging with the PMS chatbot
- **JWT Authentication**: Secure communication with JWT token validation
- **Responsive Design**: Works perfectly on desktop and mobile devices
- **Customizable**: Configurable position, theme, and branding
- **TypeScript**: Full TypeScript support for better development experience
- **Tailwind CSS**: Modern styling with Tailwind CSS
- **Easy Integration**: Simple script tag integration for any website

## 🏗️ Architecture

- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite for fast development and building
- **Styling**: Tailwind CSS with custom animations
- **Icons**: Lucide React for beautiful icons
- **HTTP Client**: Axios for API communication
- **State Management**: React hooks for local state

## 📋 Prerequisites

- Node.js 18+
- npm or yarn
- PMS API Gateway running on port 8080
- Valid JWT token for authentication

## 🛠️ Setup

### 1. Install Dependencies

```bash
npm install
```

### 2. Development

```bash
npm run dev
```

### 3. Build for Production

```bash
npm run build
```

### 4. Preview Production Build

```bash
npm run preview
```

## 📦 Integration

### Method 1: Script Tag Integration

Add the following script tag to your HTML page:

```html
<script 
  src="path/to/pms-chat-widget.js"
  data-api-url="http://localhost:8080"
  data-jwt-token="your-jwt-token"
  data-position="bottom-right"
  data-title="PMS Assistant"
  data-subtitle="How can I help you today?"
></script>
```

### Method 2: Programmatic Integration

```javascript
// Initialize the chat widget
window.PmsChatWidget.init({
  apiUrl: 'http://localhost:8080',
  jwtToken: 'your-jwt-token',
  sessionId: 'optional-session-id',
  position: 'bottom-right', // 'bottom-right', 'bottom-left', 'top-right', 'top-left'
  theme: 'light', // 'light' or 'dark'
  primaryColor: '#3b82f6', // Custom primary color
  title: 'PMS Assistant',
  subtitle: 'How can I help you today?'
});

// Destroy the widget when needed
window.PmsChatWidget.destroy();
```

### Method 3: React Component Integration

```tsx
import { ChatWidget } from 'pms-chat-widget';
import { ChatbotConfig } from 'pms-chat-widget/types';

const config: ChatbotConfig = {
  apiUrl: 'http://localhost:8080',
  jwtToken: 'your-jwt-token',
  position: 'bottom-right',
  title: 'PMS Assistant',
  subtitle: 'How can I help you today?'
};

function App() {
  return (
    <div>
      <ChatWidget
        config={config}
        onMessage={(message) => console.log('New message:', message)}
        onResponse={(response) => console.log('Bot response:', response)}
        onError={(error) => console.error('Error:', error)}
      />
    </div>
  );
}
```

## ⚙️ Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `apiUrl` | string | - | URL of the PMS API Gateway |
| `jwtToken` | string | - | JWT token for authentication |
| `sessionId` | string | auto-generated | Unique session identifier |
| `position` | string | 'bottom-right' | Widget position on screen |
| `theme` | string | 'light' | Widget theme ('light' or 'dark') |
| `primaryColor` | string | '#3b82f6' | Primary color for the widget |
| `title` | string | 'PMS Assistant' | Chat window title |
| `subtitle` | string | 'How can I help you today?' | Chat window subtitle |

## 🎨 Customization

### Custom Styling

The widget uses Tailwind CSS classes that can be customized:

```css
/* Override default styles */
.chat-widget {
  /* Custom positioning */
}

.chat-button {
  /* Custom button styling */
}

.chat-window {
  /* Custom window styling */
}
```

### Custom Colors

```javascript
window.PmsChatWidget.init({
  // ... other config
  primaryColor: '#your-custom-color'
});
```

## 📱 Responsive Design

The widget is fully responsive and adapts to different screen sizes:

- **Desktop**: Full-size chat window (320px width)
- **Tablet**: Optimized for touch interactions
- **Mobile**: Compact design with touch-friendly buttons

## 🔧 API Integration

The widget communicates with the PMS chatbot service through the following endpoints:

- `POST /api/chat/message` - Send messages to the chatbot
- `GET /api/chat/intents` - Get available intents

### Message Format

```typescript
interface ChatMessage {
  message: string;
  sessionId: string;
}
```

### Response Format

```typescript
interface ChatResponse {
  message: string;
  intent: string;
  entities: Record<string, any>;
  timestamp: string;
  sessionId: string;
  requiresAction: boolean;
  actionType?: string;
  actionData?: Record<string, any>;
}
```

## 🧪 Testing

### Run Linter

```bash
npm run lint
```

### Fix Linting Issues

```bash
npm run lint:fix
```

## 📊 Features

### Chat Functionality

- **Message History**: Persistent message history during session
- **Typing Indicators**: Visual feedback when bot is responding
- **Message Timestamps**: Time stamps for all messages
- **Intent Display**: Shows detected intent for debugging
- **Error Handling**: Graceful error handling with user feedback

### User Experience

- **Smooth Animations**: CSS animations for opening/closing
- **Keyboard Support**: Enter key to send messages
- **Auto-scroll**: Automatic scrolling to latest messages
- **Clear Messages**: Option to clear chat history
- **Close Button**: Easy way to close the chat window

## 🐳 Docker

### Build Docker Image

```bash
docker build -t pms-chat-widget .
```

### Run Container

```bash
docker run -p 3000:3000 pms-chat-widget
```

## 🚨 Error Handling

The widget handles various error scenarios:

- **Network Errors**: Shows user-friendly error messages
- **Authentication Errors**: Handles JWT token expiration
- **API Errors**: Graceful degradation with retry options
- **Invalid Configuration**: Console warnings for missing config

## 🔒 Security

- **JWT Authentication**: All API calls include JWT token
- **HTTPS Support**: Secure communication with API
- **Input Validation**: Client-side input validation
- **XSS Protection**: React's built-in XSS protection

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run the linter
5. Test your changes
6. Submit a pull request

## 📄 License

This project is part of the Patient Management System microservices architecture.

## 🆘 Support

For support and questions:

1. Check the documentation
2. Review the API integration examples
3. Check the browser console for errors
4. Ensure JWT token is valid and not expired
5. Verify API Gateway is running and accessible