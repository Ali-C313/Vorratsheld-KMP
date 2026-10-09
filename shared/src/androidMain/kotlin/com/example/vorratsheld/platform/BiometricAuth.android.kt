package com.example.vorratsheld.platform

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.vorratsheld.data.enums.BiometricResult
import com.example.vorratsheld.data.enums.BiometricType
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

@Composable
actual fun rememberBiometricAuthenticator(): BiometricAuthenticator {
    val context = LocalContext.current
    return remember(context) { AndroidBiometricAuthenticator(context.findFragmentActivity()) }
}

private fun Context.findFragmentActivity(): FragmentActivity? {
    var current: Context? = this
    while (current is ContextWrapper) {
        if (current is FragmentActivity) return current
        current = current.baseContext
    }
    return null
}

private class AndroidBiometricAuthenticator(
    private val activity: FragmentActivity?
) : BiometricAuthenticator {

    override val type: BiometricType
        get() {
            val activity = activity ?: return BiometricType.NONE
            val available = BiometricManager.from(activity).canAuthenticate(BIOMETRIC_WEAK) ==
                    BiometricManager.BIOMETRIC_SUCCESS
            if (!available) return BiometricType.NONE
            val pm = activity.packageManager
            return when {
                pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) -> BiometricType.FINGERPRINT
                pm.hasSystemFeature(PackageManager.FEATURE_FACE) -> BiometricType.FACE
                else -> BiometricType.GENERIC
            }
        }

    override suspend fun authenticate(reason: String, fallbackLabel: String): BiometricResult {
        val activity = activity ?: return BiometricResult.ERROR
        return suspendCancellableCoroutine { continuation ->
            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (continuation.isActive) continuation.resume(BiometricResult.SUCCESS)
                }

                // Ein einzelner falscher Finger landet in onAuthenticationFailed, der Dialog bleibt offen
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    val result = when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_LOCKOUT,
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> BiometricResult.USE_PIN
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_CANCELED -> BiometricResult.CANCELLED
                        else -> BiometricResult.ERROR
                    }
                    if (continuation.isActive) continuation.resume(result)
                }
            }

            val prompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity), callback)
            val info = BiometricPrompt.PromptInfo.Builder()
                .setTitle(reason)
                .setNegativeButtonText(fallbackLabel)
                .setAllowedAuthenticators(BIOMETRIC_WEAK)
                .build()

            prompt.authenticate(info)
            continuation.invokeOnCancellation { prompt.cancelAuthentication() }
        }
    }
}
