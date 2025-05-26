/**
 * Pantalla de información detallada del centro educativo en EscolaVision.
 * 
 * Esta pantalla muestra toda la información relevante del centro educativo, organizada en tres secciones:
 * - Datos generales del centro (código, denominaciones)
 * - Información de contacto (teléfonos)
 * - Datos de localización (dirección, localidad, provincia)
 * 
 * Características principales:
 * - Interfaz Material 3 con cards organizadas por categorías
 * - Menú lateral para navegación
 * - Carga dinámica de datos desde la API
 * - Manejo de estados de carga
 * - Visualización estructurada de la información
 * 
 * La pantalla proporciona una vista completa y organizada de todos los datos
 * relevantes del centro educativo al que pertenece el usuario actual.
 */

package com.escolavision.testescolavision.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.navigation.NavController
import com.escolavision.testescolavision.API.CentroCompleto
import com.escolavision.testescolavision.API.CentroResponse
import com.escolavision.testescolavision.PreferencesManager
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R

private object CentrosScreenConstants {
    const val SCREEN_TITLE = "Mi Centro"
    const val CENTER_DATA_TITLE = "Datos del centro"
    const val CONTACT_DATA_TITLE = "Datos de contacto"
    const val LOCATION_TITLE = "Localización"
    const val CENTER_CODE_TITLE = "Código del Centro"
    const val GENERIC_NAME_TITLE = "Denominación genérica"
    const val CENTER_NAME_TITLE = "Denominación del centro"
    const val ADDRESS_TITLE = "Dirección"
    const val CITY_TITLE = "Localidad"
    const val PROVINCE_TITLE = "Provincia"
    const val PHONE_TITLE = "Teléfono"
    const val SECONDARY_PHONE_TITLE = "Teléfono secundario"
    const val NO_DATA = "-"
    
    val TITLE_FONT_SIZE = 22.sp
    val SECTION_TITLE_FONT_SIZE = 18.sp
    val CONTENT_FONT_SIZE = 15.sp
    
    val CARD_ELEVATION = 8.dp
    val CARD_CORNER_RADIUS = 18.dp
    val HORIZONTAL_PADDING = 16.dp
    val VERTICAL_PADDING = 16.dp
    val SECTION_SPACING = 24.dp
    val DIVIDER_PADDING = 12.dp
    
    val CARD_BG_COLOR = Color(0xFFFAFAFA)
    val CARD_BORDER_COLOR = Color(0xFFB3E5FC)
    val NAME_COLOR = Color(0xFF333333)
    val DESC_COLOR = Color(0xFF263238)
    val SECTION_TITLE_COLOR = Color(0xFF1976D2)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentrosScreen(navController: NavController) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val (id, tipo) = preferencesManager.getLoginData()
    val idCentro = preferencesManager.getCenterData()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var centro by remember { mutableStateOf<CentroCompleto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(idCentro) {
        loadCentro(idCentro) { newCentro ->
            centro = newCentro
            isLoading = false
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
            CentrosScreenContent(
                drawerState = drawerState,
                scope = scope,
                centro = centro,
                isLoading = isLoading
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CentrosScreenContent(
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope,
    centro: CentroCompleto?,
    isLoading: Boolean
) {
    Scaffold(
        topBar = {
            CentrosTopBar(
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        content = { paddingValues ->
            CentrosContent(
                paddingValues = paddingValues,
                centro = centro,
                isLoading = isLoading
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CentrosTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = CentrosScreenConstants.SCREEN_TITLE,
                fontSize = CentrosScreenConstants.TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = CentrosScreenConstants.NAME_COLOR
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
                    tint = CentrosScreenConstants.NAME_COLOR
                )
            }
        },
        actions = {
            // Espacio para centrar el título
            Spacer(modifier = Modifier.width(48.dp))
        }
    )
}

@Composable
private fun CentrosContent(
    paddingValues: PaddingValues,
    centro: CentroCompleto?,
    isLoading: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.fondoInicio))
            .padding(paddingValues)
    ) {
        if (isLoading) {
            LoadingIndicator()
        } else {
            centro?.let { data ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = CentrosScreenConstants.HORIZONTAL_PADDING)
                        .padding(top = CentrosScreenConstants.VERTICAL_PADDING)
                        .verticalScroll(rememberScrollState())
                ) {
                    CentroCardDatosCentro(data = data)
                    Spacer(modifier = Modifier.height(CentrosScreenConstants.SECTION_SPACING))
                    CentroCardDatosContacto(data = data)
                    Spacer(modifier = Modifier.height(CentrosScreenConstants.SECTION_SPACING))
                    CentroCardLocalizacion(data = data)
                    Spacer(modifier = Modifier.height(CentrosScreenConstants.SECTION_SPACING))
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(CentrosScreenConstants.HORIZONTAL_PADDING)
        )
    }
}

@Composable
fun CentroCardDatosCentro(data: CentroCompleto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = CentrosScreenConstants.CARD_ELEVATION),
        colors = CardDefaults.cardColors(containerColor = CentrosScreenConstants.CARD_BG_COLOR),
        shape = RoundedCornerShape(CentrosScreenConstants.CARD_CORNER_RADIUS),
        border = BorderStroke(1.dp, CentrosScreenConstants.CARD_BORDER_COLOR)
    ) {
        Column(
            modifier = Modifier.padding(CentrosScreenConstants.VERTICAL_PADDING)
        ) {
            Text(
                text = CentrosScreenConstants.CENTER_DATA_TITLE,
                fontSize = CentrosScreenConstants.SECTION_TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                color = CentrosScreenConstants.SECTION_TITLE_COLOR,
                modifier = Modifier.padding(bottom = 16.dp, start = 16.dp)
            )
            InfoSection(
                title = CentrosScreenConstants.CENTER_CODE_TITLE,
                content = data.codigo
            )
            InfoSection(
                title = CentrosScreenConstants.GENERIC_NAME_TITLE,
                content = data.denominacion_generica
            )
            InfoSection(
                title = CentrosScreenConstants.CENTER_NAME_TITLE,
                content = data.denominacion_especifica,
                showDivider = false
            )
        }
    }
}

@Composable
fun CentroCardDatosContacto(data: CentroCompleto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = CentrosScreenConstants.CARD_ELEVATION),
        colors = CardDefaults.cardColors(containerColor = CentrosScreenConstants.CARD_BG_COLOR),
        shape = RoundedCornerShape(CentrosScreenConstants.CARD_CORNER_RADIUS),
        border = BorderStroke(1.dp, CentrosScreenConstants.CARD_BORDER_COLOR)
    ) {
        Column(
            modifier = Modifier.padding(CentrosScreenConstants.VERTICAL_PADDING)
        ) {
            Text(
                text = CentrosScreenConstants.CONTACT_DATA_TITLE,
                fontSize = CentrosScreenConstants.SECTION_TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                color = CentrosScreenConstants.SECTION_TITLE_COLOR,
                modifier = Modifier.padding(bottom = 16.dp, start = 16.dp)
            )
            InfoSection(
                title = CentrosScreenConstants.PHONE_TITLE,
                content = data.telefono
            )
            InfoSection(
                title = CentrosScreenConstants.SECONDARY_PHONE_TITLE,
                content = data.telefono_secundario ?: CentrosScreenConstants.NO_DATA,
                showDivider = false
            )
        }
    }
}

@Composable
fun CentroCardLocalizacion(data: CentroCompleto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = CentrosScreenConstants.CARD_ELEVATION),
        colors = CardDefaults.cardColors(containerColor = CentrosScreenConstants.CARD_BG_COLOR),
        shape = RoundedCornerShape(CentrosScreenConstants.CARD_CORNER_RADIUS),
        border = BorderStroke(1.dp, CentrosScreenConstants.CARD_BORDER_COLOR)
    ) {
        Column(
            modifier = Modifier.padding(CentrosScreenConstants.VERTICAL_PADDING)
        ) {
            Text(
                text = CentrosScreenConstants.LOCATION_TITLE,
                fontSize = CentrosScreenConstants.SECTION_TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                color = CentrosScreenConstants.SECTION_TITLE_COLOR,
                modifier = Modifier.padding(bottom = 16.dp, start = 16.dp)
            )
            InfoSection(
                title = CentrosScreenConstants.ADDRESS_TITLE,
                content = data.domicilio
            )
            InfoSection(
                title = CentrosScreenConstants.CITY_TITLE,
                content = "${data.localidad} (${data.codigo_postal})"
            )
            InfoSection(
                title = CentrosScreenConstants.PROVINCE_TITLE,
                content = "${data.provincia} (${data.comunidad_autonoma})",
                showDivider = false
            )
        }
    }
}

@Composable
private fun InfoSection(title: String, content: String, showDivider: Boolean = true) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            fontSize = CentrosScreenConstants.CONTENT_FONT_SIZE,
            fontWeight = FontWeight.Medium,
            color = CentrosScreenConstants.DESC_COLOR
        )
        Text(
            text = content,
            fontSize = CentrosScreenConstants.CONTENT_FONT_SIZE,
            color = CentrosScreenConstants.NAME_COLOR,
            modifier = Modifier.padding(top = 4.dp)
        )
        if (showDivider) {
            Divider(
                color = CentrosScreenConstants.CARD_BORDER_COLOR.copy(alpha = 0.5f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = CentrosScreenConstants.DIVIDER_PADDING)
            )
        }
    }
}

private fun loadCentro(idCentro: String, onComplete: (CentroCompleto?) -> Unit) {
    RetrofitClient.api.getCentro(id = idCentro).enqueue(object : Callback<CentroResponse> {
        override fun onResponse(call: Call<CentroResponse>, response: Response<CentroResponse>) {
            if (response.isSuccessful) {
                onComplete(response.body()?.centros?.firstOrNull())
            } else {
                onComplete(null)
            }
        }

        override fun onFailure(call: Call<CentroResponse>, t: Throwable) {
            onComplete(null)
        }
    })
}