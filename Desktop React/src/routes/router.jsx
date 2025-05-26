/**
 * @file router.jsx
 * @description Main routing configuration for the application.
 * Defines protected and public routes, manages user authentication,
 * and handles redirections based on session state. Implements a route
 * protection system that verifies login status before allowing access.
 * @author Ismael Torres González
 * @comments Ismael Torres González
 */

// Required imports for routing and components
import { createBrowserRouter } from "react-router-dom";
import { Navigate } from "react-router-dom";
import Home from "../pages/Home";
import Login from "../pages/Login";
import Register from "../pages/Register";
import Menu from "../components/Menu";

// Protected route component that verifies user authentication
const ProtectedRoute = ({ children }) => {
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
    return isLoggedIn ? children : <Navigate to="/login" />;
};

// Public route component that redirects authenticated users
const PublicRoute = ({ children }) => {
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
    return !isLoggedIn ? children : <Navigate to="/menu" />;
};

// Main router configuration
export const router = createBrowserRouter([
    {
        path: "/", // Base application path
        children: [
            {
                index: true, // Main route (home)
                element: <PublicRoute><Home /></PublicRoute>
            },
            {
                path: "login", // Login route
                element: <PublicRoute><Login /></PublicRoute>
            },
            {
                path: "menu", // Main menu route (requires authentication)
                element: <ProtectedRoute><Menu /></ProtectedRoute>
            },
            {
                path: "registro", // User registration route
                element: <PublicRoute><Register /></PublicRoute>
            },
            {
                path: "*", // Default route for not found URLs
                element: <Navigate to="/" replace />
            }
        ]
    }
]);