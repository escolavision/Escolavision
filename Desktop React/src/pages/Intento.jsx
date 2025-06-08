/**
 * @file Intento.jsx
 * @description Modern attempt management component with improved UI/UX and better component organization
 */

import React, { useEffect, useState } from 'react';
import {
  Plus,
  Save,
  Trash2,
  CheckCircle,
  Eye,
  Search,
  ChevronLeft,
  ChevronRight,
  AlertCircle,
  Calendar,
  Clock,
  BarChart3,
  User,
  FileText,
  RefreshCcw,
  Download,
  Filter,
  X,
  TrendingUp,
  Users,
  Activity
} from "lucide-react";
import { Bar, Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { motion, AnimatePresence } from "framer-motion";
import { useIdioma } from '../components/IdiomaContext.jsx';
import ConfirmationModal from "./ConfirmationModal.jsx";

// Register ChartJS components
ChartJS.register(CategoryScale, LinearScale, BarElement, LineElement, PointElement, Title, Tooltip, Legend);

const API_BASE_URL = "https://proxy-vercel-ten.vercel.app";

const Intentos = () => {
  const { idioma } = useIdioma();
  const isEnglish = idioma === "Inglés";

  // Main state management
  const [attemptData, setAttemptData] = useState({
    id: '',
    fecha: '',
    hora: '',
    resultados: '',
    idusuario: '',
    idtest: ''
  });

  const [uiState, setUiState] = useState({
    currentPage: 0,
    isDeleting: false,
    isSaved: false,
    error: null,
    searchTerm: '',
    showConfirmModal: false,
    isLoading: false,
    showModal: false,
    filtroAlumno: '',
    activeTab: 'list' // 'list' or 'stats'
  });

  // Data state
  const [intentos, setIntentos] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [tests, setTests] = useState([]);
  const [attemptStats, setAttemptStats] = useState({
    totalAttempts: 0,
    averageScore: 0,
    attemptsByTest: {},
    recentActivity: [],
    topPerformers: []
  });

  // Configuration constants
  const ATTEMPTS_PER_PAGE = 4;
  const id_centro = localStorage.getItem('id_centro');
  const tipo = localStorage.getItem('tipo');
  const idusuario = localStorage.getItem('idusuario');

  // Data fetching functions
  const fetchIntentos = async () => {
    setUiState(prev => ({ ...prev, isLoading: true }));
    try {
      const response = await fetch(`${API_BASE_URL}/leer.php?tabla=intentos&id_centro=${id_centro}`);
      if (!response.ok) throw new Error(isEnglish ? "Error fetching attempts" : "Error al obtener los intentos");
      const data = await response.json();

      const filteredAttempts = tipo === 'Alumno'
        ? data.intentos.filter(intento => intento.idusuario.toString() === idusuario)
        : data.intentos;

      setIntentos(filteredAttempts);

      // Calculate statistics
      const totalAttempts = filteredAttempts.length;
      const attemptsByTest = filteredAttempts.reduce((acc, intento) => {
        acc[intento.idtest] = (acc[intento.idtest] || 0) + 1;
        return acc;
      }, {});

      const averageScore = filteredAttempts.reduce((acc, intento) => {
        const scores = parseResultados(intento.resultados);
        return acc + (scores.reduce((a, b) => a + b, 0) / scores.length);
      }, 0) / totalAttempts;

      // Calculate recent activity
      const recentActivity = [...filteredAttempts]
        .sort((a, b) => new Date(`${b.fecha} ${b.hora}`) - new Date(`${a.fecha} ${a.hora}`))
        .slice(0, 5);

      // Calculate top performers
      const userScores = filteredAttempts.reduce((acc, intento) => {
        const scores = parseResultados(intento.resultados);
        const avgScore = scores.reduce((a, b) => a + b, 0) / scores.length;
        if (!acc[intento.idusuario] || avgScore > acc[intento.idusuario].score) {
          acc[intento.idusuario] = {
            score: avgScore,
            attempt: intento
          };
        }
        return acc;
      }, {});

      const topPerformers = Object.entries(userScores)
        .sort(([, a], [, b]) => b.score - a.score)
        .slice(0, 5)
        .map(([userId, data]) => ({
          userId,
          score: data.score,
          attempt: data.attempt
        }));

      setAttemptStats({
        totalAttempts,
        averageScore: averageScore.toFixed(1),
        attemptsByTest,
        recentActivity,
        topPerformers
      });
    } catch (error) {
      showError(error.message);
    } finally {
      setUiState(prev => ({ ...prev, isLoading: false }));
    }
  };

  const fetchUsuarios = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/leer.php?tabla=usuarios&id_centro=${id_centro}`);
      if (!response.ok) throw new Error(isEnglish ? "Error fetching users" : "Error al obtener los usuarios");
      const data = await response.json();
      setUsuarios(data.usuarios);
    } catch (error) {
      showError(error.message);
    }
  };

  const fetchTests = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/leer.php?tabla=tests&id_centro=${id_centro}`);
      if (!response.ok) throw new Error(isEnglish ? "Error fetching tests" : "Error al obtener los tests");
      const data = await response.json();
      setTests(data.tests);
    } catch (error) {
      showError(error.message);
    }
  };

  // Action Handlers
  const handleAttemptSelect = (intento) => {
    setAttemptData({
      id: String(intento.id),
      fecha: intento.fecha,
      hora: intento.hora,
      resultados: intento.resultados,
      idusuario: String(intento.idusuario),
      idtest: String(intento.idtest)
    });
  };

  const handleNewAttempt = () => {
    setAttemptData({
      id: '',
      fecha: '',
      hora: '',
      resultados: '',
      idusuario: tipo === 'Alumno' ? idusuario : '',
      idtest: ''
    });
  };

  const handleSave = async () => {
    if (!attemptData.fecha.trim() || !attemptData.hora.trim() || !attemptData.resultados.trim() || !attemptData.idusuario.trim() || !attemptData.idtest.trim()) {
      showError(isEnglish ? "Please complete all required fields" : "Por favor complete todos los campos requeridos");
      return;
    }

    setUiState(prev => ({ ...prev, isLoading: true }));
    try {
      const datos = {
        fecha: attemptData.fecha,
        hora: attemptData.hora,
        resultados: attemptData.resultados,
        idusuario: parseInt(attemptData.idusuario),
        idtest: parseInt(attemptData.idtest)
      };

      const response = await fetch(attemptData.id ? `${API_BASE_URL}/actualizar.php` : `${API_BASE_URL}/insertar.php`, {
        method: attemptData.id ? "PUT" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          tabla: "intentos",
          datos: datos,
          ...(attemptData.id && { id: attemptData.id })
        })
      });

      if (!response.ok) throw new Error(isEnglish ? "Error saving attempt" : "Error al guardar el intento");

      showSuccess();
      await fetchIntentos();
      handleNewAttempt();
    } catch (error) {
      showError(error.message);
    } finally {
      setUiState(prev => ({ ...prev, isLoading: false }));
    }
  };

  const handleDelete = async () => {
    setUiState(prev => ({ ...prev, showConfirmModal: false }));
    if (!attemptData.id) return;
    setUiState(prev => ({ ...prev, isLoading: true }));
    try {
      const response = await fetch(`${API_BASE_URL}/borrar.php`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ tabla: "intentos", id: attemptData.id })
      });

      if (!response.ok) throw new Error(isEnglish ? "Error deleting attempt" : "Error al eliminar el intento");

      setUiState(prev => ({ ...prev, isDeleting: true }));
      setTimeout(() => setUiState(prev => ({ ...prev, isDeleting: false })), 1500);

      handleNewAttempt();
      await fetchIntentos();
    } catch (error) {
      showError(error.message);
    } finally {
      setUiState(prev => ({ ...prev, isLoading: false }));
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

  const parseResultados = (resultados) => {
    return resultados.split(';').map(resultado => Math.min(10, Math.max(0, parseFloat(resultado.trim()))));
  };

  // Effects
  useEffect(() => {
    fetchIntentos();
    fetchUsuarios();
    fetchTests();
    handleNewAttempt();
  }, []);

  // Filtered attempts
  const filteredAttempts = intentos.filter(intento => {
    const matchesSearch = intento.fecha.toLowerCase().includes(uiState.searchTerm.toLowerCase()) ||
      intento.hora.toLowerCase().includes(uiState.searchTerm.toLowerCase());
    const matchesFilter = !uiState.filtroAlumno || intento.idusuario.toString() === uiState.filtroAlumno;
    return matchesSearch && matchesFilter;
  });

  // Chart data
  const chartData = {
    labels: ['Área 1', 'Área 2', 'Área 3', 'Área 4', 'Área 5'],
    datasets: [
      {
        label: isEnglish ? "Test Results" : "Resultados del Test",
        data: parseResultados(attemptData.resultados || '0;0;0;0;0'),
        backgroundColor: 'rgba(75, 192, 192, 0.6)',
        borderColor: 'rgb(75, 192, 192)',
        borderWidth: 1,
      },
    ],
  };

  const averageData = {
    labels: ['Área 1', 'Área 2', 'Área 3', 'Área 4', 'Área 5'],
    datasets: [
      {
        label: isEnglish ? "Average Results" : "Media de Resultados",
        data: filteredAttempts.reduce((acc, intento) => {
          const resultados = parseResultados(intento.resultados);
          return acc.map((sum, i) => sum + resultados[i]);
        }, [0, 0, 0, 0, 0]).map(sum => sum / filteredAttempts.length),
        backgroundColor: 'rgba(153, 102, 255, 0.6)',
        borderColor: 'rgb(153, 102, 255)',
        borderWidth: 1,
      },
    ],
  };

  const lineData = {
    labels: filteredAttempts.map(intento => `${intento.fecha} ${intento.hora}`),
    datasets: [
      {
        label: isEnglish ? "Evolution of Results" : "Evolución de Resultados",
        data: filteredAttempts.map(intento => {
          const resultados = parseResultados(intento.resultados);
          return resultados.reduce((a, b) => a + b, 0) / resultados.length;
        }),
        fill: false,
        borderColor: 'rgb(255, 99, 132)',
        tension: 0.1,
      },
    ],
  };

  const chartOptions = {
    scales: {
      y: {
        min: 0,
        max: 10,
        ticks: { stepSize: 1 }
      }
    },
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: isEnglish ? 'Test Results' : 'Resultados del Test'
      }
    }
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-gray-50 py-8">
      <div className="container mx-auto px-4">
        {/* Error/Success Messages */}
        <AnimatePresence>
          {uiState.error && (
            <motion.div
              initial={{ opacity: 0, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              className="fixed top-4 right-4 bg-red-600 text-white px-6 py-4 rounded-lg shadow-lg z-50 flex items-center gap-3"
            >
              <AlertCircle size={24} />
              <span>{uiState.error}</span>
            </motion.div>
          )}
          {uiState.success && (
            <motion.div
              initial={{ opacity: 0, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              className="fixed top-4 right-4 bg-green-600 text-white px-6 py-4 rounded-lg shadow-lg z-50 flex items-center gap-3"
            >
              <CheckCircle size={24} />
              <span>{uiState.success}</span>
            </motion.div>
          )}
        </AnimatePresence>

        <div className="max-w-7xl mx-auto">
          {/* Header */}
          <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mb-6">
            <h2 className="text-2xl font-bold text-gray-800">
              {isEnglish ? "Attempt Management" : "Gestión de Intentos"}
            </h2>

            <div className="flex items-center gap-4">
              {/* Search Bar */}
              <div className="relative">
                <input
                  type="text"
                  value={uiState.searchTerm}
                  onChange={(e) => setUiState(prev => ({ ...prev, searchTerm: e.target.value }))}
                  placeholder={isEnglish ? "Search attempts..." : "Buscar intentos..."}
                  className="pl-10 pr-4 py-2 border rounded-lg w-64 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
              </div>

              {/* New Attempt Button */}
              <button
                onClick={handleNewAttempt}
                className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 transition-colors duration-200"
              >
                <Plus size={20} />
                <span>{isEnglish ? "New Attempt" : "Nuevo Intento"}</span>
              </button>
            </div>
          </div>

          {/* Tabs */}
          <div className="flex gap-4 mb-6">
            <button
              onClick={() => setUiState(prev => ({ ...prev, activeTab: 'list' }))}
              className={`px-4 py-2 rounded-lg transition-colors duration-200 ${uiState.activeTab === 'list'
                  ? 'bg-blue-500 text-white'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                }`}
            >
              {isEnglish ? "Attempts List" : "Lista de Intentos"}
            </button>
            <button
              onClick={() => setUiState(prev => ({ ...prev, activeTab: 'stats' }))}
              className={`px-4 py-2 rounded-lg transition-colors duration-200 ${uiState.activeTab === 'stats'
                  ? 'bg-blue-500 text-white'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                }`}
            >
              {isEnglish ? "Statistics" : "Estadísticas"}
            </button>
          </div>

          {/* Main Content */}
          <AnimatePresence mode="wait">
            {uiState.activeTab === 'list' ? (
              <motion.div
                key="list"
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: 20 }}
                className="grid grid-cols-1 lg:grid-cols-3 gap-6"
              >
                {/* Attempts List */}
                <div className="lg:col-span-1 bg-white rounded-xl shadow-md p-6">
                  <h3 className="text-xl font-semibold text-gray-800 mb-4">
                    {isEnglish ? "Attempts" : "Intentos"}
                  </h3>

                  {tipo !== 'Alumno' && (
                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        {isEnglish ? "Filter by student" : "Filtrar por estudiante"}
                      </label>
                      <div className="relative">
                        <select
                          value={uiState.filtroAlumno}
                          onChange={(e) => setUiState(prev => ({ ...prev, filtroAlumno: e.target.value }))}
                          className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                          <option value="">{isEnglish ? "All students" : "Todos los estudiantes"}</option>
                          {usuarios.map((usuario) => (
                            <option key={usuario.id} value={usuario.id.toString()}>
                              {usuario.nombre}
                            </option>
                          ))}
                        </select>
                        <Filter className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                      </div>
                    </div>
                  )}

                  <div className="space-y-2">
                    {filteredAttempts
                      .slice(uiState.currentPage * ATTEMPTS_PER_PAGE, (uiState.currentPage + 1) * ATTEMPTS_PER_PAGE)
                      .map((intento) => (
                        <motion.div
                          key={intento.id}
                          initial={{ opacity: 0, y: 20 }}
                          animate={{ opacity: 1, y: 0 }}
                          transition={{ duration: 0.2 }}
                          onClick={() => handleAttemptSelect(intento)}
                          className={`p-4 rounded-lg cursor-pointer transition-all duration-200
                            ${attemptData.id === String(intento.id)
                              ? 'bg-blue-50 ring-2 ring-blue-500'
                              : 'bg-gray-50 hover:bg-gray-100'}`}
                        >
                          <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center">
                              <BarChart3 size={20} className="text-gray-500" />
                            </div>
                            <div>
                              <h4 className="font-medium text-gray-800">
                                {isEnglish ? `Attempt ${intento.id}` : `Intento ${intento.id}`}
                              </h4>
                              <p className="text-sm text-gray-500">
                                {intento.fecha} - {intento.hora}
                              </p>
                            </div>
                          </div>
                        </motion.div>
                      ))}
                  </div>

                  {/* Pagination */}
                  {filteredAttempts.length > ATTEMPTS_PER_PAGE && (
                    <div className="flex justify-center items-center gap-4 mt-6">
                      <button
                        onClick={() => setUiState(prev => ({ ...prev, currentPage: prev.currentPage - 1 }))}
                        disabled={uiState.currentPage === 0}
                        className="p-2 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
                      >
                        <ChevronLeft size={20} />
                      </button>
                      <span className="font-medium">
                        {uiState.currentPage + 1} / {Math.ceil(filteredAttempts.length / ATTEMPTS_PER_PAGE)}
                      </span>
                      <button
                        onClick={() => setUiState(prev => ({ ...prev, currentPage: prev.currentPage + 1 }))}
                        disabled={uiState.currentPage >= Math.ceil(filteredAttempts.length / ATTEMPTS_PER_PAGE) - 1}
                        className="p-2 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
                      >
                        <ChevronRight size={20} />
                      </button>
                    </div>
                  )}
                </div>

                {/* Attempt Form */}
                <div className="lg:col-span-2 bg-white rounded-xl shadow-md p-6">
                  <h3 className="text-xl font-semibold text-gray-800 mb-6">
                    {attemptData.id
                      ? (isEnglish ? "Edit Attempt" : "Editar Intento")
                      : (isEnglish ? "New Attempt" : "Nuevo Intento")}
                  </h3>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="space-y-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          {isEnglish ? "Date" : "Fecha"}
                        </label>
                        <div className="relative">
                          <input
                            type="date"
                            value={attemptData.fecha}
                            onChange={(e) => setAttemptData(prev => ({ ...prev, fecha: e.target.value }))}
                            className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                          />
                          <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                        </div>
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          {isEnglish ? "Time" : "Hora"}
                        </label>
                        <div className="relative">
                          <input
                            type="text"
                            value={attemptData.hora}
                            onChange={(e) => setAttemptData(prev => ({ ...prev, hora: e.target.value }))}
                            placeholder={isEnglish ? "HH:MM:SS" : "HH:MM:SS"}
                            className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                          />
                          <Clock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                        </div>
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          {isEnglish ? "Results" : "Resultados"}
                        </label>
                        <div className="relative">
                          <input
                            type="text"
                            value={attemptData.resultados}
                            onChange={(e) => setAttemptData(prev => ({ ...prev, resultados: e.target.value }))}
                            placeholder={isEnglish ? "0;2.5;5;7.5;10" : "0;2.5;5;7.5;10"}
                            className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                          />
                          <BarChart3 className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                        </div>
                      </div>
                    </div>

                    <div className="space-y-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          {isEnglish ? "Test" : "Test"}
                        </label>
                        <select
                          value={attemptData.idtest}
                          onChange={(e) => setAttemptData(prev => ({ ...prev, idtest: e.target.value }))}
                          className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                          <option value="" disabled>
                            {isEnglish ? "Select a test" : "Selecciona un test"}
                          </option>
                          {tests.map((test) => (
                            <option key={test.id} value={test.id}>
                              {test.nombretest}
                            </option>
                          ))}
                        </select>
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          {isEnglish ? "User" : "Usuario"}
                        </label>
                        <div className="relative">
                          <select
                            value={attemptData.idusuario}
                            onChange={(e) => setAttemptData(prev => ({ ...prev, idusuario: e.target.value }))}
                            disabled={tipo === 'Alumno'}
                            className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
                          >
                            <option value="" disabled>
                              {isEnglish ? "Select a user" : "Selecciona un usuario"}
                            </option>
                            {usuarios.map((usuario) => (
                              <option key={usuario.id} value={usuario.id}>
                                {usuario.nombre}
                              </option>
                            ))}
                          </select>
                          <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                        </div>
                      </div>

                      {/* Results Chart */}
                      {attemptData.resultados && (
                        <div className="mt-4">
                          <Bar data={chartData} options={chartOptions} />
                        </div>
                      )}
                    </div>
                  </div>

                  {/* Action Buttons */}
                  <div className="flex gap-4 mt-6">
                    <button
                      onClick={handleSave}
                      disabled={!attemptData.fecha.trim() || !attemptData.hora.trim() || !attemptData.resultados.trim() || !attemptData.idusuario.trim() || !attemptData.idtest.trim() || uiState.isSaved}
                      className={`flex-1 flex items-center justify-center gap-2 px-4 py-2 rounded-lg text-white transition-colors duration-200
                        ${!attemptData.fecha.trim() || !attemptData.hora.trim() || !attemptData.resultados.trim() || !attemptData.idusuario.trim() || !attemptData.idtest.trim() || uiState.isSaved
                          ? 'bg-blue-300 cursor-not-allowed'
                          : 'bg-blue-500 hover:bg-blue-600'}`}
                    >
                      {uiState.isSaved ? <CheckCircle size={20} /> : <Save size={20} />}
                      <span>
                        {isEnglish
                          ? attemptData.id ? "Update" : "Save"
                          : attemptData.id ? "Actualizar" : "Guardar"}
                      </span>
                    </button>

                    {attemptData.id && (
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
              </motion.div>
            ) : (
              <motion.div
                key="stats"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                className="space-y-6"
              >
                {/* Statistics Cards */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                  <div className="bg-white rounded-xl shadow-md p-6">
                    <div className="flex items-center gap-4">
                      <div className="p-3 bg-blue-100 rounded-lg">
                        <BarChart3 size={24} className="text-blue-600" />
                      </div>
                      <div>
                        <h3 className="text-sm text-gray-500">
                          {isEnglish ? "Total Attempts" : "Total Intentos"}
                        </h3>
                        <p className="text-2xl font-bold text-gray-800">{attemptStats.totalAttempts}</p>
                      </div>
                    </div>
                  </div>

                  <div className="bg-white rounded-xl shadow-md p-6">
                    <div className="flex items-center gap-4">
                      <div className="p-3 bg-green-100 rounded-lg">
                        <TrendingUp size={24} className="text-green-600" />
                      </div>
                      <div>
                        <h3 className="text-sm text-gray-500">
                          {isEnglish ? "Average Score" : "Puntuación Media"}
                        </h3>
                        <p className="text-2xl font-bold text-gray-800">{attemptStats.averageScore}</p>
                      </div>
                    </div>
                  </div>

                  <div className="bg-white rounded-xl shadow-md p-6">
                    <div className="flex items-center gap-4">
                      <div className="p-3 bg-purple-100 rounded-lg">
                        <Activity size={24} className="text-purple-600" />
                      </div>
                      <div>
                        <h3 className="text-sm text-gray-500">
                          {isEnglish ? "Active Tests" : "Tests Activos"}
                        </h3>
                        <p className="text-2xl font-bold text-gray-800">{Object.keys(attemptStats.attemptsByTest).length}</p>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Charts */}
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                  <div className="bg-white rounded-xl shadow-md p-6">
                    <h3 className="text-lg font-semibold text-gray-800 mb-4">
                      {isEnglish ? "Average Results by Area" : "Resultados Promedio por Área"}
                    </h3>
                    <Bar data={averageData} options={chartOptions} />
                  </div>

                  <div className="bg-white rounded-xl shadow-md p-6">
                    <h3 className="text-lg font-semibold text-gray-800 mb-4">
                      {isEnglish ? "Results Evolution" : "Evolución de Resultados"}
                    </h3>
                    <Line data={lineData} options={chartOptions} />
                  </div>
                </div>

                {/* Recent Activity and Top Performers */}
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                  <div className="bg-white rounded-xl shadow-md p-6">
                    <h3 className="text-lg font-semibold text-gray-800 mb-4">
                      {isEnglish ? "Recent Activity" : "Actividad Reciente"}
                    </h3>
                    <div className="space-y-4">
                      {attemptStats.recentActivity.map((intento) => (
                        <div key={intento.id} className="flex items-center gap-4 p-3 bg-gray-50 rounded-lg">
                          <div className="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center">
                            <Clock size={20} className="text-blue-600" />
                          </div>
                          <div>
                            <p className="font-medium text-gray-800">
                              {isEnglish ? `Attempt ${intento.id}` : `Intento ${intento.id}`}
                            </p>
                            <p className="text-sm text-gray-500">
                              {intento.fecha} - {intento.hora}
                            </p>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  <div className="bg-white rounded-xl shadow-md p-6">
                    <h3 className="text-lg font-semibold text-gray-800 mb-4">
                      {isEnglish ? "Top Performers" : "Mejores Resultados"}
                    </h3>
                    <div className="space-y-4">
                      {attemptStats.topPerformers.map((performer, index) => (
                        <div key={performer.userId} className="flex items-center gap-4 p-3 bg-gray-50 rounded-lg">
                          <div className="w-10 h-10 rounded-full bg-green-100 flex items-center justify-center">
                            <Users size={20} className="text-green-600" />
                          </div>
                          <div className="flex-1">
                            <p className="font-medium text-gray-800">
                              {usuarios.find(u => u.id === parseInt(performer.userId))?.nombre || 'Unknown User'}
                            </p>
                            <p className="text-sm text-gray-500">
                              {isEnglish ? `Score: ${performer.score.toFixed(1)}` : `Puntuación: ${performer.score.toFixed(1)}`}
                            </p>
                          </div>
                          <div className="text-lg font-bold text-gray-800">
                            #{index + 1}
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
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

      {/* Report Modal */}
      {uiState.showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl p-6 w-11/12 max-w-4xl max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold text-gray-800">
                {isEnglish ? "Attempt Report" : "Informe de Intento"}
              </h2>
              <button
                onClick={() => setUiState(prev => ({ ...prev, showModal: false }))}
                className="text-gray-500 hover:text-gray-700"
              >
                <X size={24} />
              </button>
            </div>

            <div className="space-y-6">
              <div>
                <h3 className="text-lg font-semibold text-gray-800 mb-4">
                  {isEnglish ? "Test Results" : "Resultados del Test"}
                </h3>
                <Bar data={chartData} options={chartOptions} />
              </div>

              <div>
                <h3 className="text-lg font-semibold text-gray-800 mb-4">
                  {isEnglish ? "Average Results" : "Media de Resultados"}
                </h3>
                <Bar data={averageData} options={chartOptions} />
              </div>

              <div>
                <h3 className="text-lg font-semibold text-gray-800 mb-4">
                  {isEnglish ? "Evolution of Results" : "Evolución de Resultados"}
                </h3>
                <Line data={lineData} options={chartOptions} />
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Intentos;