/**
 * @file RootLayout.jsx
 * @description Component that defines the main structure of the application.
 * Implements a base layout with a side menu and a dynamic content area
 * using React Router for page navigation.
 * @author Ismael Torres González
 * @comments Ismael Torres González
 */

// Import required components and hooks
import { Outlet } from "react-router-dom";
import Menu from "../components/Menu";

// Root layout component that structures the main application layout
const RootLayout = () => {
    return (
        // Main container with flex layout
        <div className="flex">
            {/* Side navigation menu */}
            <Menu />
            {/* Dynamic content area with padding and flex growth */}
            <div className="flex-grow p-4">
                <Outlet />
            </div>
        </div>
    );
};

export default RootLayout;