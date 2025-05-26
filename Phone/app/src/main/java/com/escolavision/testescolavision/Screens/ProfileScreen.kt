/**
 * Pantalla de gestión de perfil de usuario en EscolaVision.
 * 
 * Esta pantalla permite a los usuarios ver y editar su información personal:
 * - Visualización de datos del perfil (nombre, DNI, foto)
 * - Edición de información personal
 * - Actualización de contraseña
 * - Gestión de foto de perfil
 * 
 * Características principales:
 * - Interfaz Material 3 con diseño adaptativo
 * - Carga y actualización de datos desde la API
 * - Gestión de imágenes en formato Base64
 * - Validación de campos y datos
 * - Sistema de actualización pull-to-refresh
 * 
 * La pantalla proporciona una interfaz completa para que los usuarios
 * gestionen su información personal y mantengan actualizado su perfil
 * en la plataforma EscolaVision.
 */

package com.escolavision.testescolavision.Screens

import RetrofitClient
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.PreferencesManager
import com.escolavision.testescolavision.API.UpdateProfileResponse
import com.escolavision.testescolavision.API.UpdateRequest
import com.escolavision.testescolavision.API.Usuarios
import com.escolavision.testescolavision.API.UsuariosListResponse
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R

private object ProfileScreenConstants {
    const val SCREEN_TITLE = "Perfil"
    const val EDIT_PROFILE_TITLE = "Editar Perfil"
    const val NAME_LABEL = "Nombre y Apellidos"
    const val EMAIL_LABEL = "Email"
    const val BIRTH_YEAR_LABEL = "Año de nacimiento"
    const val PASSWORD_LABEL = "Contraseña"
    const val EDIT_BUTTON_TEXT = "Editar Perfil"
    const val SAVE_BUTTON_TEXT = "Guardar"
    const val CANCEL_BUTTON_TEXT = "Cancelar"
    const val GUEST_TEXT = "Invitado"
    const val DNI_PREFIX = "DNI: "
    
    const val ERROR_INVALID_ID = "ID inválido"
    const val ERROR_LOAD_USER = "Fallo al cargar los datos del usuario"
    const val ERROR_UPDATE_PROFILE = "Error al actualizar el perfil"
    const val ERROR_IMAGE_SIZE = "Imagen demasiado grande"
    const val SUCCESS_UPDATE = "Perfil actualizado con éxito"
    const val ERROR_REQUIRED_FIELDS = "Todos los campos son requeridos"
    
    val TITLE_FONT_SIZE = 22.sp
    val PROFILE_IMAGE_SIZE = 120.dp
    val CARD_PADDING = 20.dp
    val SPACING_SMALL = 8.dp
    val SPACING_MEDIUM = 16.dp
    val CARD_BG_COLOR = Color(0xFFFAFAFA)
    val CARD_BORDER_COLOR = Color(0xFFB3E5FC)
    val NAME_COLOR = Color(0xFF333333)
    val DESC_COLOR = Color(0xFF263238)
    val ICON_COLOR = Color(0xFF1976D2)
    val MAX_IMAGE_SIZE = 20000
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val (id, tipo) = preferencesManager.getLoginData()
    val id_centro = preferencesManager.getCenterData()
    
    var user by remember { mutableStateOf<Usuarios?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showEditDialog by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(id) {
        loadUserData(id.toString(), id_centro, context) { loadedUser ->
            user = loadedUser
            isLoading = false
            isRefreshing = false
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuDrawer(
                navController = navController,
                id = id,
                tipo = tipo.toString(),
                scope = scope,
                drawerState = drawerState,
                preferencesManager = preferencesManager
            )
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = ProfileScreenConstants.SCREEN_TITLE,
                                fontSize = ProfileScreenConstants.TITLE_FONT_SIZE,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = ProfileScreenConstants.NAME_COLOR
                            )
                        },
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = colorResource(id = R.color.fondoInicio)
                        ),
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menú",
                                    tint = ProfileScreenConstants.NAME_COLOR
                                )
                            }
                        },
                        actions = {
                            // Espacio para centrar el título
                            Spacer(modifier = Modifier.width(48.dp))
                        }
                    )
                },
                containerColor = colorResource(id = R.color.fondoInicio),
                content = { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        } else if (user == null) {
                            GuestView()
                        } else {
                            ProfileCard(user = user!!, onEditClick = { showEditDialog = true })
                        }
                        if (showEditDialog && user != null) {
                            EditProfileDialog(
                                user = user!!,
                                onDismiss = { showEditDialog = false },
                                onSave = { updatedUser ->
                                    showEditDialog = false
                                    handleProfileUpdate(updatedUser, context, navController) {
                                        loadUserData(id.toString(), id_centro, context) { loadedUser ->
                                            user = loadedUser
                                            isLoading = false
                                            isRefreshing = false
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            )
        }
    )
}

@Composable
private fun ProfileCard(user: Usuarios, onEditClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = ProfileScreenConstants.CARD_BG_COLOR),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, ProfileScreenConstants.CARD_BORDER_COLOR)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ProfileScreenConstants.CARD_PADDING),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileImage(user.foto)
                Spacer(modifier = Modifier.height(ProfileScreenConstants.SPACING_MEDIUM))
                Text(
                    text = user.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = ProfileScreenConstants.NAME_COLOR,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(ProfileScreenConstants.SPACING_SMALL))
                Text(
                    text = "${ProfileScreenConstants.DNI_PREFIX}${user.dni}",
                    color = ProfileScreenConstants.DESC_COLOR,
                    fontSize = 15.sp
                )
                if (!user.email.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(ProfileScreenConstants.SPACING_SMALL))
                    Text(
                        text = user.email,
                        color = ProfileScreenConstants.DESC_COLOR,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(ProfileScreenConstants.SPACING_MEDIUM))
                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ProfileScreenConstants.ICON_COLOR)
                ) {
                    Text(ProfileScreenConstants.EDIT_BUTTON_TEXT, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ProfileImage(foto: String?) {
    val imageBitmap = remember(foto) {
        try {
            if (!foto.isNullOrBlank()) {
                val imageBytes = Base64.decode(foto, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)?.asImageBitmap()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = Modifier
                .size(ProfileScreenConstants.PROFILE_IMAGE_SIZE)
                .clip(CircleShape)
        )
    } else {
        DefaultProfileImage()
    }
}

@Composable
private fun DefaultProfileImage() {
    Icon(
        painter = painterResource(id = R.drawable.ic_person2),
        contentDescription = null,
        modifier = Modifier
            .size(ProfileScreenConstants.PROFILE_IMAGE_SIZE)
            .clip(CircleShape),
        tint = ProfileScreenConstants.ICON_COLOR
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    user: Usuarios,
    onDismiss: () -> Unit,
    onSave: (Usuarios) -> Unit
) {
    var nombre by remember { mutableStateOf(user.nombre) }
    var email by remember { mutableStateOf(user.email ?: "") }
    var edad by remember { mutableStateOf(user.fecha_nacimiento ?: "") }
    var foto by remember { mutableStateOf(user.foto ?: "") }
    var error by remember { mutableStateOf("") }
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Aquí deberías convertir la imagen a base64 si lo necesitas
            foto = it.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(ProfileScreenConstants.EDIT_PROFILE_TITLE) },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(ProfileScreenConstants.NAME_LABEL) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(ProfileScreenConstants.SPACING_SMALL))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(ProfileScreenConstants.EMAIL_LABEL) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(ProfileScreenConstants.SPACING_SMALL))
                OutlinedTextField(
                    value = edad,
                    onValueChange = { edad = it },
                    label = { Text(ProfileScreenConstants.BIRTH_YEAR_LABEL) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(ProfileScreenConstants.SPACING_SMALL))
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Cambiar foto")
                }
                if (error.isNotBlank()) {
                    Spacer(modifier = Modifier.height(ProfileScreenConstants.SPACING_SMALL))
                    Text(error, color = Color.Red, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (nombre.isBlank() || email.isBlank() || edad.isBlank()) {
                    error = ProfileScreenConstants.ERROR_REQUIRED_FIELDS
                } else {
                    error = ""
                    onSave(user.copy(nombre = nombre, email = email, fecha_nacimiento = edad, foto = foto))
                }
            }) {
                Text(ProfileScreenConstants.SAVE_BUTTON_TEXT)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(ProfileScreenConstants.CANCEL_BUTTON_TEXT)
            }
        }
    )
}

@Composable
private fun GuestView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = ProfileScreenConstants.GUEST_TEXT,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = ProfileScreenConstants.NAME_COLOR
        )
    }
}

private fun loadUserData(
    userId: String,
    idCentro: String,
    context: Context,
    onComplete: (Usuarios?) -> Unit
) {
    RetrofitClient.api.getUsuarioData(id_centro = idCentro).enqueue(object : Callback<UsuariosListResponse> {
        override fun onResponse(call: Call<UsuariosListResponse>, response: Response<UsuariosListResponse>) {
            if (response.isSuccessful) {
                val alumnosList = response.body()?.usuarios ?: emptyList()
                val alumno = alumnosList.find { ""+it.id == userId }
                if (alumno != null) {
                    onComplete(Usuarios(
                        nombre = alumno.nombre,
                        dni = alumno.dni,
                        contraseña = alumno.contraseña,
                        foto = alumno.foto,
                        email = alumno.email,
                        id = alumno.id,
                        is_orientador = alumno.is_orientador,
                        fecha_nacimiento = alumno.fecha_nacimiento,
                        tipo_usuario = alumno.tipo_usuario,
                        id_centro = alumno.id_centro
                    ))
                } else {
                    onComplete(null)
                }
            } else {
                Toast.makeText(context, ProfileScreenConstants.ERROR_LOAD_USER, Toast.LENGTH_SHORT).show()
                onComplete(null)
            }
        }

        override fun onFailure(call: Call<UsuariosListResponse>, t: Throwable) {
            Log.d("ProfileScreen", "onFailure: ${t.message}")
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            onComplete(null)
        }
    })
}

private fun handleProfileUpdate(
    updatedUser: Usuarios,
    context: Context,
    navController: NavController,
    loadUserData: () -> Unit
) {
    if (updatedUser.nombre.isBlank()) {
        Toast.makeText(context, ProfileScreenConstants.ERROR_REQUIRED_FIELDS, Toast.LENGTH_SHORT).show()
        return
    }

    val base64Image = updatedUser.foto
    if (base64Image != null && base64Image.length > ProfileScreenConstants.MAX_IMAGE_SIZE) {
        Toast.makeText(context, ProfileScreenConstants.ERROR_IMAGE_SIZE, Toast.LENGTH_SHORT).show()
        return
    }

    val updateRequest = UpdateRequest(
        datos = Usuarios(
            id = updatedUser.id,
            nombre = updatedUser.nombre,
            dni = updatedUser.dni,
            fecha_nacimiento = updatedUser.fecha_nacimiento ?: "",
            contraseña = updatedUser.contraseña,
            foto = base64Image,
            tipo_usuario = if ((updatedUser.tipo_usuario == "Alumno")) "1" else "2",
            is_orientador = updatedUser.is_orientador,
            email = updatedUser.email,
            id_centro = updatedUser.id_centro
        ),
        tabla = "usuarios",
        id = updatedUser.id
    )

    RetrofitClient.api.update(updateRequest).enqueue(object : Callback<UpdateProfileResponse> {
        override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
            if (response.isSuccessful && response.message() == "OK") {
                Toast.makeText(context, ProfileScreenConstants.SUCCESS_UPDATE, Toast.LENGTH_SHORT).show()
                loadUserData()
            } else {
                Toast.makeText(context, ProfileScreenConstants.ERROR_UPDATE_PROFILE, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}

