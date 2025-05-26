package ej.Tablas;

public class Intento {
    private int id;
    private int idTest;
    private int idUsuario;
    private double puntuacion;
    private String fecha;
    
    public Intento(int id, int idTest, int idUsuario, double puntuacion, String fecha) {
        this.id = id;
        this.idTest = idTest;
        this.idUsuario = idUsuario;
        this.puntuacion = puntuacion;
        this.fecha = fecha;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public int getIdTest() {
        return idTest;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public double getPuntuacion() {
        return puntuacion;
    }
    
    public String getFecha() {
        return fecha;
    }
    
    // Setters
    public void setId(int id) {
        this.id = id;
    }
    
    public void setIdTest(int idTest) {
        this.idTest = idTest;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public void setPuntuacion(double puntuacion) {
        this.puntuacion = puntuacion;
    }
    
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    
    @Override
    public String toString() {
        return String.format("Intento{id=%d, idTest=%d, idUsuario=%d, puntuacion=%.2f, fecha='%s'}", 
            id, idTest, idUsuario, puntuacion, fecha);
    }
} 