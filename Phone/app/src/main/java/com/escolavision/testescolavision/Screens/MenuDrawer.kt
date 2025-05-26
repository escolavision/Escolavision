/**
 * Componente de navegación lateral (Drawer) de EscolaVision.
 * 
 * Este componente implementa el menú lateral de la aplicación y gestiona:
 * - Navegación entre diferentes pantallas
 * - Control de acceso basado en roles de usuario
 * - Opciones específicas según tipo de usuario:
 *   · Alumnos: Tests, Áreas, Perfil, Mi Centro, Resultados, Ayuda
 *   · Profesores: Alumnos, Áreas, Perfil, Mi Centro, Resultados, Ayuda
 *   · Invitados: Tests, Áreas, Perfil, Mi Centro, Ayuda
 * 
 * Características principales:
 * - Interfaz Material 3 con diseño adaptativo
 * - Gestión de estados del drawer
 * - Control de sesión de usuario
 * - Navegación condicional según permisos
 * 
 * El componente actúa como el hub principal de navegación,
 * proporcionando acceso contextual a las diferentes
 * funcionalidades de la aplicación según el rol del usuario.
 */

package com.escolavision.testescolavision.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.escolavision.testescolavision.R

private object MenuDrawerConstants {
    val DRAWER_WIDTH = 280.dp
    val PADDING = 16.dp
    val ITEM_PADDING = 12.dp
    val ICON_SIZE = 24.dp
    val DIVIDER_PADDING = 8.dp
    val HEADER_HEIGHT = 80.dp
    val LOGOUT_TOP_PADDING = 24.dp

    val BACKGROUND_COLOR = Color(0xFFE3F0FF) // Azul claro igual que fondo de AreasScreen
    val HEADER_COLOR = Color(0xFFFAFAFA) // Fondo blanco
    val ITEM_BG_SELECTED = Color(0xFFB3E5FC) // Celeste más intenso
    val ITEM_BG_NORMAL = Color.Transparent
    val BUTTON_COLOR = Color(0xFF1976D2) // Azul para acentos
    val LOGOUT_COLOR = Color(0xFFFF7043) // Coral suave
    val TEXT_COLOR = Color(0xFF1976D2) // Azul para texto
    val FONT_SIZE = 16.sp
    val HEADER_FONT_SIZE = 22.sp
    val ICON_COLOR = Color(0xFF1976D2) // Azul para iconos
    
    const val MENU_TESTS = "Tests"
    const val MENU_AREAS = "Áreas"
    const val MENU_STUDENTS = "Alumnos"
    const val MENU_PROFILE = "Perfil"
    const val MENU_CENTER = "Mi Centro"
    const val MENU_RESULTS = "Resultados"
    const val MENU_HELP = "Ayuda"
    const val MENU_LOGOUT = "Cerrar sesión"
}

data class MenuItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
    val isVisible: (String, Int) -> Boolean = { _, _ -> true }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDrawer(
    navController: NavController,
    id: Int,
    tipo: String,
    scope: CoroutineScope,
    drawerState: DrawerState,
    preferencesManager: PreferencesManager
) {
    val menuItems = listOf(
        MenuItem(
            label = MenuDrawerConstants.MENU_TESTS,
            route = "home_screen",
            icon = Icons.Default.Assignment,
            isVisible = { tipo, isOrientador -> 
                tipo == "Alumno" || tipo == "invitado" || isOrientador == 1 
            }
        ),
        MenuItem(
            label = MenuDrawerConstants.MENU_AREAS,
            route = "areas_screen",
            icon = Icons.Default.List
        ),
        MenuItem(
            label = MenuDrawerConstants.MENU_STUDENTS,
            route = "students_screen",
            icon = Icons.Default.Group,
            isVisible = { tipo, _ -> tipo == "Profesor" }
        ),
        MenuItem(
            label = MenuDrawerConstants.MENU_PROFILE,
            route = "profile_screen",
            icon = Icons.Default.Person
        ),
        MenuItem(
            label = MenuDrawerConstants.MENU_CENTER,
            route = "centros_screen",
            icon = Icons.Default.School
        ),
        MenuItem(
            label = MenuDrawerConstants.MENU_RESULTS,
            route = "results_screen",
            icon = Icons.Default.Analytics,
            isVisible = { tipo, _ -> tipo != "invitado" }
        ),
        MenuItem(
            label = MenuDrawerConstants.MENU_HELP,
            route = "help_screen",
            icon = Icons.Default.Info
        )
    )

    ModalDrawerSheet(
        modifier = Modifier
            .width(MenuDrawerConstants.DRAWER_WIDTH)
            .background(MenuDrawerConstants.BACKGROUND_COLOR)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(MenuDrawerConstants.BACKGROUND_COLOR)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MenuDrawerConstants.HEADER_HEIGHT)
                    .background(MenuDrawerConstants.HEADER_COLOR),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "EscolaVision",
                    fontSize = MenuDrawerConstants.HEADER_FONT_SIZE,
                    fontWeight = FontWeight.Bold,
                    color = MenuDrawerConstants.TEXT_COLOR,
                    modifier = Modifier.padding(start = MenuDrawerConstants.PADDING)
                )
            }
            Divider(
                color = Color.White.copy(alpha = 0.15f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = MenuDrawerConstants.DIVIDER_PADDING)
            )
            // Menu Items
            MenuItems(
                menuItems = menuItems,
                tipo = tipo,
                isOrientador = preferencesManager.getIsOrientador(),
                onItemClick = { route ->
                    navController.navigate(route)
                    scope.launch { drawerState.close() }
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            Divider(
                color = Color.White.copy(alpha = 0.15f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = MenuDrawerConstants.DIVIDER_PADDING)
            )
            // Logout Button
            LogoutButton(
                onLogout = {
                    preferencesManager.clearLogin()
                    navController.navigate("first_screen") {
                        popUpTo("home_screen") { inclusive = true }
                    }
                    scope.launch { drawerState.close() }
                }
            )
            Spacer(modifier = Modifier.height(MenuDrawerConstants.LOGOUT_TOP_PADDING))
        }
    }
}

@Composable
private fun MenuItems(
    menuItems: List<MenuItem>,
    tipo: String,
    isOrientador: Int,
    onItemClick: (String) -> Unit
) {
    menuItems.forEach { item ->
        if (item.isVisible(tipo, isOrientador)) {
            MenuButton(
                label = item.label,
                icon = item.icon,
                onClick = { onItemClick(item.route) }
            )
        }
    }
}

@Composable
private fun MenuButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        color = MenuDrawerConstants.ITEM_BG_NORMAL,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MenuDrawerConstants.PADDING, vertical = 2.dp)
            .height(48.dp)
            .background(MenuDrawerConstants.ITEM_BG_NORMAL)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
                .padding(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MenuDrawerConstants.ICON_COLOR,
                modifier = Modifier.size(MenuDrawerConstants.ICON_SIZE)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                fontSize = MenuDrawerConstants.FONT_SIZE,
                fontWeight = FontWeight.Medium,
                color = MenuDrawerConstants.TEXT_COLOR
            )
        }
    }
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MenuDrawerConstants.PADDING, vertical = 2.dp)
            .height(48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onLogout)
                .padding(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = MenuDrawerConstants.MENU_LOGOUT,
                tint = MenuDrawerConstants.LOGOUT_COLOR,
                modifier = Modifier.size(MenuDrawerConstants.ICON_SIZE)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = MenuDrawerConstants.MENU_LOGOUT,
                fontSize = MenuDrawerConstants.FONT_SIZE,
                fontWeight = FontWeight.Medium,
                color = MenuDrawerConstants.LOGOUT_COLOR
            )
        }
    }
}
