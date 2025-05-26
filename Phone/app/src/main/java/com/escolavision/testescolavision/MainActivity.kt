package com.escolavision.testescolavision

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.escolavision.testescolavision.ui.theme.EscolaVisionTheme

/**
 * Actividad principal de la aplicación EscolaVision.
 * 
 * Esta actividad sirve como punto de entrada de la aplicación y configura:
 * - El tema de Material Design 3
 * - El sistema de navegación
 * - La estructura base de la UI
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EscolaVisionApp()
        }
    }
}

@Composable
private fun EscolaVisionApp() {
    EscolaVisionTheme {
        val navController = rememberNavController()
        
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            NavigationComponent(navController)
        }
    }
}
