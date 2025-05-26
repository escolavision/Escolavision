/**
 * @file Register.jsx
 * @description User registration component that allows creating new accounts in the system.
 * Manages the collection and validation of personal data, including name, email, ID,
 * password, user type (student/teacher/counselor), profile picture, and educational center.
 * Integrates geographic search for centers through external API and form validation.
 * @author Ismael Torres González
 * @coauthor Adrián Ruiz Sánchez
 * @comments Ismael Torres González
 */

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Modal from '../components/Modal';
import { useIdioma } from '../components/IdiomaContext.jsx'; 

const Register = () => {
    // User information state
    const { idioma } = useIdioma();
    const [nombre, setNombre] = useState('');
    const [contrasena, setContrasena] = useState('');
    const [confirmarContrasena, setConfirmarContrasena] = useState('');
    const [email, setEmail] = useState('');
    const [tipoUsuario, setTipoUsuario] = useState('alumno');
    const [isOrientador, setIsOrientador] = useState(false);
    const [dni, setDni] = useState('');
    const [fechaNacimiento, setFechaNacimiento] = useState('');
    const [foto, setFoto] = useState(null);

    // UI state management
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(false);

    // Location and center selection state
    const [centroSeleccionado, setCentroSeleccionado] = useState(1);
    const [comunidad, setComunidad] = useState('');
    const [provincia, setProvincia] = useState('');
    const [localidad, setLocalidad] = useState('');
    const [centros, setCentros] = useState([]);
    const [comunidades, setComunidades] = useState([]);
    const [provincias, setProvincias] = useState([]);
    const [localidades, setLocalidades] = useState([]);

    // Navigation and API configuration
    const navigate = useNavigate();
    const key = 'a4bed7909a6572f45ec3fcc7bc36722db648c87dd6cdef01666f1b04e242b40c';

    // Load initial data and handle location changes
    useEffect(() => {
        fetchComunidades();
    }, []);

    // Reset and update location data when community changes
    useEffect(() => {
        setProvincias([]);
        setLocalidades([]);
        setLocalidad('');
        setProvincia('');
        setCentros([]);
        if (comunidad) {
            fetchProvincias(comunidad);
        }
    }, [comunidad]);

    // Update localities when province changes
    useEffect(() => {
        setLocalidades([]);
        setLocalidad('');
        if (provincia) {
            fetchLocalidades(provincia);
        }
    }, [provincia]);

    // API fetch functions for location data
    const fetchComunidades = async () => {
        try {
            const response = await fetch(`https://apiv1.geoapi.es/comunidades?type=JSON&key=${key}&sandbox=0`);
            const data = await response.json();
            setComunidades(data.data);
        } catch (error) {
            console.error('Error fetching comunidades:', error);
        }
    };

    const fetchProvincias = async (comunidadId) => {
        try {
            const response = await fetch(`https://apiv1.geoapi.es/provincias?CCOM=${comunidadId}&type=JSON&key=${key}&sandbox=0`);
            const data = await response.json();
            setProvincias(data.data);
        } catch (error) {
            console.error('Error fetching provincias:', error);
        }
    };

    const fetchLocalidades = async (provinciaId) => {
        try {
            const response = await fetch(`https://apiv1.geoapi.es/municipios?CPRO=${provinciaId}&type=JSON&key=${key}&sandbox=0`);
            const data = await response.json();
            setLocalidades(data.data);
        } catch (error) {
            console.error('Error fetching localidades:', error);
        }
    };

    // Load educational centers for selected location
    const cargarCentros = async (localidadId) => {
        if (!localidadId) {
            console.error('No se puede cargar centros: localidadId está vacío.');
            return;
        }
    
        try {
            const response = await fetch(`https://proxy-vercel-ten.vercel.app/leer.php?tabla=centros&localidad=${localidadId}`);
            const data = await response.json();
    
            if (data && data.centros) {
                setCentros(data.centros);
            } else {
                console.error('No se encontraron centros para la localidad seleccionada.');
                setCentros([]);
            }
        } catch (error) {
            console.error('Error al cargar los centros:', error);
        }
    };
    // File handling functions
    const handleFileChange = (e) => {
        if (e.target.files && e.target.files[0]) {
            setFoto(e.target.files[0]);
        }
    };

    const convertToBase64 = (file) => {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onloadend = () => resolve(reader.result);
            reader.onerror = (error) => reject(error);
            reader.readAsDataURL(file);
        });
    };

    // Form submission handler with validation
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        // Password validation
        if (contrasena !== confirmarContrasena) {
            setError('Passwords do not match');
            setLoading(false);
            return;
        }

        // Validate email and ID format
        const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
        if (!emailRegex.test(email)) {
            setError('Invalid email');
            setLoading(false);
            return;
        }

        const dniRegex = /^[0-9]{8}[A-Za-z]{1}$/; // Spanish ID format
        if (!dniRegex.test(dni)) {
            setError('Invalid ID');
            setLoading(false);
            return;
        }

        try {
            // Convert photo to Base64 if one was selected
            let fotoBase64 = foto ? await convertToBase64(foto) : '';
            // Create data object
            const datos = {
                nombre: nombre,
                email: email,
                contraseña: contrasena,
                tipo_usuario: tipoUsuario === 'alumno' ? 1 : 2,
                is_orientador: isOrientador ? 1 : 0,
                dni: dni,
                fecha_nacimiento: fechaNacimiento,
                foto: fotoBase64,
                id_centro: centroSeleccionado, // Include selected center in data
            };
            const body = JSON.stringify({
                datos,
                tabla: 'usuarios',
            });

            const response = await fetch('https://proxy-vercel-ten.vercel.app/insertar.php', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: body,
            });

            const data = await response.json();

            if (!data || data.status !== 'success') {
                throw new Error(data.message || 'Error registering user');
            }

            navigate('/login');
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };
    
    const onBackClick = () => {
        navigate('/login');
    }

    return (
        <div className="flex justify-center items-center min-h-screen bg-[#AED6F1]">
            <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow-md w-full max-w-lg">
                <h2 className="text-2xl font-bold mb-4 text-center">{idioma == "Inglés" && "Register" || idioma == "Español" && "Registro"}</h2>
                {error && <p className="text-red-500 text-center mb-4">{error}</p>}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-4">
                    <div>
                        <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="usuario">
                            {idioma == "Inglés" && "Full Name" || idioma == "Español" && "Nombre completo"}
                        </label>
                        <input
                            type="text"
                            id="usuario"
                            placeholder={idioma == "Inglés" && "Enter full name" || idioma == "Español" && "Introduce el nombre completo"}
                            value={nombre}
                            onChange={(e) => setNombre(e.target.value)}
                            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="email">
                            {idioma == "Inglés" && "Email" || idioma == "Español" && "Correo electrónico"}
                        </label>
                        <input
                            type="email"
                            id="email"
                            placeholder={idioma == "Inglés" && "Enter email" || idioma == "Español" && "Introduce el correo electrónico"}
                            autoComplete="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="fecha_nacimiento">
                            {idioma == "Inglés" && "Date of Birth" || idioma == "Español" && "Fecha de nacimiento"}
                        </label>
                        <input
                            type="date"
                            id="fecha_nacimiento"
                            value={fechaNacimiento}
                            onChange={(e) => setFechaNacimiento(e.target.value)}
                            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        />
                    </div>
                    <div>
                        <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="dni">
                            {idioma == "Inglés" && "National ID" || idioma == "Español" && "DNI"}
                        </label>
                        <input
                            type="text"
                            id="dni"
                            placeholder={idioma == "Inglés" && "Enter National ID" || idioma == "Español" && "Introduce el DNI"}
                            autoComplete="dni"
                            value={dni}
                            onChange={(e) => setDni(e.target.value)}
                            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        />
                    </div>
                    <div className="col-span-2">
                        <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="foto">
                            {idioma == "Inglés" && "Photo" || idioma == "Español" && "Foto"}
                        </label>
                        <input
                            type="file"
                            id="foto"
                            onChange={handleFileChange}
                            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        />
                    </div>
                    <div>
                        <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="tipo_usuario">
                            {idioma == "Inglés" && "User Type" || idioma == "Español" && "Tipo de usuario"}
                        </label>
                        <select
                            id="tipo_usuario"
                            value={tipoUsuario}
                            onChange={(e) => {
                                setTipoUsuario(e.target.value);
                                setIsOrientador(false); // Reset counselor status
                            }}
                            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        >
                            <option value="alumno">{idioma == "Inglés" && "Student" || idioma == "Español" && "Estudiante"}</option>
                            <option value="profesor">{idioma == "Inglés" && "Teacher" || idioma == "Español" && "Profesor"}</option>
                        </select>
                    </div>
                    {tipoUsuario === 'profesor' && (
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="is_orientador">
                                {idioma == "Inglés" && "Is Counselor?" || idioma == "Español" && "¿Es orientador?"}
                            </label>
                            <input
                                type="checkbox"
                                id="is_orientador"
                                checked={isOrientador}
                                onChange={(e) => setIsOrientador(e.target.checked)}
                                className="mr-2"
                            />
                        </div>
                    )}
                    <div className="relative">
                        <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="centroSeleccionado">
                            {idioma == "Inglés" && "Educational Center" || idioma == "Español" && "Centro educativo"}
                        </label>
                        <input
                            type="text"
                            id="centroSeleccionado"
                            value={centros.find(centro => centro.id === centroSeleccionado)?.denominacion_especifica || idioma == "Inglés" && 'Select Center' || idioma == "Español" && 'Selecciona un centro'}
                            readOnly
                            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        />
                        <button
                            type="button"
                            onClick={() => setIsModalOpen(true)}
                            className="absolute right-1 top-8 bg-transparent hover:bg-gray-200 text-gray-700 font-semibold py-0.5 px-3 border border-gray-300 rounded"
                        >
                            🔍
                        </button>
                    </div>
                    <div className="col-span-2">
                        <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="contrasena">
                            {idioma == "Inglés" && "Password" || idioma == "Español" && "Contraseña"}
                        </label>
                        <div className="flex gap-4">
                            <input
                                type="password"
                                id="contrasena"
                                placeholder={idioma == "Inglés" && "Enter password" || idioma == "Español" && "Introduce la contraseña"}
                                autoComplete="new-password"
                                value={contrasena}
                                onChange={(e) => setContrasena(e.target.value)}
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                required
                            />
                            <input
                                type="password"
                                id="confirmarContrasena"
                                placeholder={idioma == "Inglés" && "Confirm Password" || idioma == "Español" && "Confirmar contraseña"}
                                autoComplete="new-password"
                                value={confirmarContrasena}
                                onChange={(e) => setConfirmarContrasena(e.target.value)}
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                required
                            />
                        </div>
                    </div>
                </div>
                <div className="flex justify-between">
                    <button
                        type="submit"
                        className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                        disabled={loading}
                    >
                        {
                        loading 
                            ? (idioma === "Inglés" ? 'Registering...' : idioma === "Español" ? 'Registrando...' : 'Cargando...')
                            : (idioma === "Inglés" ? 'Register' : idioma === "Español" ? 'Registrar' : 'Por defecto')
                        }
                    </button>
                    <button
                        type="button"
                        className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                        onClick={onBackClick}
                    >
                        {idioma == "Inglés" && "Back" || idioma == "Español" && "Volver"}
                    </button>
                </div>
            </form>

            {isModalOpen && (
                <Modal
                    comunidades={comunidades}
                    provincias={provincias}
                    localidades={localidades}
                    comunidad={comunidad}
                    provincia={provincia}
                    localidad={localidad}
                    centros={centros} // Lista de centros
                    setComunidad={setComunidad}
                    setProvincia={setProvincia}
                    setLocalidad={setLocalidad} // Actualiza la localidad
                    cargarCentros={cargarCentros} // Carga los centros educativos
                    seleccionarCentro={(idCentro) => setCentroSeleccionado(idCentro)} // Selecciona un centro
                    closeModal={() => setIsModalOpen(false)}
                />
            )}
        </div>
    );
};

export default Register;