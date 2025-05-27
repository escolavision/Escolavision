/**
 * @file Downloads.tsx
 * @description This file contains the Downloads component of the application.
 * @author Ismael Torres González
 */
import React from "react";
import "./Downloads.css";
import { FaGooglePlay, FaWindows, FaLinux, FaApple } from "react-icons/fa";

function Downloads() {
  return (
    <div className="download-links">
      <div className="download-section">
        <h2>Descarga nuestra app Movil:</h2>
        <div className="download-button-container">
          <a
            href="https://play.google.com/store/apps/details?id=com.escolavision.testescolavision&pcampaignid=web_share"
            target="_blank"
            rel="noopener noreferrer"
          >
            <button className="download-button">
              <FaGooglePlay className="icon" />
              <span className="lblButton">Descargar desde PlayStore</span>
            </button>
          </a>
          <a
            href="https://drive.google.com/file/d/11jAIOyFpoGKVwlo1Jz5rHHxYCqpz3lhj/view?usp=sharing"
            target="_blank"
            rel="noopener noreferrer"
          >
            <button className="download-button">
              <FaGooglePlay className="icon" />
              <span className="lblButton">Descargar APK</span>
            </button>
          </a>
        </div>
      </div>

      <div className="download-section">
        <h2>Descarga nuestra app de Escritorio:</h2>
        <div className="download-button-container">
          <a
            href="https://drive.google.com/file/d/1BFO0cZ6UY3FlwaIL7UHrx4CeU0KrgjHb/view?usp=sharing"
            target="_blank"
            rel="noopener noreferrer"
          >
            <button className="download-button">
              <FaWindows className="icon" />
              <span className="lblButton">Descargar app Windows</span>
            </button>
          </a>
        </div>
      </div>
      <hr />
    </div>
  );
}

export default Downloads;