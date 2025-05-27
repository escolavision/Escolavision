/**
 * @file EscolaVision-Desktop.tsx
 * @description EscolaVision Desktop component.
 * @author Adrian Ruiz Sanchez
 */

const EscolavisionDesktop = () => {
  return (
    <div style={{ margin: 0, overflow: "hidden", height: "82vh" }}>
      <iframe
        src="https://ismaeltg05.github.io/EscolaVision-WebApp"
        style={{ width: "100%", height: "80vh", border: "none" }}
        title="Escolavision"
      ></iframe>
    </div>
  );
};

export default EscolavisionDesktop;