package com.example.vorratsheld.platform

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalGetImage::class)
@Composable
actual fun BarcodeScanner(
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier,
    torchEnabled: Boolean
) {
    CameraPermissionGate(modifier) {
        val currentOnBarcodeDetected = rememberUpdatedState(onBarcodeDetected)
        val barcodeScanner = remember { BarcodeScanning.getClient() }

        // Nach dem ersten Treffer nicht mehrfach navigieren
        val alreadyDetected = remember { AtomicBoolean(false) }

        DisposableEffect(Unit) {
            onDispose { barcodeScanner.close() }
        }

        val analyzer = remember {
            ImageAnalysis.Analyzer { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage == null || alreadyDetected.get()) {
                    imageProxy.close()
                    return@Analyzer
                }
                val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                barcodeScanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        val value = barcodes.firstOrNull()?.rawValue
                        if (value != null && alreadyDetected.compareAndSet(false, true)) {
                            currentOnBarcodeDetected.value(value)
                        }
                    }
                    .addOnCompleteListener { imageProxy.close() }
            }
        }

        CameraAnalyzerPreview(analyzer = analyzer, modifier = modifier, torchEnabled = torchEnabled)
    }
}
