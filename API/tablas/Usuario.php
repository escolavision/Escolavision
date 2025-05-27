<?php
class Usuario
{
    private $tabla = "usuarios";
    public $id;
    public $nombre;
    public $email;
    public $contraseña;
    public $fecha_nacimiento;
    public $dni;
    public $foto;
    public $tipo_usuario;
    public $is_orientador;
    public $id_centro;
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
        } elseif (isset($this->dni)) {
            $stmt = $this->conn->prepare("SELECT * FROM " . $this->tabla . " WHERE dni = ?");
            $stmt->bind_param("s", $this->dni);
        } elseif (isset($this->id_centro)) {
            $stmt = $this->conn->prepare("SELECT * FROM " . $this->tabla . " WHERE id_centro = ?");
            $stmt->bind_param("i", $this->id_centro);
        } else {
            $stmt = $this->conn->prepare("SELECT * FROM " . $this->tabla);
        }
        $stmt->execute();
        $result = $stmt->get_result();
        return $result;
    }

    function insertar()
    {
        try {
            $this->nombre = $this->nombre !== null ? strip_tags($this->nombre) : '';
            $this->email = $this->email !== null ? strip_tags($this->email) : '';
            $this->contraseña = $this->contraseña !== null ? strip_tags($this->contraseña) : '';
            $this->foto = $this->foto !== null ? strip_tags($this->foto) : '';
            $this->tipo_usuario = $this->tipo_usuario !== null ? strip_tags($this->tipo_usuario) : '';
            $this->fecha_nacimiento = $this->fecha_nacimiento !== null ? strip_tags($this->fecha_nacimiento) : '';
            $this->dni = $this->dni !== null ? strip_tags($this->dni) : '';
            $this->is_orientador = $this->is_orientador !== null ? strip_tags($this->is_orientador) : '';
            $this->id_centro = $this->id_centro !== null ? (int)$this->id_centro : null;

            $this->is_orientador = (is_numeric($this->is_orientador) && ($this->is_orientador == 0 || $this->is_orientador == 1)) ? (int)$this->is_orientador : 0;
            $this->contraseña = password_hash($this->contraseña, PASSWORD_BCRYPT);

            $stmt = $this->conn->prepare("INSERT INTO " . $this->tabla . "(`nombre`, `email`, `contraseña`, `foto`, `tipo_usuario`, `fecha_nacimiento`, `dni`, `is_orientador`, `id_centro`) VALUES(?,?,?,?,?,?,?,?,?)");
            $stmt->bind_param("sssssssii", $this->nombre, $this->email, $this->contraseña, $this->foto, $this->tipo_usuario, $this->fecha_nacimiento, $this->dni, $this->is_orientador, $this->id_centro);

            if ($stmt->execute()) {
                return json_encode(["status" => "success", "message" => "Usuario insertado correctamente"]);
            } else {
                return json_encode(["status" => "error", "message" => "Error al insertar usuario"]);
            }
        } catch (mysqli_sql_exception $e) {
            if ($e->getCode() == 1062) { 
                return json_encode(["status" => "error", "message" => "El DNI ya está registrado"]);
            } else {
                return json_encode(["status" => "error", "message" => "Error en la base de datos: " . $e->getMessage()]);
            }
        }
    }

    function leerPorCentro()
    {
        if (isset($this->id_centro)) {
            $stmt = $this->conn->prepare("SELECT * FROM " . $this->tabla . " WHERE id_centro = ?");
            $stmt->bind_param("i", $this->id_centro);
            $stmt->execute();
            return $stmt->get_result();
        }
        return null;
    }
    
    function actualizar()
    {
        try {
            $setClause = [];
            $params = [];
            $paramTypes = "";
    
            if (!empty($this->nombre)) {
                $setClause[] = "nombre = ?";
                $params[] = $this->nombre;
                $paramTypes .= "s";
            }
            if (!empty($this->email)) {
                $setClause[] = "email = ?";
                $params[] = $this->email;
                $paramTypes .= "s";
            }
            if (!empty($this->contraseña)) {
                $setClause[] = "contraseña = ?";
                $params[] = password_hash($this->contraseña, PASSWORD_BCRYPT);
                $paramTypes .= "s";
            }
            if (!empty($this->foto)) {
                $setClause[] = "foto = ?";
                $params[] = $this->foto;
                $paramTypes .= "s";
            }
            if (!empty($this->tipo_usuario)) {
                $setClause[] = "tipo_usuario = ?";
                $params[] = $this->tipo_usuario;
                $paramTypes .= "s";
            }
            if (!empty($this->fecha_nacimiento)) {
                $setClause[] = "fecha_nacimiento = ?";
                $params[] = $this->fecha_nacimiento;
                $paramTypes .= "s";
            }
            if (!empty($this->dni)) {
                $setClause[] = "dni = ?";
                $params[] = $this->dni;
                $paramTypes .= "s";
            }
            if (!is_null($this->is_orientador)) {
                $setClause[] = "is_orientador = ?";
                $params[] = (int)$this->is_orientador;
                $paramTypes .= "i";
            }
            if (!is_null($this->id_centro)) {
                $setClause[] = "id_centro = ?";
                $params[] = (int)$this->id_centro;
                $paramTypes .= "i";
            }
    
            if (empty($setClause)) {
                return json_encode(["status" => "error", "message" => "No hay datos para actualizar"]);
            }
    
            $params[] = $this->id;
            $paramTypes .= "i";
    
            $sql = "UPDATE " . $this->tabla . " SET " . implode(", ", $setClause) . " WHERE id = ?";
            $stmt = $this->conn->prepare($sql);
            $stmt->bind_param($paramTypes, ...$params);
    
            if ($stmt->execute()) {
                return json_encode(["status" => "success", "message" => "Usuario actualizado correctamente"]);
            } else {
                return json_encode(["status" => "error", "message" => "Error al actualizar usuario"]);
            }
        } catch (mysqli_sql_exception $e) {
            return json_encode(["status" => "error", "message" => "Error en la base de datos: " . $e->getMessage()]);
        }
    }
    
    function borrar()
    {
        try {
            if (!isset($this->id) || empty($this->id)) {
                return json_encode(["status" => "error", "message" => "ID de usuario no proporcionado"]);
            }
    
            $stmt = $this->conn->prepare("DELETE FROM " . $this->tabla . " WHERE id = ?");
            $stmt->bind_param("i", $this->id);
    
            if ($stmt->execute()) {
                return json_encode(["status" => "success", "message" => "Usuario eliminado correctamente"]);
            } else {
                return json_encode(["status" => "error", "message" => "Error al eliminar usuario"]);
            }
        } catch (mysqli_sql_exception $e) {
            return json_encode(["status" => "error", "message" => "Error en la base de datos: " . $e->getMessage()]);
        }
    }

}
?>
