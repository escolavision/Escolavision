/**
 * Pantalla principal de tests de EscolaVision.
 * 
 * Esta pantalla muestra la lista de tests disponibles para los usuarios y permite:
 * - Visualizar todos los tests activos del sistema
 * - Acceder a los detalles de cada test
 * - Actualizar la lista mediante pull-to-refresh
 * - Gestionar tests favoritos
 * 
 * Características principales:
 * - Interfaz Material 3 con lista dinámica de tests
 * - Menú lateral para navegación
 * - Sistema de actualización en tiempo real
 * - Filtrado automático de tests visibles
 * - Gestión de estados con composables
 * 
 * La pantalla actúa como punto central de acceso a las evaluaciones
 * disponibles en el sistema, proporcionando una interfaz intuitiva
 * para que los usuarios accedan a los diferentes tests.
 */

package com.escolavision.testescolavision.Screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.escolavision.testescolavision.API.Test
import com.escolavision.testescolavision.API.TestsResponse
import com.escolavision.testescolavision.PreferencesManager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.escolavision.testescolavision.R

private object HomeScreenConstants {
    const val SCREEN_TITLE = "Tests"
    const val TEST_DETAIL_ROUTE = "test_detail_screen/"
    const val FAVORITES_PREFS = "favorite_tests"
    
    val TOP_BAR_FONT_SIZE = 22.sp
    val WELCOME_FONT_SIZE = 24.sp
    val SUBTITLE_FONT_SIZE = 18.sp
    val CONTENT_PADDING = 16.dp
    val SPACING = 16.dp
    val CARD_ELEVATION = 4.dp
    val BUTTON_COLOR = Color(0xFF007AFF)
    val CARD_COLOR = Color(0xFFFFFFFF)
    val TEXT_COLOR = Color(0xFF333333)
    val SUBTITLE_COLOR = Color(0xFF666666)
}

// Clase de datos que representa un elemento de test con su estado de favorito
data class TestItem(
    val test: Test,
    var isFavorite: Boolean = false
)

// Pantalla principal que muestra la lista de tests disponibles
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val (id, tipo) = preferencesManager.getLoginData()
    
    var tests by remember { mutableStateOf<List<TestItem>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Cargar tests y sus estados de favorito
    LaunchedEffect(Unit) {
        loadTests { newTests ->
            // Cargar estados de favorito desde SharedPreferences
            val favoriteTests = context.getSharedPreferences(HomeScreenConstants.FAVORITES_PREFS, Context.MODE_PRIVATE)
            tests = newTests.map { testItem ->
                testItem.copy(isFavorite = favoriteTests.getBoolean("test_${testItem.test.id}", false))
            }
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
            HomeScreenContent(
                navController = navController,
                tests = tests,
                isRefreshing = isRefreshing,
                onRefresh = { 
                    loadTests { newTests ->
                        val favoriteTests = context.getSharedPreferences(HomeScreenConstants.FAVORITES_PREFS, Context.MODE_PRIVATE)
                        tests = newTests.map { testItem ->
                            testItem.copy(isFavorite = favoriteTests.getBoolean("test_${testItem.test.id}", false))
                        }
                    }
                },
                onFavoriteClick = { testId ->
                    val favoriteTests = context.getSharedPreferences(HomeScreenConstants.FAVORITES_PREFS, Context.MODE_PRIVATE)
                    val currentState = favoriteTests.getBoolean("test_$testId", false)
                    favoriteTests.edit().putBoolean("test_$testId", !currentState).apply()
                    
                    // Actualizar el estado local
                    tests = tests.map { testItem ->
                        if (testItem.test.id == testId) {
                            testItem.copy(isFavorite = !testItem.isFavorite)
                        } else {
                            testItem
                        }
                    }
                },
                drawerState = drawerState,
                scope = scope
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    navController: NavController,
    tests: List<TestItem>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onFavoriteClick: (Int) -> Unit,
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        content = { paddingValues ->
            HomeContent(
                paddingValues = paddingValues,
                tests = tests,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                onTestClick = { testId -> 
                    navController.navigate("${HomeScreenConstants.TEST_DETAIL_ROUTE}$testId") 
                },
                onFavoriteClick = onFavoriteClick
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = HomeScreenConstants.SCREEN_TITLE,
                    fontSize = HomeScreenConstants.TOP_BAR_FONT_SIZE,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(id = R.color.titulos),
                )
            }
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
private fun HomeContent(
    paddingValues: PaddingValues,
    tests: List<TestItem>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onTestClick: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.fondoInicio))
            .padding(paddingValues)
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = onRefresh
        ) {
            TestsList(
                tests = tests,
                onTestClick = onTestClick,
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}

@Composable
private fun TestsList(
    tests: List<TestItem>,
    onTestClick: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(HomeScreenConstants.CONTENT_PADDING),
        verticalArrangement = Arrangement.spacedBy(HomeScreenConstants.SPACING),
        modifier = Modifier.fillMaxSize()
    ) {
        items(tests) { testItem ->
            TestItem(
                testItem = testItem,
                onClick = { onTestClick(testItem.test.id) },
                onFavoriteClick = { onFavoriteClick(testItem.test.id) }
            )
        }
    }
}

@Composable
private fun TestItem(
    testItem: TestItem,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = HomeScreenConstants.CARD_ELEVATION),
        colors = CardDefaults.cardColors(containerColor = HomeScreenConstants.CARD_COLOR),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(HomeScreenConstants.CONTENT_PADDING),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = testItem.test.nombretest,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HomeScreenConstants.TEXT_COLOR
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Haz clic para comenzar",
                    fontSize = 14.sp,
                    color = HomeScreenConstants.SUBTITLE_COLOR
                )
            }
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = if (testItem.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (testItem.isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                    tint = if (testItem.isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}

private fun loadTests(onTestsLoaded: (List<TestItem>) -> Unit) {
    RetrofitClient.api.getTests().enqueue(object : Callback<TestsResponse> {
        override fun onResponse(call: Call<TestsResponse>, response: Response<TestsResponse>) {
            if (response.isSuccessful) {
                val visibleTests = response.body()?.tests
                    ?.filter { it.isVisible == 1 }
                    ?.map { TestItem(test = it) }
                    ?: emptyList()
                onTestsLoaded(visibleTests)
            }
        }
        
        override fun onFailure(call: Call<TestsResponse>, t: Throwable) {
            onTestsLoaded(emptyList())
        }
    })
}
