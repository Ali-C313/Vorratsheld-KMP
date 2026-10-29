package com.example.vorratsheld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.ui.theme.Cream
import com.example.vorratsheld.ui.theme.LockGradient
import com.example.vorratsheld.ui.theme.RedSoft
import com.example.vorratsheld.ui.theme.VorratsheldTheme

// PIN-Punkte und Ziffernfeld, für den Sperrbildschirm und das Festlegen des PINs
@Composable
fun PinPad(
    title: String,
    subtitle: String?,
    entered: String,
    onDigit: (Char) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    shakeTrigger: Int = 0,
    enabled: Boolean = true,
    extraKey: (@Composable () -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Cream,
            textAlign = TextAlign.Center
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Cream.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp)
            )
        }

        PinDots(
            filled = entered.length,
            isError = errorText != null,
            shakeTrigger = shakeTrigger,
            modifier = Modifier.padding(top = 28.dp)
        )

        // Feste Höhe, damit das Ziffernfeld nicht springt
        Box(modifier = Modifier.height(40.dp), contentAlignment = Alignment.Center) {
            if (errorText != null) {
                Text(
                    text = errorText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = RedSoft,
                    textAlign = TextAlign.Center
                )
            }
        }

        NumberPad(
            onDigit = onDigit,
            onDelete = onDelete,
            enabled = enabled,
            extraKey = extraKey
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PinPadPreview() {
    VorratsheldTheme {
        Box(modifier = Modifier.background(LockGradient).padding(24.dp)) {
            PinPad(
                title = "Vorratsheld ist gesperrt",
                subtitle = "Gib deinen PIN ein",
                entered = "12",
                onDigit = {},
                onDelete = {}
            )
        }
    }
}
