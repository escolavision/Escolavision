package ej.Tablas;

public class Area {
	private int id;
	private String nombre;
	private String descripcion;
	private String logo;
	
	public Area() {
	}


    public Area(int id, String nombre, String descripcion, String logo) {
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.logo = logo;
    }


    public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String getLogo() {
		return logo;
	}
	
	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
}
