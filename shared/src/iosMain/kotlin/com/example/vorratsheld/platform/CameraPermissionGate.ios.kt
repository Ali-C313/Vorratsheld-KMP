package com.example.vorratsheld.platform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.scan_no_permission
import com.example.vorratsheld.resources.scan_open_settings
import org.jetbrains.compose.resources.stringResource
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

// Fragt die Kamera-Berechtigung ab und zeigt den Inhalt erst, wenn sie erteilt ist
@Composable
internal fun CameraPermissionGate(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    var permission by remember {
        mutableStateOf(AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo))
    }

    // Beim ersten Mal fragt iOS selbst nach (Text aus NSCameraUsageDescription)
    LaunchedEffect(Unit) {
        if (permission == AVAuthorizationStatusNotDetermined) {
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { _ ->
                dispatch_async(dispatch_get_main_queue()) {
                    permission = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
                }
            }
        }
    }

    when (permission) {
        AVAuthorizationStatusAuthorized -> content()
        AVAuthorizationStatusNotDetermined -> Unit
        else -> {
            // Einmal abgelehnt geht es nur noch über die Einstellungen
            Column(
                modifier = modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(Res.string.scan_no_permission),
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = {
                        val url = NSURL(string = UIApplicationOpenSettingsURLString)
                        UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any?>(), completionHandler = null)
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(stringResource(Res.string.scan_open_settings))
                }
            }
        }
    }
}
