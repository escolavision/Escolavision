/**
 * @file Test.jsx
 * @description Modern test management component with improved UI/UX and better component organization
 */

import React, { useEffect, useState } from "react";
import { 
  Plus, 
  Save, 
  Trash2, 
  CheckCircle, 
  Search,
  ChevronLeft,
  ChevronRight,
  Eye,
  EyeOff,
  AlertCircle,
  X,
  BarChart3,
  PieChart,
  Users,
  Activity,
  TrendingUp
} from "lucide-react";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
} from 'chart.js';
import { Bar, Pie } from 'react-chartjs-2';
import { motion, AnimatePresence } from "framer-motion";
import ConfirmationModal from "./ConfirmationModal.jsx";
import { useIdioma } from "../components/IdiomaContext.jsx";

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
);

const API_BASE_URL = "https://proxy-vercel-ten.vercel.app";

const Test = ({ logout }) => {
  const { idioma } = useIdioma();
  const isEnglish = idioma === "Inglés";

  // State Management
  const [tests, setTests] = useState([]);
  const [testStats, setTestStats] = useState({
    totalAttempts: 0,
    averageScore: 0,
    activeTests: 0,
    attemptsPerTest: {},
    averageScoresPerTest: {}
  });
  const [currentPage, setCurrentPage] = useState(0);
  const [selectedTest, setSelectedTest] = useState({
    id: "",
    name: "",
    isVisible: "sí"
  });
  const [uiState, setUiState] = useState({
    isDeleting: false,
    isSaved: false,
    error: null,
    searchTerm: "",
    showConfirmModal: false,
    isLoading: false,
    activeTab: 'list' // 'list' or 'stats'
  });

  const TESTS_PER_PAGE = 6;

  // API Calls
  const fetchTests = async () => {
    setUiState(prev => ({ ...prev, isLoading: true, error: null }));
    try {
      const response = await fetch(`${API_BASE_URL}/leer.php?tabla=tests`);
      if (!response.ok) throw new Error(isEnglish ? "Error fetching tests" : "Error al cargar los tests");
      
      const data = await response.json();
      setTests(Array.isArray(data.tests) ? data.tests : []);
    } catch (error) {
      showError(isEnglish ? "Error loading tests" : "Error al cargar los tests");
    } finally {
      setUiState(prev => ({ ...prev, isLoading: false }));
    }
  };

  const saveTest = async () => {
    if (!selectedTest.name.trim()) return;

    setUiState(prev => ({ ...prev, isLoading: true, error: null }));
    try {
      const testData = {
        id: selectedTest.id,
        nombretest: selectedTest.name,
        isVisible: selectedTest.isVisible === "sí" ? 1 : 0,
      };

      const endpoint = selectedTest.id ? "actualizar.php" : "insertar.php";
      const body = selectedTest.id
        ? { tabla: "tests", datos: testData, id: selectedTest.id }
        : { tabla: "tests", datos: testData };

      const response = await fetch(`${API_BASE_URL}/${endpoint}`, {
        method: selectedTest.id ? "PUT" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });

      if (!response.ok) throw new Error(isEnglish ? "Error saving test" : "Error al guardar el test");

      showSuccess();
      handleNewTest();
    fetchTests();
    } catch (error) {
      showError(isEnglish ? "Error saving test" : "Error al guardar el test");
    } finally {
      setUiState(prev => ({ ...prev, isLoading: false }));
    }
  };

  const deleteTest = async () => {
    setUiState(prev => ({ ...prev, isLoading: true, error: null, showConfirmModal: false }));
    try {
      const response = await fetch(`${API_BASE_URL}/borrar.php`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ tabla: "tests", id: selectedTest.id }),
      });

      if (!response.ok) {
        throw new Error(
          response.status === 503
            ? isEnglish 
              ? "Cannot delete a test with associated questions"
              : "No se puede eliminar un test con preguntas asociadas"
            : isEnglish
              ? "Error deleting test"
              : "Error al eliminar el test"
        );
      }

      setUiState(prev => ({ ...prev, isDeleting: true }));
      setTimeout(() => {
        setUiState(prev => ({ ...prev, isDeleting: false }));
      }, 1500);
      
      handleNewTest();
      fetchTests();
    } catch (error) {
      showError(error.message);
    } finally {
      setUiState(prev => ({ ...prev, isLoading: false }));
    }
  };

  // Fetch test statistics
  const fetchTestStats = async () => {
    try {
      // Fetch attempts data
      const id_centro = localStorage.getItem('id_centro');
      const response = await fetch(`${API_BASE_URL}/leer.php?tabla=intentos&id_centro=${id_centro}`);
      if (!response.ok) throw new Error(isEnglish ? "Error fetching attempts" : "Error al cargar los intentos");
      
      const data = await response.json();
      const attempts = Array.isArray(data.intentos) ? data.intentos : [];

      // Calculate statistics
      const totalAttempts = attempts.length;
      
      // Calculate average score from all results
      const allScores = attempts.flatMap(attempt => 
        attempt.resultados.split(';').map(score => parseFloat(score))
      );
      const averageScore = allScores.length > 0 
        ? (allScores.reduce((acc, curr) => acc + curr, 0) / allScores.length).toFixed(1)
        : 0;
      
      // Count attempts per test
      const attemptsPerTest = attempts.reduce((acc, curr) => {
        acc[curr.idtest] = (acc[curr.idtest] || 0) + 1;
        return acc;
      }, {});

      // Calculate average score per test
      const scoresPerTest = attempts.reduce((acc, curr) => {
        if (!acc[curr.idtest]) {
          acc[curr.idtest] = [];
        }
        const scores = curr.resultados.split(';').map(score => parseFloat(score));
        acc[curr.idtest].push(...scores);
        return acc;
      }, {});

      // Calculate average score for each test
      const averageScoresPerTest = Object.entries(scoresPerTest).reduce((acc, [testId, scores]) => {
        acc[testId] = scores.length > 0 
          ? (scores.reduce((sum, score) => sum + score, 0) / scores.length).toFixed(1)
          : 0;
        return acc;
      }, {});

      // Get top 5 tests by attempts
      const topTests = Object.entries(attemptsPerTest)
        .sort(([, a], [, b]) => b - a)
        .slice(0, 5);

      // Update chart data with real attempts
      setAttemptsData(prev => ({
        ...prev,
        labels: topTests.map(([testId]) => {
          const test = tests.find(t => t.id === parseInt(testId));
          return test ? test.nombretest : `Test ${testId}`;
        }),
        datasets: [{
          ...prev.datasets[0],
          data: topTests.map(([, count]) => count)
        }]
      }));

      // Calculate active tests
      const activeTests = tests.filter(t => t.isVisible === 1).length;

      // Update test stats
      setTestStats({
        totalAttempts,
        averageScore,
        activeTests,
        attemptsPerTest,
        averageScoresPerTest
      });

    } catch (error) {
      console.error('Error fetching test stats:', error);
      showError(isEnglish ? "Error loading statistics" : "Error al cargar las estadísticas");
    }
  };

  // State for chart data
  const [attemptsData, setAttemptsData] = useState({
    labels: [],
    datasets: [{
      label: isEnglish ? 'Attempts' : 'Intentos',
      data: [],
      backgroundColor: '#60A5FA',
      borderColor: '#2563EB',
      borderWidth: 1,
    }]
  });

  // UI Helpers
  const showError = (message) => {
    setUiState(prev => ({ ...prev, error: message }));
    setTimeout(() => setUiState(prev => ({ ...prev, error: null })), 3000);
  };

  const showSuccess = () => {
    setUiState(prev => ({ ...prev, isSaved: true }));
    setTimeout(() => setUiState(prev => ({ ...prev, isSaved: false })), 1500);
  };

  const handleNewTest = () => {
    setSelectedTest({
      id: "",
      name: "",
      isVisible: "sí"
    });
  };

  // Effects
  useEffect(() => {
    const loadData = async () => {
      await fetchTests();
      await fetchTestStats();
      handleNewTest();
    };
    loadData();
  }, []);

  // Update stats when tests change
  useEffect(() => {
    if (tests.length > 0) {
      fetchTestStats();
    }
  }, [tests]);

  const filteredTests = tests.filter(test => 
    test.nombretest.toLowerCase().includes(uiState.searchTerm.toLowerCase())
  );

  // Chart data
  const visibilityData = {
    labels: isEnglish ? ['Visible', 'Hidden'] : ['Visible', 'Oculto'],
    datasets: [{
      data: [
        tests.filter(t => t.isVisible === 1).length,
        tests.filter(t => t.isVisible === 0).length
      ],
      backgroundColor: ['#60A5FA', '#F87171'],
      borderColor: ['#2563EB', '#DC2626'],
      borderWidth: 1,
    }]
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
      },
    },
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-gray-50 py-8">
      {/* Error Toast */}
      {uiState.error && (
        <div className="fixed top-4 right-4 bg-red-600 text-white px-6 py-4 rounded-lg shadow-lg z-50 flex items-center gap-3 animate-slideIn">
          <AlertCircle size={24} />
          <span>{uiState.error}</span>
        </div>
      )}

      {/* Main Content */}
      <div className="container mx-auto px-4">
        <div className="max-w-7xl mx-auto">
          {/* Header */}
          <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mb-6">
            <h2 className="text-2xl font-bold text-gray-800">
              {isEnglish ? "Test Management" : "Gestión de Tests"}
        </h2>
            
            <div className="flex items-center gap-4">
              {/* Search Bar */}
              <div className="relative">
                <input
                  type="text"
                  value={uiState.searchTerm}
                  onChange={(e) => setUiState(prev => ({ ...prev, searchTerm: e.target.value }))}
                  placeholder={isEnglish ? "Search tests..." : "Buscar tests..."}
                  className="pl-10 pr-4 py-2 border rounded-lg w-64 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
              </div>

              {/* New Test Button */}
              <button
                onClick={handleNewTest}
                className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 transition-colors duration-200"
              >
                <Plus size={20} />
                <span>{isEnglish ? "New Test" : "Nuevo Test"}</span>
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
              {isEnglish ? "Tests List" : "Lista de Tests"}
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
                {/* Tests List */}
                <div className="lg:col-span-1 bg-white rounded-xl shadow-md p-6">
                  <h3 className="text-xl font-semibold text-gray-800 mb-4">
                    {isEnglish ? "Tests" : "Tests"}
                  </h3>
                  
                  <div className="space-y-2 max-h-[420px] overflow-y-auto pr-2 custom-scrollbar">
                    {filteredTests
                      .slice(currentPage * TESTS_PER_PAGE, (currentPage + 1) * TESTS_PER_PAGE)
                      .map((test) => (
                        <motion.div
                          key={test.id}
                          initial={{ opacity: 0, y: 20 }}
                          animate={{ opacity: 1, y: 0 }}
                          transition={{ duration: 0.2 }}
                          onClick={() => setSelectedTest({
                            id: test.id,
                            name: test.nombretest,
                            isVisible: test.isVisible === 1 ? "sí" : "no"
                          })}
                          className={`p-4 rounded-lg cursor-pointer transition-all duration-200
                            ${selectedTest.id === test.id 
                              ? 'bg-blue-50 ring-2 ring-blue-500' 
                              : 'bg-gray-50 hover:bg-gray-100'}`}
                        >
                          <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center">
                              {test.isVisible === 1 
                                ? <Eye size={20} className="text-gray-500" />
                                : <EyeOff size={20} className="text-gray-500" />
                              }
                            </div>
                            <div className="flex-1">
                              <h4 className="font-medium text-gray-800 line-clamp-1">
                                {test.nombretest}
                              </h4>
                              <div className="flex items-center gap-2 text-sm text-gray-500">
                                <span>
                                  {isEnglish ? "Attempts" : "Intentos"}: {testStats.attemptsPerTest[test.id] || 0}
                                </span>
                                <span className="text-xs">•</span>
                                <span>
                                  {isEnglish ? "Avg" : "Media"}: {testStats.averageScoresPerTest[test.id] || 0}
                                </span>
                              </div>
                            </div>
                          </div>
                        </motion.div>
                      ))}
                  </div>

                  {/* Pagination */}
                  {filteredTests.length > TESTS_PER_PAGE && (
                    <div className="flex justify-center items-center gap-4 mt-6">
                      <button
                        onClick={() => setCurrentPage(prev => prev - 1)}
                        disabled={currentPage === 0}
                        className="p-2 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
                      >
                        <ChevronLeft size={20} />
                      </button>
                      <span className="font-medium">
                        {currentPage + 1} / {Math.ceil(filteredTests.length / TESTS_PER_PAGE)}
                      </span>
                      <button
                        onClick={() => setCurrentPage(prev => prev + 1)}
                        disabled={currentPage >= Math.ceil(filteredTests.length / TESTS_PER_PAGE) - 1}
                        className="p-2 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
                      >
                        <ChevronRight size={20} />
                      </button>
                    </div>
                  )}
                </div>

                {/* Test Form */}
                <div className="lg:col-span-2 bg-white rounded-xl shadow-md p-6">
                  <h3 className="text-xl font-semibold text-gray-800 mb-6">
                    {selectedTest.id 
                      ? (isEnglish ? "Edit Test" : "Editar Test")
                      : (isEnglish ? "New Test" : "Nuevo Test")}
                  </h3>

                  <div className="space-y-6">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        {isEnglish ? "Test Name" : "Nombre del Test"}
                      </label>
                      <input
                        type="text"
                        value={selectedTest.name}
                        onChange={(e) => setSelectedTest(prev => ({ ...prev, name: e.target.value }))}
                        className="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder={isEnglish ? "Enter test name" : "Ingrese nombre del test"}
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        {isEnglish ? "Visibility" : "Visibilidad"}
                      </label>
                      <div className="flex gap-4">
                        {["sí", "no"].map(option => (
                          <label key={option} className="flex items-center gap-2">
                            <input
                              type="radio"
                              value={option}
                              checked={selectedTest.isVisible === option}
                              onChange={(e) => setSelectedTest(prev => ({ ...prev, isVisible: e.target.value }))}
                              className="form-radio text-blue-500"
                            />
                            <span className="capitalize">{option}</span>
                          </label>
                        ))}
                      </div>
                    </div>

                    {/* Usage Stats if editing existing test */}
                    {selectedTest.id && (
                      <div className="bg-gray-50 p-4 rounded-lg">
                        <h4 className="font-medium text-gray-700 mb-2">
                          {isEnglish ? "Test Statistics" : "Estadísticas del Test"}
                        </h4>
                        <div className="grid grid-cols-2 gap-4">
                          <div>
                            <p className="text-sm text-gray-500">
                              {isEnglish ? "Total Attempts" : "Intentos Totales"}
                            </p>
                            <p className="text-lg font-bold text-gray-800">
                              {testStats.attemptsPerTest[selectedTest.id] || 0}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">
                              {isEnglish ? "Average Score" : "Puntuación Media"}
                            </p>
                            <p className="text-lg font-bold text-gray-800">
                              {testStats.averageScoresPerTest[selectedTest.id] || 0}%
                            </p>
                          </div>
                        </div>
                      </div>
                    )}

                    {/* Action Buttons */}
                    <div className="flex gap-4 mt-6">
                      <button
                        onClick={saveTest}
                        disabled={!selectedTest.name.trim() || uiState.isSaved}
                        className={`flex-1 flex items-center justify-center gap-2 px-4 py-2 rounded-lg text-white transition-colors duration-200
                          ${!selectedTest.name.trim() || uiState.isSaved
                            ? 'bg-blue-300 cursor-not-allowed'
                            : 'bg-blue-500 hover:bg-blue-600'}`}
                      >
                        {uiState.isSaved ? <CheckCircle size={20} /> : <Save size={20} />}
                        <span>
                          {isEnglish
                            ? selectedTest.id ? "Update" : "Save"
                            : selectedTest.id ? "Actualizar" : "Guardar"}
                        </span>
        </button>

                      {selectedTest.id && (
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
                        <BarChart3 size={24} className="text-blue-600" />
                      </div>
                      <div>
                        <h3 className="text-sm text-gray-500">
                          {isEnglish ? "Total Attempts" : "Intentos Totales"}
                        </h3>
                        <p className="text-2xl font-bold text-gray-800">{testStats.totalAttempts}</p>
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
                        <p className="text-2xl font-bold text-gray-800">{testStats.averageScore}%</p>
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
                        <p className="text-2xl font-bold text-gray-800">{testStats.activeTests}</p>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Charts */}
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                  <div className="bg-white rounded-xl shadow-md p-6">
                    <h3 className="text-lg font-semibold text-gray-800 mb-4">
                      {isEnglish ? "Test Visibility Distribution" : "Distribución de Visibilidad"}
                    </h3>
                    <div className="h-64">
                      <Pie data={visibilityData} options={chartOptions} />
                    </div>
                  </div>

                  <div className="bg-white rounded-xl shadow-md p-6">
                    <h3 className="text-lg font-semibold text-gray-800 mb-4">
                      {isEnglish ? "Top 5 Tests by Attempts" : "Top 5 Tests por Intentos"}
                    </h3>
                    <div className="h-64">
                      <Bar data={attemptsData} options={chartOptions} />
                    </div>
                  </div>
                </div>
              </motion.div>
            )}
          </AnimatePresence>

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
      </div>

      {/* Confirmation Modal */}
      {uiState.showConfirmModal && (
        <ConfirmationModal
          onConfirm={deleteTest}
          onCancel={() => setUiState(prev => ({ ...prev, showConfirmModal: false }))}
          idioma={idioma}
        />
      )}
    </div>
  );
};

export default Test;