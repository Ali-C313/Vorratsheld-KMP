package com.example.vorratsheld

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.vorratsheld.ui.locale.AppEnvironment
import com.example.vorratsheld.ui.navigation.VorratsheldNavGraph
import com.example.vorratsheld.ui.theme.VorratsheldTheme

// Einstiegspunkt der UI – wird von MainActivity (Android) und MainViewController (iOS) aufgerufen
@Composable
fun App() {
    // AppEnvironment ganz außen, damit ein Sprachwechsel wirklich alles neu aufbaut
    AppEnvironment {
        VorratsheldTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                VorratsheldNavGraph()
            }
        }
    }
}
