<?php
class Test
{
    private $tabla = "test";
    public $id;
    public $nombretest;
    public $isVisible;  // Nueva propiedad para la visibilidad
    private $conn;

    public function __construct($db)
    {
        $this->conn = $db;
    }

    // Método para leer datos, incluyendo isVisible
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

    // Método para insertar un nuevo test, incluyendo isVisible
    function insertar()
    {
        $stmt = $this->conn->prepare("INSERT INTO " . $this->tabla . "(nombretest, isVisible) VALUES(?, ?)");

        $this->nombretest = strip_tags($this->nombretest);
        $this->isVisible = isset($this->isVisible) ? $this->isVisible : 1; // Default a 1 (visible)

        $stmt->bind_param("si", $this->nombretest, $this->isVisible);

        return $stmt->execute();
    }

    // Método para actualizar los datos, incluyendo isVisible
    function actualizar()
    {
        $stmt = $this->conn->prepare("UPDATE " . $this->tabla . " SET nombretest = ?, isVisible = ? WHERE id = ?");

        $this->nombretest = strip_tags($this->nombretest);
        $this->isVisible = isset($this->isVisible) ? $this->isVisible : 1; // Default a 1 (visible)
        $this->id = strip_tags($this->id);
        
        $stmt->bind_param("sii", $this->nombretest, $this->isVisible, $this->id);

        return $stmt->execute();
    }

    // Método para borrar un test
   function borrar()
    {
        // Intentamos borrar el test
        $stmt = $this->conn->prepare("DELETE FROM " . $this->tabla . " WHERE id = ?");
        $this->id = strip_tags($this->id);
        $stmt->bind_param("i", $this->id);
    
        // Ejecutamos la sentencia
        $result = $stmt->execute();
    
        // Verificamos si ocurrió un error en la base de datos (restricción de clave foránea)
        if ($result === false) {
            // Si hubo un error, obtenemos el error específico
            $error = $this->conn->error;
            // Aquí puedes lanzar una excepción o retornar el error como prefieras
            throw new Exception("Error al eliminar el test: " . $error);
        }
    
        return $result;
    }

}
?>