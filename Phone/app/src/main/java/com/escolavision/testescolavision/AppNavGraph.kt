package com.escolavision.testescolavision

import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.escolavision.testescolavision.Screens.*

private object NavigationConstants {
    // Rutas de navegación
    const val FIRST_SCREEN = "first_screen"
    const val HOME_SCREEN = "home_screen"
    const val PROFILE_SCREEN = "profile_screen"
    const val RESULTS_SCREEN = "results_screen"
    const val SETTINGS_SCREEN = "settings_screen"
    const val HELP_SCREEN = "help_screen"
    const val ABOUT_SCREEN = "about_screen"
    const val TEST_DETAIL_SCREEN = "test_detail_screen/{id}"
    const val LOGIN_SCREEN = "login_screen"
    const val REGISTER_SCREEN = "register_screen"
    const val AREAS_SCREEN = "areas_screen"
    const val STUDENTS_SCREEN = "students_screen"
    const val RESULT_TEST_SCREEN = "result_test_screen/{resultados}/{pantallaAnterior}"
    const val CENTROS_SCREEN = "centros_screen"

    // Parámetros de navegación
    const val TEST_ID_PARAM = "id"
    const val RESULTADOS_PARAM = "resultados"
    const val PANTALLA_ANTERIOR_PARAM = "pantallaAnterior"
}

@Composable
fun NavigationComponent(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationConstants.FIRST_SCREEN
    ) {
        // Pantalla principal
        composable(NavigationConstants.HOME_SCREEN) {
            HomeScreen(navController)
            handleBackNavigation(navController, NavigationConstants.FIRST_SCREEN)
        }

        // Pantalla de perfil
        composable(NavigationConstants.PROFILE_SCREEN) {
            ProfileScreen(navController)
            handleBackNavigation(navController, NavigationConstants.FIRST_SCREEN)
        }

        // Pantalla de resultados
        composable(NavigationConstants.RESULTS_SCREEN) {
            ResultsScreen(navController)
        }

        // Pantalla de configuración
        composable(NavigationConstants.SETTINGS_SCREEN) {
            SettingsScreen(navController)
        }

        // Pantalla de ayuda
        composable(NavigationConstants.HELP_SCREEN) {
            HelpScreen(navController)
        }

        // Pantalla de información
        composable(NavigationConstants.ABOUT_SCREEN) {
            AboutScreen(navController)
        }

        // Pantalla de detalle de test
        composable(NavigationConstants.TEST_DETAIL_SCREEN) { backStackEntry ->
            val testId = backStackEntry.arguments?.getString(NavigationConstants.TEST_ID_PARAM) ?: "0"
            TestDetailScreen(navController, testId)
        }

        // Pantalla inicial
        composable(NavigationConstants.FIRST_SCREEN) {
            FirstScreen(navController)
        }

        // Pantalla de login
        composable(NavigationConstants.LOGIN_SCREEN) {
            LoginScreen(navController)
        }

        // Pantalla de registro
        composable(NavigationConstants.REGISTER_SCREEN) {
            RegisterScreen(navController)
        }

        // Pantalla de áreas
        composable(NavigationConstants.AREAS_SCREEN) {
            AreasScreen(navController)
        }

        // Pantalla de estudiantes
        composable(NavigationConstants.STUDENTS_SCREEN) {
            StudentsScreen(navController)
        }

        // Pantalla de resultados de test
        composable(NavigationConstants.RESULT_TEST_SCREEN) { backStackEntry ->
            val resultadosString = backStackEntry.arguments?.getString(NavigationConstants.RESULTADOS_PARAM) ?: ""
            val resultados = resultadosString.split(";").mapNotNull { it.toDoubleOrNull() }
            val pantallaAnterior = backStackEntry.arguments?.getString(NavigationConstants.PANTALLA_ANTERIOR_PARAM) ?: ""
            ResultTestScreen(navController, resultados, pantallaAnterior)
        }

        // Pantalla de centros
        composable(NavigationConstants.CENTROS_SCREEN) {
            CentrosScreen(navController)
        }
    }
}

@Composable
private fun handleBackNavigation(navController: NavHostController, destination: String) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    onBackPressedDispatcher?.addCallback {
        navController.navigate(destination) {
            popUpTo(destination) { inclusive = true }
        }
    }
}
