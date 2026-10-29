package com.example.vorratsheld.ui.scan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.platform.BarcodeScanner
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_back
import com.example.vorratsheld.resources.scan_hint
import com.example.vorratsheld.ui.components.TorchButton
import com.example.vorratsheld.ui.navigation.Screen
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import org.jetbrains.compose.resources.stringResource

// Kamera und Erkennung kommen von der Plattform (Android: CameraX/ML Kit, iOS: AVFoundation),
// Sucher-Rahmen, Hinweis und Zurück-Button liegen für beide gleich darüber
@Composable
fun ScanScreen(navController: NavHostController) {
    var torchEnabled by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        BarcodeScanner(
            modifier = Modifier.fillMaxSize(),
            torchEnabled = torchEnabled,
            onBarcodeDetected = { barcode ->
                navController.navigate(Screen.ProductDetail(barcode = barcode)) {
                    // Zurück zum Scan-Screen soll nicht möglich sein, Übersicht bleibt im Backstack
                    popUpTo(Screen.Scan) { inclusive = true }
                }
            }
        )

        ScanFrame(modifier = Modifier.align(Alignment.Center))

        FilledTonalIconButton(
            onClick = { navController.popBackStack() },
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.45f),
                contentColor = Color.White
            ),
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back))
        }

        TorchButton(
            enabled = torchEnabled,
            onToggle = { torchEnabled = !torchEnabled },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp)
        )

        Surface(
            color = Color.Black.copy(alpha = 0.55f),
            contentColor = Color.White,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(Res.string.scan_hint),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScanScreenPreview() {
    VorratsheldTheme {
        ScanScreen(navController = rememberNavController())
    }
}
