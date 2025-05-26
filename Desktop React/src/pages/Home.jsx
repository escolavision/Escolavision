/**
 * @file Home.jsx
 * @description Main component for EscolaVision's home page.
 * Presents a welcome interface with animated application logo,
 * smooth transition titles, and a login access button.
 * Uses Framer Motion for animations and visual effects.
 * @author Ismael Torres González
 * @coauthor Adrián Ruiz Sánchez
 * @comments Ismael Torres González
 */

import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useIdioma } from '../components/IdiomaContext.jsx'; 
import escolavisionLogo from '/escolavision.png';

function Home() {
  // Hook for programmatic navigation
  const navigate = useNavigate();
  const { idioma } = useIdioma();

  return (
    // Main container with centered content
    <div className="flex flex-col justify-center items-center min-h-screen">
      {/* Animated logo section with fade-in and slide-down effect */}
      <motion.img
        src={escolavisionLogo}
        alt="EscolaVision Logo"
        className="h-64 mb-6 rounded-lg"
        initial={{ opacity: 0, y: -50 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 1 }}
      />

      {/* Main title with slide-in from left animation */}
      <motion.h1
        className="text-5xl font-extrabold text-black mb-2"
        initial={{ opacity: 0, x: -100 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.8 }}
      >
        EscolaVision Desktop
      </motion.h1>

      {/* Subtitle with slide-in from right animation */}
      {idioma == "Inglés" &&
          <motion.h3
            className="text-3xl text-black mb-6"
            initial={{ opacity: 0, x: 100 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.8 }}
          >
            Your School Guidance App for Teachers
          </motion.h3>
      }
      {idioma == "Español" &&
        <motion.h3
          className="text-3xl text-black mb-6"
          initial={{ opacity: 0, x: 100 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.8 }}
        >
          Tu App de Orientación Escolar para el Profesorado
        </motion.h3>
  } 

      {/* Login button container with fade-in animation */}
      <motion.div
        className="card flex flex-col items-center p-4"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 1.2 }}
      >
        {/* Interactive button with hover effects */}
        {idioma == "Inglés" &&
          <button
            className="bg-blue-600 text-white py-3 px-6 rounded-lg shadow-lg hover:bg-blue-700 transform transition-all duration-300 ease-in-out"
            onClick={() => navigate('/login')}
          >
            Go to Login
          </button>
        }
        {idioma == "Español" &&
          <button
            className="bg-blue-600 text-white py-3 px-6 rounded-lg shadow-lg hover:bg-blue-700 transform transition-all duration-300 ease-in-out"
            onClick={() => navigate('/login')}
          >
            Ir al Login
          </button>
        }
      </motion.div>
    </div>
  );
}

export default Home;
