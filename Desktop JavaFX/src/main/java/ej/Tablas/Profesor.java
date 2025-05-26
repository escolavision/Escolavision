package ej.Tablas;

public class Profesor {
	private int id;
	private String nombre;
	private String apellidos;
	private String dni;
	private String foto;
	private Area area;
	private String claveaccesoprof;
	private boolean isOrientador;

	public Profesor(int id, String nombre, String apellidos, String dni, String claveaccesoprof, int idarea, String foto) {
		this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.claveaccesoprof = claveaccesoprof;
        this.area = new Area(idarea, "", "", "");
        this.foto = foto;
	}


	public Area getArea() {
		return area;
	}
	
	public void setArea(Area area) {
		this.area = area;
	}
	
	
	public Profesor() {
	}
	
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
	
	public String getApellidos() {
		return apellidos;
	}
	
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	
	public String getDni() {
		return dni;
	}
	
	public void setDni(String dni) {
		this.dni = dni;
	}
	
	public String getFoto() {
		return foto;
	}
	
	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	
	public String getClaveaccesoprof() {
		return claveaccesoprof;
	}
	
	public void setClaveaccesoprof(String claveaccesoprof) {
		this.claveaccesoprof = claveaccesoprof;
	}
	
	public boolean isOrientador() {
		return isOrientador;
	}
	
	public void setOrientador(boolean orientador) {
		isOrientador = orientador;
	}
}
