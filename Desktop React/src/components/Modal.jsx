/**
 * @file Modal.jsx
 * @description Modal component for selecting geographical and educational information. 
 * The modal allows users to select an autonomous community, province, city, and educational center 
 * while adapting to the selected language (English or Spanish).
 * @author Adrián Ruiz Sánchez
 * @comments Ismael Torres González
 */

import React from 'react';
import { useIdioma } from './IdiomaContext.jsx';

// Modal component for location and educational center selection
// Receives props for managing geographical data and selection state
const Modal = ({ 
    comunidades, // Array of autonomous communities objects with CCOM and COM properties
    provincias,  // Array of province objects with CPRO and PRO properties
    localidades, // Array of locality objects with DMUN50 property
    comunidad,   // String: ID of the selected autonomous community
    provincia,   // String: ID of the selected province
    localidad,   // String: Name of the selected locality
    centros,     // Array of educational center objects with id, denominacion_generica, and denominacion_especifica
    setComunidad, // Function(string): Updates selected community
    setProvincia, // Function(string): Updates selected province
    setLocalidad, // Function(string): Updates selected locality
    cargarCentros, // Function(string): Fetches centers for a given locality
    seleccionarCentro, // Function(string): Handles center selection
    closeModal   // Function: Closes the modal dialog
}) => {
    // Get current language setting from context
    const { idioma } = useIdioma();

    /**
     * Handles changes in the locality selection.
     * Updates the locality state and triggers the loading of educational centers.
     * @param {Object} e Event object from the select input.
     */
    const handleLocalidadChange = (e) => {
        const nuevaLocalidad = e.target.value;
        setLocalidad(nuevaLocalidad); // Update the locality state.
        if (nuevaLocalidad) {
            cargarCentros(nuevaLocalidad); // Load centers only if a new locality is selected.
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
            <div className="bg-white p-6 rounded-lg max-w-md w-full">
                {/* Modal Title */}
                <h2 className="text-xl font-bold mb-4">
                    {idioma === "Inglés" ? "Select Location" : "Selecciona Localidad"}
                </h2>
                
                {/* Autonomous Community Selection */}
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        {idioma === "Inglés" ? "Autonomous Community" : "Comunidad Autónoma"}  
                    </label>
                    <select
                        value={comunidad}
                        onChange={(e) => setComunidad(e.target.value)}
                        className="w-full p-2 border rounded"
                    >
                        <option value="">
                            {idioma === "Inglés" ? "Select Community" : "Selecciona Comunidad Autónoma"}
                        </option>
                        {comunidades.map((com) => (
                            <option key={com.CCOM} value={com.CCOM}>
                                {com.COM}
                            </option>
                        ))}
                    </select>
                </div>

                {/* Province Selection */}
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        {idioma === "Inglés" ? "Province" : "Provincia"}
                    </label>
                    <select
                        value={provincia}
                        onChange={(e) => setProvincia(e.target.value)}
                        className="w-full p-2 border rounded"
                        disabled={!comunidad} // Disable if no autonomous community is selected.
                    >
                        <option value="">
                            {idioma === "Inglés" ? "Select Province" : "Selecciona Provincia"}
                        </option>
                        {provincias.map((prov) => (
                            <option key={prov.CPRO} value={prov.CPRO}>
                                {prov.PRO}
                            </option>
                        ))}
                    </select>
                </div>

                {/* City Selection */}
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">
                        {idioma === "Inglés" ? "City" : "Ciudad"}
                    </label>
                    <select
                        value={localidad}
                        onChange={handleLocalidadChange} 
                        className="w-full p-2 border rounded"
                        disabled={!provincia} // Disable if no province is selected.
                    >
                        <option value="">
                            {idioma === "Inglés" ? "Select City" : "Selecciona Ciudad"}
                        </option>
                        {localidades.map((loc) => (
                            <option key={loc.DMUN50} value={loc.DMUN50}>
                                {loc.DMUN50}
                            </option>
                        ))}
                    </select>
                </div>

                {/* Educational Centers List */}
                {centros.length > 0 && (
                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2">
                            {idioma === "Inglés" ? "Educational Centers" : "Centros Educativos"}
                        </label>
                        <ul className="border rounded p-2 max-h-40 overflow-y-auto">
                            {centros.map((centro) => (
                                <li
                                    key={centro.id}
                                    className="cursor-pointer p-2 hover:bg-gray-100"
                                    onClick={() => {
                                        seleccionarCentro(centro.id); // Select the center.
                                        closeModal(); // Close the modal.
                                    }}
                                >
                                    {centro.denominacion_generica} - {centro.denominacion_especifica}
                                </li>
                            ))}
                        </ul>
                    </div>
                )}

                {/* Close Modal Button */}
                <div className="flex justify-end">
                    <button
                        onClick={closeModal}
                        className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                    >
                        {idioma === "Inglés" ? "Close" : "Cerrar"}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Modal;