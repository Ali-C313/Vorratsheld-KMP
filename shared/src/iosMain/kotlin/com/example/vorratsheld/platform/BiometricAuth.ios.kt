package com.example.vorratsheld.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.vorratsheld.data.enums.BiometricResult
import com.example.vorratsheld.data.enums.BiometricType
import kotlin.coroutines.resume
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.LocalAuthentication.LABiometryTypeFaceID
import platform.LocalAuthentication.LABiometryTypeTouchID
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAErrorAppCancel
import platform.LocalAuthentication.LAErrorBiometryLockout
import platform.LocalAuthentication.LAErrorSystemCancel
import platform.LocalAuthentication.LAErrorUserCancel
import platform.LocalAuthentication.LAErrorUserFallback
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberBiometricAuthenticator(): BiometricAuthenticator = remember { IosBiometricAuthenticator() }

@OptIn(ExperimentalForeignApi::class)
private class IosBiometricAuthenticator : BiometricAuthenticator {

    override val type: BiometricType
        get() {
            val context = LAContext()
            if (!context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)) {
                return BiometricType.NONE
            }
            return when (context.biometryType) {
                LABiometryTypeFaceID -> BiometricType.FACE_ID
                LABiometryTypeTouchID -> BiometricType.TOUCH_ID
                else -> BiometricType.GENERIC
            }
        }

    // "PIN verwenden" zeigt iOS erst nach dem ersten Fehlversuch an
    override suspend fun authenticate(reason: String, fallbackLabel: String): BiometricResult =
        suspendCancellableCoroutine { continuation ->
            val context = LAContext()
            context.localizedFallbackTitle = fallbackLabel

            context.evaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, reason) { success, error ->
                val code = error?.code
                val result = when {
                    success -> BiometricResult.SUCCESS
                    code == LAErrorUserFallback || code == LAErrorBiometryLockout -> BiometricResult.USE_PIN
                    code == LAErrorUserCancel || code == LAErrorSystemCancel || code == LAErrorAppCancel -> BiometricResult.CANCELLED
                    else -> BiometricResult.ERROR
                }
                // Antwort kommt auf einem Hintergrund-Thread
                dispatch_async(dispatch_get_main_queue()) {
                    if (continuation.isActive) continuation.resume(result)
                }
            }

            continuation.invokeOnCancellation { context.invalidate() }
        }
}
