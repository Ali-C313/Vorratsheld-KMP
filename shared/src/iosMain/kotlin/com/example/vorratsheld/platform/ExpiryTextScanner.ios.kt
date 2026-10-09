package com.example.vorratsheld.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureVideoDataOutput
import platform.AVFoundation.AVCaptureVideoDataOutputSampleBufferDelegateProtocol
import platform.CoreMedia.CMSampleBufferGetImageBuffer
import platform.CoreMedia.CMSampleBufferRef
import platform.ImageIO.kCGImagePropertyOrientationRight
import platform.Vision.VNImageRequestHandler
import platform.Vision.VNRecognizeTextRequest
import platform.Vision.VNRecognizedText
import platform.Vision.VNRecognizedTextObservation
import platform.Vision.VNRequestTextRecognitionLevelAccurate
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_create

// Texterkennung mit Apple Vision, läuft komplett offline auf dem Gerät
@Composable
actual fun ExpiryTextScanner(
    onTextRecognized: (String) -> Unit,
    modifier: Modifier,
    torchEnabled: Boolean
) {
    CameraPermissionGate(modifier) {
        val currentOnText by rememberUpdatedState(onTextRecognized)
        val delegate = remember { TextFrameDelegate { text -> currentOnText(text) } }
        val queue = remember { dispatch_queue_create("vorratsheld.ocr", null) }

        CameraSessionView(modifier, torchEnabled) { session ->
            val output = AVCaptureVideoDataOutput()
            output.alwaysDiscardsLateVideoFrames = true
            if (session.canAddOutput(output)) {
                session.addOutput(output)
                output.setSampleBufferDelegate(delegate, queue)
            }
        }
    }
}

// Bekommt die Kamerabilder und liest höchstens ca. zweimal pro Sekunde den Text aus
@OptIn(ExperimentalForeignApi::class)
private class TextFrameDelegate(
    private val onText: (String) -> Unit
) : NSObject(), AVCaptureVideoDataOutputSampleBufferDelegateProtocol {

    private var lastRun = TimeSource.Monotonic.markNow()

    @ObjCSignatureOverride
    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputSampleBuffer: CMSampleBufferRef?,
        fromConnection: AVCaptureConnection
    ) {
        if (lastRun.elapsedNow() < 400.milliseconds) return
        lastRun = TimeSource.Monotonic.markNow()

        val pixelBuffer = CMSampleBufferGetImageBuffer(didOutputSampleBuffer) ?: return
        val request = VNRecognizeTextRequest()
        request.recognitionLevel = VNRequestTextRecognitionLevelAccurate
        request.usesLanguageCorrection = false

        // Hochkant gehaltene Rückkamera liefert die Bilder um 90° gedreht
        val handler = VNImageRequestHandler(
            cVPixelBuffer = pixelBuffer,
            orientation = kCGImagePropertyOrientationRight,
            options = emptyMap<Any?, Any?>()
        )
        handler.performRequests(listOf(request), null)

        val text = request.results
            ?.filterIsInstance<VNRecognizedTextObservation>()
            ?.mapNotNull { (it.topCandidates(1u).firstOrNull() as? VNRecognizedText)?.string }
            ?.joinToString("\n")

        if (!text.isNullOrBlank()) {
            dispatch_async(dispatch_get_main_queue()) { onText(text) }
        }
    }
}
