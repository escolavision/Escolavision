<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: PUT"); // Cambiado a PUT para actualizar
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
    
    // Verificar que se hayan pasado los datos requeridos para la actualización
    if (isset($datos['datos']) && isset($datos['id'])) {
        $id = $datos['id']; // Obtener el ID para la actualización
        
        // Asignar el ID al objeto
        $classInstance->id = $id; // Asegúrate de que la propiedad se llame 'id'

        // Asignar cada dato al objeto
        foreach ($datos['datos'] as $key => $value) {
            $classInstance->{$key} = $value; 
        }

        if ($classInstance->actualizar()) {
            http_response_code(200);
            $response["message"] = "El registro de la tabla " . ucfirst($tabla) . " fue actualizado con éxito.";
        } else {
            http_response_code(503);
            $response['status'] = "error";
            $response["message"] = "No se puede actualizar el registro en la tabla " . ucfirst($tabla) . ".";
        }
    } else {
        http_response_code(400);
        $response['status'] = "error";
        $response["message"] = "Datos no especificados para la actualización o ID no proporcionado.";
    }
} else {
    http_response_code(400);
    $response['status'] = "error";
    $response["message"] = "Tabla no reconocida o no especificada.";
}

// Devolver la respuesta JSON
echo json_encode($response);
?>
