import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import { RouterProvider } from 'react-router-dom';
import { useState } from 'react';
import './App.css';
import { router } from './routes/router';

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(() => {
        return localStorage.getItem('isLoggedIn') === 'true';
    });

    const handleLoginSuccess = () => {
        setIsLoggedIn(true);
    };

    const handleLogout = () => {
        setIsLoggedIn(false);
        localStorage.removeItem('isLoggedIn');
        localStorage.removeItem('idusuario');
        localStorage.removeItem('tipo');
        localStorage.removeItem('isOrientador');
        localStorage.removeItem('id_centro');
    };

    return (
        <div className="min-h-screen bg-[#AED6F1]">
            <RouterProvider router={router} />
        </div>
    );
}

export default App;