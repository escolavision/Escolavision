package ej.Tablas;

public class Test {
	private int id;
	private String nombre;
	private int isVisible;

    public Test(int id, String nombre, int isVisible) {
		this.id = id;
		this.nombre = nombre;
		this.isVisible = isVisible;
    }
	public Test() {}

    public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(int isVisible) {
		this.isVisible = isVisible;
	}
}
