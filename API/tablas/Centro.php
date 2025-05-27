<?php
class Centro
{
    private $tabla = "centros";
    public $id;
    public $comunidad_autonoma;
    public $provincia;
    public $localidad;
    public $denominacion_generica;
    public $denominacion_especifica;
    public $codigo;
    public $naturaleza;
    public $domicilio;
    public $codigo_postal;
    public $telefono;
    private $conn;

    public function __construct($db)
    {
        $this->conn = $db;
    }

    // Método para leer datos de la tabla centros
    function leer()
    {
        if (isset($this->id) && $this->id >= 0) {
            $stmt = $this->conn->prepare("SELECT * FROM " . $this->tabla . " WHERE id = ?");
            $stmt->bind_param("i", $this->id);
        } else { 
            $stmt = $this->conn->prepare("SELECT * FROM " . $this->tabla);
        }
        $stmt->execute();
        $result = $stmt->get_result();
        return $result;
    }

    // Método para insertar un nuevo centro
    function insertar()
    {
        $stmt = $this->conn->prepare("INSERT INTO " . $this->tabla . "(comunidad_autonoma, provincia, localidad, denominacion_generica, denominacion_especifica, codigo, naturaleza, domicilio, codigo_postal, telefono) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        $this->comunidad_autonoma = strip_tags($this->comunidad_autonoma);
        $this->provincia = strip_tags($this->provincia);
        $this->localidad = strip_tags($this->localidad);
        $this->denominacion_generica = strip_tags($this->denominacion_generica);
        $this->denominacion_especifica = strip_tags($this->denominacion_especifica);
        $this->codigo = strip_tags($this->codigo);
        $this->naturaleza = strip_tags($this->naturaleza);
        $this->domicilio = strip_tags($this->domicilio);
        $this->codigo_postal = strip_tags($this->codigo_postal);
        $this->telefono = strip_tags($this->telefono);

        $stmt->bind_param("ssssssssss", $this->comunidad_autonoma, $this->provincia, $this->localidad, $this->denominacion_generica, $this->denominacion_especifica, $this->codigo, $this->naturaleza, $this->domicilio, $this->codigo_postal, $this->telefono);

        return $stmt->execute();
    }
    
      // Método para leer centros por localidad y devolver denominación específica y código
     function leerPorLocalidad()
    {
        if (isset($this->localidad) && !empty($this->localidad)) {
            $stmt = $this->conn->prepare("SELECT denominacion_especifica, id FROM " . $this->tabla . " WHERE localidad = ?");
            $stmt->bind_param("s", $this->localidad);
            $stmt->execute();
            $result = $stmt->get_result();
            return $result;
        } else {
            return null;  // Si no se pasa localidad, se retorna null
        }
    }

    // Método para actualizar los datos de un centro
    function actualizar()
    {
        $stmt = $this->conn->prepare("UPDATE " . $this->tabla . " SET comunidad_autonoma = ?, provincia = ?, localidad = ?, denominacion_generica = ?, denominacion_especifica = ?, codigo = ?, naturaleza = ?, domicilio = ?, codigo_postal = ?, telefono = ? WHERE id = ?");

        $this->comunidad_autonoma = strip_tags($this->comunidad_autonoma);
        $this->provincia = strip_tags($this->provincia);
        $this->localidad = strip_tags($this->localidad);
        $this->denominacion_generica = strip_tags($this->denominacion_generica);
        $this->denominacion_especifica = strip_tags($this->denominacion_especifica);
        $this->codigo = strip_tags($this->codigo);
        $this->naturaleza = strip_tags($this->naturaleza);
        $this->domicilio = strip_tags($this->domicilio);
        $this->codigo_postal = strip_tags($this->codigo_postal);
        $this->telefono = strip_tags($this->telefono);
        $this->id = strip_tags($this->id);

        $stmt->bind_param("ssssssssssi", $this->comunidad_autonoma, $this->provincia, $this->localidad, $this->denominacion_generica, $this->denominacion_especifica, $this->codigo, $this->naturaleza, $this->domicilio, $this->codigo_postal, $this->telefono, $this->id);

        return $stmt->execute();
    }

    // Método para borrar un centro
    function borrar()
    {
        $stmt = $this->conn->prepare("DELETE FROM " . $this->tabla . " WHERE id = ?");
        $this->id = strip_tags($this->id);
        $stmt->bind_param("i", $this->id);

        // Ejecutamos la sentencia
        $result = $stmt->execute();

        if ($result === false) {
            $error = $this->conn->error;
            throw new Exception("Error al eliminar el centro: " . $error);
        }

        return $result;
    }
}
?>
