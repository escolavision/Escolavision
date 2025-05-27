<?php
class Intentos
{
	private $tabla = "intentos";
	public $id;
	public $idtest;
	public $idusuario;
    public $fecha;
    public $hora;
    public $resultados;
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

	// Método para insertar un nuevo registro en la tabla alumno
	function insertar()
	{
		$stmt = $this->conn->prepare("INSERT INTO " . $this->tabla . "(`idtest`, `idusuario`, `fecha`, `hora`, `resultados`) VALUES(?,?,?,?,?)");

		$this->idtest = strip_tags($this->idtest);
		$this->idusuario = strip_tags($this->idusuario);
		$this->fecha = strip_tags($this->fecha);
		$this->hora = strip_tags($this->hora);
		$this->resultados = strip_tags($this->resultados);

		$stmt->bind_param("sssss", $this->idtest, $this->idusuario, $this->fecha, $this->hora, $this->resultados);

		return $stmt->execute();

	}

	// Método para actualizar un registro existente
	function actualizar()
	{
		$stmt = $this->conn->prepare("UPDATE " . $this->tabla . " SET idtest = ?, idusuario = ?, fecha = ?, hora = ?, resultados = ? WHERE id = ?");


		$this->idtest = strip_tags($this->idtest);
		$this->idusuario = strip_tags($this->idusuario);
		$this->fecha = strip_tags($this->fecha);
		$this->hora = strip_tags($this->hora);
		$this->resultados = strip_tags($this->resultados);
		$this->id = strip_tags($this->id);
		$stmt->bind_param("sssssi", $this->idtest, $this->idusuario, $this->fecha, $this->hora, $this->resultados, $this->id);

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

	// Método para leer registros por centro
    function leerPorCentro($id_centro)
    {
        // Validar que $id_centro es un entero
        /*if (!is_int($id_centro)) {
            throw new Exception("El ID del centro debe ser un número entero.");
        }*/

        $stmt = $this->conn->prepare("
            SELECT intentos.* FROM " . $this->tabla . " 
            INNER JOIN usuarios ON intentos.idusuario = usuarios.id 
            WHERE usuarios.id_centro = ?
        ");
        $stmt->bind_param("i", $id_centro); // El id_centro debe ser un entero
        $stmt->execute();
        return $stmt->get_result();
    }
}
?>
