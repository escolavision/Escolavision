/**
 * @file Home.tsx
 * @description This file contains the Home page of the application.
 * @author Ismael Torres González
 */

import Downloads from "../components/Downloads";

function Home() {
  return (
    <div>
      <Downloads />
      <div className="home-content">
        <div className="p-12 max-w-5xl mx-auto bg-white shadow-xl rounded-lg overflow-hidden">
          <h1 className="text-5xl font-extrabold text-center text-blue-700 mb-12">
            Encuentra tu Camino Académico y Profesional
          </h1>

          <p className="text-xl text-gray-800 mb-8 leading-relaxed">
            El abandono universitario sigue siendo un desafío global. La falta
            de orientación, desmotivación y dificultades económicas son algunas
            de las causas que afectan a muchos estudiantes. Nuestra misión es
            cambiar esa realidad, ayudando a los estudiantes a encontrar el
            camino que mejor se adapte a sus intereses y habilidades.
          </p>
          <hr />
          <div className="bg-blue-50 p-8 rounded-lg mb-8 shadow-md">
            <h2 className="text-3xl font-semibold text-blue-700 mb-6">
              ¿Cómo funciona nuestra aplicación?
            </h2>
            <p className="text-lg text-gray-700 leading-relaxed">
              Nuestra plataforma ofrece tests especializados que evalúan
              aptitudes y áreas profesionales para brindar a cada estudiante una
              orientación personalizada. Estos tests están basados en criterios
              pedagógicos y psicológicos, permitiendo una visión detallada de
              las mejores opciones para cada uno.
            </p>
          </div>
          <hr />
          <h2 className="text-4xl font-semibold text-blue-700 text-center mb-12">
            Solución Integral: Orientación para tu Futuro
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
            <div className="bg-gray-50 p-8 rounded-lg shadow-md">
              <h3 className="text-2xl font-semibold text-gray-800 mb-4">
                📱 Aplicación Móvil
              </h3>
              <p className="text-gray-700 leading-relaxed">
                Los estudiantes pueden acceder a los tests en cualquier momento
                y desde cualquier lugar. La aplicación es intuitiva, y sus
                resultados proporcionan recomendaciones precisas sobre las áreas
                y ciclos formativos más adecuados para cada perfil.
              </p>
            </div>
            <div className="bg-gray-50 p-8 rounded-lg shadow-md">
              <h3 className="text-2xl font-semibold text-gray-800 mb-4">
                💻 Aplicación de Escritorio
              </h3>
              <p className="text-gray-700 leading-relaxed">
                Esta versión está diseñada para su uso en centros educativos,
                permitiendo a los orientadores realizar un análisis más
                detallado de los resultados y personalizar las recomendaciones
                según las necesidades de cada estudiante.
              </p>
            </div>
          </div>

          <p className="text-lg text-gray-700 mt-8 leading-relaxed">
            Nuestro objetivo es que los estudiantes no solo descubran qué
            quieren hacer, sino también lo que no quieren hacer. Este
            conocimiento temprano puede ayudar a evitar decisiones equivocadas,
            mejorando la satisfacción y reduciendo el riesgo de abandono
            académico.
          </p>
          <hr />
          <blockquote className="text-lg italic text-gray-600 text-center mt-8">
            "La educación no cambia el mundo, cambia a las personas que
            cambiarán el mundo." - Paulo Freire
          </blockquote>

          <p className="text-lg text-gray-700 text-center mt-6">
            Queremos ser parte del proceso que guía a los estudiantes hacia la
            mejor opción para su futuro, asegurando que elijan un camino
            motivador, basado en sus intereses y habilidades, con el
            conocimiento y la confianza necesarios para triunfar.
          </p>
        </div>
      </div>
    </div>
  );
}

export default Home;
