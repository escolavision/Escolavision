/**
 * Pantalla de autenticación de EscolaVision.
 * 
 * Esta pantalla maneja todo el proceso de inicio de sesión, incluyendo:
 * - Autenticación de usuarios registrados
 * - Acceso como invitado
 * - Redirección a registro de nuevos usuarios
 * 
 * Características principales:
 * - Interfaz Material 3 con campos de entrada seguros
 * - Validación de credenciales contra API
 * - Gestión de sesiones con PreferencesManager
 * - Navegación condicional según tipo de usuario:
 *   · Profesores -> students_screen
 *   · Alumnos/Orientadores -> home_screen
 *   · Invitados -> home_screen
 * 
 * La pantalla actúa como punto de entrada principal al sistema,
 * gestionando la autenticación y el flujo de navegación inicial
 * según el rol del usuario.
 */

package com.escolavision.testescolavision.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
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
import com.escolavision.testescolavision.API.LoginRequest
import com.escolavision.testescolavision.API.LoginResponse
import com.escolavision.testescolavision.ShowAlertDialog
import com.escolavision.testescolavision.ViewModel.LoginViewModel
import com.escolavision.testescolavision.PreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

private object LoginScreenConstants {
    const val APP_NAME = "EscolaVision"
    const val LOGIN_BUTTON_TEXT = "Iniciar Sesión"
    const val REGISTER_TEXT = "¿No tienes cuenta? Regístrate"
    const val GUEST_TEXT = "Continuar como invitado"
    
    val LOGO_SIZE = 80.dp
    val SPACING_SMALL = 8.dp
    val SPACING_MEDIUM = 16.dp
    val SPACING_LARGE = 32.dp
    val CORNER_RADIUS = 10.dp
}

// Pantalla de inicio de sesión que maneja la autenticación de usuarios
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = viewModel()) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    if (showErrorDialog) {
        ShowAlertDialog(message = errorMessage) { showErrorDialog = false }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorResource(id = R.color.fondoInicio),
                        colorResource(id = R.color.fondoInicio).copy(alpha = 0.8f)
                    )
                )
            )
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(LoginScreenConstants.SPACING_MEDIUM),
            shape = RoundedCornerShape(LoginScreenConstants.CORNER_RADIUS),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(LoginScreenConstants.SPACING_MEDIUM)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppLogo()
                AppTitle()
                LoginForm(viewModel)
                LoginButtons(
                    onLoginClick = {
                        isLoading = true
                        handleLogin(viewModel, preferencesManager, navController, { msg ->
                            errorMessage = msg
                            showErrorDialog = true
                            isLoading = false
                        })
                    },
                    onRegisterClick = { navController.navigate("register_screen") },
                    onGuestClick = { handleGuestLogin(preferencesManager, navController) },
                    isLoading = isLoading
                )
            }
        }
    }
}

@Composable
private fun AppLogo() {
    Image(
        painter = painterResource(id = R.drawable.logo_app),
        contentDescription = "Logo de EscolaVision",
        modifier = Modifier.size(LoginScreenConstants.LOGO_SIZE)
    )
}

@Composable
private fun AppTitle() {
    Spacer(modifier = Modifier.height(LoginScreenConstants.SPACING_MEDIUM))
    Text(
        LoginScreenConstants.APP_NAME,
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        color = colorResource(id = R.color.titulos)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginForm(viewModel: LoginViewModel) {
    Spacer(modifier = Modifier.height(LoginScreenConstants.SPACING_LARGE))
    
    OutlinedTextField(
        value = viewModel.usuario,
        onValueChange = { viewModel.updateUsuario(it) },
        singleLine = true,
        maxLines = 1,
        label = { Text("Usuario") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
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
    
    Spacer(modifier = Modifier.height(LoginScreenConstants.SPACING_SMALL))
    
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = viewModel.contraseña,
        onValueChange = { viewModel.updateContraseña(it) },
        label = { Text("Contraseña") },
        singleLine = true,
        maxLines = 1,
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
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
private fun LoginButtons(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGuestClick: () -> Unit,
    isLoading: Boolean
) {
    Spacer(modifier = Modifier.height(LoginScreenConstants.SPACING_MEDIUM))
    
    Button(
        onClick = onLoginClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(LoginScreenConstants.CORNER_RADIUS),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.azulBoton)
        ),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Text(LoginScreenConstants.LOGIN_BUTTON_TEXT)
        }
    }
    
    Spacer(modifier = Modifier.height(LoginScreenConstants.SPACING_SMALL))
    
    TextButton(
        onClick = onRegisterClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = Color.Gray
        )
    ) {
        Text(LoginScreenConstants.REGISTER_TEXT)
    }
    
    TextButton(
        onClick = onGuestClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(LoginScreenConstants.CORNER_RADIUS),
        colors = ButtonDefaults.textButtonColors(
            contentColor = Color.Gray
        )
    ) {
        Text(LoginScreenConstants.GUEST_TEXT)
    }
}

private fun handleLogin(
    viewModel: LoginViewModel,
    preferencesManager: PreferencesManager,
    navController: NavController,
    onError: (String) -> Unit
) {
    val loginRequest = LoginRequest(viewModel.usuario, viewModel.contraseña)
    Log.d("LoginDebug", "Usuario: ${viewModel.usuario}, Contraseña: ${viewModel.contraseña}")
    
    RetrofitClient.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
            Log.d("LoginDebug", "Respuesta del servidor: ${response.body()?.toString() ?: "null"}")
            
            if (response.isSuccessful && response.body()?.status == "success") {
                handleSuccessfulLogin(response.body()!!, preferencesManager, navController)
            } else {
                onError("Login fallido: ${response.body()?.message ?: "Error desconocido"}")
            }
        }

        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            Log.d("Error de red", "" + t.message)
            onError("Error de red: ${t.message}")
        }
    })
}

private fun handleSuccessfulLogin(
    response: LoginResponse,
    preferencesManager: PreferencesManager,
    navController: NavController
) {
    val id = response.id ?: 0
    val tipo = response.tipo ?: ""
    val isOrientador = response.is_orientador ?: 0
    val idCentro = response.id_centro ?: ""
    
    preferencesManager.saveLogin(id, tipo, idCentro)
    preferencesManager.saveIsOrientador(isOrientador)
    
    navigateBasedOnUserType(tipo, isOrientador, navController)
}

private fun navigateBasedOnUserType(
    tipo: String,
    isOrientador: Int,
    navController: NavController
) {
    val destination = if (tipo != "Profesor" || isOrientador == 1) {
        "home_screen"
    } else {
        "students_screen"
    }
    
    navController.navigate(destination) {
        popUpTo("first_screen") { inclusive = true }
    }
}

private fun handleGuestLogin(preferencesManager: PreferencesManager, navController: NavController) {
    preferencesManager.saveLogin(0, "invitado", "1")
    preferencesManager.saveIsOrientador(0)
    navController.navigate("home_screen") {
        popUpTo("first_screen") { inclusive = false }
    }
}
