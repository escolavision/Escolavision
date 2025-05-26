package ej.Tablas;

import java.time.LocalDate;

public class Intentos {
	private int id;
	private Test test;
	private Alumno alumno;
	private LocalDate fecha;
	private String hora;
	private String resultados;


	public Intentos(){}

	public Intentos(int id, int idtest, int idalumno, String fecha, String hora, String resultados) {
		this.id = id;
        this.test = new Test(idtest, null, 0);
        this.alumno = new Alumno(idalumno, null, null, null, 0, null, null);
        this.fecha = LocalDate.parse(fecha);
        this.hora = hora;
        this.resultados = resultados;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Test getTest() {
		return test;
	}
	
	public void setTest(Test test) {
		this.test = test;
	}
	
	public Alumno getAlumno() {
		return alumno;
	}
	
	public void setAlumno(Alumno alumno) {
		this.alumno = alumno;
	}
	
	public LocalDate getFecha() {
		return fecha;
	}
	
	public void setFecha(String fecha) {
		this.fecha = LocalDate.parse(fecha);
	}
	
	public String getHora() {
		return hora;
	}
	
	public void setHora(String hora) {
		this.hora = hora;
	}
	
	public String getResultados() {
		return resultados;
	}
	
	public void setResultados(String resultados) {
		this.resultados = resultados;
	}
}
