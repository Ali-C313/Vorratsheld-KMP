package com.example.vorratsheld.ui.screens.productdetail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.platform.ExpiryTextScanner
import com.example.vorratsheld.platform.PlatformBackHandler
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_apply
import com.example.vorratsheld.resources.action_back
import com.example.vorratsheld.resources.expiry_scan_detected
import com.example.vorratsheld.resources.expiry_scan_hint
import com.example.vorratsheld.resources.expiry_scan_manual
import com.example.vorratsheld.resources.expiry_scan_searching
import com.example.vorratsheld.ui.components.TorchButton
import com.example.vorratsheld.util.DateHelper
import com.example.vorratsheld.util.ExpiryDateParser
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

// Vollbild-Kamera, die das MHD von der Verpackung abliest
@Composable
fun ExpiryScanOverlay(
    onDateConfirmed: (Long) -> Unit,
    onManualPick: () -> Unit,
    onClose: () -> Unit
) {
    var lastSeen by remember { mutableStateOf<LocalDate?>(null) }
    var detected by remember { mutableStateOf<LocalDate?>(null) }
    var torchEnabled by remember { mutableStateOf(false) }

    PlatformBackHandler(onBack = onClose)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        ExpiryTextScanner(
            modifier = Modifier.fillMaxSize(),
            torchEnabled = torchEnabled,
            onTextRecognized = { text ->
                val date = ExpiryDateParser.parse(text)
                // Erst anzeigen, wenn zwei Bilder hintereinander dasselbe Datum liefern
                if (date != null && date == lastSeen) detected = date
                lastSeen = date
            }
        )

        TextFrame(
            found = detected != null,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 120.dp)
        )

        FilledTonalIconButton(
            onClick = onClose,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.45f),
                contentColor = Color.White
            ),
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Close, contentDescription = stringResource(Res.string.action_back))
        }

        TorchButton(
            enabled = torchEnabled,
            onToggle = { torchEnabled = !torchEnabled },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp)
        )

        Surface(
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                AnimatedContent(
                    targetState = detected,
                    transitionSpec = { (fadeIn() + scaleIn(initialScale = 0.9f)) togetherWith fadeOut() }
                ) { date ->
                    if (date == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Text(
                                    text = stringResource(Res.string.expiry_scan_searching),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Text(
                                text = stringResource(Res.string.expiry_scan_hint),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(Res.string.expiry_scan_detected),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = DateHelper.formatDate(ExpiryDateParser.toMillis(date)),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Button(
                    onClick = { detected?.let { onDateConfirmed(ExpiryDateParser.toMillis(it)) } },
                    enabled = detected != null,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(stringResource(Res.string.action_apply))
                }

                TextButton(onClick = onManualPick) {
                    Icon(Icons.Filled.EditCalendar, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(
                        text = stringResource(Res.string.expiry_scan_manual),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
