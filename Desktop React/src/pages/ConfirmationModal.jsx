/**
 * @file ConfirmationModal.jsx
 * @description Modal de confirmación para eliminar un test
 */

import React from "react";

const ConfirmationModal = ({ onConfirm, onCancel, idioma }) => {
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white p-6 rounded-lg shadow-xl text-center max-w-sm">
        <h3 className="text-lg font-semibold mb-4">
          {idioma === "Inglés"
            ? "Are you sure you want to delete this test?"
            : "¿Estás seguro de que deseas eliminar este test?"}
        </h3>
        <div className="flex justify-around gap-4">
          <button
            onClick={onConfirm}
            className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600"
          >
            {idioma === "Inglés" ? "Delete" : "Eliminar"}
          </button>
          <button
            onClick={onCancel}
            className="bg-gray-300 text-black px-4 py-2 rounded-lg hover:bg-gray-400"
          >
            {idioma === "Inglés" ? "Cancel" : "Cancelar"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmationModal;