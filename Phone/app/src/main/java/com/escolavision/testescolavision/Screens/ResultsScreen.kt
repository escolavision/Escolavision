/**
 * Pantalla de visualización de resultados de EscolaVision.
 * 
 * Esta pantalla permite ver y gestionar los resultados de los tests:
 * - Visualización de intentos de tests realizados
 * - Filtrado de resultados según tipo de usuario
 * - Gráficos de barras para visualización de datos
 * - Detalles específicos por intento
 * 
 * Características principales:
 * - Interfaz Material 3 con lista dinámica de resultados
 * - Sistema de actualización pull-to-refresh
 * - Gráficos interactivos por áreas
 * - Vista diferenciada para alumnos y profesores
 * - Navegación a detalles específicos de cada intento
 * 
 * La pantalla actúa como centro de análisis de resultados,
 * permitiendo tanto a alumnos como profesores revisar
 * el progreso y rendimiento en los tests realizados.
 */

package com.escolavision.testescolavision.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.API.Intento
import com.escolavision.testescolavision.API.IntentoListResponse
import com.escolavision.testescolavision.API.UsuariosListResponse
import com.escolavision.testescolavision.PreferencesManager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R

private object ResultsScreenConstants {
    const val SCREEN_TITLE = "Resultados"
    const val TEST_ID_PREFIX = "ID Test: "
    const val USER_PREFIX = "Usuario: "
    const val DATE_PREFIX = "Fecha: "
    const val RESULTS_LABEL = "Resultados:"
    const val LOADING_TEXT = "Cargando..."
    const val TEACHER_NOT_FOUND = "Profesor no encontrado"
    const val ERROR_FETCH_TEACHER = "Error al obtener el nombre del profesor"
    const val ERROR_NETWORK = "Error de red: "
    
    val TITLE_FONT_SIZE = 22.sp
    val TEST_ID_FONT_SIZE = 18.sp
    val CONTENT_FONT_SIZE = 15.sp
    val AREA_LABEL_FONT_SIZE = 14.sp
    
    val CONTENT_PADDING = 16.dp
    val ITEM_PADDING = 12.dp
    val CHART_PADDING = 16.dp
    val CHART_SPACING = 12.dp
    val CARD_ELEVATION = 8.dp
    val CARD_CORNER_RADIUS = 18.dp
    
    val BAR_WIDTH = 24.dp
    val BAR_SPACING = 12.dp
    val BAR_CORNER_RADIUS = 8.dp
    
    val CARD_BG_COLOR = Color(0xFFFAFAFA)
    val CARD_BORDER_COLOR = Color(0xFFB3E5FC)
    val NAME_COLOR = Color(0xFF333333)
    val DESC_COLOR = Color(0xFF263238)
    val CHART_COLOR = Color(0xFF1976D2)
    val CHART_LABEL_COLOR = Color(0xFF757575)
    
    val AREAS = listOf("Área\n1", "Área\n2", "Área\n3", "Área\n4", "Área\n5")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(navController: NavController) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val (id, tipo) = preferencesManager.getLoginData()
    val id_centro = preferencesManager.getCenterData()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var intentos by remember { mutableStateOf<List<Intento>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val esAlumno = tipo == "Alumno"

    LaunchedEffect(Unit) {
        loadIntentos(id_centro, id.toString(), esAlumno) { loadedIntentos ->
            intentos = loadedIntentos
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
            ResultsScreenContent(
                drawerState = drawerState,
                scope = scope,
                isLoading = isLoading,
                intentos = intentos,
                esAlumno = esAlumno,
                id_centro = id_centro,
                navController = navController,
                onRefresh = {
                    isLoading = true
                    loadIntentos(id_centro, id.toString(), esAlumno) { loadedIntentos ->
                        intentos = loadedIntentos
                        isLoading = false
                    }
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultsScreenContent(
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope,
    isLoading: Boolean,
    intentos: List<Intento>,
    esAlumno: Boolean,
    id_centro: String,
    navController: NavController,
    onRefresh: () -> Unit
) {
    Scaffold(
        topBar = {
            ResultsTopBar(
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        content = { paddingValues ->
            ResultsContent(
                paddingValues = paddingValues,
                isLoading = isLoading,
                intentos = intentos,
                esAlumno = esAlumno,
                id_centro = id_centro,
                navController = navController,
                onRefresh = onRefresh
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultsTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = ResultsScreenConstants.SCREEN_TITLE,
                fontSize = ResultsScreenConstants.TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = ResultsScreenConstants.NAME_COLOR
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
                    tint = ResultsScreenConstants.NAME_COLOR
                )
            }
        },
        actions = {
            Spacer(modifier = Modifier.width(48.dp))
        }
    )
}

@Composable
private fun ResultsContent(
    paddingValues: PaddingValues,
    isLoading: Boolean,
    intentos: List<Intento>,
    esAlumno: Boolean,
    id_centro: String,
    navController: NavController,
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ResultsScreenConstants.CONTENT_PADDING)
            ) {
                items(intentos) { intento ->
                    IntentoItem(
                        intento = intento,
                        esAlumno = esAlumno,
                        id_centro = id_centro,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun IntentoItem(
    intento: Intento,
    esAlumno: Boolean,
    id_centro: String,
    navController: NavController
) {
    var alumnoNombre by remember { mutableStateOf(ResultsScreenConstants.LOADING_TEXT) }

    if (!esAlumno) {
        LaunchedEffect(intento.idusuario) {
            fetchAlumnoName(intento.idusuario, id_centro) { nombre ->
                alumnoNombre = nombre
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ResultsScreenConstants.ITEM_PADDING)
            .clickable {
                navController.navigate("result_test_screen/${intento.resultados}/results_screen")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = ResultsScreenConstants.CARD_ELEVATION),
        colors = CardDefaults.cardColors(containerColor = ResultsScreenConstants.CARD_BG_COLOR),
        shape = RoundedCornerShape(ResultsScreenConstants.CARD_CORNER_RADIUS),
        border = BorderStroke(1.dp, ResultsScreenConstants.CARD_BORDER_COLOR)
    ) {
        Column(modifier = Modifier.padding(ResultsScreenConstants.CONTENT_PADDING)) {
            TestInfo(
                testId = intento.idtest,
                alumnoNombre = alumnoNombre,
                fecha = intento.fecha,
                hora = intento.hora,
                esAlumno = esAlumno
            )
            Spacer(modifier = Modifier.height(ResultsScreenConstants.ITEM_PADDING))
            ResultsChart(resultados = intento.resultados)
        }
    }
}

@Composable
private fun TestInfo(
    testId: Int,
    alumnoNombre: String,
    fecha: String,
    hora: String,
    esAlumno: Boolean
) {
    Text(
        text = "${ResultsScreenConstants.TEST_ID_PREFIX}$testId",
        fontWeight = FontWeight.Bold,
        fontSize = ResultsScreenConstants.TEST_ID_FONT_SIZE,
        color = ResultsScreenConstants.NAME_COLOR
    )
    if (!esAlumno) {
        Text(
            text = "${ResultsScreenConstants.USER_PREFIX}$alumnoNombre",
            fontSize = ResultsScreenConstants.CONTENT_FONT_SIZE,
            color = ResultsScreenConstants.DESC_COLOR
        )
    }
    Text(
        text = "${ResultsScreenConstants.DATE_PREFIX}$fecha $hora",
        fontSize = ResultsScreenConstants.CONTENT_FONT_SIZE,
        color = ResultsScreenConstants.DESC_COLOR
    )
    Spacer(modifier = Modifier.height(ResultsScreenConstants.ITEM_PADDING))
    Text(
        text = ResultsScreenConstants.RESULTS_LABEL,
        fontWeight = FontWeight.Bold,
        fontSize = ResultsScreenConstants.CONTENT_FONT_SIZE,
        color = ResultsScreenConstants.NAME_COLOR
    )
}

@Composable
fun ResultsChart(resultados: String) {
    val data = resultados.split(";").mapNotNull { it.toFloatOrNull()?.toInt() }
    val maxValue = data.maxOrNull() ?: 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ResultsScreenConstants.CHART_PADDING)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { value ->
                    val barHeight = (value.toFloat() / maxValue) * 100
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = value.toString(),
                            color = ResultsScreenConstants.CHART_COLOR,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Box(
                            modifier = Modifier
                                .width(ResultsScreenConstants.BAR_WIDTH)
                                .height(barHeight.dp)
                                .background(
                                    color = ResultsScreenConstants.CHART_COLOR,
                                    shape = RoundedCornerShape(
                                        topStart = ResultsScreenConstants.BAR_CORNER_RADIUS,
                                        topEnd = ResultsScreenConstants.BAR_CORNER_RADIUS
                                    )
                                )
                        )
                    }
                    if (data.last() != value) {
                        Spacer(modifier = Modifier.width(ResultsScreenConstants.BAR_SPACING))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(ResultsScreenConstants.CHART_SPACING))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ResultsScreenConstants.AREAS.forEach { area ->
                    Text(
                        text = area,
                        fontSize = ResultsScreenConstants.AREA_LABEL_FONT_SIZE,
                        color = ResultsScreenConstants.CHART_LABEL_COLOR,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        modifier = Modifier
                            .width(ResultsScreenConstants.BAR_WIDTH + ResultsScreenConstants.BAR_SPACING)
                            .padding(horizontal = 2.dp)
                    )
                }
            }
        }
    }
}

private fun loadIntentos(
    id_centro: String,
    userId: String,
    esAlumno: Boolean,
    onComplete: (List<Intento>) -> Unit
) {
    RetrofitClient.api.getIntentos(id_centro = id_centro).enqueue(object : Callback<IntentoListResponse> {
        override fun onResponse(call: Call<IntentoListResponse>, response: Response<IntentoListResponse>) {
            if (response.isSuccessful) {
                val allIntentos = response.body()?.intentos ?: emptyList()
                val filteredIntentos = if (esAlumno) {
                    allIntentos.filter { ""+it.idusuario == userId }
                } else {
                    allIntentos
                }
                onComplete(filteredIntentos)
            } else {
                onComplete(emptyList())
            }
        }

        override fun onFailure(call: Call<IntentoListResponse>, t: Throwable) {
            onComplete(emptyList())
        }
    })
}

private fun fetchAlumnoName(alumnoId: Int, id_centro: String, callback: (String) -> Unit) {
    RetrofitClient.api.getUsuarioData(id_centro = id_centro).enqueue(object : Callback<UsuariosListResponse> {
        override fun onResponse(call: Call<UsuariosListResponse>, response: Response<UsuariosListResponse>) {
            if (response.isSuccessful) {
                val alumnosList = response.body()?.usuarios ?: emptyList()
                val alumno = alumnosList.find { it.id == alumnoId }
                callback(alumno?.nombre ?: ResultsScreenConstants.TEACHER_NOT_FOUND)
            } else {
                callback(ResultsScreenConstants.ERROR_FETCH_TEACHER)
            }
        }

        override fun onFailure(call: Call<UsuariosListResponse>, t: Throwable) {
            callback("${ResultsScreenConstants.ERROR_NETWORK}${t.message}")
        }
    })
}


