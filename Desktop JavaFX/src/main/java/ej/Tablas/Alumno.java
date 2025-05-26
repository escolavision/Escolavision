package ej.Tablas;

public class Alumno {
	private int id;
	private String nombre;
	private String dni;
	private String foto;
	private String contraseña;
	private int fecha_nacimiento;
	private String email;



    public Alumno() {}

	public Alumno(int id, String nombre,  String dni, String claveaccesoalum, int fecha_nacimiento, String foto, String email) {
		this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.contraseña = claveaccesoalum;
        this.fecha_nacimiento = fecha_nacimiento;
        this.foto = foto;
		this.email = email;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public void setDni(String dni) {
		this.dni = dni;
	}
	
	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	public void setClaveaccesoalumno(String claveaccesoalumno) {
		this.contraseña = claveaccesoalumno;
	}

	
	public String getNombre() {
		return nombre;
	}

	public String getDni() {
		return dni;
	}
	
	public String getFoto() {
		return foto;
	}
	
	public String getContraseña() {
		return contraseña;
	}

	public String getEmail() {
		return email;
	}

	public int getEdad() {
		return fecha_nacimiento;
	}
	
	
}
