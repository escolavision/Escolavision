package ej.Tablas;

public class Pregunta {
	private int id;
	private String titulo;
	private String enunciado;
	private int idTest;

	public Pregunta() {}

	public Pregunta(int id, String titulo, String enunciado, int idTest) {
		this.id = id;
		this.titulo = titulo;
		this.enunciado = enunciado;
		this.idTest = idTest;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitulo() {
		return titulo;
	}
	
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	public String getEnunciado() {
		return enunciado;
	}
	
	public void setEnunciado(String enunciado) {
		this.enunciado = enunciado;
	}
	
	public int getIdTest() {
		return idTest;
	}
	
	public void setIdTest(int idTest) {
		this.idTest = idTest;
	}
}