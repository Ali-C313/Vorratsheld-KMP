package com.example.vorratsheld.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.AVFoundation.AVMetadataObjectTypeCode128Code
import platform.AVFoundation.AVMetadataObjectTypeEAN13Code
import platform.AVFoundation.AVMetadataObjectTypeEAN8Code
import platform.AVFoundation.AVMetadataObjectTypeUPCECode
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun BarcodeScanner(
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier,
    torchEnabled: Boolean
) {
    CameraPermissionGate(modifier) {
        val currentOnBarcodeDetected by rememberUpdatedState(onBarcodeDetected)

        // Delegate muss gehalten werden, AVFoundation speichert ihn nur schwach
        val delegate = remember { BarcodeDelegate { code -> currentOnBarcodeDetected(code) } }

        CameraSessionView(modifier, torchEnabled) { session ->
            val output = AVCaptureMetadataOutput()
            if (session.canAddOutput(output)) {
                session.addOutput(output)
                output.setMetadataObjectsDelegate(delegate, dispatch_get_main_queue())
                // Typen erst nach addOutput setzen, sonst stürzt AVFoundation ab
                output.metadataObjectTypes = listOf(
                    AVMetadataObjectTypeEAN13Code,
                    AVMetadataObjectTypeEAN8Code,
                    AVMetadataObjectTypeUPCECode,
                    AVMetadataObjectTypeCode128Code
                )
            }
        }
    }
}

// Meldet nur den ersten erkannten Code weiter
private class BarcodeDelegate(
    private val onCode: (String) -> Unit
) : NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {

    private var alreadyDetected = false

    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputMetadataObjects: List<*>,
        fromConnection: AVCaptureConnection
    ) {
        if (alreadyDetected) return
        val code = didOutputMetadataObjects
            .filterIsInstance<AVMetadataMachineReadableCodeObject>()
            .firstOrNull()
            ?.stringValue ?: return
        alreadyDetected = true
        onCode(code)
    }
}
