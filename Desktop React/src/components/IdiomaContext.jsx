/**
 * @file IdiomaContext.jsx
 * @description Context provider for language management across the application.
 * Provides a centralized way to manage and switch between different languages.
 * Implements a context system that allows any component to access and modify
 * the current language setting.
 * @author Adrian Ruiz Sanchez
 * @comments Ismael Torres González
 */

import React, { createContext, useContext, useState } from 'react';

// Create a context for language management
const IdiomaContext = createContext();

// Provider component that wraps the app to provide language context
export const IdiomaProvider = ({ children }) => {
  // State to store the current language, defaults to Spanish
  const [idioma, setIdioma] = useState("Español");

  // Provide the language state and setter function to child components
  return (
    <IdiomaContext.Provider value={{ idioma, setIdioma }}>
      {children}
    </IdiomaContext.Provider>
  );
};

// Custom hook to easily access the language context in any component
export const useIdioma = () => useContext(IdiomaContext);
