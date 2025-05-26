import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App';
import { IdiomaProvider } from './components/IdiomaContext.jsx';

const root = createRoot(document.getElementById('root'));
root.render(
  <IdiomaProvider>
    <App />
  </IdiomaProvider>
);