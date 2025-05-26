package ej.Tablas;

public class PxA {
	private int id;
	private Area area;
	private Pregunta pregunta;

    public PxA(int id, int idPregunta, int idArea) {
		this.id = id;
		this.pregunta = new Pregunta(idPregunta, "", "", 0);
		this.area = new Area(idArea, "", "", "");
    }

    public PxA() {
    }

    public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Area getArea() {
		return area;
	}
	
	public void setArea(Area area) {
		this.area = area;
	}
	
	public Pregunta getPregunta() {
		return pregunta;
	}
	
	public void setPregunta(Pregunta pregunta) {
		this.pregunta = pregunta;
	}
}
