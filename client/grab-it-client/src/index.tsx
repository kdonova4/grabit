import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import { AuthProvider } from './AuthContext';
import { CartProvider } from './CartContext';
import { WatchProvider } from './WatchContext';


const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  <AuthProvider>
    <CartProvider>
      <WatchProvider>
        <App />
      </WatchProvider>
    </CartProvider>
    
  </AuthProvider>
  
    
  
);


