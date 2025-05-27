<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: DELETE");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../basedatos/EscolaVision.php';
include_once '../tablas/Usuario.php'; // Cambié 'Alumnos.php' y 'Profesor.php' por 'Usuario.php'
include_once '../tablas/Area.php';
include_once '../tablas/Preguntas.php';
include_once '../tablas/Intentos.php';
include_once '../tablas/PxA.php';
include_once '../tablas/Test.php';
include_once '../tablas/Centro.php'; // Añadimos la clase Centro

$database = new EscolaVision();
$conex = $database->dameConexion();

$tables = [
    "usuarios" => new Usuario($conex),
    "areas" => new Area($conex),
    "preguntas" => new Preguntas($conex),
    "intentos" => new Intentos($conex),
    "pxa" => new PxA($conex),
    "tests" => new Test($conex),
    "centros" => new Centro($conex) // Añadimos la clase Centro
];

$response = array();

// Leer los datos del cuerpo de la solicitud
$datos = json_decode(file_get_contents("php://input"), true);

// Verificar que se hayan pasado los parámetros necesarios
if (isset($datos['tabla']) && array_key_exists($datos['tabla'], $tables)) {
    $tabla = $datos['tabla'];
    $classInstance = $tables[$tabla];

    // Verificar que se haya pasado un ID para la eliminación
    if (isset($datos['id'])) {
        $classInstance->id = $datos['id'];

        // Intentar borrar el registro
        try {
            // Intentamos borrar el registro
            if ($classInstance->borrar()) {
                http_response_code(200);
                $response["status"] = "success";  // Se añade status success
                $response["message"] = "El registro con ID " . $classInstance->id . " fue borrado con éxito.";
            }
        } catch (Exception $e) {
            http_response_code(503);
            $response["status"] = "error";  // Se añade status error
            $response["message"] = "Error al eliminar el registro: " . $e->getMessage();
        }

    } else {
        http_response_code(400);
        $response["status"] = "error";  // Se añade status error
        $response["message"] = "ID no especificado.";
    }
} else {
    http_response_code(400);
    $response["status"] = "error";  // Se añade status error
    $response["message"] = "Tabla no reconocida o no especificada.";
}

// Devolver la respuesta JSON
echo json_encode($response);
?>
