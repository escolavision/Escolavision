/**
 * Pantalla "Acerca de" de la aplicación EscolaVision.
 * 
 * Esta pantalla muestra información general sobre la aplicación, incluyendo:
 * - Versión de la aplicación
 * - Información del equipo de desarrollo
 * - Política de privacidad
 * 
 * Características principales:
 * - Implementa un menú lateral (drawer) para navegación
 * - Utiliza Material Design 3 para la interfaz de usuario
 * - Mantiene consistencia con el diseño general de la aplicación
 * - Gestiona las preferencias del usuario para personalización
 * 
 * La pantalla es accesible desde el menú principal y proporciona
 * información esencial sobre la aplicación y sus políticas.
 */

package com.escolavision.testescolavision.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.PreferencesManager
import kotlinx.coroutines.launch
import com.escolavision.testescolavision.R

private object AboutScreenConstants {
    const val SCREEN_TITLE = "Acerca de"
    const val APP_NAME = "Escolavision App"
    const val APP_VERSION = "Versión 1.0.0"
    const val DEVELOPER_INFO = "Desarrollado por Escolavision Team"
    const val PRIVACY_TITLE = "Política de Privacidad"
    const val PRIVACY_TEXT = "Nuestra aplicación respeta tu privacidad y no comparte tus datos con terceros."
    
    val TITLE_FONT_SIZE = 22.sp
    val SUBTITLE_FONT_SIZE = 20.sp
    val BODY_FONT_SIZE = 16.sp
    val SMALL_FONT_SIZE = 14.sp
    val PADDING = 16.dp
    val SMALL_PADDING = 8.dp
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val (id, tipo) = preferencesManager.getLoginData()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
            AboutScreenContent(
                drawerState = drawerState,
                scope = scope
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutScreenContent(
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    Scaffold(
        topBar = {
            AboutTopBar(
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        content = { paddingValues ->
            AboutContent(paddingValues = paddingValues)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = AboutScreenConstants.SCREEN_TITLE,
                fontSize = AboutScreenConstants.TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = colorResource(id = R.color.titulos)
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
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = Color.Transparent
                )
            }
        }
    )
}

@Composable
private fun AboutContent(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.fondoInicio))
            .padding(paddingValues)
    ) {
        AppInfo()
        PrivacyPolicy()
    }
}

@Composable
private fun AppInfo() {
    Column {
        Text(
            text = AboutScreenConstants.APP_NAME,
            fontSize = AboutScreenConstants.SUBTITLE_FONT_SIZE,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.titulos),
            modifier = Modifier.padding(AboutScreenConstants.PADDING)
        )
        
        Text(
            text = AboutScreenConstants.APP_VERSION,
            fontSize = AboutScreenConstants.BODY_FONT_SIZE,
            color = colorResource(id = R.color.titulos),
            modifier = Modifier.padding(
                start = AboutScreenConstants.PADDING,
                end = AboutScreenConstants.PADDING,
                bottom = AboutScreenConstants.SMALL_PADDING
            )
        )
        
        Text(
            text = AboutScreenConstants.DEVELOPER_INFO,
            fontSize = AboutScreenConstants.BODY_FONT_SIZE,
            color = colorResource(id = R.color.titulos),
            modifier = Modifier.padding(
                start = AboutScreenConstants.PADDING,
                end = AboutScreenConstants.PADDING,
                bottom = AboutScreenConstants.SMALL_PADDING
            )
        )
    }
}

@Composable
private fun PrivacyPolicy() {
    Column {
        Text(
            text = AboutScreenConstants.PRIVACY_TITLE,
            fontSize = AboutScreenConstants.SUBTITLE_FONT_SIZE,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(id = R.color.titulos),
            modifier = Modifier.padding(AboutScreenConstants.PADDING)
        )
        
        Text(
            text = AboutScreenConstants.PRIVACY_TEXT,
            fontSize = AboutScreenConstants.SMALL_FONT_SIZE,
            color = colorResource(id = R.color.titulos),
            modifier = Modifier.padding(
                start = AboutScreenConstants.PADDING,
                end = AboutScreenConstants.PADDING,
                bottom = AboutScreenConstants.SMALL_PADDING
            )
        )
    }
}