package com.example.vorratsheld.platform

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

// Texterkennung mit ML Kit, läuft komplett offline auf dem Gerät
@OptIn(ExperimentalGetImage::class)
@Composable
actual fun ExpiryTextScanner(
    onTextRecognized: (String) -> Unit,
    modifier: Modifier,
    torchEnabled: Boolean
) {
    CameraPermissionGate(modifier) {
        val currentOnText = rememberUpdatedState(onTextRecognized)
        val recognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

        DisposableEffect(Unit) {
            onDispose { recognizer.close() }
        }

        val analyzer = remember {
            ImageAnalysis.Analyzer { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage == null) {
                    imageProxy.close()
                    return@Analyzer
                }
                val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                recognizer.process(inputImage)
                    .addOnSuccessListener { result ->
                        if (result.text.isNotBlank()) currentOnText.value(result.text)
                    }
                    .addOnCompleteListener { imageProxy.close() }
            }
        }

        CameraAnalyzerPreview(analyzer = analyzer, modifier = modifier, torchEnabled = torchEnabled)
    }
}
