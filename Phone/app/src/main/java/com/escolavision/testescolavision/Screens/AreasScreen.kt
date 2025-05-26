/**
 * Pantalla de visualización de áreas de evaluación de EscolaVision.
 * 
 * Esta pantalla muestra una lista interactiva de todas las áreas de evaluación
 * disponibles en la aplicación, incluyendo:
 * - Visualización de logos de áreas
 * - Descripción detallada de cada área
 * - Interfaz con pull-to-refresh para actualización
 * 
 * Características principales:
 * - Diseño Material 3 con cards personalizadas
 * - Menú lateral para navegación
 * - Carga y decodificación de imágenes Base64
 * - Sistema de actualización de datos en tiempo real
 * - Manejo de estados de carga
 * 
 * La pantalla proporciona una vista general de las diferentes áreas
 * de evaluación disponibles en el sistema EscolaVision, permitiendo
 * a los usuarios comprender el alcance de cada área evaluada.
 */

package com.escolavision.testescolavision.Screens

// Importaciones necesarias para la funcionalidad de la pantalla
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.API.Area
import com.escolavision.testescolavision.API.AreaListResponse
import com.escolavision.testescolavision.PreferencesManager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R

private object AreasScreenConstants {
    const val SCREEN_TITLE = "Áreas"
    const val ERROR_LOADING_AREAS = "Error al cargar áreas: %s"
    
    val TITLE_FONT_SIZE = 22.sp
    val AREA_NAME_FONT_SIZE = 18.sp
    val AREA_DESCRIPTION_FONT_SIZE = 14.sp
    
    val CONTENT_PADDING = 16.dp
    val VERTICAL_PADDING = 10.dp
    val LOGO_SIZE = 70.dp
    val SMALL_SPACING = 6.dp
    val CARD_ELEVATION = 6.dp
    val CARD_CORNER_RADIUS = 18.dp
    val CARD_BG_COLOR = Color(0xFFFAFAFA)
    val CARD_BORDER_COLOR = Color(0xFFB3E5FC)
    val NAME_COLOR = Color(0xFF333333)
    val DESC_COLOR = Color(0xFF263238)
}

// Pantalla principal de Áreas que muestra una lista de áreas disponibles
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreasScreen(navController: NavController) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val (id, tipo) = preferencesManager.getLoginData()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var areas by remember { mutableStateOf<List<Area>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        loadAreas { newAreas ->
            areas = newAreas
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
            AreasScreenContent(
                drawerState = drawerState,
                scope = scope,
                areas = areas,
                isLoading = isLoading,
                onRefresh = {
                    loadAreas { newAreas ->
                        areas = newAreas
                        isLoading = false
                    }
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AreasScreenContent(
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope,
    areas: List<Area>,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Scaffold(
        topBar = {
            AreasTopBar(
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        containerColor = colorResource(id = R.color.fondoInicio),
        content = { paddingValues ->
            AreasContent(
                paddingValues = paddingValues,
                areas = areas,
                isLoading = isLoading,
                onRefresh = onRefresh
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AreasTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = AreasScreenConstants.SCREEN_TITLE,
                fontSize = AreasScreenConstants.TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = AreasScreenConstants.NAME_COLOR
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
                    tint = AreasScreenConstants.NAME_COLOR
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
private fun AreasContent(
    paddingValues: PaddingValues,
    areas: List<Area>,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.fondoInicio))
            .padding(paddingValues)
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isLoading),
            onRefresh = onRefresh
        ) {
            AreasList(areas = areas)
        }
    }
}

@Composable
private fun AreasList(areas: List<Area>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(AreasScreenConstants.CONTENT_PADDING),
        verticalArrangement = Arrangement.spacedBy(AreasScreenConstants.VERTICAL_PADDING)
    ) {
        items(areas) { area ->
            AreaItem(area = area)
        }
    }
}

@Composable
fun AreaItem(area: Area) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = AreasScreenConstants.CARD_BG_COLOR),
        elevation = CardDefaults.cardElevation(AreasScreenConstants.CARD_ELEVATION),
        shape = RoundedCornerShape(AreasScreenConstants.CARD_CORNER_RADIUS),
        border = BorderStroke(1.dp, AreasScreenConstants.CARD_BORDER_COLOR)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AreasScreenConstants.CONTENT_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AreaLogo(logo = area.logo)
            Spacer(modifier = Modifier.width(18.dp))
            AreaInfo(
                name = area.nombre,
                description = area.descripción
            )
        }
    }
}

@Composable
private fun AreaLogo(logo: String) {
    val decodedString = Base64.decode(logo, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    val imageBitmap: ImageBitmap = bitmap.asImageBitmap()

    Image(
        bitmap = imageBitmap,
        contentDescription = null,
        modifier = Modifier
            .size(AreasScreenConstants.LOGO_SIZE)
            .padding(end = 0.dp)
    )
}

@Composable
private fun AreaInfo(name: String, description: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            fontSize = AreasScreenConstants.AREA_NAME_FONT_SIZE,
            color = AreasScreenConstants.NAME_COLOR
        )
        Spacer(modifier = Modifier.height(AreasScreenConstants.SMALL_SPACING))
        Text(
            text = description,
            fontSize = AreasScreenConstants.AREA_DESCRIPTION_FONT_SIZE,
            color = AreasScreenConstants.DESC_COLOR,
            textAlign = TextAlign.Justify
        )
    }
}

private fun loadAreas(onComplete: (List<Area>) -> Unit) {
    RetrofitClient.api.getAreas().enqueue(object : Callback<AreaListResponse> {
        override fun onResponse(call: Call<AreaListResponse>, response: Response<AreaListResponse>) {
            if (response.isSuccessful) {
                onComplete(response.body()?.areas ?: emptyList())
            } else {
                onComplete(emptyList())
            }
        }

        override fun onFailure(call: Call<AreaListResponse>, t: Throwable) {
            Log.e("AreasScreen", String.format(AreasScreenConstants.ERROR_LOADING_AREAS, t.message))
            onComplete(emptyList())
        }
    })
}
