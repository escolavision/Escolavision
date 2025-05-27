<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../basedatos/EscolaVision.php';

$database = new EscolaVision();
$conex = $database->dameConexion();
$response = array();

// Leer datos JSON de la solicitud
$data = json_decode(file_get_contents("php://input"), true);

if ($data === null) {
    $response['status'] = "error";
    $response['message'] = "JSON inválido";
    echo json_encode($response);
    exit;
}

if (isset($data['usuario'], $data['contrasena'])) {
    $usuario = trim($data['usuario']);
    $contrasena = trim($data['contrasena']);

    // Verificar si es un email o un DNI y ajustar la consulta
    if (filter_var($usuario, FILTER_VALIDATE_EMAIL)) {
        $query = "SELECT id, nombre, dni, tipo_usuario, is_orientador, contraseña, id_centro FROM usuarios WHERE email = ?";
    } else {
        $query = "SELECT id, nombre, dni, tipo_usuario, is_orientador, contraseña, id_centro FROM usuarios WHERE dni = ?";
    }

    $stmt = $conex->prepare($query);
    $stmt->bind_param("s", $usuario);
    $stmt->execute();
    $resultado = $stmt->get_result();

    if ($resultado->num_rows > 0) {
        $usuarioData = $resultado->fetch_assoc();

        // Verificar si la contraseña ingresada coincide con el hash
        if (password_verify($contrasena, $usuarioData['contraseña'])) {
            $response = [
                'status' => "success",
                'message' => "Login exitoso",
                'id' => $usuarioData['id'],
                'nombre' => $usuarioData['nombre'],
                'dni' => $usuarioData['dni'],
                'tipo' => $usuarioData['tipo_usuario'],
                'is_orientador' => ($usuarioData['tipo_usuario'] === 'Profesor') ? $usuarioData['is_orientador'] : 0,
                'id_centro' => $usuarioData['id_centro']
            ];
        } else {
            $response['status'] = "error";
            $response['message'] = "Credenciales incorrectas";
        }

    } else {
        $response['status'] = "error";
        $response['message'] = "Usuario no encontrado";
    }

    $stmt->close();
} else {
    $response['status'] = "error";
    $response['message'] = "Faltan parámetros necesarios";
}

echo json_encode($response);
?>
