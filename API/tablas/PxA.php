<?php
class PxA
{
	private $tabla = "pxa";
	public $id;
	public $idpregunta;
	public $idarea;
	private $conn;

	public function __construct($db)
	{
		$this->conn = $db;
	}

	// Método para leer registros de la base de datos
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
	
	 // Método para leer registros por idpregunta
    function leerPorPregunta()
    {
        var_dump(isset($this->idpregunta));
        if (isset($this->idpregunta) && $this->idpregunta >= 0) {
            $stmt = $this->conn->prepare("SELECT * FROM " . $this->tabla . " WHERE idpregunta = ?");
            $stmt->bind_param("i", $this->idpregunta);
            $stmt->execute();
            $result = $stmt->get_result();
            return $result;
        } else {
            return null;
        }
    }

	// Método para insertar un nuevo registro en la tabla alumno
	function insertar()
	{
		$stmt = $this->conn->prepare("INSERT INTO " . $this->tabla . "(`idpregunta`, `idarea`) VALUES(?,?)");

		$this->idpregunta = strip_tags($this->idpregunta);
		$this->idarea = strip_tags($this->idarea);

		$stmt->bind_param("ii", $this->idpregunta, $this->idarea);

		return $stmt->execute();

	}

	// Método para actualizar un registro existente
	function actualizar()
	{
		$stmt = $this->conn->prepare("UPDATE " . $this->tabla . " SET idpregunta = ?, idarea = ? WHERE id = ?");


		$this->idpregunta = strip_tags($this->idpregunta);
		$this->idarea = strip_tags($this->idarea);
		$this->id = strip_tags($this->id);
		
		$stmt->bind_param("iii", $this->idpregunta, $this->idarea, $this->id);

		return $stmt->execute();
	}

	// Método para borrar un registro
	function borrar()
	{
		$stmt = $this->conn->prepare("DELETE FROM " . $this->tabla . " WHERE id = ?");
		$this->id = strip_tags($this->id);
		$stmt->bind_param("i", $this->id);
		return $stmt->execute();
	}
}
?>