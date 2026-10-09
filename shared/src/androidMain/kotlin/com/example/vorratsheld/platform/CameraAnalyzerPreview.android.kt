package com.example.vorratsheld.platform

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.Executors

// Rückkamera mit Vorschau, jedes Bild geht an den analyzer
@Composable
internal fun CameraAnalyzerPreview(
    analyzer: ImageAnalysis.Analyzer,
    modifier: Modifier,
    torchEnabled: Boolean = false
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val camera = remember { CameraHandle() }
    var boundCamera by remember { mutableStateOf<Camera?>(null) }

    // Taschenlampe nur, wenn das Gerät einen Blitz hat
    LaunchedEffect(boundCamera, torchEnabled) {
        boundCamera
            ?.takeIf { it.cameraInfo.hasFlashUnit() }
            ?.cameraControl
            ?.enableTorch(torchEnabled)
    }

    // Kamera beim Verlassen freigeben, auch wenn der Screen selbst noch offen bleibt
    DisposableEffect(Unit) {
        onDispose {
            camera.disposed = true
            camera.provider?.unbindAll()
            analysisExecutor.shutdown()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                if (camera.disposed) return@addListener
                val cameraProvider = cameraProviderFuture.get()
                camera.provider = cameraProvider

                val preview = CameraPreview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { it.setAnalyzer(analysisExecutor, analyzer) }

                try {
                    cameraProvider.unbindAll()
                    boundCamera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}

private class CameraHandle {
    var provider: ProcessCameraProvider? = null
    var disposed = false
}
