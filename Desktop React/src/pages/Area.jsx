/**
 * @file Area.jsx
 * @description Modern area management component with improved UI/UX and better component organization
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
  BookOpen,
  Target,
  X,
  Image,
  Layers,
  FileText,
  BarChart3
} from "lucide-react";
import ConfirmationModal from "./ConfirmationModal.jsx";
import { useIdioma } from '../components/IdiomaContext.jsx'; 
import { Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title } from 'chart.js';
import { Pie, Bar } from 'react-chartjs-2';
import { AnimatePresence, motion } from 'framer-motion';

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title);

const API_BASE_URL = "https://proxy-vercel-ten.vercel.app";

const Area = () => {
  const { idioma } = useIdioma();
  const isEnglish = idioma === "Inglés";

  // Main state management
  const [areaData, setAreaData] = useState({
    id: '',
    nombre: '',
    descripción: '',
    logo: ''
  });

  const [uiState, setUiState] = useState({
    currentPage: 0,
    isDeleting: false,
    isSaved: false,
    error: null,
    searchTerm: '',
    showConfirmModal: false,
    isLoading: false,
    activeTab: 'list' // 'list' or 'stats'
  });

  // Data state
  const [areas, setAreas] = useState([]);
  const [areaStats, setAreaStats] = useState({
    totalAreas: 0,
    areasWithQuestions: 0,
    averageQuestionsPerArea: 0
  });

  const AREAS_PER_PAGE = 8;
  const isOrientador = localStorage.getItem("isOrientador") === "1";

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

  // Calculate questions by area data
  const getQuestionsByAreaData = () => {
    const areaCounts = {};
    
    // Initialize counts for all areas
    areas.forEach(area => {
      areaCounts[area.id] = {
        name: area.nombre,
        count: 0
      };
    });

    // Count questions per area
    const pxaData = areaStats.pxaData || [];
    pxaData.forEach(pxa => {
      if (areaCounts[pxa.idarea]) {
        areaCounts[pxa.idarea].count++;
      }
    });

    // Prepare data for chart
    const labels = Object.values(areaCounts).map(area => area.name);
    const data = Object.values(areaCounts).map(area => area.count);

    return {
      labels,
      datasets: [
        {
          label: isEnglish ? 'Questions per Area' : 'Preguntas por Área',
          data,
          backgroundColor: [
            'rgba(255, 99, 132, 0.6)',
            'rgba(54, 162, 235, 0.6)',
            'rgba(255, 206, 86, 0.6)',
            'rgba(75, 192, 192, 0.6)',
            'rgba(153, 102, 255, 0.6)',
            'rgba(255, 159, 64, 0.6)',
            'rgba(199, 199, 199, 0.6)',
            'rgba(83, 102, 255, 0.6)',
            'rgba(40, 159, 64, 0.6)',
            'rgba(210, 199, 199, 0.6)',
          ],
          borderColor: [
            'rgb(255, 99, 132)',
            'rgb(54, 162, 235)',
            'rgb(255, 206, 86)',
            'rgb(75, 192, 192)',
            'rgb(153, 102, 255)',
            'rgb(255, 159, 64)',
            'rgb(199, 199, 199)',
            'rgb(83, 102, 255)',
            'rgb(40, 159, 64)',
            'rgb(210, 199, 199)',
          ],
          borderWidth: 1,
        },
      ],
    };
  };

  // Calculate area usage data
  const getAreaUsageData = () => {
    const usageData = {
      labels: [
        isEnglish ? 'Areas with Questions' : 'Áreas con Preguntas',
        isEnglish ? 'Areas without Questions' : 'Áreas sin Preguntas'
      ],
      datasets: [
        {
          label: isEnglish ? 'Area Usage' : 'Uso de Áreas',
          data: [
            areaStats.areasWithQuestions,
            areaStats.totalAreas - areaStats.areasWithQuestions
          ],
          backgroundColor: [
            'rgba(75, 192, 192, 0.6)',
            'rgba(255, 99, 132, 0.6)'
          ],
          borderColor: [
            'rgb(75, 192, 192)',
            'rgb(255, 99, 132)'
          ],
          borderWidth: 1,
        },
      ],
    };
    return usageData;
  };

  // API Calls
  const fetchAreas = async () => {
    setUiState(prev => ({ ...prev, isLoading: true }));
    try {
      const response = await fetch(`${API_BASE_URL}/leer.php?tabla=areas`);
      if (!response.ok) throw new Error(isEnglish ? "Error fetching areas" : "Error al obtener las áreas");
      const data = await response.json();
      
      if (Array.isArray(data.areas)) {
        setAreas(data.areas);
        
        // Calculate statistics
        const totalAreas = data.areas.length;
        
        // Fetch questions to calculate statistics
        const questionsResponse = await fetch(`${API_BASE_URL}/leer.php?tabla=pxa`);
        if (!questionsResponse.ok) throw new Error(isEnglish ? "Error fetching questions" : "Error al obtener las preguntas");
        const questionsData = await questionsResponse.json();
        
        const areasWithQuestions = new Set(questionsData.pxa?.map(q => q.idarea) || []).size;
        const averageQuestionsPerArea = questionsData.pxa?.length / totalAreas || 0;
        
        setAreaStats({
          totalAreas,
          areasWithQuestions,
          averageQuestionsPerArea: averageQuestionsPerArea.toFixed(1),
          pxaData: questionsData.pxa || []
        });
      } else {
        console.error('Incorrect data:', data);
        setAreas([]);
      }
    } catch (error) {
      showError(error.message);
      setAreas([]);
    } finally {
      setUiState(prev => ({ ...prev, isLoading: false }));
    }
  };

  // Action Handlers
  const handleAreaSelect = (area) => {
    setAreaData({
      id: String(area.id),
      nombre: area.nombre,
      descripción: area.descripción,
      logo: area.logo
    });
  };

  const handleNewArea = () => {
    setAreaData({
      id: '',
      nombre: '',
      descripción: '',
      logo: ''
    });
  };

  const handleSave = async () => {
    if (!areaData.nombre.trim() || !areaData.descripción.trim()) {
      showError(isEnglish ? "Please complete all required fields" : "Por favor complete todos los campos requeridos");
      return;
    }

    setUiState(prev => ({ ...prev, isLoading: true }));
    try {
      const response = await fetch(`${API_BASE_URL}/leer.php?tabla=areas`, {
        method: areaData.id ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          id: areaData.id,
          nombre: areaData.nombre,
          descripción: areaData.descripción,
          logo: areaData.logo
        })
      });

      if (!response.ok) throw new Error(isEnglish ? "Error saving area" : "Error al guardar el área");

      showSuccess();
      await fetchAreas();
      handleNewArea();
    } catch (error) {
      showError(error.message);
    } finally {
      setUiState(prev => ({ ...prev, isLoading: false }));
    }
  };

  const handleDelete = async () => {
    if (!areaData.id) return;
    setUiState(prev => ({ ...prev, isLoading: true }));
    try {
      const response = await fetch(`${API_BASE_URL}/borrar.php`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ tabla: "areas", id: areaData.id })
      });

      if (!response.ok) {
        if (response.status === 503) {
          throw new Error(isEnglish ? "Cannot delete an area with associated questions" : "No se puede eliminar un área con preguntas asociadas");
        } else {
          throw new Error(isEnglish ? "Error deleting area" : "Error al eliminar el área");
        }
      }

      setUiState(prev => ({ ...prev, isDeleting: true }));
      setTimeout(() => setUiState(prev => ({ ...prev, isDeleting: false })), 1500);
      
      handleNewArea();
      await fetchAreas();
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
        setAreaData(prev => ({ ...prev, logo: e.target?.result?.split(',')[1] || '' }));
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
    fetchAreas();
    handleNewArea();
  }, []);

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
          {/* Header */}
          <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mb-6">
            <h2 className="text-2xl font-bold text-gray-800">
              {isEnglish ? "Area Management" : "Gestión de Áreas"}
            </h2>
            
            <div className="flex items-center gap-4">
              {/* Search Bar */}
              <div className="relative">
                <input
                  type="text"
                  value={uiState.searchTerm}
                  onChange={(e) => setUiState(prev => ({ ...prev, searchTerm: e.target.value }))}
                  placeholder={isEnglish ? "Search areas..." : "Buscar áreas..."}
                  className="pl-10 pr-4 py-2 border rounded-lg w-64 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
              </div>

              {/* New Area Button */}
              <button
                onClick={handleNewArea}
                disabled={!isOrientador}
                className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <Plus size={20} />
                <span>{isEnglish ? "New Area" : "Nueva Área"}</span>
              </button>
            </div>
          </div>

          {/* Tabs */}
          <div className="flex gap-4 mb-6">
            <button
              onClick={() => setUiState(prev => ({ ...prev, activeTab: 'list' }))}
              className={`px-4 py-2 rounded-lg transition-colors duration-200 ${
                uiState.activeTab === 'list'
                  ? 'bg-blue-500 text-white'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
              }`}
            >
              {isEnglish ? "Areas List" : "Lista de Áreas"}
            </button>
            <button
              onClick={() => setUiState(prev => ({ ...prev, activeTab: 'stats' }))}
              className={`px-4 py-2 rounded-lg transition-colors duration-200 ${
                uiState.activeTab === 'stats'
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
                {/* Areas List */}
                <div className="lg:col-span-1 bg-white rounded-xl shadow-md p-6">
                  <h3 className="text-xl font-semibold text-gray-800 mb-4">
                    {isEnglish ? "Areas" : "Áreas"}
                  </h3>
                  
                  <div className="space-y-2">
                    {areas
                      .filter(a => a.nombre.toLowerCase().includes(uiState.searchTerm.toLowerCase()))
                      .slice(uiState.currentPage * AREAS_PER_PAGE, (uiState.currentPage + 1) * AREAS_PER_PAGE)
                      .map((area) => (
                        <div
                          key={area.id}
                          onClick={() => handleAreaSelect(area)}
                          className={`p-4 rounded-lg cursor-pointer transition-all duration-200
                            ${areaData.id === String(area.id) 
                              ? 'bg-blue-50 ring-2 ring-blue-500' 
                              : 'bg-gray-50 hover:bg-gray-100'}`}
                        >
                          <div className="flex items-center gap-3">
                            <div>
                              <h4 className="font-medium text-gray-800">{area.nombre}</h4>
                              <p className="text-sm text-gray-500 line-clamp-1">{area.descripción}</p>
                            </div>
                          </div>
                        </div>
                      ))}
                  </div>

                  {/* Pagination */}
                  {areas.length > AREAS_PER_PAGE && (
                    <div className="flex justify-center items-center gap-4 mt-6">
                      <button
                        onClick={() => setUiState(prev => ({ ...prev, currentPage: prev.currentPage - 1 }))}
                        disabled={uiState.currentPage === 0}
                        className="p-2 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
                      >
                        <ChevronLeft size={20} />
                      </button>
                      <span className="font-medium">
                        {uiState.currentPage + 1} / {Math.ceil(areas.length / AREAS_PER_PAGE)}
                      </span>
                      <button
                        onClick={() => setUiState(prev => ({ ...prev, currentPage: prev.currentPage + 1 }))}
                        disabled={uiState.currentPage >= Math.ceil(areas.length / AREAS_PER_PAGE) - 1}
                        className="p-2 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
                      >
                        <ChevronRight size={20} />
                      </button>
                    </div>
                  )}
                </div>

                {/* Area Form */}
                <div className="lg:col-span-2 bg-white rounded-xl shadow-md p-6">
                  <h3 className="text-xl font-semibold text-gray-800 mb-6">
                    {areaData.id 
                      ? (isEnglish ? "Edit Area" : "Editar Área")
                      : (isEnglish ? "New Area" : "Nueva Área")}
                  </h3>

                  <div className="space-y-6">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        {isEnglish ? "Name" : "Nombre"}
                      </label>
                      <div className="relative">
                        <input
                          type="text"
                          value={areaData.nombre}
                          onChange={(e) => setAreaData(prev => ({ ...prev, nombre: e.target.value }))}
                          className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                          placeholder={isEnglish ? "Enter area name..." : "Ingrese el nombre del área..."}
                        />
                        <Layers className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        {isEnglish ? "Description" : "Descripción"}
                      </label>
                      <div className="relative">
                        <textarea
                          value={areaData.descripción}
                          onChange={(e) => setAreaData(prev => ({ ...prev, descripción: e.target.value }))}
                          className="w-full pl-10 pr-4 py-2 border rounded-lg h-32 focus:outline-none focus:ring-2 focus:ring-blue-500"
                          placeholder={isEnglish ? "Enter area description..." : "Ingrese la descripción del área..."}
                        />
                        <FileText className="absolute left-3 top-3 text-gray-400" size={18} />
                      </div>
                    </div>

                    {/* Logo Upload */}
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        {isEnglish ? "Logo" : "Logo"}
                      </label>
                      <div className="w-full h-48 border-2 border-dashed border-gray-300 rounded-lg flex flex-col items-center justify-center relative overflow-hidden">
                        {areaData.logo ? (
                          <img 
                            src={`data:image/png;base64,${areaData.logo}`} 
                            alt="Area logo" 
                            className="w-full h-full object-contain"
                          />
                        ) : (
                          <div className="text-center">
                            <Image size={48} className="mx-auto text-gray-400 mb-2" />
                            <p className="text-gray-500">
                              {isEnglish ? "Click to upload logo" : "Haga clic para subir logo"}
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
                    <div className="flex gap-4">
                      <button
                        onClick={handleSave}
                        disabled={!areaData.nombre.trim() || !areaData.descripción.trim() || uiState.isSaved || !isOrientador}
                        className={`flex-1 flex items-center justify-center gap-2 px-4 py-2 rounded-lg text-white transition-colors duration-200
                          ${!areaData.nombre.trim() || !areaData.descripción.trim() || uiState.isSaved || !isOrientador
                            ? 'bg-blue-300 cursor-not-allowed'
                            : 'bg-blue-500 hover:bg-blue-600'}`}
                      >
                        {uiState.isSaved ? <CheckCircle size={20} /> : <Save size={20} />}
                        <span>
                          {isEnglish
                            ? areaData.id ? "Update" : "Save"
                            : areaData.id ? "Actualizar" : "Guardar"}
                        </span>
                      </button>

                      {areaData.id && (
                        <button
                          onClick={() => setUiState(prev => ({ ...prev, showConfirmModal: true }))}
                          disabled={uiState.isDeleting || !isOrientador}
                          className="flex items-center justify-center gap-2 px-4 py-2 rounded-lg bg-red-500 text-white hover:bg-red-600 transition-colors duration-200 disabled:bg-red-300 disabled:cursor-not-allowed"
                        >
                          <Trash2 size={20} />
                          <span>{isEnglish ? "Delete" : "Eliminar"}</span>
                        </button>
                      )}
                    </div>
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
                        <Layers size={24} className="text-blue-600" />
                      </div>
                      <div>
                        <h3 className="text-sm text-gray-500">
                          {isEnglish ? "Total Areas" : "Total Áreas"}
                        </h3>
                        <p className="text-2xl font-bold text-gray-800">{areaStats.totalAreas}</p>
                      </div>
                    </div>
                  </div>

                  <div className="bg-white rounded-xl shadow-md p-6">
                    <div className="flex items-center gap-4">
                      <div className="p-3 bg-green-100 rounded-lg">
                        <Target size={24} className="text-green-600" />
                      </div>
                      <div>
                        <h3 className="text-sm text-gray-500">
                          {isEnglish ? "Areas with Questions" : "Áreas con Preguntas"}
                        </h3>
                        <p className="text-2xl font-bold text-gray-800">{areaStats.areasWithQuestions}</p>
                      </div>
                    </div>
                  </div>

                  <div className="bg-white rounded-xl shadow-md p-6">
                    <div className="flex items-center gap-4">
                      <div className="p-3 bg-purple-100 rounded-lg">
                        <BarChart3 size={24} className="text-purple-600" />
                      </div>
                      <div>
                        <h3 className="text-sm text-gray-500">
                          {isEnglish ? "Average Questions per Area" : "Promedio de Preguntas por Área"}
                        </h3>
                        <p className="text-2xl font-bold text-gray-800">{areaStats.averageQuestionsPerArea}</p>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Charts */}
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                  <div className="bg-white rounded-xl shadow-md p-6">
                    <h3 className="text-lg font-semibold text-gray-800 mb-4">
                      {isEnglish ? "Questions Distribution" : "Distribución de Preguntas"}
                    </h3>
                    <div className="h-64">
                      <Pie data={getQuestionsByAreaData()} options={chartOptions} />
                    </div>
                  </div>

                  <div className="bg-white rounded-xl shadow-md p-6">
                    <h3 className="text-lg font-semibold text-gray-800 mb-4">
                      {isEnglish ? "Area Usage" : "Uso de Áreas"}
                    </h3>
                    <div className="h-64">
                      <Bar data={getAreaUsageData()} options={barChartOptions} />
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
    </div>
  );
};

export default Area;