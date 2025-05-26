/**
 * Pantalla de realización de tests de EscolaVision.
 * 
 * Esta pantalla permite a los usuarios realizar tests específicos:
 * - Visualización de preguntas del test
 * - Sistema de respuestas mediante sliders (0-10)
 * - Cálculo automático de resultados por áreas
 * - Guardado de intentos en la base de datos
 * 
 * Características principales:
 * - Interfaz Material 3 con diseño adaptativo
 * - Sistema de actualización pull-to-refresh
 * - Cálculo dinámico de resultados
 * - Gestión de respuestas por pregunta
 * - Integración con API para guardar resultados
 * - Navegación a vista de resultados
 * 
 * La pantalla actúa como el núcleo de la evaluación,
 * permitiendo a los usuarios responder preguntas y
 * obtener resultados inmediatos de su desempeño
 * en las diferentes áreas evaluadas.
 */

package com.escolavision.testescolavision.Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.API.*
import com.escolavision.testescolavision.PreferencesManager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private object TestDetailScreenConstants {
    const val SCREEN_TITLE = "Preguntas del Test"
    const val NEW_BUTTON_TEXT = "Nuevo"
    const val SUBMIT_BUTTON_TEXT = "Enviar Test"
    const val SUCCESS_MESSAGE = "Test realizado correctamente"
    const val ERROR_MESSAGE = "Test fallido"
    const val NETWORK_ERROR_MESSAGE = "Error de red"
    const val INTENTOS_TABLE = "intentos"
    const val PROGRESS_TEXT = "Progreso: %d/%d"
    const val INFO_DIALOG_TITLE = "Información del Test"
    const val INFO_DIALOG_TEXT = "Responde a cada pregunta usando el slider (0-10).\n\n" +
            "0: Totalmente en desacuerdo\n" +
            "5: Neutral\n" +
            "10: Totalmente de acuerdo"
    
    val TITLE_FONT_SIZE = 22.sp
    val NEW_BUTTON_FONT_SIZE = 16.sp
    val SUBMIT_BUTTON_FONT_SIZE = 20.sp
    val QUESTION_FONT_SIZE = 18.sp
    val SLIDER_LABEL_FONT_SIZE = 14.sp
    val PROGRESS_FONT_SIZE = 16.sp
    
    val CONTENT_PADDING = 16.dp
    val CARD_PADDING = 16.dp
    val QUESTION_SPACING = 12.dp
    val SLIDER_SPACING = 8.dp
    val SLIDER_HEIGHT = 48.dp
    val SUBMIT_BUTTON_HEIGHT = 60.dp
    
    val CARD_SHADOW = 5.dp
    val CARD_CORNER_RADIUS = 10.dp
    
    val SLIDER_COLOR = Color(0xFF1976D2)
    val PROGRESS_COLOR = Color(0xFF4CAF50)
    val DEFAULT_SLIDER_VALUE = 5f
    val SLIDER_MIN_VALUE = 0f
    val SLIDER_MAX_VALUE = 10f
    val SLIDER_STEPS = 9
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestDetailScreen(navController: NavController, testId: Any) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val (idUsuario, tipoUsuario) = preferencesManager.getLoginData()
    
    var preguntas by remember { mutableStateOf<List<Preguntas>>(emptyList()) }
    var respuestas by remember { mutableStateOf<Map<Int, Float>>(emptyMap()) }
    var pxa by remember { mutableStateOf<List<PxA>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var testInfo by remember { mutableStateOf<Test?>(null) }

    LaunchedEffect(Unit) {
        loadTestInfo(testId.toString()) { info ->
            testInfo = info
        }
        loadPreguntas(testId.toString()) { loadedPreguntas ->
            preguntas = loadedPreguntas
            isRefreshing = false
        }
        loadPxa { loadedPxa ->
            pxa = loadedPxa
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text(TestDetailScreenConstants.INFO_DIALOG_TITLE) },
            text = { Text(TestDetailScreenConstants.INFO_DIALOG_TEXT) },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }

    TestDetailScreenContent(
        navController = navController,
        preguntas = preguntas,
        respuestas = respuestas,
        isRefreshing = isRefreshing,
        testInfo = testInfo,
        onRefresh = {
            isRefreshing = true
            loadPreguntas(testId.toString()) { loadedPreguntas ->
                preguntas = loadedPreguntas
                isRefreshing = false
            }
        },
        onResetAnswers = {
            respuestas = preguntas.associate { it.id to TestDetailScreenConstants.DEFAULT_SLIDER_VALUE }
        },
        onAnswerChange = { preguntaId, value ->
            respuestas = respuestas.toMutableMap().apply {
                put(preguntaId, value)
            }
        },
        onInfoClick = { showInfoDialog = true },
        onSubmit = {
            val resultados = calcularResultados(preguntas, respuestas, pxa)
            handleTestSubmission(
                context = context,
                navController = navController,
                testId = testId.toString(),
                idUsuario = idUsuario.toString(),
                resultados = resultados
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TestDetailScreenContent(
    navController: NavController,
    preguntas: List<Preguntas>,
    respuestas: Map<Int, Float>,
    isRefreshing: Boolean,
    testInfo: Test?,
    onRefresh: () -> Unit,
    onResetAnswers: () -> Unit,
    onAnswerChange: (Int, Float) -> Unit,
    onInfoClick: () -> Unit,
    onSubmit: () -> Unit
) {
    Scaffold(
        topBar = {
            TestDetailTopBar(
                navController = navController,
                onResetClick = onResetAnswers,
                onInfoClick = onInfoClick
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.fondoInicio))
                    .padding(paddingValues)
            ) {
                testInfo?.let {
                    TestInfoCard(testInfo = it)
                }
                
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing),
                    onRefresh = onRefresh
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(TestDetailScreenConstants.CONTENT_PADDING),
                        verticalArrangement = Arrangement.spacedBy(TestDetailScreenConstants.QUESTION_SPACING)
                    ) {
                        items(preguntas) { pregunta ->
                            QuestionCard(
                                pregunta = pregunta,
                                respuesta = respuestas[pregunta.id] ?: TestDetailScreenConstants.DEFAULT_SLIDER_VALUE,
                                onAnswerChange = { onAnswerChange(pregunta.id, it) }
                            )
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            SubmitButton(
                                onSubmit = onSubmit,
                                isEnabled = true
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun TestInfoCard(testInfo: Test) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(TestDetailScreenConstants.CONTENT_PADDING),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(TestDetailScreenConstants.CARD_PADDING)
        ) {
            Text(
                text = testInfo.nombretest,
                fontSize = TestDetailScreenConstants.QUESTION_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                color = TestDetailScreenConstants.SLIDER_COLOR
            )
        }
    }
}

@Composable
private fun ProgressIndicator(totalQuestions: Int, answeredQuestions: Int) {
    val progress = if (totalQuestions > 0) (answeredQuestions.toFloat() / totalQuestions) else 0f
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(TestDetailScreenConstants.CONTENT_PADDING)
    ) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = TestDetailScreenConstants.PROGRESS_COLOR
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = TestDetailScreenConstants.PROGRESS_TEXT.format(answeredQuestions, totalQuestions),
            fontSize = TestDetailScreenConstants.PROGRESS_FONT_SIZE,
            color = TestDetailScreenConstants.PROGRESS_COLOR,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TestDetailTopBar(
    navController: NavController,
    onResetClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = TestDetailScreenConstants.SCREEN_TITLE,
                fontSize = TestDetailScreenConstants.TITLE_FONT_SIZE,
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
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Información",
                    tint = Color.White
                )
            }
            TextButton(onClick = onResetClick) {
                Text(
                    text = TestDetailScreenConstants.NEW_BUTTON_TEXT,
                    color = Color.White,
                    fontSize = TestDetailScreenConstants.NEW_BUTTON_FONT_SIZE,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
private fun QuestionsList(
    preguntas: List<Preguntas>,
    respuestas: Map<Int, Float>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onAnswerChange: (Int, Float) -> Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh
    ) {
        LazyColumn(
            contentPadding = PaddingValues(TestDetailScreenConstants.CONTENT_PADDING),
            verticalArrangement = Arrangement.spacedBy(TestDetailScreenConstants.QUESTION_SPACING)
        ) {
            items(preguntas) { pregunta ->
                QuestionCard(
                    pregunta = pregunta,
                    respuesta = respuestas[pregunta.id] ?: TestDetailScreenConstants.DEFAULT_SLIDER_VALUE,
                    onAnswerChange = { onAnswerChange(pregunta.id, it) }
                )
            }
        }
    }
}

@Composable
private fun QuestionCard(
    pregunta: Preguntas,
    respuesta: Float,
    onAnswerChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(TestDetailScreenConstants.CARD_SHADOW, RoundedCornerShape(TestDetailScreenConstants.CARD_CORNER_RADIUS)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(TestDetailScreenConstants.CARD_PADDING)
        ) {
            Text(
                text = pregunta.enunciado,
                color = Color.Black,
                fontSize = TestDetailScreenConstants.QUESTION_FONT_SIZE,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(TestDetailScreenConstants.SLIDER_SPACING))
            SliderSection(
                value = respuesta,
                onValueChange = onAnswerChange
            )
        }
    }
}

@Composable
private fun SliderSection(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(TestDetailScreenConstants.SLIDER_HEIGHT),
            contentAlignment = Alignment.Center
        ) {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = TestDetailScreenConstants.SLIDER_MIN_VALUE..TestDetailScreenConstants.SLIDER_MAX_VALUE,
                steps = TestDetailScreenConstants.SLIDER_STEPS,
                colors = SliderDefaults.colors(
                    thumbColor = TestDetailScreenConstants.SLIDER_COLOR,
                    activeTrackColor = TestDetailScreenConstants.SLIDER_COLOR
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(TestDetailScreenConstants.SLIDER_SPACING))
        SliderLabels()
    }
}

@Composable
private fun SliderLabels() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 0..10) {
            Text(
                text = "$i",
                fontSize = TestDetailScreenConstants.SLIDER_LABEL_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                color = TestDetailScreenConstants.SLIDER_COLOR,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SubmitButton(onSubmit: () -> Unit, isEnabled: Boolean) {
    Button(
        onClick = onSubmit,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = TestDetailScreenConstants.CONTENT_PADDING)
            .height(TestDetailScreenConstants.SUBMIT_BUTTON_HEIGHT),
        shape = RoundedCornerShape(TestDetailScreenConstants.CARD_CORNER_RADIUS),
        colors = ButtonDefaults.buttonColors(
            containerColor = TestDetailScreenConstants.SLIDER_COLOR
        ),
        enabled = true
    ) {
        Text(
            text = TestDetailScreenConstants.SUBMIT_BUTTON_TEXT,
            fontSize = TestDetailScreenConstants.SUBMIT_BUTTON_FONT_SIZE,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun loadTestInfo(testId: String, onComplete: (Test?) -> Unit) {
    RetrofitClient.api.getTests().enqueue(object : Callback<TestsResponse> {
        override fun onResponse(call: Call<TestsResponse>, response: Response<TestsResponse>) {
            if (response.isSuccessful) {
                val test = response.body()?.tests?.find { it.id.toString() == testId }
                onComplete(test)
            } else {
                onComplete(null)
            }
        }

        override fun onFailure(call: Call<TestsResponse>, t: Throwable) {
            onComplete(null)
        }
    })
}

private fun loadPreguntas(testId: String, onComplete: (List<Preguntas>) -> Unit) {
    RetrofitClient.api.getPreguntas().enqueue(object : Callback<PreguntasListResponse> {
        override fun onResponse(call: Call<PreguntasListResponse>, response: Response<PreguntasListResponse>) {
            if (response.isSuccessful) {
                val testIdd = Integer.parseInt(testId)
                val filteredPreguntas = response.body()?.preguntas?.filter { it.idtest == testIdd } ?: emptyList()
                onComplete(filteredPreguntas)
            } else {
                onComplete(emptyList())
            }
        }

        override fun onFailure(call: Call<PreguntasListResponse>, t: Throwable) {
            onComplete(emptyList())
        }
    })
}

private fun loadPxa(onComplete: (List<PxA>) -> Unit) {
    RetrofitClient.api.getPxa().enqueue(object : Callback<PxaListResponse> {
        override fun onResponse(call: Call<PxaListResponse>, response: Response<PxaListResponse>) {
            if (response.isSuccessful) {
                onComplete(response.body()?.pxa ?: emptyList())
            } else {
                onComplete(emptyList())
            }
        }

        override fun onFailure(call: Call<PxaListResponse>, t: Throwable) {
            onComplete(emptyList())
        }
    })
}

private fun calcularResultados(
    preguntas: List<Preguntas>,
    respuestas: Map<Int, Float>,
    pxa: List<PxA>
): String {
    val respuestasCompletas = preguntas.associate { it.id to (respuestas[it.id] ?: TestDetailScreenConstants.DEFAULT_SLIDER_VALUE) }
    val resultadosPorArea = mutableMapOf<Int, Float>()
    val totalPorArea = mutableMapOf<Int, Int>()

    respuestasCompletas.forEach { (idPregunta, respuesta) ->
        val areas = pxa.filter { it.idpregunta == idPregunta }.map { it.idarea }
        areas.forEach { area ->
            resultadosPorArea[area] = (resultadosPorArea[area] ?: 0f) + respuesta
            totalPorArea[area] = (totalPorArea[area] ?: 0) + 1
        }
    }

    val resultados = mutableListOf<Float>()
    for (i in 1..5) {
        val totalRespuestas = totalPorArea[i] ?: 0
        val sumaRespuestas = resultadosPorArea[i] ?: 0f
        resultados.add(if (totalRespuestas > 0) sumaRespuestas / totalRespuestas else 0f)
    }

    return resultados.joinToString(";")
}

private fun handleTestSubmission(
    context: android.content.Context,
    navController: NavController,
    testId: String,
    idUsuario: String,
    resultados: String
) {
    val now = LocalDateTime.now()
    val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss")
    val fechaActual = now.format(formatterDate)
    val horaActual = now.format(formatterTime)
    
    val testIdd = Integer.parseInt(testId)
    val intentoData = IntentoRequest(
        tabla = TestDetailScreenConstants.INTENTOS_TABLE,
        datos = Intento(
            idtest = testIdd,
            idusuario = idUsuario.toInt(),
            fecha = fechaActual,
            hora = horaActual,
            resultados = resultados
        )
    )

    if (idUsuario.toInt() != 0) {
        RetrofitClient.api.insertarIntento(intentoData).enqueue(object : Callback<IntentoResponse> {
            override fun onResponse(call: Call<IntentoResponse>, response: Response<IntentoResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(context, TestDetailScreenConstants.SUCCESS_MESSAGE, Toast.LENGTH_SHORT).show()
                    navController.navigate("result_test_screen/${resultados}/home_screen")
                } else {
                    Toast.makeText(context, TestDetailScreenConstants.ERROR_MESSAGE, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<IntentoResponse>, t: Throwable) {
                Toast.makeText(context, TestDetailScreenConstants.NETWORK_ERROR_MESSAGE, Toast.LENGTH_SHORT).show()
            }
        })
    } else {
        navController.navigate("result_test_screen/${resultados}/home_screen")
    }
}
