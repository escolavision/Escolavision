<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../basedatos/EscolaVision.php';
include_once '../tablas/Usuario.php';
include_once '../tablas/Area.php';
include_once '../tablas/Preguntas.php';
include_once '../tablas/Intentos.php';
include_once '../tablas/PxA.php';
include_once '../tablas/Test.php';
include_once '../tablas/Centro.php';

$database = new EscolaVision();
$conex = $database->dameConexion();

$tables = [
    "usuarios" => new Usuario($conex),
    "areas" => new Area($conex),
    "preguntas" => new Preguntas($conex),
    "intentos" => new Intentos($conex),
    "pxa" => new PxA($conex),
    "tests" => new Test($conex),
    "centros" => new Centro($conex)
];

$response = array();

if (isset($_GET['tabla']) && array_key_exists($_GET['tabla'], $tables)) {
    $tabla = $_GET['tabla'];
    $classInstance = $tables[$tabla];

    if (isset($_GET['id'])) {
        $classInstance->id = $_GET['id'];
        $result = $classInstance->leer();
    } else {
        if ($tabla == "usuarios") {
            if (isset($_GET['dni'])) {
                $classInstance->dni = $_GET['dni'];
                $result = $classInstance->leerDNI();
            } elseif (isset($_GET['id_centro'])) {
                $classInstance->id_centro = $_GET['id_centro'];
                $result = $classInstance->leerPorCentro();
            } else {
                $result = $classInstance->leer();
            }
        } elseif ($tabla == "centros" && isset($_GET['localidad'])) {
            $classInstance->localidad = $_GET['localidad'];
            $result = $classInstance->leerPorLocalidad();
        } elseif ($tabla == "intentos" && isset($_GET['id_centro'])) {
            $result = $classInstance->leerPorCentro($_GET['id_centro']);
        } elseif ($tabla == "preguntas" && isset($_GET['ultima']) && $_GET['ultima'] == 'true') {
            $result = $classInstance->leerUltima();
        } elseif ($tabla == "pxa" && isset($_GET['idpregunta'])) {
            $classInstance->idpregunta = $_GET['idpregunta'];
            $result = $classInstance->leerPorPregunta();
        } else {
            $result = $classInstance->leer();
        }
    }

    if ($result && $result->num_rows > 0) {
        $dataList = array();
        while ($data = $result->fetch_assoc()) {
            $dataList[] = $data;
        }
        $response[$tabla] = $dataList;
    } else {
        $response[$tabla] = array();
    }
} else {
    http_response_code(400);
    echo json_encode(array("message" => "Tabla no reconocida o no especificada"));
    exit();
}

http_response_code(200);
echo json_encode($response);
?>
