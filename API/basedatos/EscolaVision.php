<?php
class EscolaVision
{
	private $host = '172.17.0.1';
	private $user = 'EscolaVision';
	private $password = "EscolaVision";
	private $database = "EscolaVision";

	// rhVBlfz5TtEB -- Adrián
	// S0aShRCJndWi -- Ismael

	public function dameConexion()
	{
		$conn = new mysqli($this->host, $this->user, $this->password, $this->database);
		$conn->set_charset("utf8"); //Para evitar problemas con tildes, ñ y caracteres no estandar
		if ($conn->connect_error) {
			die("Error al conectar con MYSQL" . $conn->connect_error);
		} else {
			return $conn;
		}
	}
}
?>