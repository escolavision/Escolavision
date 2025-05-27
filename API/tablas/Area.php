<?php
class Area
{
	private $tabla = "area";
	public $id;
	public $nombre;
	public $descripcion;
	public $logo;
	private $conn;

	public function __construct($db)
	{
		$this->conn = $db;
	}

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

	function insertar()
	{
		$stmt = $this->conn->prepare("INSERT INTO " . $this->tabla . "(`nombre`, `descripción`, `logo` ) VALUES(?,?,?)");

		$this->nombre = strip_tags($this->nombre);
		$this->descripcion = strip_tags($this->descripcion);
		$this->logo = strip_tags($this->logo);
		$stmt->bind_param("sss", $this->nombre, $this->descripcion, $this->logo);

		return $stmt->execute();

	}
	
	function actualizar()
	{
		$stmt = $this->conn->prepare("UPDATE " . $this->tabla . " SET nombre = ?, descripción = ?, logo = ? WHERE id = ?");


		$this->nombre = strip_tags($this->nombre);	
    	$this->descripcion = strip_tags($this->descripcion);
		$this->logo = strip_tags($this->logo);
		$this->id = strip_tags($this->id);
		$stmt->bind_param("sssi", $this->nombre, $this->descripcion, $this->logo, $this->id);

		return $stmt->execute();
	}

	function borrar()
	{
		$stmt = $this->conn->prepare("DELETE FROM " . $this->tabla . " WHERE id = ?");
		$this->id = strip_tags($this->id);
		$stmt->bind_param("i", $this->id);
		return $stmt->execute();
	}
}
?>
