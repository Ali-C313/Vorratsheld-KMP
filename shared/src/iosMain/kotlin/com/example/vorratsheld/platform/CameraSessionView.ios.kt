package com.example.vorratsheld.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureTorchModeOff
import platform.AVFoundation.AVCaptureTorchModeOn
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.hasTorch
import platform.AVFoundation.torchMode
import platform.CoreGraphics.CGRectZero
import platform.UIKit.UIView
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue

// Rückkamera mit Vorschau, addOutputs hängt die eigentliche Erkennung an die Session
@OptIn(ExperimentalForeignApi::class)
@Composable
internal fun CameraSessionView(
    modifier: Modifier,
    torchEnabled: Boolean = false,
    addOutputs: (AVCaptureSession) -> Unit
) {
    val session = remember { AVCaptureSession() }
    val previewLayer = remember { AVCaptureVideoPreviewLayer(session = session) }
    val device = remember { AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) }

    LaunchedEffect(torchEnabled) {
        device?.setTorch(torchEnabled)
    }

    DisposableEffect(Unit) {
        val input = device?.let { AVCaptureDeviceInput.deviceInputWithDevice(it, null) as? AVCaptureDeviceInput }
        if (input != null && session.canAddInput(input)) {
            session.addInput(input)
        }

        addOutputs(session)
        previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill

        // startRunning blockiert, deshalb nicht auf dem Main-Thread
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0u)) {
            session.startRunning()
        }

        onDispose {
            session.stopRunning()
        }
    }

    UIKitView(
        factory = { CameraPreviewView(previewLayer) },
        modifier = modifier
    )
}

// Geht nur bei laufender Kamera, beim Stoppen der Session geht das Licht von selbst aus
@OptIn(ExperimentalForeignApi::class)
private fun AVCaptureDevice.setTorch(on: Boolean) {
    if (!hasTorch) return
    if (lockForConfiguration(null)) {
        torchMode = if (on) AVCaptureTorchModeOn else AVCaptureTorchModeOff
        unlockForConfiguration()
    }
}

// Zieht die Kamera-Vorschau immer auf die eigene Größe
@OptIn(ExperimentalForeignApi::class)
private class CameraPreviewView(
    private val previewLayer: AVCaptureVideoPreviewLayer
) : UIView(frame = CGRectZero.readValue()) {

    init {
        layer.addSublayer(previewLayer)
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        previewLayer.frame = bounds
    }
}
