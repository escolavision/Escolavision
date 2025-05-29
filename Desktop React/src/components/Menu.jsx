/**
 * @file Menu.jsx
 * @description Modern navigation component with responsive sidebar that manages the interface based on user role.
 * Displays different menu options based on user type (Student, Teacher, or Counselor).
 * Features a sleek sidebar design with icons and responsive layout.
 */

import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Pregunta from "../pages/Pregunta";
import Test from "../pages/Test";
import Usuario from "../pages/Usuario";
import Area from "../pages/Area";
import Intento from "../pages/Intento";
import { useIdioma } from './IdiomaContext.jsx';

// Import icons from a popular icon library (you'll need to install @heroicons/react)
import {
  UserGroupIcon,
  DocumentTextIcon,
  QuestionMarkCircleIcon,
  ChartPieIcon,
  ClipboardDocumentListIcon,
  ArrowLeftOnRectangleIcon,
  LanguageIcon,
  Bars3Icon,
  XMarkIcon,
} from '@heroicons/react/24/outline';

const Menu = () => {
  const { idioma, setIdioma } = useIdioma();
  const [active, setActive] = useState("usuarios");
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [menuConfig, setMenuConfig] = useState({
    title: "",
    items: [],
  });
  
  const navigate = useNavigate();
  const tipo = localStorage.getItem("tipo");
  const isOrientador = localStorage.getItem("isOrientador");

  // Menu icons mapping
  const menuIcons = {
    users: UserGroupIcon,
    usuarios: UserGroupIcon,
    tests: DocumentTextIcon,
    questions: QuestionMarkCircleIcon,
    preguntas: QuestionMarkCircleIcon,
    areas: ChartPieIcon,
    áreas: ChartPieIcon,
    attempts: ClipboardDocumentListIcon,
    intentos: ClipboardDocumentListIcon,
  };

  useEffect(() => {
    const storedIdUsuario = localStorage.getItem("idusuario");
    if (!storedIdUsuario) {
      navigate("/login");
      return;
    }

    const config = {
      Alumno: {
        Inglés: {
          title: "Student Panel",
          items: ["users", "attempts"],
        },
        Español: {
          title: "Panel de Estudiante",
          items: ["usuarios", "intentos"],
        },
      },
      Profesor: {
        Inglés: {
          title: "Teacher Panel",
          items: ["users", "attempts", "areas"],
        },
        Español: {
          title: "Panel de Profesor",
          items: ["usuarios", "intentos", "áreas"],
        },
      },
      Orientador: {
        Inglés: {
          title: "Counselor Panel",
          items: ["tests", "questions", "users", "areas", "attempts"],
        },
        Español: {
          title: "Panel de Orientador",
          items: ["tests", "preguntas", "usuarios", "áreas", "intentos"],
        },
      },
    };

    const userType = isOrientador === "1" ? "Orientador" : tipo;
    const userConfig = config[userType] || config["Alumno"]; // Fallback seguro
    const currentConfig = userConfig[idioma] || userConfig["Español"]; // Fallback seguro
    
    setMenuConfig(currentConfig);
    setActive(currentConfig.items[0]);
  }, [navigate, tipo, isOrientador, idioma]);

  const handleLogout = () => {
    const itemsToRemove = [
      'idusuario',
      'nombre',
      'tipo',
      'isOrientador',
      'id_centro',
      'isLoggedIn'
    ];
    
    itemsToRemove.forEach(item => localStorage.removeItem(item));
    navigate('/');
  };

  const toggleIdioma = () => {
    setIdioma(idioma === "Español" ? "Inglés" : "Español");
  };

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-blue-50">
      {/* Mobile menu button */}
      <button
        className="fixed top-4 left-4 z-50 lg:hidden bg-white p-2 rounded-md shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105"
        onClick={toggleSidebar}
      >
        {isSidebarOpen ? (
          <XMarkIcon className="h-6 w-6 text-gray-600" />
        ) : (
          <Bars3Icon className="h-6 w-6 text-gray-600" />
        )}
      </button>

      {/* Sidebar */}
      <div
        className={`fixed inset-y-0 left-0 z-40 w-64 bg-gradient-to-b from-blue-600 to-blue-800 shadow-xl transform transition-transform duration-300 ease-in-out
          ${isSidebarOpen ? 'translate-x-0' : '-translate-x-full'} lg:translate-x-0`}
      >
        <div className="h-full flex flex-col">
          {/* Sidebar Header */}
          <div className="p-6 border-b border-blue-500/30">
            <h1 className="text-2xl font-bold text-white">{menuConfig.title}</h1>
          </div>

          {/* Navigation Links */}
          <nav className="flex-1 overflow-y-auto p-4 space-y-2">
            {menuConfig.items.map((item) => {
              const Icon = menuIcons[item];
              return (
                <button
                  key={item}
                  onClick={() => setActive(item)}
                  className={`w-full flex items-center space-x-3 px-4 py-3 rounded-lg transition-all duration-200 group
                    ${active === item 
                      ? 'bg-white text-blue-600 shadow-lg' 
                      : 'text-white/90 hover:bg-white/10'}`}
                >
                  {Icon && <Icon className={`h-5 w-5 transition-transform duration-200 ${active === item ? '' : 'group-hover:scale-110'}`} />}
                  <span className="font-medium">{item.charAt(0).toUpperCase() + item.slice(1)}</span>
                </button>
              );
            })}
          </nav>

          {/* Sidebar Footer */}
          <div className="p-4 border-t border-blue-500/30 space-y-2">
            <button
              onClick={toggleIdioma}
              className="w-full flex items-center space-x-3 px-4 py-3 rounded-lg text-white/90 hover:bg-white/10 transition-all duration-200"
            >
              <LanguageIcon className="h-5 w-5" />
              <span>
                {idioma === "Español" ? "Switch to English" : "Cambiar a Español"}
              </span>
            </button>

            <button
              onClick={handleLogout}
              className="w-full flex items-center space-x-3 px-4 py-3 rounded-lg bg-red-500 text-white hover:bg-red-600 transition-all duration-200 hover:shadow-lg"
            >
              <ArrowLeftOnRectangleIcon className="h-5 w-5" />
              <span>{idioma === "Inglés" ? "Logout" : "Cerrar sesión"}</span>
            </button>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className={`transition-all duration-300 ${isSidebarOpen ? 'lg:ml-64' : 'ml-0'}`}>
        <div className="container mx-auto p-8">
          {/* Dynamic Content */}
          <div className="bg-white rounded-2xl shadow-xl p-6 transition-all duration-300 hover:shadow-2xl">
            {active === "usuarios" && <Usuario />}
            {active === "tests" && <Test />}
            {active === "preguntas" && <Pregunta />}
            {active === "áreas" && <Area />}
            {active === "intentos" && <Intento />}
            {active === "attempts" && <Intento />}
            {active === "users" && <Usuario />}
            {active === "questions" && <Pregunta />}
            {active === "areas" && <Area />}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Menu;