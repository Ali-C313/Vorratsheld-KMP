package com.example.vorratsheld.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.vorratsheld.data.enums.BiometricType
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.biometric_face
import com.example.vorratsheld.resources.biometric_face_id
import com.example.vorratsheld.resources.biometric_fingerprint
import com.example.vorratsheld.resources.biometric_generic
import com.example.vorratsheld.resources.biometric_touch_id
import org.jetbrains.compose.resources.stringResource

// Name und Icon für Face ID, Touch ID oder Fingerabdruck
object BiometricHelper {

    // Name, wie ihn der Nutzer von seinem Gerät kennt
    @Composable
    fun label(type: BiometricType): String = stringResource(
        when (type) {
            BiometricType.FACE_ID -> Res.string.biometric_face_id
            BiometricType.TOUCH_ID -> Res.string.biometric_touch_id
            BiometricType.FINGERPRINT -> Res.string.biometric_fingerprint
            BiometricType.FACE -> Res.string.biometric_face
            else -> Res.string.biometric_generic
        }
    )

    fun icon(type: BiometricType): ImageVector = when (type) {
        BiometricType.FACE_ID, BiometricType.FACE -> Icons.Filled.Face
        else -> Icons.Filled.Fingerprint
    }
}
