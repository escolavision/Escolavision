/**
 * @file Usuario.jsx
 * @description Modern user management component with improved UI/UX and better component organization
 */

import React, { useEffect, useState } from 'react';
import {
  Plus,
  Save,
  Trash2,
  CheckCircle,
  RefreshCcw,
  Search,
  ChevronLeft,
  ChevronRight,
  AlertCircle,
  User,
  Mail,
  Calendar,
  Key,
  Image,
  Users,
  GraduationCap,
  Shield
} from "lucide-react";
import ConfirmationModal from "./ConfirmationModal.jsx";
import { useIdioma } from '../components/IdiomaContext.jsx';
import { Pie, Bar } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title } from 'chart.js';

const API_BASE_URL = "https://proxy-vercel-ten.vercel.app";

const Usuario = () => {
  const { idioma } = useIdioma();
  const isEnglish = idioma === "Inglés";

  // Main state management
  const [userData, setUserData] = useState({
    id: '',
    nombre: '',
    email: '',
    dni: '',
    password: '',
    fecha_nacimiento: '',
    foto: '',
    tipo_usuario: ''
  });

  const [uiState, setUiState] = useState({
    currentPage: 0,
    isDeleting: false,
    isSaved: false,
    error: null,
    searchTerm: '',
    showConfirmModal: false,
    isLoading: false,
    activeTab: 'list',
  });

  // Data state
  const [usuarios, setUsuarios] = useState([]);
  const [userStats, setUserStats] = useState({
    totalUsers: 0,
    students: 0,
    teachers: 0
  });

  const USERS_PER_PAGE = 8;
  const tipo = localStorage.getItem('tipo');

  // API Calls
  const fetchUsuarios = async () => {
    setUiState(prev => ({ ...prev, isLoading: true }));
    try {
      const id_centro = localStorage.getItem('id_centro');
      const response = await fetch(`${API_BASE_URL}/leer.php?tabla=usuarios&id_centro=${id_centro}`);
      if (!response.ok) throw new Error(isEnglish ? "Error fetching users" : "Error al obtener los usuarios");
      const data = await response.json();
      
      if (tipo === 'Alumno') {
        const idusuario = localStorage.getItem('idusuario');
        const usuario = data.usuarios.filter((usuario) => usuario.id === parseInt(idusuario ? idusuario : '0'));
        setUsuarios(usuario);
        handleUserSelect(usuario[0]);
      } else {
        setUsuarios(data.usuarios);
        
        // Calculate statistics
        const totalUsers = data.usuarios.length;
        const students = data.usuarios.filter(u => u.tipo_usuario === 'Alumno').length;
        const teachers = data.usuarios.filter(u => u.tipo_usuario === 'Profesor').length;
        
        setUserStats({
          totalUsers,
          students,
          teachers
        });
      }
    } catch (error) {
      showError(error.message);
    } finally {
      setUiState(prev => ({ ...prev, isLoading: false }));
    }
  };

  // Action Handlers
  const handleUserSelect = (usuario) => {
    setUserData({
      id: String(usuario.id),
      nombre: usuario.nombre,
      email: usuario.email || '',
      dni: usuario.dni || '',
      password: '',
      fecha_nacimiento: usuario.fecha_nacimiento?.split(' ')[0] || '',
      foto: usuario.foto ? `data:image/jpeg;base64,${usuario.foto}` : '',
      tipo_usuario: usuario.tipo_usuario
    });
  };

  const handleNewUser = () => {
    setUserData({
      id: '',
      nombre: '',
      email: '',
      dni: '',
      password: '',
      fecha_nacimiento: '',
      foto: '',
      tipo_usuario: ''
    });
  };

  const handleSave = async () => {
    if (!userData.nombre.trim() || !userData.email.trim() || !userData.dni.trim() || !userData.fecha_nacimiento.trim() || (!userData.id && !userData.password.trim())) {
      showError(isEnglish ? "Please complete all required fields" : "Por favor complete todos los campos requeridos");
      return;
    }

    setUiState(prev => ({ ...prev, isLoading: true }));
    try {
      const id_centro = localStorage.getItem('id_centro');
      const userPayload = {
        id: userData.id ? parseInt(userData.id) : undefined,
        nombre: userData.nombre,
        dni: userData.dni,
        contraseña: userData.password || undefined,
        foto: userData.foto ? userData.foto.split(',')[1] : '',
        fecha_nacimiento: userData.fecha_nacimiento,
        tipo_usuario: userData.tipo_usuario === 'Alumno' ? 1 : 2,
        email: userData.email,
        id_centro: id_centro
      };

      if (tipo === 'Alumno') {
        const response = await fetch(`${API_BASE_URL}/actualizar.php`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            tabla: "usuarios",
            datos: userPayload,
            id: userData.id
          })
        });

        if (!response.ok) throw new Error(isEnglish ? "Error updating user" : "Error al actualizar el usuario");
      } else {
        const response = await fetch(userData.id ? `${API_BASE_URL}/actualizar.php` : `${API_BASE_URL}/insertar.php`, {
          method: userData.id ? "PUT" : "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            tabla: "usuarios",
            datos: userPayload,
            ...(userData.id && { id: userData.id })
          })
        });

        if (!response.ok) throw new Error(isEnglish ? "Error saving user" : "Error al guardar el usuario");
      }

      showSuccess();
      await fetchUsuarios();
      handleNewUser();
    } catch (error) {
      showError(error.message);
    } finally {
      setUiState(prev => ({ ...prev, isLoading: false }));
    }
  };

  const handleDelete = async () => {
    if (!userData.id) return;
    setUiState(prev => ({ ...prev, isLoading: true }));
    try {
      const response = await fetch(`${API_BASE_URL}/borrar.php`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ tabla: "usuarios", id: userData.id })
      });

      if (!response.ok) throw new Error(isEnglish ? "Error deleting user" : "Error al eliminar el usuario");

      setUiState(prev => ({ ...prev, isDeleting: true }));
      setTimeout(() => setUiState(prev => ({ ...prev, isDeleting: false })), 1500);
      
      handleNewUser();
      await fetchUsuarios();
    } catch (error) {
      showError(error.message);
    } finally {
      setUiState(prev => ({ ...prev, isLoading: false }));
    }
  };

  const handleImageChange = (event) => {
    if (event.target.files && event.target.files[0]) {
      const reader = new FileReader();
      reader.onload = (e) => {
        setUserData(prev => ({ ...prev, foto: e.target?.result || '' }));
      };
      reader.readAsDataURL(event.target.files[0]);
    }
  };

  // UI Helpers
  const showError = (message) => {
    setUiState(prev => ({ ...prev, error: message }));
    setTimeout(() => setUiState(prev => ({ ...prev, error: null })), 3000);
  };

  const showSuccess = (message = null) => {
    setUiState(prev => ({ ...prev, isSaved: true }));
    setTimeout(() => setUiState(prev => ({ ...prev, isSaved: false })), 1500);
    if (message) {
      setUiState(prev => ({ ...prev, success: message }));
      setTimeout(() => setUiState(prev => ({ ...prev, success: null })), 3000);
    }
  };

  // Effects
  useEffect(() => {
    fetchUsuarios();
    handleNewUser();
  }, []);

  // Chart options
  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
      },
    },
  };

  // Pie chart data for user roles
  const getUserRolePieData = () => {
    return {
      labels: [isEnglish ? 'Students' : 'Alumnos', isEnglish ? 'Teachers' : 'Profesores'],
      datasets: [
        {
          label: isEnglish ? 'Users by Role' : 'Usuarios por Rol',
          data: [userStats.students, userStats.teachers],
          backgroundColor: [
            'rgba(54, 162, 235, 0.6)', // blue
            'rgba(153, 102, 255, 0.6)', // purple
          ],
          borderColor: [
            'rgb(54, 162, 235)',
            'rgb(153, 102, 255)',
          ],
          borderWidth: 1,
        },
      ],
    };
  };

  // Bar chart data for user registration by month (if fecha_nacimiento is used as proxy)
  const getUserBirthBarData = () => {
    // Agrupar usuarios por mes de nacimiento
    const months = [
      isEnglish ? 'Jan' : 'Ene', isEnglish ? 'Feb' : 'Feb', isEnglish ? 'Mar' : 'Mar', isEnglish ? 'Apr' : 'Abr',
      isEnglish ? 'May' : 'May', isEnglish ? 'Jun' : 'Jun', isEnglish ? 'Jul' : 'Jul', isEnglish ? 'Aug' : 'Ago',
      isEnglish ? 'Sep' : 'Sep', isEnglish ? 'Oct' : 'Oct', isEnglish ? 'Nov' : 'Nov', isEnglish ? 'Dec' : 'Dic',
    ];
    const monthCounts = Array(12).fill(0);
    usuarios.forEach(u => {
      if (u.fecha_nacimiento) {
        const m = new Date(u.fecha_nacimiento).getMonth();
        if (!isNaN(m)) monthCounts[m]++;
      }
    });
    return {
      labels: months,
      datasets: [
        {
          label: isEnglish ? 'Users by Birth Month' : 'Usuarios por Mes de Nacimiento',
          data: monthCounts,
          backgroundColor: 'rgba(75, 192, 192, 0.6)',
          borderColor: 'rgb(75, 192, 192)',
          borderWidth: 1,
        },
      ],
    };
  };

  const barChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          stepSize: 1
        }
      }
    }
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-gray-50 py-8">
      <div className="container mx-auto px-4">
        {/* Error/Success Messages */}
        {uiState.error && (
          <div className="fixed top-4 right-4 bg-red-600 text-white px-6 py-4 rounded-lg shadow-lg z-50 flex items-center gap-3 animate-slideIn">
            <AlertCircle size={24} />
            <span>{uiState.error}</span>
          </div>
        )}
        {uiState.success && (
          <div className="fixed top-4 right-4 bg-green-600 text-white px-6 py-4 rounded-lg shadow-lg z-50 flex items-center gap-3 animate-slideIn">
            <CheckCircle size={24} />
            <span>{uiState.success}</span>
          </div>
        )}

        <div className="max-w-7xl mx-auto">
          {/* Header: solo para tab de lista y no Alumno */}
          {tipo !== 'Alumno' && uiState.activeTab === 'list' && (
            <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mb-6">
              <h2 className="text-2xl font-bold text-gray-800">
                {isEnglish ? "User Management" : "Gestión de Usuarios"}
              </h2>
              <div className="flex items-center gap-4">
                {/* Search Bar */}
                <div className="relative">
                  <input
                    type="text"
                    value={uiState.searchTerm}
                    onChange={(e) => setUiState(prev => ({ ...prev, searchTerm: e.target.value }))}
                    placeholder={isEnglish ? "Search users..." : "Buscar usuarios..."}
                    className="pl-10 pr-4 py-2 border rounded-lg w-64 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                </div>
                {/* New User Button */}
                <button
                  onClick={handleNewUser}
                  className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 transition-colors duration-200"
                >
                  <Plus size={20} />
                  <span>{isEnglish ? "New User" : "Nuevo Usuario"}</span>
                </button>
              </div>
            </div>
          )}

          {/* Tabs (solo para no Alumno) */}
          {tipo !== 'Alumno' && (
            <div className="flex gap-4 mb-6">
              <button
                onClick={() => setUiState(prev => ({ ...prev, activeTab: 'list' }))}
                className={`px-4 py-2 rounded-lg transition-colors duration-200 ${
                  uiState.activeTab === 'list'
                    ? 'bg-blue-500 text-white'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                }`}
              >
                {isEnglish ? 'Users List' : 'Lista de Usuarios'}
              </button>
              <button
                onClick={() => setUiState(prev => ({ ...prev, activeTab: 'stats' }))}
                className={`px-4 py-2 rounded-lg transition-colors duration-200 ${
                  uiState.activeTab === 'stats'
                    ? 'bg-blue-500 text-white'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                }`}
              >
                {isEnglish ? 'Statistics' : 'Estadísticas'}
              </button>
            </div>
          )}

          {/* Main Content: Tabs para no Alumno, solo lista para Alumno */}
          {(tipo === 'Alumno' || uiState.activeTab === 'list') && (
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              {/* Users List */}
              <div className="lg:col-span-1 bg-white rounded-xl shadow-md p-6">
                <h3 className="text-xl font-semibold text-gray-800 mb-4">
                  {isEnglish ? "Users" : "Usuarios"}
                </h3>
                <div className="space-y-2">
                  {usuarios
                    .filter(u => u.nombre.toLowerCase().includes(uiState.searchTerm.toLowerCase()))
                    .slice(uiState.currentPage * USERS_PER_PAGE, (uiState.currentPage + 1) * USERS_PER_PAGE)
                    .map((usuario) => (
                      <div
                        key={usuario.id}
                        onClick={() => handleUserSelect(usuario)}
                        className={`p-4 rounded-lg cursor-pointer transition-all duration-200
                          ${userData.id === String(usuario.id)
                            ? 'bg-blue-50 ring-2 ring-blue-500'
                            : 'bg-gray-50 hover:bg-gray-100'}`}
                      >
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center overflow-hidden">
                            {usuario.foto ? (
                              <img
                                src={`data:image/jpeg;base64,${usuario.foto}`}
                                alt={usuario.nombre}
                                className="w-full h-full object-cover"
                              />
                            ) : (
                              <User size={20} className="text-gray-500" />
                            )}
                          </div>
                          <div>
                            <h4 className="font-medium text-gray-800">{usuario.nombre}</h4>
                            <p className="text-sm text-gray-500">{usuario.email}</p>
                          </div>
                        </div>
                      </div>
                    ))}
                </div>
                {/* Pagination */}
                {usuarios.length > USERS_PER_PAGE && (
                  <div className="flex justify-center items-center gap-4 mt-6">
                    <button
                      onClick={() => setUiState(prev => ({ ...prev, currentPage: prev.currentPage - 1 }))}
                      disabled={uiState.currentPage === 0}
                      className="p-2 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
                    >
                      <ChevronLeft size={20} />
                    </button>
                    <span className="font-medium">
                      {uiState.currentPage + 1} / {Math.ceil(usuarios.length / USERS_PER_PAGE)}
                    </span>
                    <button
                      onClick={() => setUiState(prev => ({ ...prev, currentPage: prev.currentPage + 1 }))}
                      disabled={uiState.currentPage >= Math.ceil(usuarios.length / USERS_PER_PAGE) - 1}
                      className="p-2 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
                    >
                      <ChevronRight size={20} />
                    </button>
                  </div>
                )}
              </div>
              {/* User Form */}
              <div className="lg:col-span-2 bg-white rounded-xl shadow-md p-6">
                <h3 className="text-xl font-semibold text-gray-800 mb-6">
                  {userData.id
                    ? (isEnglish ? "Edit User" : "Editar Usuario")
                    : (isEnglish ? "New User" : "Nuevo Usuario")}
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        {isEnglish ? "Name" : "Nombre"}
                      </label>
                      <div className="relative">
                        <input
                          type="text"
                          value={userData.nombre}
                          onChange={(e) => setUserData(prev => ({ ...prev, nombre: e.target.value }))}
                          className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                          placeholder={isEnglish ? "Enter name..." : "Ingrese el nombre..."}
                        />
                        <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Email
                      </label>
                      <div className="relative">
                        <input
                          type="email"
                          value={userData.email}
                          onChange={(e) => setUserData(prev => ({ ...prev, email: e.target.value }))}
                          className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                          placeholder={isEnglish ? "Enter email..." : "Ingrese el email..."}
                        />
                        <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        {isEnglish ? "National ID" : "DNI"}
                      </label>
                      <div className="relative">
                        <input
                          type="text"
                          value={userData.dni}
                          onChange={(e) => setUserData(prev => ({ ...prev, dni: e.target.value }))}
                          className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                          placeholder={isEnglish ? "Enter National ID..." : "Ingrese el DNI..."}
                        />
                        <Shield className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                      </div>
                    </div>
                  </div>

                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        {isEnglish ? "Password" : "Contraseña"}
                      </label>
                      <div className="relative">
                        <input
                          type="password"
                          value={userData.password}
                          onChange={(e) => setUserData(prev => ({ ...prev, password: e.target.value }))}
                          className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                          placeholder={isEnglish ? "Enter password..." : "Ingrese la contraseña..."}
                          required={!userData.id}
                        />
                        <Key className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        {isEnglish ? "Date of Birth" : "Fecha de Nacimiento"}
                      </label>
                      <div className="relative">
                        <input
                          type="date"
                          value={userData.fecha_nacimiento}
                          onChange={(e) => setUserData(prev => ({ ...prev, fecha_nacimiento: e.target.value }))}
                          className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                        <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        {isEnglish ? "User Role" : "Rol de Usuario"}
                      </label>
                      <select
                        value={userData.tipo_usuario}
                        onChange={(e) => setUserData(prev => ({ ...prev, tipo_usuario: e.target.value }))}
                        disabled={tipo === 'Alumno'}
                        className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
                      >
                        <option value="" disabled>
                          {isEnglish ? "Select role..." : "Seleccione rol..."}
                        </option>
                        <option value="Alumno">
                          {isEnglish ? "Student" : "Alumno"}
                        </option>
                        <option value="Profesor">
                          {isEnglish ? "Teacher" : "Profesor"}
                        </option>
                      </select>
                    </div>
                  </div>
                </div>

                {/* Profile Image */}
                <div className="mt-6">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    {isEnglish ? "Profile Image" : "Imagen de Perfil"}
                  </label>
                  <div className="w-full h-48 border-2 border-dashed border-gray-300 rounded-lg flex flex-col items-center justify-center relative overflow-hidden">
                    {userData.foto ? (
                      <img 
                        src={userData.foto} 
                        alt="Profile" 
                        className="w-full h-full object-cover"
                      />
                    ) : (
                      <div className="text-center">
                        <Image size={48} className="mx-auto text-gray-400 mb-2" />
                        <p className="text-gray-500">
                          {isEnglish ? "Click to upload image" : "Haga clic para subir imagen"}
                        </p>
                      </div>
                    )}
                    <input
                      type="file"
                      accept="image/*"
                      onChange={handleImageChange}
                      className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
                    />
                  </div>
                </div>

                {/* Action Buttons */}
                <div className="flex gap-4 mt-6">
                  <button
                    onClick={handleSave}
                    disabled={!userData.nombre.trim() || !userData.email.trim() || !userData.dni.trim() || !userData.fecha_nacimiento.trim() || (!userData.id && !userData.password.trim()) || uiState.isSaved}
                    className={`flex-1 flex items-center justify-center gap-2 px-4 py-2 rounded-lg text-white transition-colors duration-200
                      ${!userData.nombre.trim() || !userData.email.trim() || !userData.dni.trim() || !userData.fecha_nacimiento.trim() || (!userData.id && !userData.password.trim()) || uiState.isSaved
                        ? 'bg-blue-300 cursor-not-allowed'
                        : 'bg-blue-500 hover:bg-blue-600'}`}
                  >
                    {uiState.isSaved ? <CheckCircle size={20} /> : <Save size={20} />}
                    <span>
                      {isEnglish
                        ? userData.id ? "Update" : "Save"
                        : userData.id ? "Actualizar" : "Guardar"}
                    </span>
                  </button>

                  {userData.id && (
                    <button
                      onClick={() => setUiState(prev => ({ ...prev, showConfirmModal: true }))}
                      disabled={uiState.isDeleting}
                      className="flex items-center justify-center gap-2 px-4 py-2 rounded-lg bg-red-500 text-white hover:bg-red-600 transition-colors duration-200 disabled:bg-red-300 disabled:cursor-not-allowed"
                    >
                      <Trash2 size={20} />
                      <span>{isEnglish ? "Delete" : "Eliminar"}</span>
                    </button>
                  )}
                </div>
              </div>
            </div>
          )}
          {/* Estadísticas Tab */}
          {tipo !== 'Alumno' && uiState.activeTab === 'stats' && (
            <>
              <div className="bg-white rounded-xl shadow-md p-6 mb-6">
                <h3 className="text-xl font-semibold text-gray-800 mb-6">
                  {isEnglish ? "User Statistics" : "Estadísticas de Usuarios"}
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                  <div className="bg-gray-50 p-6 rounded-xl">
                    <div className="flex items-center gap-4">
                      <div className="p-3 bg-blue-100 rounded-lg">
                        <Users size={24} className="text-blue-600" />
                      </div>
                      <div>
                        <h3 className="text-sm text-gray-500">
                          {isEnglish ? "Total Users" : "Total Usuarios"}
                        </h3>
                        <p className="text-2xl font-bold text-gray-800">{userStats.totalUsers}</p>
                      </div>
                    </div>
                  </div>
                  <div className="bg-gray-50 p-6 rounded-xl">
                    <div className="flex items-center gap-4">
                      <div className="p-3 bg-green-100 rounded-lg">
                        <GraduationCap size={24} className="text-green-600" />
                      </div>
                      <div>
                        <h3 className="text-sm text-gray-500">
                          {isEnglish ? "Students" : "Alumnos"}
                        </h3>
                        <p className="text-2xl font-bold text-gray-800">{userStats.students}</p>
                      </div>
                    </div>
                  </div>
                  <div className="bg-gray-50 p-6 rounded-xl">
                    <div className="flex items-center gap-4">
                      <div className="p-3 bg-purple-100 rounded-lg">
                        <Shield size={24} className="text-purple-600" />
                      </div>
                      <div>
                        <h3 className="text-sm text-gray-500">
                          {isEnglish ? "Teachers" : "Profesores"}
                        </h3>
                        <p className="text-2xl font-bold text-gray-800">{userStats.teachers}</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              {/* User Charts */}
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
                <div className="bg-white rounded-xl shadow-md p-6">
                  <h3 className="text-lg font-semibold text-gray-800 mb-4">
                    {isEnglish ? "Users by Role" : "Usuarios por Rol"}
                  </h3>
                  <div className="h-64">
                    <Pie data={getUserRolePieData()} options={chartOptions} />
                  </div>
                </div>
                <div className="bg-white rounded-xl shadow-md p-6">
                  <h3 className="text-lg font-semibold text-gray-800 mb-4">
                    {isEnglish ? "Users by Birth Month" : "Usuarios por Mes de Nacimiento"}
                  </h3>
                  <div className="h-64">
                    <Bar data={getUserBirthBarData()} options={barChartOptions} />
                  </div>
                </div>
              </div>
            </>
          )}
        </div>
      </div>

      {/* Confirmation Modal */}
      {uiState.showConfirmModal && (
        <ConfirmationModal
          onConfirm={handleDelete}
          onCancel={() => setUiState(prev => ({ ...prev, showConfirmModal: false }))}
          idioma={idioma}
        />
      )}
    </div>
  );
};

export default Usuario;