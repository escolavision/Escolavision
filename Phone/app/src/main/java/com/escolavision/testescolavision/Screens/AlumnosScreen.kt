/**
 * Pantalla de gestión de alumnos de la aplicación EscolaVision.
 * 
 * Esta pantalla proporciona una interfaz completa para la gestión de alumnos, incluyendo:
 * - Listado de alumnos con búsqueda en tiempo real
 * - Funcionalidad CRUD completa (Crear, Leer, Actualizar, Eliminar)
 * - Gestión de imágenes de perfil
 * - Validación de datos de entrada
 * 
 * Características principales:
 * - Interfaz de usuario con Material Design 3
 * - Menú lateral para navegación
 * - Sistema de búsqueda integrado
 * - Gestión de estados con ViewModel
 * - Manejo de permisos y roles de usuario
 * - Integración con API REST para operaciones de base de datos
 * 
 * La pantalla es accesible solo para usuarios con permisos de administración
 * y permite la gestión completa de la información de los alumnos del centro.
 */

package com.escolavision.testescolavision.Screens

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.escolavision.testescolavision.*
import com.escolavision.testescolavision.API.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.format.DateTimeFormatter
import com.escolavision.testescolavision.R
import com.escolavision.testescolavision.ViewModel.AlumnosViewModel

private object AlumnosScreenConstants {
    const val SCREEN_TITLE = "Alumnos"
    const val SEARCH_LABEL = "Buscar Alumno"
    const val DELETE_CONFIRMATION = "¿Estás seguro de que deseas eliminar a %s?"
    const val IMAGE_SIZE_ERROR = "Imagen demasiado grande"
    const val REQUIRED_FIELDS_ERROR = "Todos los campos son requeridos"
    const val PROFILE_UPDATE_SUCCESS = "Perfil actualizado con éxito"
    const val PROFILE_UPDATE_ERROR = "Error al actualizar el perfil"
    const val DELETE_SUCCESS = "Alumno eliminado correctamente"
    const val DELETE_ERROR = "Eliminación fallida"
    const val ADD_SUCCESS = "Alumno añadido correctamente"
    const val ADD_ERROR = "Registro fallido"
    const val NETWORK_ERROR = "Error de red: %s"
    
    val TITLE_FONT_SIZE = 22.sp
    val ERROR_FONT_SIZE = 12.sp
    val PADDING = 16.dp
    val SMALL_PADDING = 8.dp
    val CARD_ELEVATION = 6.dp
    val CARD_CORNER_RADIUS = 18.dp
    val CARD_BG_COLOR = Color(0xFFFAFAFA)
    val CARD_BORDER_COLOR = Color(0xFFB3E5FC)
    val NAME_COLOR = Color(0xFF333333)
    val DESC_COLOR = Color(0xFF263238)
    val ICON_COLOR = Color(0xFF1976D2)
    val ICON_DELETE_COLOR = Color(0xFFFF7043)
    val SEARCH_BG_COLOR = Color.White
    val SEARCH_BORDER_COLOR = Color(0xFFB3E5FC)
    val MAX_IMAGE_SIZE = 20000
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(navController: NavController, viewModel: AlumnosViewModel = viewModel()) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val (id, tipo) = preferencesManager.getLoginData()
    val id_centro = preferencesManager.getCenterData()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var alumnos by remember { mutableStateOf<List<Usuarios>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedAlumno by remember { mutableStateOf<Usuarios?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = alumnos.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) || 
        it.dni.contains(searchQuery, ignoreCase = true)
    }

    LaunchedEffect(Unit) {
        loadUsuarios(id_centro) { newAlumnos ->
            alumnos = newAlumnos
            isLoading = false
        }
    }

    if (showErrorDialog) {
        ShowAlertDialog(message = errorMessage) { showErrorDialog = false }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuDrawer(navController, id, tipo.toString(), scope, drawerState, preferencesManager)
        },
        content = {
            StudentsScreenContent(
                drawerState = drawerState,
                scope = scope,
                isLoading = isLoading,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                filteredList = filteredList,
                onAddClick = { showAddDialog = true },
                onEditClick = { alumno ->
                    selectedAlumno = alumno
                    showDialog = true
                },
                onDeleteClick = { alumno ->
                    selectedAlumno = alumno
                    showDeleteDialog = true
                }
            )
        }
    )

    // Diálogos
    if (showDialog && selectedAlumno != null) {
        EditAlumnoDialog(
            alumno = selectedAlumno!!,
            onDismiss = { showDialog = false },
            onSave = { updatedAlumno ->
                handleProfileUpdate(updatedAlumno, context, navController) {
                    loadUsuarios(id_centro) { newAlumnos ->
                        alumnos = newAlumnos
                    }
                }
            }
        )
    }

    if (showDeleteDialog && selectedAlumno != null) {
        DeleteAlumnoDialog(
            alumno = selectedAlumno!!,
            onDismiss = { showDeleteDialog = false },
            onDelete = {
                handleDeleteAlumno(selectedAlumno!!, context) {
                    alumnos = alumnos.filter { it.id != selectedAlumno!!.id }
                }
                showDeleteDialog = false
            }
        )
    }

    if (showAddDialog) {
        AddAlumnoDialog(
            id_centro = id_centro,
            onDismiss = { showAddDialog = false },
            onAdd = { newAlumno ->
                handleAddAlumno(newAlumno, context) {
                    loadUsuarios(id_centro) { newAlumnos ->
                        alumnos = newAlumnos
                    }
                }
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentsScreenContent(
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope,
    isLoading: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filteredList: List<Usuarios>,
    onAddClick: () -> Unit,
    onEditClick: (Usuarios) -> Unit,
    onDeleteClick: (Usuarios) -> Unit
) {
    Scaffold(
        topBar = {
            StudentsTopBar(
                onMenuClick = { scope.launch { drawerState.open() } },
                onAddClick = onAddClick
            )
        },
        containerColor = colorResource(id = R.color.fondoInicio),
        content = { paddingValues ->
            StudentsContent(
                paddingValues = paddingValues,
                isLoading = isLoading,
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                filteredList = filteredList,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentsTopBar(
    onMenuClick: () -> Unit,
    onAddClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = AlumnosScreenConstants.SCREEN_TITLE,
                fontSize = AlumnosScreenConstants.TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = AlumnosScreenConstants.NAME_COLOR
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = colorResource(id = R.color.fondoInicio)
        ),
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = AlumnosScreenConstants.NAME_COLOR
                )
            }
        },
        actions = {
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Añadir",
                    tint = AlumnosScreenConstants.NAME_COLOR
                )
            }
        }
    )
}

@Composable
private fun StudentsContent(
    paddingValues: PaddingValues,
    isLoading: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filteredList: List<Usuarios>,
    onEditClick: (Usuarios) -> Unit,
    onDeleteClick: (Usuarios) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.fondoInicio))
            .padding(paddingValues)
    ) {
        SearchField(
            value = searchQuery,
            onValueChange = onSearchQueryChange
        )

        if (isLoading) {
            LoadingIndicator()
        } else {
            StudentsList(
                students = filteredList,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(AlumnosScreenConstants.SMALL_PADDING),
        label = { Text(AlumnosScreenConstants.SEARCH_LABEL) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = AlumnosScreenConstants.DESC_COLOR,
            cursorColor = AlumnosScreenConstants.NAME_COLOR,
            focusedBorderColor = AlumnosScreenConstants.SEARCH_BORDER_COLOR,
            unfocusedBorderColor = Color.Gray,
            containerColor = AlumnosScreenConstants.SEARCH_BG_COLOR,
            focusedLabelColor = AlumnosScreenConstants.NAME_COLOR,
            unfocusedLabelColor = AlumnosScreenConstants.NAME_COLOR
        )
    )
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun StudentsList(
    students: List<Usuarios>,
    onEditClick: (Usuarios) -> Unit,
    onDeleteClick: (Usuarios) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AlumnosScreenConstants.PADDING),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(students.size) { index ->
            AlumnoItem(
                alumno = students[index],
                onEdit = onEditClick,
                onDelete = onDeleteClick
            )
        }
    }
}

private fun loadUsuarios(id_centro: String, onSuccess: (List<Usuarios>) -> Unit) {
    RetrofitClient.api.getUsuarioData(id_centro = id_centro).enqueue(object : Callback<UsuariosListResponse> {
        override fun onResponse(call: Call<UsuariosListResponse>, response: Response<UsuariosListResponse>) {
            if (response.isSuccessful) {
                val alumnos = response.body()?.usuarios?.filter { it.tipo_usuario == "Alumno" } ?: emptyList()
                onSuccess(alumnos)
            }
        }

        override fun onFailure(call: Call<UsuariosListResponse>, t: Throwable) {
            onSuccess(emptyList())
        }
    })
}

private fun handleProfileUpdate(
    updatedUser: Usuarios,
    context: Context,
    navController: NavController,
    onSuccess: () -> Unit
) {
    if (updatedUser.nombre.isBlank() || updatedUser.fecha_nacimiento?.isBlank() == true) {
        Toast.makeText(context, AlumnosScreenConstants.REQUIRED_FIELDS_ERROR, Toast.LENGTH_SHORT).show()
        return
    }

    val base64Image = updatedUser.foto
    if (base64Image != null && base64Image.length > AlumnosScreenConstants.MAX_IMAGE_SIZE) {
        Toast.makeText(context, AlumnosScreenConstants.IMAGE_SIZE_ERROR, Toast.LENGTH_SHORT).show()
        return
    }

    val updateRequest = UpdateRequest(
        datos = updatedUser.copy(
            tipo_usuario = if (updatedUser.tipo_usuario == "Alumno") "1" else "2"
        ),
        tabla = "usuarios",
        id = updatedUser.id
    )

    RetrofitClient.api.update(updateRequest).enqueue(object : Callback<UpdateProfileResponse> {
        override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
            if (response.isSuccessful && response.message() == "OK") {
                Toast.makeText(context, AlumnosScreenConstants.PROFILE_UPDATE_SUCCESS, Toast.LENGTH_SHORT).show()
                onSuccess()
            } else {
                Toast.makeText(context, AlumnosScreenConstants.PROFILE_UPDATE_ERROR, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
            Toast.makeText(context, String.format(AlumnosScreenConstants.NETWORK_ERROR, t.message), Toast.LENGTH_SHORT).show()
        }
    })
}

private fun handleDeleteAlumno(alumno: Usuarios, context: Context, onSuccess: () -> Unit) {
    val deleteRequest = DeleteRequest(
        tabla = "usuarios",
        id = alumno.id
    )

    RetrofitClient.api.delete(deleteRequest).enqueue(object : Callback<DeleteResponse> {
        override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
            if (response.isSuccessful && response.body()?.status == "success") {
                Toast.makeText(context, AlumnosScreenConstants.DELETE_SUCCESS, Toast.LENGTH_SHORT).show()
                onSuccess()
            } else {
                Toast.makeText(context, AlumnosScreenConstants.DELETE_ERROR, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
            Toast.makeText(context, String.format(AlumnosScreenConstants.NETWORK_ERROR, t.message), Toast.LENGTH_SHORT).show()
        }
    })
}

private fun handleAddAlumno(alumno: Usuarios, context: Context, onSuccess: () -> Unit) {
    val registerRequest = RegisterRequest(
        tabla = "usuarios",
        datos = alumno
    )

    RetrofitClient.api.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
        override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
            if (response.isSuccessful && response.body()?.status == "success") {
                Toast.makeText(context, AlumnosScreenConstants.ADD_SUCCESS, Toast.LENGTH_SHORT).show()
                onSuccess()
            } else {
                Toast.makeText(context, AlumnosScreenConstants.ADD_ERROR, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
            Toast.makeText(context, String.format(AlumnosScreenConstants.NETWORK_ERROR, t.message), Toast.LENGTH_SHORT).show()
        }
    })
}

@Composable
fun AlumnoItem(alumno: Usuarios, onEdit: (Usuarios) -> Unit, onDelete: (Usuarios) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = AlumnosScreenConstants.CARD_BG_COLOR),
        elevation = CardDefaults.cardElevation(AlumnosScreenConstants.CARD_ELEVATION),
        shape = RoundedCornerShape(AlumnosScreenConstants.CARD_CORNER_RADIUS),
        border = BorderStroke(1.dp, AlumnosScreenConstants.CARD_BORDER_COLOR)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AlumnosScreenConstants.PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alumno.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AlumnosScreenConstants.NAME_COLOR
                )
                Text(
                    text = "DNI: ${alumno.dni}",
                    fontSize = 14.sp,
                    color = AlumnosScreenConstants.DESC_COLOR
                )
            }
            Row {
                IconButton(onClick = { onEdit(alumno) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = AlumnosScreenConstants.ICON_COLOR
                    )
                }
                IconButton(onClick = { onDelete(alumno) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = AlumnosScreenConstants.ICON_DELETE_COLOR
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAlumnoDialog(alumno: Usuarios, onDismiss: () -> Unit, onSave: (Usuarios) -> Unit) {
    var nombre by remember { mutableStateOf(alumno.nombre) }
    var email by remember { mutableStateOf(alumno.email) }
    var edad by remember { mutableStateOf(alumno.fecha_nacimiento) }
    var dni by remember { mutableStateOf(alumno.dni) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.azulBoton)),
                onClick = {
                val updatedAlumno = alumno.copy(
                    nombre = nombre,
                    email = email,
                    fecha_nacimiento = edad,
                    dni = dni
                )
                onSave(updatedAlumno) 
                onDismiss() 
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.azulBoton)),
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        },
        text = {
            Column {
                TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = dni, onValueChange = { dni = it }, label = { Text("DNI") })
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = edad.toString(), onValueChange = { edad = it }, label = { Text("Año de Nacimiento") })
            }
        }
    )
}

@Composable
fun DeleteAlumnoDialog(alumno: Usuarios, onDismiss: () -> Unit, onDelete: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Eliminar", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.azulBoton))) {
                Text("Cancelar")
            }
        },
        text = {
            Text("¿Estás seguro de que deseas eliminar a ${alumno.nombre}?")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlumnoDialog(id_centro: String, onDismiss: () -> Unit, onAdd: (Usuarios) -> Unit, viewModel: AlumnosViewModel = viewModel(), context: Context = LocalContext.current) {
    var isDniValid by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(true) }
    var isEdadValid by remember { mutableStateOf(true) }

    val isValid = viewModel.nombre.value.isNotEmpty() && (viewModel.dni.value.isNotEmpty() || viewModel.email.value.isNotEmpty()) && viewModel.edad.value.isNotEmpty()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> viewModel.updateImageUri(uri) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        val base64Image = viewModel.selectedImageUri.value?.let { imageToBase64(it, context) }
                        if (base64Image != null && base64Image.length > 20000) {
                            Toast.makeText(context, "Imagen demasiado grande", Toast.LENGTH_SHORT).show()
                        }
                        val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val fechavm = viewModel.edad.value
                        val fecha = fechavm.format(formatterDate)
                        val newAlumno = Usuarios(
                            id = 0,
                            nombre = viewModel.nombre.value,
                            dni = viewModel.dni.value,
                            tipo_usuario = "1",
                            foto = base64Image,
                            fecha_nacimiento = fecha,
                            is_orientador = 0,
                            contraseña = "",
                            email = viewModel.email.value,
                            id_centro = id_centro
                        )

                        onAdd(newAlumno)
                        onDismiss()
                    }
                },
                enabled = isValid
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = {
            Text("Añadir Alumno")
        },
        text = {
            Column {
                InputField(
                    value = viewModel.nombre.value,
                    label = "Nombre y Apellidos"
                ) { viewModel.nombre.value = it }
                InputField(value = viewModel.email.value, label = "Email") {
                    viewModel.email.value = it; isEmailValid =
                    Patterns.EMAIL_ADDRESS.matcher(it).matches()
                }
                InputField(value = viewModel.dni.value, label = "DNI") {
                    viewModel.dni.value = it; isDniValid = it.length == 9
                }

                if ((viewModel.dni.value.isNotEmpty() && !isDniValid) || (viewModel.email.value.isNotEmpty() && !isEmailValid)) {
                    Text("DNI o Email no válido", color = Color.Red, fontSize = 12.sp)
                }
                InputField(
                    value = viewModel.edad.value,
                    label = "Año de Nacimiento"
                ) { viewModel.edad.value = it; isEdadValid = it.length == 4 }
                if ((viewModel.edad.value.isNotEmpty() && !isEdadValid)) {
                    Text("Año de nacimiento no válido", color = Color.Red, fontSize = 12.sp)
                }
                ImagePicker(imagePickerLauncher, viewModel.selectedImageUri.value.toString())
            }
        }
    )
}