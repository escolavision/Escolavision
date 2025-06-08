/**
 * @file Pregunta.jsx
 * @description Modern question management component with improved UI/UX and better component organization
 */

import React, { useEffect, useState } from "react";
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
  BarChart3,
  PieChart,
  Users,
  Activity,
  TrendingUp
} from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import ConfirmationModal from "./ConfirmationModal.jsx";
import { useIdioma } from '../components/IdiomaContext.jsx';
import { Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title } from 'chart.js';
import { Pie, Bar } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title);

const API_BASE_URL = "https://proxy-vercel-ten.vercel.app";

const Pregunta = () => {
  const { idioma } = useIdioma();
  const isEnglish = idioma === "Inglés";

  // Main state management
  const [selectedTest, setSelectedTest] = useState({
    id: null,
    name: ''
  });

  const [questionData, setQuestionData] = useState({
    id: '',
    title: '',
    prompt: '',
    testId: 0
  });

  const [uiState, setUiState] = useState({
    currentPage: 0,
    isDeleting: false,
    isSaved: false,
    error: null,
    searchTerm: '',
    showTestModal: true,
    showAreaModal: false,
    showInfoModal: false,
    isLoading: false,
    activeTab: 'list'
  });

  // Data state
  const [questions, setQuestions] = useState([]);
  const [tests, setTests] = useState([]);
  const [areas, setAreas] = useState([]);
  const [associatedAreas, setAssociatedAreas] = useState([]);
  const [selectedArea, setSelectedArea] = useState(null);
  const [pxaData, setPxaData] = useState([]);
  const [questionStats, setQuestionStats] = useState({
    totalQuestions: 0,
    questionsWithAreas: 0,
    averageAreasPerQuestion: 0
  });

  const QUESTIONS_PER_PAGE = 6;

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

  // API Calls
  const fetchTests = async () => {
    try {
      const id_centro = localStorage.getItem('id_centro');
      const response = await fetch(`${API_BASE_URL}/leer.php?tabla=tests&id_centro=${id_centro}`);
      if (!response.ok) throw new Error(isEnglish ? "Error fetching tests" : "Error al obtener los tests");
      const data = await response.json();
      setTests(data.tests || []);
    } catch (error) {
      showError(error.message);
    }
  };

  const fetchAreas = async () => {
    try {
      const id_centro = localStorage.getItem('id_centro');
      const response = await fetch(`${API_BASE_URL}/leer.php?tabla=areas&id_centro=${id_centro}`);
      if (!response.ok) throw new Error(isEnglish ? "Error fetching areas" : "Error al obtener las áreas");
      const data = await response.json();
      setAreas(data.areas || []);
    } catch (error) {
      showError(error.message);
    }
  };

  const fetchQuestions = async (testId) => {
    if (!testId) return;
    setUiState(prev => ({ ...prev, isLoading: true }));
    try {
      const id_centro = localStorage.getItem('id_centro');
      const response = await fetch(`${API_BASE_URL}/leer.php?tabla=preguntas&id_centro=${id_centro}`);
      if (!response.ok) throw new Error(isEnglish ? "Error fetching questions" : "Error al obtener las preguntas");
      const data = await response.json();
      const filteredQuestions = data.preguntas.filter(q => q.idtest === testId);
      setQuestions(filteredQuestions);
      
      // Fetch area associations
      const pxaResponse = await fetch(`${API_BASE_URL}/leer.php?tabla=pxa`);
      if (!pxaResponse.ok) throw new Error(isEnglish ? "Error fetching area associations" : "Error al obtener las asociaciones de área");
      const pxa = await pxaResponse.json();
      setPxaData(pxa.pxa || []);
      
      // Calculate statistics
      const totalQuestions = filteredQuestions.length;
      const questionsWithAreas = filteredQuestions.filter(q => 
        pxa.pxa.some(pxa => pxa.idpregunta === q.id)
      ).length;
      const averageAreasPerQuestion = questionsWithAreas / totalQuestions || 0;
      
      setQuestionStats({
        totalQuestions,
        questionsWithAreas,
        averageAreasPerQuestion: averageAreasPerQuestion.toFixed(1)
      });
    } catch (error) {
      showError(error.message);
    } finally {
      setUiState(prev => ({ ...prev, isLoading: false }));
    }
  };

  const fetchAssociatedAreas = async (questionId) => {
    try {
      const id_centro = localStorage.getItem('id_centro');
      const response = await fetch(`${API_BASE_URL}/leer.php?tabla=pxa&idpregunta=${questionId}&id_centro=${id_centro}`);
      if (!response.ok) throw new Error(isEnglish ? "Error fetching associated areas" : "Error al obtener las áreas asociadas");
      const data = await response.json();
      
      if (data.pxa && data.pxa.length > 0) {
        const areaIds = data.pxa.map(pxa => pxa.idarea);
        setAssociatedAreas(areas.filter(area => areaIds.includes(area.id)));
      } else {
        setAssociatedAreas([]);
      }
    } catch (error) {
      showError(error.message);
    }
  };

  // Action Handlers
  const handleQuestionSelect = async (question) => {
    setQuestionData({
      id: String(question.id),
      title: question.titulo,
      prompt: question.enunciado,
      testId: question.idtest
    });
    await fetchAssociatedAreas(question.id);
  };

  const handleTestSelect = async (test) => {
    setSelectedTest({
      id: test.id,
      name: test.nombretest
    });
    setQuestionData(prev => ({ ...prev, testId: test.id }));
    setUiState(prev => ({ ...prev, showTestModal: false, currentPage: 0 }));
    await fetchQuestions(test.id);
  };

  const handleSave = async () => {
    if (!questionData.title.trim() || !questionData.prompt.trim()) {
      showError(isEnglish ? "Please complete all fields" : "Por favor complete todos los campos");
      return;
    }

    setUiState(prev => ({ ...prev, isLoading: true }));
    try {
      const id_centro = localStorage.getItem('id_centro');
      const questionPayload = {
        id: questionData.id,
        titulo: questionData.title,
        enunciado: questionData.prompt,
        idtest: questionData.testId,
        id_centro: id_centro
      };

      const endpoint = questionData.id ? "actualizar.php" : "insertar.php";
      const body = questionData.id
        ? { tabla: "preguntas", datos: questionPayload, id: questionData.id }
        : { tabla: "preguntas", datos: questionPayload };

      const response = await fetch(`${API_BASE_URL}/${endpoint}`, {
        method: questionData.id ? "PUT" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
      });

      if (!response.ok) throw new Error(isEnglish ? "Error saving question" : "Error al guardar la pregunta");

      if (!questionData.id) {
        const lastQuestionResponse = await fetch(`${API_BASE_URL}/leer.php?tabla=preguntas&ultima=true&id_centro=${id_centro}`);
        if (!lastQuestionResponse.ok) throw new Error(isEnglish ? "Error getting new question ID" : "Error al obtener el ID de la nueva pregunta");
        const data = await lastQuestionResponse.json();
        if (data.preguntas.length > 0) {
          setQuestionData(prev => ({ ...prev, id: String(data.preguntas[0].id) }));
          setUiState(prev => ({ ...prev, showInfoModal: true }));
        }
      }

      showSuccess();
      await fetchQuestions(selectedTest.id);
      resetForm();
    } catch (error) {
      showError(error.message);
    } finally {
      setUiState(prev => ({ ...prev, isLoading: false }));
    }
  };

  const handleDelete = async () => {
    setUiState(prev => ({ ...prev, showConfirmModal: false }));
    if (!questionData.id) return;
    setUiState(prev => ({ ...prev, isLoading: true }));
    try {
      const id_centro = localStorage.getItem('id_centro');
      // Delete area association first
      const areaAssocResponse = await fetch(`${API_BASE_URL}/leer.php?tabla=pxa&idpregunta=${questionData.id}&id_centro=${id_centro}`);
      if (!areaAssocResponse.ok) throw new Error(isEnglish ? "Error fetching area associations" : "Error al obtener las asociaciones de área");
      
      const areaAssocData = await areaAssocResponse.json();
      const associatedArea = areaAssocData.pxa[0];
      
      if (associatedArea) {
        await fetch(`${API_BASE_URL}/borrar.php`, {
          method: "DELETE",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ tabla: "pxa", id: associatedArea.id })
        });
      }

      // Then delete the question
      const response = await fetch(`${API_BASE_URL}/borrar.php`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ tabla: "preguntas", id: questionData.id })
      });

      if (!response.ok) throw new Error(isEnglish ? "Error deleting question" : "Error al eliminar la pregunta");

      setUiState(prev => ({ ...prev, isDeleting: true }));
      setTimeout(() => setUiState(prev => ({ ...prev, isDeleting: false })), 1500);
      
      resetForm();
      await fetchQuestions(selectedTest.id);
    } catch (error) {
      showError(error.message);
    } finally {
      setUiState(prev => ({ ...prev, isLoading: false }));
    }
  };

  const handleAssignArea = async () => {
    if (!selectedArea) {
      showError(isEnglish ? "Please select an area" : "Por favor seleccione un área");
      return;
    }

    try {
      const id_centro = localStorage.getItem('id_centro');
      const response = await fetch(`${API_BASE_URL}/insertar.php`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          tabla: "pxa",
          datos: { 
            idpregunta: questionData.id, 
            idarea: selectedArea,
            id_centro: id_centro
          }
        })
      });

      if (!response.ok) throw new Error(isEnglish ? "Error assigning area" : "Error al asignar el área");

      showSuccess(isEnglish ? "Area assigned successfully" : "Área asignada correctamente");
      setUiState(prev => ({ ...prev, showAreaModal: false }));
      resetForm();
      await fetchQuestions(selectedTest.id);
    } catch (error) {
      showError(error.message);
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

  const resetForm = () => {
    setQuestionData({
      id: '',
      title: '',
      prompt: '',
      testId: selectedTest.id
    });
    setSelectedArea(null);
    setAssociatedAreas([]);
  };

  const handleNewQuestion = () => {
    resetForm();
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

  // Calculate questions by test data
  const getQuestionsByTestData = () => {
    const testCounts = {};
    
    // Initialize counts for all tests
    tests.forEach(test => {
      testCounts[test.id] = {
        name: test.nombretest,
        count: 0
      };
    });

    // Count questions per test
    questions.forEach(question => {
      if (testCounts[question.idtest]) {
        testCounts[question.idtest].count++;
      }
    });

    // Prepare data for chart
    const labels = Object.values(testCounts).map(test => test.name);
    const data = Object.values(testCounts).map(test => test.count);

    return {
      labels,
      datasets: [
        {
          label: isEnglish ? 'Questions per Test' : 'Preguntas por Test',
          data,
          backgroundColor: 'rgba(75, 192, 192, 0.6)',
          borderColor: 'rgb(75, 192, 192)',
          borderWidth: 1,
        },
      ],
    };
  };

  // Bar chart options
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

  // Effects
  useEffect(() => {
    fetchTests();
    fetchAreas();
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
            <div className="flex items-center gap-3">
              <h2 className="text-2xl font-bold text-gray-800">
                {isEnglish ? "Question Management" : "Gestión de Preguntas"}
              </h2>
              {selectedTest.id && (
                <span className="text-gray-500">- {selectedTest.name}</span>
              )}
            </div>
            
            <div className="flex items-center gap-4">
              {/* Search Bar */}
              <div className="relative">
                <input
                  type="text"
                  value={uiState.searchTerm}
                  onChange={(e) => setUiState(prev => ({ ...prev, searchTerm: e.target.value }))}
                  placeholder={isEnglish ? "Search questions..." : "Buscar preguntas..."}
                  className="pl-10 pr-4 py-2 border rounded-lg w-64 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
              </div>

              {selectedTest.id ? (
                <>
                  {/* New Question Button */}
                  <button
                    onClick={handleNewQuestion}
                    className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 transition-colors duration-200"
                  >
                    <Plus size={20} />
                    <span>{isEnglish ? "New Question" : "Nueva Pregunta"}</span>
                  </button>
                  
                  {/* Change Test Button */}
                  <button
                    onClick={() => setUiState(prev => ({ ...prev, showTestModal: true }))}
                    className="bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 transition-colors duration-200"
                  >
                    <RefreshCcw size={20} />
                    <span>{isEnglish ? "Change Test" : "Cambiar Test"}</span>
                  </button>
                </>
              ) : (
                <button
                  onClick={() => setUiState(prev => ({ ...prev, showTestModal: true }))}
                  className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 transition-colors duration-200"
                >
                  <BookOpen size={20} />
                  <span>{isEnglish ? "Select Test" : "Seleccionar Test"}</span>
                </button>
              )}
            </div>
          </div>

          {selectedTest.id && (
            <>
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
                  {isEnglish ? "Questions List" : "Lista de Preguntas"}
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
                    {/* Questions List */}
                    <div className="lg:col-span-1 bg-white rounded-xl shadow-md p-6">
                      <h3 className="text-xl font-semibold text-gray-800 mb-4">
                        {isEnglish ? "Questions" : "Preguntas"}
                      </h3>
                      
                      <div className="space-y-2 max-h-[430px] overflow-y-auto pr-2 custom-scrollbar">
                        {questions
                          .filter(q => q.titulo.toLowerCase().includes(uiState.searchTerm.toLowerCase()))
                          .map((question) => (
                            <motion.div
                              key={question.id}
                              initial={{ opacity: 0, y: 20 }}
                              animate={{ opacity: 1, y: 0 }}
                              transition={{ duration: 0.2 }}
                              onClick={() => handleQuestionSelect(question)}
                              className={`p-4 rounded-lg cursor-pointer transition-all duration-200
                                ${questionData.id === String(question.id) 
                                  ? 'bg-blue-50 ring-2 ring-blue-500' 
                                  : 'bg-gray-50 hover:bg-gray-100'}`}
                            >
                              <div className="flex items-center gap-3">
                                <div className="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center">
                                  <BookOpen size={20} className="text-gray-500" />
                                </div>
                                <div className="flex-1">
                                  <h4 className="font-medium text-gray-800 line-clamp-1">{question.titulo}</h4>
                                  <p className="text-sm text-gray-500 line-clamp-2">{question.enunciado}</p>
                                </div>
                              </div>
                            </motion.div>
                          ))}
                      </div>
                    </div>

                    {/* Question Form */}
                    <div className="lg:col-span-2 bg-white rounded-xl shadow-md p-6">
                      <h3 className="text-xl font-semibold text-gray-800 mb-6">
                        {questionData.id 
                          ? (isEnglish ? "Edit Question" : "Editar Pregunta")
                          : (isEnglish ? "New Question" : "Nueva Pregunta")}
                      </h3>

                      <div className="space-y-6">
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-2">
                            {isEnglish ? "Title" : "Título"}
                          </label>
                          <input
                            type="text"
                            value={questionData.title}
                            onChange={(e) => setQuestionData(prev => ({ ...prev, title: e.target.value }))}
                            className="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder={isEnglish ? "Enter question title..." : "Ingrese el título de la pregunta..."}
                          />
                        </div>

                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-2">
                            {isEnglish ? "Question Prompt" : "Enunciado"}
                          </label>
                          <textarea
                            value={questionData.prompt}
                            onChange={(e) => setQuestionData(prev => ({ ...prev, prompt: e.target.value }))}
                            className="w-full p-3 border rounded-lg h-32 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder={isEnglish ? "Enter question prompt..." : "Ingrese el enunciado de la pregunta..."}
                          />
                        </div>

                        {associatedAreas.length > 0 && (
                          <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                              {isEnglish ? "Associated Area" : "Área Asociada"}
                            </label>
                            <div className="bg-gray-50 p-4 rounded-lg">
                              {associatedAreas.map(area => (
                                <div key={area.id} className="flex items-center gap-2 text-blue-600">
                                  <Target size={18} />
                                  <span>{area.nombre}</span>
                                </div>
                              ))}
                            </div>
                          </div>
                        )}

                        {!associatedAreas.length && questionData.id && (
                          <button
                            onClick={() => setUiState(prev => ({ ...prev, showAreaModal: true }))}
                            className="w-full p-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors duration-200 flex items-center justify-center gap-2"
                          >
                            <Target size={18} />
                            {isEnglish ? "Assign Area" : "Asignar Área"}
                          </button>
                        )}

                        {/* Action Buttons */}
                        <div className="flex gap-4 mt-6">
                          <button
                            onClick={handleSave}
                            disabled={!questionData.title.trim() || !questionData.prompt.trim() || uiState.isSaved}
                            className={`flex-1 flex items-center justify-center gap-2 px-4 py-2 rounded-lg text-white transition-colors duration-200
                              ${!questionData.title.trim() || !questionData.prompt.trim() || uiState.isSaved
                                ? 'bg-blue-300 cursor-not-allowed'
                                : 'bg-blue-500 hover:bg-blue-600'}`}
                          >
                            {uiState.isSaved ? <CheckCircle size={20} /> : <Save size={20} />}
                            <span>
                              {isEnglish
                                ? questionData.id ? "Update" : "Save"
                                : questionData.id ? "Actualizar" : "Guardar"}
                            </span>
                          </button>

                          {questionData.id && (
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
                            <BookOpen size={24} className="text-blue-600" />
                          </div>
                          <div>
                            <h3 className="text-sm text-gray-500">
                              {isEnglish ? "Total Questions" : "Total Preguntas"}
                            </h3>
                            <p className="text-2xl font-bold text-gray-800">{questionStats.totalQuestions}</p>
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
                              {isEnglish ? "Questions with Areas" : "Preguntas con Áreas"}
                            </h3>
                            <p className="text-2xl font-bold text-gray-800">{questionStats.questionsWithAreas}</p>
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
                              {isEnglish ? "Average Areas per Question" : "Promedio de Áreas por Pregunta"}
                            </h3>
                            <p className="text-2xl font-bold text-gray-800">{questionStats.averageAreasPerQuestion}</p>
                          </div>
                        </div>
                      </div>
                    </div>

                    {/* Charts */}
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                      <div className="bg-white rounded-xl shadow-md p-6">
                        <h3 className="text-lg font-semibold text-gray-800 mb-4">
                          {isEnglish ? "Questions by Area" : "Preguntas por Área"}
                        </h3>
                        <div className="h-64">
                          <Pie data={getQuestionsByAreaData()} options={chartOptions} />
                        </div>
                      </div>

                      <div className="bg-white rounded-xl shadow-md p-6">
                        <h3 className="text-lg font-semibold text-gray-800 mb-4">
                          {isEnglish ? "Questions by Test" : "Preguntas por Test"}
                        </h3>
                        <div className="h-64">
                          <Bar data={getQuestionsByTestData()} options={barChartOptions} />
                        </div>
                      </div>
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>
            </>
          )}
          
          {!selectedTest.id && (
            <div className="bg-white rounded-xl shadow-md p-8 flex items-center justify-center">
              <div className="text-center">
                <BookOpen size={48} className="mx-auto text-gray-400 mb-4" />
                <h3 className="text-xl font-semibold text-gray-800 mb-2">
                  {isEnglish ? "No Test Selected" : "Ningún Test Seleccionado"}
                </h3>
                <p className="text-gray-600 mb-6">
                  {isEnglish ? "Please select a test to manage questions" : "Por favor seleccione un test para gestionar preguntas"}
                </p>
                <button
                  onClick={() => setUiState(prev => ({ ...prev, showTestModal: true }))}
                  className="bg-blue-500 hover:bg-blue-600 text-white px-6 py-2 rounded-lg inline-flex items-center gap-2 transition-colors duration-200"
                >
                  <BookOpen size={20} />
                  <span>{isEnglish ? "Select Test" : "Seleccionar Test"}</span>
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Test Selection Modal */}
      {uiState.showTestModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl shadow-xl w-full max-w-md p-6 m-4">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-xl font-bold text-gray-800">
                {isEnglish ? "Select Test" : "Seleccionar Test"}
              </h3>
              <button
                onClick={() => selectedTest.id && setUiState(prev => ({ ...prev, showTestModal: false }))}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors duration-200"
              >
                <X size={20} className="text-gray-500" />
              </button>
            </div>

            <div className="max-h-[60vh] overflow-auto">
              <div className="space-y-2">
                {tests.map((test) => (
                  <button
                    key={test.id}
                    onClick={() => handleTestSelect(test)}
                    className="w-full p-4 text-left rounded-lg transition-colors duration-200 hover:bg-gray-100"
                  >
                    {test.nombretest}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Area Selection Modal */}
      {uiState.showAreaModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl shadow-xl w-full max-w-md p-6 m-4">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-xl font-bold text-gray-800">
                {isEnglish ? "Assign Area" : "Asignar Área"}
              </h3>
              <button
                onClick={() => setUiState(prev => ({ ...prev, showAreaModal: false }))}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors duration-200"
              >
                <X size={20} className="text-gray-500" />
              </button>
            </div>

            <div className="space-y-6">
              <select
                className="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={selectedArea || ""}
                onChange={(e) => setSelectedArea(Number(e.target.value))}
              >
                <option value="">
                  {isEnglish ? "Select an area..." : "Seleccionar un área..."}
                </option>
                {areas.map((area) => (
                  <option key={area.id} value={area.id}>
                    {area.nombre}
                  </option>
                ))}
              </select>

              <div className="flex justify-end gap-4">
                <button
                  onClick={() => setUiState(prev => ({ ...prev, showAreaModal: false }))}
                  className="px-6 py-2 rounded-lg bg-gray-100 hover:bg-gray-200 transition-colors duration-200"
                >
                  {isEnglish ? "Cancel" : "Cancelar"}
                </button>
                <button
                  onClick={handleAssignArea}
                  className="px-6 py-2 rounded-lg bg-blue-500 text-white hover:bg-blue-600 transition-colors duration-200"
                >
                  {isEnglish ? "Assign" : "Asignar"}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Info Modal */}
      {uiState.showInfoModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl shadow-xl w-full max-w-md p-6 m-4">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-xl font-bold text-gray-800">
                {isEnglish ? "Important Information" : "Información Importante"}
              </h3>
              <button
                onClick={() => setUiState(prev => ({ ...prev, showInfoModal: false }))}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors duration-200"
              >
                <X size={20} className="text-gray-500" />
              </button>
            </div>

            <div className="space-y-4">
              <div className="flex items-start gap-3">
                <AlertCircle size={24} className="text-blue-500 mt-1" />
                <p className="text-gray-600">
                  {isEnglish 
                    ? "To ensure this question is counted in the results, you need to assign an area to it."
                    : "Para que esta pregunta se contabilice en los resultados, debe asignarle un área."}
                </p>
              </div>

              <div className="flex justify-end gap-4 mt-6">
                <button
                  onClick={() => setUiState(prev => ({ ...prev, showInfoModal: false }))}
                  className="px-6 py-2 rounded-lg bg-gray-100 hover:bg-gray-200 transition-colors duration-200"
                >
                  {isEnglish ? "Perfect." : "Perfecto."}  
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Confirmation Modal */}
      {uiState.showConfirmModal && (
        <ConfirmationModal
          onConfirm={handleDelete}
          onCancel={() => setUiState(prev => ({ ...prev, showConfirmModal: false }))}
          idioma={idioma}
          message={isEnglish 
            ? "Are you sure you want to delete this question?"
            : "¿Estás seguro de que deseas eliminar esta pregunta?"}
        />
      )}

      <style jsx global>{`
        .custom-scrollbar::-webkit-scrollbar {
          width: 6px;
        }
        .custom-scrollbar::-webkit-scrollbar-track {
          background: #f1f1f1;
          border-radius: 8px;
        }
        .custom-scrollbar::-webkit-scrollbar-thumb {
          background: #ddd;
          border-radius: 8px;
        }
        .custom-scrollbar::-webkit-scrollbar-thumb:hover {
          background: #ccc;
        }
      `}</style>
    </div>
  );
};

export default Pregunta;
