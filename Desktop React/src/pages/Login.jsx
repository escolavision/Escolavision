/**
 * @file Login.jsx
 * @description Login component that handles user authentication.
 * Allows users to enter their credentials, stores session information
 * in localStorage, and manages post-login navigation based on user type.
 * @author Ismael Torres González
 * @coauthor Adrián Ruiz Sánchez
 * @comments Ismael Torres González
 */

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useIdioma } from '../components/IdiomaContext.jsx'; 


const Login = () => {
    // State management for form and UI
    const { idioma } = useIdioma();
    const [usuario, setUsuario] = useState('');
    const [contrasena, setContrasena] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    // Form submission handler
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
    
        // API configuration
        const apiUrl = 'https://proxy-vercel-ten.vercel.app/login.php';
        const loginData = {
            usuario: usuario,
            contrasena: contrasena
        };
    
        try {
            // API request handling
            const response = await fetch(apiUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                mode: 'cors',
                body: JSON.stringify(loginData)
            });
    
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
    
            // Process response and store session data
            const data = await response.json();
            
            // Store user session data
            localStorage.setItem('idusuario', ""+data.id);
            localStorage.setItem('nombre', data.nombre);
            localStorage.setItem('tipo', data.tipo);
            localStorage.setItem('isOrientador', data.is_orientador);
            localStorage.setItem('id_centro', data.id_centro);
            localStorage.setItem('isLoggedIn', 'true');
            
            // Verify stored data
            const storedData = {
                idusuario: localStorage.getItem('idusuario'),
                tipo: localStorage.getItem('tipo'),
                isOrientador: localStorage.getItem('isOrientador'),
                id_centro: localStorage.getItem('id_centro'),
                isLoggedIn: localStorage.getItem('isLoggedIn'),
                usuario: localStorage.getItem('usuario')
            };

            // Navigate if session is valid
            if (data.status !== 'error') {
                navigate('/menu');
            } else {
                setError('Error saving session data');
            }
        } catch (error) {
            console.error('Error details:', error);
            setError('Connection error. Please check your internet connection and try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        // Main container with animation
        <div className="flex justify-center items-center min-h-screen">
            <motion.div
                initial={{ opacity: 0, y: -50 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.8 }}
                className="bg-white p-8 rounded-lg shadow-lg w-full max-w-sm"
            >
                {/* Login form with animated buttons and error handling */}
                <h2 className="text-3xl font-bold mb-6 text-center text-blue-600">Login</h2>

                {/* Error message display */}
                {error && (
                    <p className="text-red-500 text-center mb-4 bg-red-100 p-2 rounded">
                        {error}
                    </p>
                )}

                {/* Login form with input fields and buttons */}
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        {idioma === "Inglés" && 
                            <label htmlFor="usuario" className="block text-gray-700 font-semibold mb-1">
                                Username
                            </label>
                        }
                        {idioma === "Español" &&
                            <label htmlFor="usuario" className="block text-gray-700 font-semibold mb-1">
                                Usuario
                            </label>
                        }
                        <input
                            type="text"
                            id="usuario"
                            value={usuario}
                            onChange={(e) => setUsuario(e.target.value)}
                            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
                            required
                        />
                    </div>

                    <div className="mb-6">
                        {idioma === "Inglés" &&
                            <label htmlFor="contrasena" className="block text-gray-700 font-semibold mb-1">
                                Password
                            </label>
                        }
                        {idioma === "Español" &&
                            <label htmlFor="contrasena" className="block text-gray-700 font-semibold mb-1">
                                Contraseña
                            </label>
                        }
                        <input
                            type="password"
                            id="contrasena"
                            value={contrasena}
                            onChange={(e) => setContrasena(e.target.value)}
                            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
                            required
                        />
                    </div>

                    <div className="flex flex-col space-y-3">
                        {idioma == "Inglés" &&
                            <motion.button
                                type="submit"
                                className="bg-blue-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded-lg focus:outline-none transition-all"
                                disabled={loading}
                                whileTap={{ scale: 0.95 }}
                            >
                                {loading ? 'Verifying...' : 'Login'}
                            </motion.button>
                        }
                        {idioma == "Español" &&
                            <motion.button
                                type="submit"
                                className="bg-blue-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded-lg focus:outline-none transition-all"
                                disabled={loading}
                                whileTap={{ scale: 0.95 }}
                            >
                                {loading ? 'Verificando...' : 'Iniciar Sesión'}
                            </motion.button>
                        }


                        {idioma === "Inglés" &&
                            <motion.button
                                type="button"
                                className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-lg focus:outline-none transition-all"
                                onClick={() => navigate('/')}
                                whileTap={{ scale: 0.95 }}
                            >
                                Back
                            </motion.button>
                        }
                        {idioma === "Español" &&
                            <motion.button
                                type="button"
                                className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-lg focus:outline-none transition-all"
                                onClick={() => navigate('/')}
                                whileTap={{ scale: 0.95 }}
                            >
                                Volver
                            </motion.button>
                        }
                    </div>

                    <div className="text-center mt-4">
                        {idioma === "Inglés" &&
                            <p className="text-gray-700">
                                Don't have an account?{' '}
                                <button
                                    type="button"
                                    className="text-indigo-600 hover:underline"
                                    onClick={() => navigate('/registro')}
                                >
                                    Register here
                                </button>
                            </p>
                        }
                        {idioma === "Español" &&
                            <p className="text-gray-700">
                                ¿No tienes una cuenta?{' '}
                                <button
                                    type="button"
                                    className="text-indigo-600 hover:underline"
                                    onClick={() => navigate('/registro')}
                                >
                                    Regístrate aquí
                                </button>
                            </p>
                        }
                    </div>
                </form>
            </motion.div>
        </div>
    );
};

export default Login;
