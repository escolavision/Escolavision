/**
 * Pantalla de bienvenida y presentación inicial de EscolaVision.
 * 
 * Esta pantalla sirve como punto de entrada principal a la aplicación, mostrando:
 * - Logo e información del IES Politécnico Hermenegildo Lanz
 * - Identidad visual de EscolaVision (logo y nombre)
 * - Sistema de redirección inteligente basado en el tipo de usuario
 * 
 * Características principales:
 * - Diseño Material 3 con elementos visuales corporativos
 * - Gestión automática de sesiones de usuario
 * - Navegación condicional según el tipo de usuario:
 *   · Alumnos y orientadores -> home_screen
 *   · Otros usuarios -> students_screen
 *   · Sin sesión -> login_screen
 * 
 * Esta pantalla actúa como punto de partida de la aplicación,
 * proporcionando una experiencia de usuario personalizada según
 * el tipo de acceso y los permisos del usuario.
 */

package com.escolavision.testescolavision.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.PreferencesManager
import kotlinx.coroutines.launch
import com.escolavision.testescolavision.R

private object FirstScreenConstants {
    const val INSTITUTE_NAME = "IES Politécnico Hermenegildo Lanz"
    const val INSTITUTE_LOCATION = "Granada"
    const val APP_NAME = "EscolaVision"
    const val APP_SUBTITLE = "Tu App de Orientación Escolar"
    
    val LOGO_SIZE = 64.dp
    val APP_LOGO_SIZE = 256.dp
    val BUTTON_SIZE = 120.dp
    val SPACING = 16.dp
}

@Composable
fun FirstScreen(navController: NavController) {
    val scope = rememberCoroutineScope()

    MaterialTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.fondoInicio))
                .padding(FirstScreenConstants.SPACING)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            InstituteHeader()
            AppPresentation()
            NavigationButton(navController, scope)
        }
    }
}

@Composable
private fun InstituteHeader() {
    Row(
        Modifier
            .padding(FirstScreenConstants.SPACING)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painterResource(id = R.drawable.logo_instituto),
            contentDescription = "Logo del instituto",
            modifier = Modifier.size(FirstScreenConstants.LOGO_SIZE)
        )
        Spacer(modifier = Modifier.width(FirstScreenConstants.SPACING))
        Column {
            Text(
                FirstScreenConstants.INSTITUTE_NAME,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = colorResource(id = R.color.titulos)
                )
            )
            Text(
                FirstScreenConstants.INSTITUTE_LOCATION,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Composable
private fun AppPresentation() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = FirstScreenConstants.SPACING)
    ) {
        Image(
            painterResource(id = R.drawable.logo_app),
            contentDescription = "Logo de EscolaVision",
            modifier = Modifier.size(FirstScreenConstants.APP_LOGO_SIZE)
        )
        Text(
            FirstScreenConstants.APP_NAME,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 55.sp,
                color = colorResource(id = R.color.titulos)
            )
        )
        Text(
            FirstScreenConstants.APP_SUBTITLE,
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.Gray,
                fontSize = 25.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun NavigationButton(navController: NavController, scope: kotlinx.coroutines.CoroutineScope) {
    IconButton(
        onClick = { handleNavigation(navController, scope) },
        modifier = Modifier
            .padding(FirstScreenConstants.SPACING)
            .size(FirstScreenConstants.BUTTON_SIZE)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_forward),
            contentDescription = "Ir a la siguiente pantalla",
            tint = Color.Unspecified
        )
    }
}

private fun handleNavigation(navController: NavController, scope: kotlinx.coroutines.CoroutineScope) {
    scope.launch {
        val context = navController.context
        val preferencesManager = PreferencesManager(context)
        
        when {
            !preferencesManager.isLoggedIn() -> {
                navigateToLogin(navController)
            }
            else -> {
                val (_, tipo) = preferencesManager.getLoginData()
                val isOrientador = preferencesManager.getIsOrientador()
                
                when {
                    tipo == "Alumno" || isOrientador == 1 || tipo == "invitado" -> {
                        navigateToHome(navController)
                    }
                    else -> {
                        navigateToStudents(navController)
                    }
                }
            }
        }
    }
}

private fun navigateToLogin(navController: NavController) {
    navController.navigate("login_screen") {
        popUpTo("first_screen") { inclusive = true }
    }
}

private fun navigateToHome(navController: NavController) {
    navController.navigate("home_screen") {
        popUpTo("first_screen") { inclusive = true }
    }
}

private fun navigateToStudents(navController: NavController) {
    navController.navigate("students_screen") {
        popUpTo("first_screen") { inclusive = true }
    }
}