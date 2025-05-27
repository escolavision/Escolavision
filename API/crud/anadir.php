<?php
// Habilitar la visualización de errores para depuración
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// Asegurar que el archivo sea interpretado como UTF-8
header('Content-Type: text/html; charset=UTF-8');

include_once '../basedatos/EscolaVision.php';

// Crear instancia de la conexión a la base de datos
$database = new EscolaVision();
$conex = $database->dameConexion();

// Verificar conexión a la base de datos
if ($conex->connect_error) {
    die("Error de conexión: " . $conex->connect_error);
}

// Establecer la codificación de la conexión a la base de datos
$conex->set_charset("utf8mb4");

// Nombre del archivo JSON
$archivo_json = 'centros.json'; // Asegúrate de que el archivo esté en el lugar correcto

// Verificar si el archivo JSON existe
if (!file_exists($archivo_json)) {
    die("El archivo JSON no existe.");
}

// Leer el contenido completo del archivo JSON
$contenido_json = file_get_contents($archivo_json);
$datos_json = json_decode($contenido_json, true);

// Verificar si la decodificación fue exitosa
if (json_last_error() !== JSON_ERROR_NONE) {
    die("Error al decodificar el archivo JSON: " . json_last_error_msg());
}

// Verificar si la clave 'Listado de centros' existe en el archivo JSON
if (!isset($datos_json['Listado de centros'])) {
    die("No se encontró la clave 'Listado de centros' en el archivo JSON.");
}

// Preparar el lote de datos
$batchSize = 1000; // Número de registros por lote para insertar
$datosLote = []; // Almacena los datos por lote

// Procesar los centros en el 'Listado de centros'
foreach ($datos_json['Listado de centros'] as $centro) {
    // Asignar valores por defecto si faltan campos
    $comunidadAutonoma = isset($centro["COMUNIDAD AUTÓNOMA"]) ? $centro["COMUNIDAD AUTÓNOMA"] : null;
    $provincia = isset($centro["PROVINCIA"]) ? $centro["PROVINCIA"] : null;
    $localidad = isset($centro["LOCALIDAD"]) ? $centro["LOCALIDAD"] : null;
    $denominacionGenerica = isset($centro["DENOMINACIÓN GENÉRICA"]) ? $centro["DENOMINACIÓN GENÉRICA"] : null;
    $denominacionEspecifica = isset($centro["DENOMINACIÓN ESPECÍFICA"]) ? $centro["DENOMINACIÓN ESPECÍFICA"] : null;
    $codigo = isset($centro["CÓDIGO"]) ? $centro["CÓDIGO"] : null;
    $naturaleza = isset($centro["NATURALEZA"]) ? $centro["NATURALEZA"] : null;
    $domicilio = isset($centro["DOMICILIO"]) ? $centro["DOMICILIO"] : null;
    $codigoPostal = isset($centro["CÓD POSTAL"]) ? $centro["CÓD POSTAL"] : null;
    $telefono = isset($centro["TELÉFONO"]) ? $centro["TELÉFONO"] : null;
    
    if($telefono != null){
        // Separar el teléfono si tiene el formato "telefono/telefono_secundario"
        if ($telefono && strpos($telefono, '/') !== false) {
            list($telefonoPrincipal, $telefonoSecundario) = explode('/', $telefono);
        } else {
            $telefonoPrincipal = $telefono;
            $telefonoSecundario = null;
        }
    }
    

    // Asegurarse de que los números no superen la longitud máxima (20 caracteres)
    $telefonoPrincipal = $telefonoPrincipal !== null && strlen($telefonoPrincipal) > 20 ? substr($telefonoPrincipal, 0, 20) : $telefonoPrincipal;
    $telefonoSecundario = $telefonoSecundario !== null && strlen($telefonoSecundario) > 20 ? substr($telefonoSecundario, 0, 20) : $telefonoSecundario;


    // Preparar el lote de datos
    $datosLote[] = [
        "comunidad_autonoma" => $comunidadAutonoma,
        "provincia" => $provincia,
        "localidad" => $localidad,
        "denominacion_generica" => $denominacionGenerica,
        "denominacion_especifica" => $denominacionEspecifica,
        "codigo" => $codigo,
        "naturaleza" => $naturaleza,
        "domicilio" => $domicilio,
        "codigo_postal" => $codigoPostal,
        "telefono" => $telefonoPrincipal,  // Teléfono principal
        "telefono_secundario" => $telefonoSecundario // Teléfono secundario
    ];

    // Si el lote alcanza el tamaño máximo, insertar los datos en la base de datos
    if (count($datosLote) >= $batchSize) {
        insertarLote($conex, $datosLote);
        $datosLote = []; // Limpiar el lote para el siguiente grupo de registros
    }
}

// Insertar cualquier dato restante que no haya sido procesado
if (count($datosLote) > 0) {
    insertarLote($conex, $datosLote);
}

// Función para insertar un lote de datos en la base de datos
function insertarLote($conex, $datosLote) {
    // Iniciar transacción
    $conex->begin_transaction();

    try {
        // Preparar la consulta de inserción
        $query = "INSERT INTO centros (comunidad_autonoma, provincia, localidad, denominacion_generica, denominacion_especifica, codigo, naturaleza, domicilio, codigo_postal, telefono, telefono_secundario)
                  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        $stmt = $conex->prepare($query);

        foreach ($datosLote as $centro) {
            // Vincular los parámetros y ejecutar la consulta
            $stmt->bind_param("sssssssssss", 
                $centro["comunidad_autonoma"],
                $centro["provincia"],
                $centro["localidad"],
                $centro["denominacion_generica"],
                $centro["denominacion_especifica"],
                $centro["codigo"],
                $centro["naturaleza"],
                $centro["domicilio"],
                $centro["codigo_postal"],
                $centro["telefono"],
                $centro["telefono_secundario"]
            );
            $stmt->execute();
        }

        // Confirmar la transacción
        $conex->commit();
    } catch (Exception $e) {
        // Si ocurre un error, revertir la transacción
        $conex->rollback();
        echo "Error al insertar los datos: " . $e->getMessage();
    }
}

echo "Los datos se han insertado correctamente.";
?>
