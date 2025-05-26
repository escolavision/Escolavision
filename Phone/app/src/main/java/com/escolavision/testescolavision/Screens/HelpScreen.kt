/**
 * Pantalla de ayuda y soporte de EscolaVision.
 * 
 * Esta pantalla proporciona información completa de ayuda y soporte para los usuarios, incluyendo:
 * - Preguntas frecuentes (FAQ)
 * - Instrucciones de uso
 * - Información de contacto
 * - Detalles sobre la aplicación
 * - Política de privacidad
 * 
 * Características principales:
 * - Interfaz Material 3 con diseño estructurado por secciones
 * - Menú lateral para navegación
 * - Información organizada y fácil de leer
 * - Acceso a recursos de ayuda
 * 
 * La pantalla sirve como centro de recursos y soporte para todos los usuarios
 * de la aplicación, proporcionando respuestas a dudas comunes y
 * facilitando el acceso a la información de contacto y soporte técnico.
 */

package com.escolavision.testescolavision.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.PreferencesManager
import kotlinx.coroutines.launch
import com.escolavision.testescolavision.R

private object HelpScreenConstants {
    const val SCREEN_TITLE = "Ayuda"
    const val HELP_SUPPORT_TITLE = "Ayuda y Soporte"
    const val FAQ_TITLE = "Preguntas Frecuentes"
    const val CONTACT_TITLE = "Contacto"
    const val ABOUT_TITLE = "Acerca de"
    const val PRIVACY_TITLE = "Política de Privacidad"
    const val APP_NAME = "Escolavision App"
    const val APP_VERSION = "Versión 1.0.0"
    const val DEVELOPER_INFO = "Desarrollado por Escolavision Team"
    
    const val PASSWORD_QUESTION = "1. ¿Cómo puedo cambiar mi contraseña?"
    const val PASSWORD_ANSWER = "Para cambiar tu contraseña, dirígete a la sección de Perfil y selecciona la opción 'Cambiar Contraseña'. Deberás introducir tu contraseña actual y la nueva contraseña dos veces para confirmar el cambio."
    
    const val RESULTS_QUESTION = "2. ¿Cómo puedo ver mis resultados?"
    const val RESULTS_ANSWER = "Para ver tus resultados, accede a la sección 'Resultados' desde el menú principal. Allí encontrarás un historial completo de todos tus tests realizados, organizados por fecha y con detalles específicos de cada área evaluada."
    
    const val CONTACT_INFO = "Si necesitas ayuda adicional o tienes alguna consulta específica, puedes contactar con nuestro equipo de soporte a través del correo electrónico escolavisionhlanz@gmail.com."
    
    const val PRIVACY_INFO = "En Escolavision, nos tomamos muy en serio la privacidad de nuestros usuarios. Todos los datos recopilados se utilizan únicamente para mejorar la experiencia de evaluación y seguimiento. No compartimos información personal con terceros y cumplimos con todas las normativas de protección de datos vigentes."
    
    val TITLE_FONT_SIZE = 22.sp
    val SECTION_TITLE_FONT_SIZE = 20.sp
    val SUBSECTION_TITLE_FONT_SIZE = 18.sp
    val QUESTION_FONT_SIZE = 16.sp
    val CONTENT_FONT_SIZE = 14.sp
    
    val PADDING = 16.dp
    val SMALL_PADDING = 8.dp
    val SECTION_SPACING = 20.dp
    val CARD_CORNER_RADIUS = 18.dp
    val CARD_ELEVATION = 6.dp
    val CARD_BG_COLOR = Color(0xFFFAFAFA)
    val NAME_COLOR = Color(0xFF333333)
    val DESC_COLOR = Color(0xFF263238)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
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
            HelpScreenContent(
                drawerState = drawerState,
                scope = scope
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HelpScreenContent(
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    Scaffold(
        topBar = {
            HelpTopBar(
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        content = { paddingValues ->
            HelpContent(paddingValues = paddingValues)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HelpTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = HelpScreenConstants.SCREEN_TITLE,
                fontSize = HelpScreenConstants.TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = HelpScreenConstants.NAME_COLOR
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
                    tint = HelpScreenConstants.NAME_COLOR
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
private fun HelpContent(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.fondoInicio))
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        HelpSectionCard {
            HelpSupportSection()
        }
        Spacer(modifier = Modifier.height(HelpScreenConstants.SECTION_SPACING))
        HelpSectionCard {
            FAQSection()
        }
        Spacer(modifier = Modifier.height(HelpScreenConstants.SECTION_SPACING))
        HelpSectionCard {
            ContactSection()
        }
        Spacer(modifier = Modifier.height(HelpScreenConstants.SECTION_SPACING))
        HelpSectionCard {
            AboutSection()
        }
        Spacer(modifier = Modifier.height(HelpScreenConstants.SECTION_SPACING))
        HelpSectionCard {
            PrivacySection()
        }
    }
}

@Composable
private fun HelpSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HelpScreenConstants.PADDING),
        colors = CardDefaults.cardColors(containerColor = HelpScreenConstants.CARD_BG_COLOR),
        elevation = CardDefaults.cardElevation(defaultElevation = HelpScreenConstants.CARD_ELEVATION),
        shape = RoundedCornerShape(HelpScreenConstants.CARD_CORNER_RADIUS)
    ) {
        Column(modifier = Modifier.padding(HelpScreenConstants.PADDING), content = content)
    }
}

@Composable
private fun HelpSupportSection() {
    SectionTitle(title = HelpScreenConstants.HELP_SUPPORT_TITLE)
}

@Composable
private fun FAQSection() {
    SectionTitle(title = HelpScreenConstants.FAQ_TITLE)
    FAQItem(
        question = HelpScreenConstants.PASSWORD_QUESTION,
        answer = HelpScreenConstants.PASSWORD_ANSWER
    )
    FAQItem(
        question = HelpScreenConstants.RESULTS_QUESTION,
        answer = HelpScreenConstants.RESULTS_ANSWER
    )
}

@Composable
private fun ContactSection() {
    SectionTitle(title = HelpScreenConstants.CONTACT_TITLE)
    ContentText(text = HelpScreenConstants.CONTACT_INFO)
}

@Composable
private fun AboutSection() {
    SectionTitle(title = HelpScreenConstants.ABOUT_TITLE)
    ContentText(
        text = HelpScreenConstants.APP_NAME,
        fontSize = HelpScreenConstants.SUBSECTION_TITLE_FONT_SIZE,
        fontWeight = FontWeight.Bold
    )
    ContentText(text = HelpScreenConstants.APP_VERSION)
    ContentText(text = HelpScreenConstants.DEVELOPER_INFO)
}

@Composable
private fun PrivacySection() {
    SectionTitle(title = HelpScreenConstants.PRIVACY_TITLE)
    ContentText(text = HelpScreenConstants.PRIVACY_INFO)
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = HelpScreenConstants.SECTION_TITLE_FONT_SIZE,
        fontWeight = FontWeight.Bold,
        color = HelpScreenConstants.NAME_COLOR,
        modifier = Modifier.padding(bottom = HelpScreenConstants.SMALL_PADDING)
    )
}

@Composable
private fun FAQItem(question: String, answer: String) {
    Text(
        text = question,
        fontSize = HelpScreenConstants.QUESTION_FONT_SIZE,
        color = HelpScreenConstants.NAME_COLOR,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 2.dp)
    )
    ContentText(text = answer)
}

@Composable
private fun ContentText(
    text: String,
    fontSize: TextUnit = HelpScreenConstants.CONTENT_FONT_SIZE,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = HelpScreenConstants.DESC_COLOR,
        modifier = Modifier.padding(bottom = HelpScreenConstants.SMALL_PADDING)
    )
}