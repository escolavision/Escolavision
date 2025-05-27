/**
 * @file Navbar.tsx
 * @description This file contains the Navbar component of the application.
 * @author Ismael Torres González
 */

import { useState } from "react";
import { Link } from "react-router-dom";
import { Menu, X } from "lucide-react"; // Íconos de menú
import "./Navbar.css";
import logo from "/logo-escolavision.png";

function Navbar() {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <nav className="navbar">
      <div className="navbar-logo">
        <Link to="/Web-Escolavision/">
          <img src={logo} alt="Logo" className="logo" />
        </Link>
      </div>

      {/* Botón de menú hamburguesa */}
      <button className="menu-toggle" onClick={() => setIsOpen(!isOpen)}>
        {isOpen ? <X size={30} /> : <Menu size={30} />}
      </button>

      {/* Menú de navegación */}
      <ul className={`navbar-menu ${isOpen ? "open" : ""}`}>
        <li><Link to="/Web-Escolavision/" className="nav-link">Inicio</Link></li>
        <li><Link to="/Web-Escolavision/about" className="nav-link">Sobre Nosotros</Link></li>
        <li><Link to="/Web-Escolavision/contact" className="nav-link">Contacto</Link></li>
        {/* <li><Link to="/Web-Escolavision/EscolaVision-Desktop" className="nav-link">EscolaVision Desktop</Link></li> */}
      </ul>
    </nav>
  );
}

export default Navbar;
