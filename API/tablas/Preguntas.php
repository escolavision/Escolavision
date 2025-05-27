<?php
class Preguntas
{
    private $tabla = "pregunta";
    public $id;
    public $idtest;
    public $enunciado;
    public $titulo;
    private $conn;

    public function __construct($db)
    {
        $this->conn = $db;
    }

    // Método para leer todos los registros de la base de datos
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

    // Nuevo método para leer la última pregunta
    function leerUltima()
    {
        $stmt = $this->conn->prepare("SELECT * FROM " . $this->tabla . " ORDER BY id DESC LIMIT 1");
        $stmt->execute();
        $result = $stmt->get_result();
        return $result;
    }

    // Método para insertar un nuevo registro en la tabla pregunta
    function insertar()
    {
        $stmt = $this->conn->prepare("INSERT INTO " . $this->tabla . "(`idtest`, `enunciado`, `titulo`) VALUES(?,?,?)");

        $this->idtest = strip_tags($this->idtest);
        $this->enunciado = strip_tags($this->enunciado);
        $this->titulo = strip_tags($this->titulo);

        $stmt->bind_param("sss", $this->idtest, $this->enunciado, $this->titulo);

        return $stmt->execute();
    }

    // Método para actualizar un registro existente
    function actualizar()
    {
        $stmt = $this->conn->prepare("UPDATE " . $this->tabla . " SET idtest = ?, enunciado = ?, titulo = ? WHERE id = ?");

        $this->idtest = strip_tags($this->idtest);
        $this->enunciado = strip_tags($this->enunciado);
        $this->titulo = strip_tags($this->titulo);
        $this->id = strip_tags($this->id);

        $stmt->bind_param("sssi", $this->idtest, $this->enunciado, $this->titulo, $this->id);

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
