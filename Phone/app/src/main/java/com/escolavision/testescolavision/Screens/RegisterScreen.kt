/**
 * Pantalla de registro de usuarios de EscolaVision.
 * 
 * Esta pantalla gestiona el proceso completo de registro de nuevos usuarios:
 * - Captura de datos personales (nombre, DNI, email, etc.)
 * - Selección de tipo de usuario (Alumno/Profesor)
 * - Búsqueda y selección de centro educativo
 * - Gestión de foto de perfil
 * - Validación de datos
 * 
 * Características principales:
 * - Interfaz Material 3 con formulario estructurado
 * - Integración con API GeoAPI para búsqueda de centros
 * - Sistema de filtrado por ubicación (Comunidad, Provincia, Municipio)
 * - Validación de campos y gestión de errores
 * - Manejo de imágenes y conversión a Base64
 * 
 * La pantalla proporciona un flujo completo de registro,
 * asegurando la correcta captura de información necesaria
 * para crear nuevas cuentas en la plataforma EscolaVision.
 */

 package com.escolavision.testescolavision.Screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.escolavision.testescolavision.ShowAlertDialog
import com.escolavision.testescolavision.ViewModel.RegisterViewModel
import com.escolavision.testescolavision.imageToBase64
import com.escolavision.testescolavision.API.Centro
import com.escolavision.testescolavision.API.CentroListResponse
import com.escolavision.testescolavision.API.RegisterRequest
import com.escolavision.testescolavision.API.RegisterResponse
import com.escolavision.testescolavision.API.Usuarios
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.time.format.DateTimeFormatter
import com.escolavision.testescolavision.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search


// Pantalla de registro que permite a nuevos usuarios crear una cuenta
@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = viewModel()) {
    // Configuración inicial y estados para manejo de errores
    val context = LocalContext.current
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Diálogo de error
    if (showErrorDialog) {
        ShowAlertDialog(message = errorMessage) { showErrorDialog = false }
    }

    // Función auxiliar para mostrar errores
    fun showError(message: String) {
        errorMessage = message
        showErrorDialog = true
    }

    // Estructura principal de la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.fondoInicio))
            .padding(16.dp)
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header()
            RegisterForm(viewModel, context, navController, ::showError)
        }
    }
}

// Componente de encabezado con logo y título
@Composable
fun Header() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.logo_app),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Registro",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.titulos)
        )
    }
    Spacer(modifier = Modifier.height(32.dp))
}

// Formulario principal de registro
@Composable
fun RegisterForm(viewModel: RegisterViewModel, context: Context, navController: NavController, showError: (String) -> Unit) {
    // Campos de entrada para datos del usuario
    InputField(value = viewModel.nombre.value, label = "Nombre y Apellidos") { viewModel.updateNombre(it) }
    InputField(value = viewModel.email.value, label = "Email") { viewModel.updateEmail(it) }
    InputField(value = viewModel.dni.value, label = "DNI") { viewModel.updateDni(it) }
    InputField(label = "Año de Nacimiento", value = viewModel.edad.value) { viewModel.updateEdad(it) }
    InputField(value = viewModel.claveAcceso.value, label = "Contraseña", isPassword = true) { viewModel.updateClaveAcceso(it) }

    ToggleButtons(viewModel)

    // --- NUEVO: Botón para abrir el diálogo de filtro de centro ---
    var showFilterDialog by remember { mutableStateOf(false) }
    val municipioSeleccionado = viewModel.selectedMunicipio.value
    Button(
        onClick = { showFilterDialog = true },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.azulBoton))
    ) {
        Icon(Icons.Default.Search, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(if (municipioSeleccionado.isNullOrEmpty()) "Buscar centro educativo" else "Buscar centro en: $municipioSeleccionado", color = Color.White)
    }
    if (showFilterDialog) {
        FilterDialog(viewModel = viewModel, onDismiss = { showFilterDialog = false })
    }
    // --- FIN NUEVO ---

    // Solo buscar centros si hay municipio seleccionado
    if (!municipioSeleccionado.isNullOrEmpty()) {
        SearchCentros(viewModel, context)
    }
    CentrosList(viewModel) { centro ->
        viewModel.centroSeleccionado.value = centro
    }
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = { handleRegister(viewModel, context, navController, showError) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.azulBoton))
    ) {
        Text("Registrarse", color = Color.White)
    }
}

// Diálogo para filtrar centros por ubicación
@Composable
fun FilterDialog(viewModel: RegisterViewModel, onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    var comunidades by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var provincias by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var municipios by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedComunidad by remember { mutableStateOf<Pair<String, String>?>(null) }
    var selectedProvincia by remember { mutableStateOf<Pair<String, String>?>(null) }
    var selectedMunicipio by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    // Cargar comunidades al abrir
    LaunchedEffect(Unit) {
        isLoading = true
        comunidades = fetchComunidades()
        isLoading = false
    }

    // Cargar provincias al seleccionar comunidad
    LaunchedEffect(selectedComunidad) {
        if (selectedComunidad != null) {
            isLoading = true
            provincias = fetchProvincias(selectedComunidad!!.second)
            isLoading = false
            selectedProvincia = null
            municipios = emptyList()
            selectedMunicipio = ""
        }
    }
    // Cargar municipios al seleccionar provincia
    LaunchedEffect(selectedProvincia) {
        if (selectedProvincia != null) {
            isLoading = true
            municipios = fetchMunicipios(selectedProvincia!!.second)
            isLoading = false
            selectedMunicipio = ""
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecciona tu centro educativo") },
        text = {
            Column {
                DropdownSelector(
                    label = "Comunidad Autónoma",
                    options = comunidades.map { it.first },
                    selectedOption = selectedComunidad?.first ?: "",
                    enabled = comunidades.isNotEmpty(),
                    onOptionSelected = { name ->
                        selectedComunidad = comunidades.find { it.first == name }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DropdownSelector(
                    label = "Provincia",
                    options = provincias.map { it.first },
                    selectedOption = selectedProvincia?.first ?: "",
                    enabled = provincias.isNotEmpty(),
                    onOptionSelected = { name ->
                        selectedProvincia = provincias.find { it.first == name }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DropdownSelector(
                    label = "Municipio",
                    options = municipios,
                    selectedOption = selectedMunicipio,
                    enabled = municipios.isNotEmpty(),
                    onOptionSelected = { name ->
                        selectedMunicipio = name
                    }
                )
                if (isLoading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                if (error.isNotEmpty()) {
                    Text(error, color = Color.Red)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedMunicipio.isNotEmpty()) {
                        viewModel.selectedMunicipio.value = selectedMunicipio
                        onDismiss()
                    } else {
                        error = "Selecciona un municipio"
                    }
                },
                enabled = selectedMunicipio.isNotEmpty()
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

suspend fun fetchComunidades(): List<Pair<String, String>> {
    return try {
        withContext(Dispatchers.IO) {
            val key = "a4bed7909a6572f45ec3fcc7bc36722db648c87dd6cdef01666f1b04e242b40c"
            val response = URL("https://apiv1.geoapi.es/comunidades?type=JSON&key=$key&sandbox=0").readText()
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONArray("data")
            List(jsonArray.length()) {
                val comunidad = jsonArray.getJSONObject(it)
                val nombreComunidad = comunidad.getString("COM")
                val codigoComunidad = comunidad.getString("CCOM")
                Pair(nombreComunidad, codigoComunidad)
            }
        }
    } catch (e: Exception) {
        emptyList()
    }
}

suspend fun fetchProvincias(comunidad: String): List<Pair<String, String>> {
    return try {
        withContext(Dispatchers.IO) {
            val key = "a4bed7909a6572f45ec3fcc7bc36722db648c87dd6cdef01666f1b04e242b40c"
            val response = URL("https://apiv1.geoapi.es/provincias?CCOM=$comunidad&type=JSON&key=$key&sandbox=0").readText()
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONArray("data")
            List(jsonArray.length()) {
                val provincia = jsonArray.getJSONObject(it)
                val nombreProvincia = provincia.getString("PRO")
                val codigoProvincia = provincia.getString("CPRO")
                Pair(nombreProvincia, codigoProvincia)
            }
        }
    } catch (e: Exception) {
        emptyList()
    }
}

suspend fun fetchMunicipios(provincia: String): List<String> {
    return try {
        withContext(Dispatchers.IO) {
            val key = "a4bed7909a6572f45ec3fcc7bc36722db648c87dd6cdef01666f1b04e242b40c"
            val response = URL("https://apiv1.geoapi.es/municipios?CPRO=$provincia&type=JSON&key=$key&sandbox=0").readText()
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONArray("data")
            List(jsonArray.length()) {
                val municipio = jsonArray.getJSONObject(it)
                municipio.getString("DMUN50")
            }
        }
    } catch (e: Exception) {
        emptyList()
    }
}

// Componente de selector desplegable reutilizable
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    enabled: Boolean = true,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (enabled) Color.Gray else Color.LightGray, shape = RoundedCornerShape(8.dp))
                .clickable(enabled) { expanded = true }
                .padding(12.dp)
        ) {
            Text(text = if (selectedOption.isNotEmpty()) selectedOption else "Seleccionar", color = if (enabled) Color.Black else Color.Gray)
        }
        DropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}




fun SearchCentros(viewModel: RegisterViewModel, context: Context) {

    val localidad:String = viewModel.selectedMunicipio.value.toString()
    RetrofitClient.api.searchCentros(localidad = localidad).enqueue(object : Callback<CentroListResponse> {
        override fun onResponse(call: Call<CentroListResponse>, response: Response<CentroListResponse>) {
            if (response.isSuccessful) {
                val centros = response.body()?.centros
                viewModel.updateCentros(centros)
            } else {
                Toast.makeText(context,"No se encontraron resultados", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<CentroListResponse>, t: Throwable) {
            Toast.makeText(context,"Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}

@Composable
fun CentrosList(viewModel: RegisterViewModel, onCentroSeleccionado: (Centro) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, Color.Gray)
            .background(color = Color.White)
    ) {
        for (centro in viewModel.centros.value) {
            item {
                CentroItem(centro, onCentroSeleccionado)
            }
        }
        if(viewModel.centros.value.isEmpty()){
            item {
                Text("No se encontraron centros en ese municipio")
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    viewModel.centroSeleccionado.value?.let {
        Text(text = "Centro seleccionado: ${it.denominacion_especifica}", color = colorResource(id = R.color.titulos))
    }
}


@Composable
fun CentroItem(centro: Centro, onCentroSeleccionado: (Centro) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCentroSeleccionado(centro) },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.azulBoton))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Centro: ${centro.denominacion_especifica}", fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun ImagePicker(imagePickerLauncher: ManagedActivityResultLauncher<String, Uri?>, selectedImageUri: String?) {
    Button(
        onClick = { imagePickerLauncher.launch("image/*") },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.azulBoton))
    ) {
        Text("Seleccionar imagen", color = Color.White)
    }
    selectedImageUri?.let { uri ->
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = rememberImagePainter(uri),
            contentDescription = null,
            modifier = Modifier.size(128.dp).clip(CircleShape)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(value: String, label: String, isPassword: Boolean = false, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color.Black,
            cursorColor = Color.White,
            focusedBorderColor = colorResource(id = R.color.azulBoton),
            unfocusedBorderColor = Color.Gray,
            containerColor = Color.White,
            focusedLabelColor = colorResource(id = R.color.azulBoton),
            unfocusedLabelColor = colorResource(id = R.color.azulBoton)
        )
    )
}

@Composable
fun ToggleButtons(viewModel: RegisterViewModel) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { viewModel.selectAlumno() },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (viewModel.isAlumnoSelected.value) colorResource(id = R.color.azulBoton) else Color.Gray
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Alumno", color = Color.White)
        }
        Button(
            onClick = { viewModel.selectProfesor() },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!viewModel.isAlumnoSelected.value) colorResource(id = R.color.azulBoton) else Color.Gray
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Profesor", color = Color.White)
        }
    }
}

private fun handleRegister(viewModel: RegisterViewModel, context: Context, navController: NavController, showError: (String) -> Unit) {
    if (viewModel.nombre.value.isEmpty() || viewModel.dni.value.isEmpty() || viewModel.claveAcceso.value.isEmpty()) {
        showError("Todos los campos son requeridos")
        return
    }

    val base64Image = viewModel.selectedImageUri.value?.let { imageToBase64(it, context) }
    if (base64Image != null && base64Image.length > 20000) {
        showError("Imagen demasiado grande")
        return
    }

    val fecha = try {
        val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        viewModel.edad.value.format(formatterDate)
    } catch (e: Exception) {
        ""
    }

    val registerRequest = RegisterRequest(
        tabla = "usuarios",
        datos = Usuarios(
            nombre = viewModel.nombre.value,
            dni = viewModel.dni.value,
            email = viewModel.email.value,
            fecha_nacimiento = fecha,
            tipo_usuario = if (viewModel.tipo.value == "Alumno") "1" else "2",
            contraseña = viewModel.claveAcceso.value,
            foto = base64Image,
            id = 0,
            is_orientador = 0,
            id_centro = viewModel.centroSeleccionado.value.id
        )
    )

    RetrofitClient.api.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
        override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
            if (response.isSuccessful && response.body()?.status == "success") {
                Toast.makeText(context, "Registro realizado correctamente", Toast.LENGTH_SHORT).show()
                navController.navigate("login_screen") { popUpTo("first_screen") { inclusive = false } }
            } else {
                showError("Registro fallido")
            }
        }

        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
            showError("Error de red: ${t.message}")
        }
    })
}