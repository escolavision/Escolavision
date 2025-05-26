/**
 * Pantalla de visualización detallada de resultados de test en EscolaVision.
 * 
 * Esta pantalla muestra el desglose detallado de los resultados de un test específico:
 * - Visualización de puntuaciones por área
 * - Descripción detallada de cada área evaluada
 * - Navegación contextual según la pantalla de origen
 * 
 * Características principales:
 * - Interfaz Material 3 con diseño de tarjetas
 * - Carga dinámica de información de áreas
 * - Sistema de navegación adaptativo
 * - Visualización detallada de resultados por área
 * - Integración con la API para obtener descripciones de áreas
 * 
 * La pantalla actúa como vista detallada de resultados,
 * permitiendo a los usuarios comprender en profundidad
 * su desempeño en cada área evaluada del test.
 */

package com.escolavision.testescolavision.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.escolavision.testescolavision.API.Area
import com.escolavision.testescolavision.API.AreaListResponse
import com.escolavision.testescolavision.PreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R

private object ResultTestScreenConstants {
    const val SCREEN_TITLE = "Resultados"
    const val NO_RESULTS_MESSAGE = "No hay resultados para mostrar."
    const val HOME_SCREEN_ROUTE = "home_screen"
    const val RESULTS_SCREEN_ROUTE = "results_screen"
    
    val TITLE_FONT_SIZE = 22.sp
    val RESULT_TITLE_FONT_SIZE = 18.sp
    val RESULT_DESCRIPTION_FONT_SIZE = 16.sp
    val ERROR_FONT_SIZE = 18.sp
    
    val CONTENT_PADDING = 16.dp
    val CARD_VERTICAL_PADDING = 8.dp
    val CARD_INNER_PADDING = 16.dp
    val CARD_CORNER_RADIUS = 18.dp
    val CARD_ELEVATION = 8.dp
    
    val CARD_BG_COLOR = Color(0xFFFAFAFA)
    val CARD_BORDER_COLOR = Color(0xFFB3E5FC)
    val NAME_COLOR = Color(0xFF333333)
    val DESC_COLOR = Color(0xFF263238)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultTestScreen(navController: NavController, resultados: List<Double>, pantallaAnterior: String) {
    var areas by remember { mutableStateOf<List<Area>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        loadAreas { loadedAreas ->
            areas = loadedAreas
            isLoading = false
        }
    }

    ResultTestScreenContent(
        navController = navController,
        pantallaAnterior = pantallaAnterior,
        isLoading = isLoading,
        areas = areas,
        resultados = resultados
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultTestScreenContent(
    navController: NavController,
    pantallaAnterior: String,
    isLoading: Boolean,
    areas: List<Area>,
    resultados: List<Double>
) {
    Scaffold(
        topBar = {
            ResultTestTopBar(
                navController = navController,
                pantallaAnterior = pantallaAnterior
            )
        },
        content = { paddingValues ->
            ResultTestContent(
                paddingValues = paddingValues,
                isLoading = isLoading,
                areas = areas,
                resultados = resultados
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultTestTopBar(
    navController: NavController,
    pantallaAnterior: String
) {
    TopAppBar(
        title = {
            Text(
                text = ResultTestScreenConstants.SCREEN_TITLE,
                fontSize = ResultTestScreenConstants.TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = ResultTestScreenConstants.NAME_COLOR
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = colorResource(id = R.color.fondoInicio)
        ),
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigate(
                        if (pantallaAnterior == ResultTestScreenConstants.HOME_SCREEN_ROUTE)
                            ResultTestScreenConstants.HOME_SCREEN_ROUTE
                        else
                            ResultTestScreenConstants.RESULTS_SCREEN_ROUTE
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = ResultTestScreenConstants.NAME_COLOR
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
private fun ResultTestContent(
    paddingValues: PaddingValues,
    isLoading: Boolean,
    areas: List<Area>,
    resultados: List<Double>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id =  R.color.fondoInicio))
            .padding(paddingValues)
    ) {
        when {
            isLoading -> LoadingIndicator()
            areas.size >= resultados.size -> ResultsList(areas = areas, resultados = resultados)
            else -> NoResultsMessage()
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = ResultTestScreenConstants.CARD_BORDER_COLOR)
    }
}

@Composable
private fun ResultsList(areas: List<Area>, resultados: List<Double>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(ResultTestScreenConstants.CONTENT_PADDING)
    ) {
        items(resultados.indices.toList()) { index ->
            if (areas.isNotEmpty() && areas.size > index) {
                ResultCard(
                    area = areas[index],
                    resultado = resultados[index]
                )
            }
        }
    }
}

@Composable
private fun ResultCard(area: Area, resultado: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ResultTestScreenConstants.CARD_VERTICAL_PADDING),
        colors = CardDefaults.cardColors(
            containerColor = ResultTestScreenConstants.CARD_BG_COLOR
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = ResultTestScreenConstants.CARD_ELEVATION
        ),
        shape = RoundedCornerShape(ResultTestScreenConstants.CARD_CORNER_RADIUS),
        border = BorderStroke(1.dp, ResultTestScreenConstants.CARD_BORDER_COLOR)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ResultTestScreenConstants.CARD_INNER_PADDING)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = area.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = ResultTestScreenConstants.RESULT_TITLE_FONT_SIZE,
                    color = ResultTestScreenConstants.NAME_COLOR,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = resultado.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = ResultTestScreenConstants.RESULT_TITLE_FONT_SIZE,
                    color = ResultTestScreenConstants.NAME_COLOR,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = area.descripción,
                fontSize = ResultTestScreenConstants.RESULT_DESCRIPTION_FONT_SIZE,
                color = ResultTestScreenConstants.DESC_COLOR
            )
        }
    }
}

@Composable
private fun NoResultsMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(ResultTestScreenConstants.CONTENT_PADDING)
                .fillMaxWidth(0.8f),
            colors = CardDefaults.cardColors(
                containerColor = ResultTestScreenConstants.CARD_BG_COLOR
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = ResultTestScreenConstants.CARD_ELEVATION
            ),
            shape = RoundedCornerShape(ResultTestScreenConstants.CARD_CORNER_RADIUS),
            border = BorderStroke(1.dp, Color.Red)
        ) {
            Text(
                text = ResultTestScreenConstants.NO_RESULTS_MESSAGE,
                color = Color.Red,
                fontSize = ResultTestScreenConstants.ERROR_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ResultTestScreenConstants.CARD_INNER_PADDING)
            )
        }
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
            onComplete(emptyList())
        }
    })
}

