/**
 * @ƒile This file is the About page of the application.
 * @description This file contains the About component of the application.
 * @author Ismael Torres González
 */

import React from 'react';
import "./about.css";

function About() {
  return (
    <div className="aboutus">
      <h1 className="text-3xl font-bold mb-6 text-center">Sobre Nosotros</h1>
      <div className="w-full max-w-4xl flex flex-col items-center">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 w-full">
          <div className="bg-white p-6 rounded-2xl shadow-lg flex flex-col items-center text-center">
            <img 
              src="./assets/isma.jpeg" 
              alt="Ismael Torres" 
              className="w-24 h-24 rounded-full mb-4" 
              style={{ maxHeight: "256px", maxWidth: "256px" }} 
            />
            <h2 className="text-xl font-semibold">Ismael Torres</h2>
            <p className="text-gray-600 text-justify leading-relaxed">Estudiante de último curso de FP, apasionado por la tecnología y el desarrollo de software. Decidió crear esta aplicación para apoyar a los estudiantes en su camino académico, proporcionando herramientas útiles para su orientación educativa.</p>
            <p className="text-gray-500 mt-4">📧 ismaeltg@escolavision.com</p>
          </div>
          <div className="bg-white p-6 rounded-2xl shadow-lg flex flex-col items-center text-center">
            <img 
              src="./assets/adri.jpeg" 
              alt="Adrián Ruiz" 
              className="w-24 h-24 rounded-full mb-4" 
              style={{ maxHeight: "256px", maxWidth: "256px" }} 
            />
            <h2 className="text-xl font-semibold">Adrián Ruiz</h2>
            <p className="text-gray-600 text-justify leading-relaxed">Estudiante de último curso de FP, con un enfoque en la innovación tecnológica. Su objetivo es ayudar a los estudiantes a encontrar su camino profesional a través de herramientas personalizadas y eficaces.</p>
            <p className="text-gray-500 mt-4">📧 adrianrs@escolavision.com</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default About;
