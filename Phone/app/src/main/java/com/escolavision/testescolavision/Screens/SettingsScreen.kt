/**
 * Pantalla de configuración de EscolaVision.
 * 
 * Esta pantalla permite a los usuarios personalizar la aplicación:
 * - Gestión del tema de la aplicación (claro/oscuro)
 * - Configuraciones de usuario
 * - Opciones de cierre de sesión
 * 
 * Características principales:
 * - Interfaz Material 3 con diseño adaptativo
 * - Persistencia de preferencias de usuario
 * - Gestión de temas visuales
 * - Navegación integrada con menú lateral
 * - Control de sesión de usuario
 * 
 * La pantalla actúa como centro de control para las
 * preferencias y configuraciones personalizadas del usuario,
 * permitiendo adaptar la experiencia de la aplicación
 * a sus necesidades específicas.
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

private object SettingsScreenConstants {
    const val SCREEN_TITLE = "Configuración"
    const val DARK_THEME_LABEL = "Tema Oscuro"
    const val LOGOUT_BUTTON_TEXT = "Cerrar Sesión"
    
    val TITLE_FONT_SIZE = 22.sp
    val SETTING_LABEL_FONT_SIZE = 18.sp
    
    val CONTENT_PADDING = 16.dp
    val SWITCH_PADDING = 8.dp
    val SECTION_SPACING = 16.dp
}

// Pantalla de configuración que permite al usuario personalizar la aplicación
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val (id, tipo) = preferencesManager.getLoginData()
    var isDarkTheme by remember { mutableStateOf(preferencesManager.getDarkTheme()) }
    
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
            SettingsScreenContent(
                drawerState = drawerState,
                scope = scope,
                isDarkTheme = isDarkTheme,
                onThemeChange = { isDark ->
                    isDarkTheme = isDark
                    preferencesManager.saveDarkTheme(isDark)
                },
                onLogout = {
                    // TODO: Implementar lógica de cierre de sesión
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            SettingsTopBar(
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        content = { paddingValues ->
            SettingsContent(
                paddingValues = paddingValues,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onLogout = onLogout
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = SettingsScreenConstants.SCREEN_TITLE,
                fontSize = SettingsScreenConstants.TITLE_FONT_SIZE,
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
private fun SettingsContent(
    paddingValues: PaddingValues,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.fondoInicio))
            .padding(paddingValues)
            .padding(SettingsScreenConstants.CONTENT_PADDING)
    ) {
        DarkThemeSetting(
            isDarkTheme = isDarkTheme,
            onThemeChange = onThemeChange
        )
        
        Spacer(modifier = Modifier.height(SettingsScreenConstants.SECTION_SPACING))
        
        LogoutButton(onLogout = onLogout)
    }
}

@Composable
private fun DarkThemeSetting(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Text(
        text = SettingsScreenConstants.DARK_THEME_LABEL,
        fontSize = SettingsScreenConstants.SETTING_LABEL_FONT_SIZE,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    Switch(
        checked = isDarkTheme,
        onCheckedChange = onThemeChange,
        modifier = Modifier.padding(start = SettingsScreenConstants.SWITCH_PADDING)
    )
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    Button(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(SettingsScreenConstants.LOGOUT_BUTTON_TEXT)
    }
}
