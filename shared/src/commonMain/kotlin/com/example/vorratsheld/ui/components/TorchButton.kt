package com.example.vorratsheld.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.torch_off
import com.example.vorratsheld.resources.torch_on
import com.example.vorratsheld.ui.theme.Gold
import com.example.vorratsheld.ui.theme.GoldDark
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import org.jetbrains.compose.resources.stringResource

// Taschenlampe für die Kamera-Screens, leuchtet gelb wenn an
@Composable
fun TorchButton(
    enabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container by animateColorAsState(if (enabled) Gold else Color.Black.copy(alpha = 0.45f))
    val content by animateColorAsState(if (enabled) GoldDark else Color.White)

    FilledTonalIconButton(
        onClick = onToggle,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = container,
            contentColor = content
        ),
        modifier = modifier
    ) {
        Icon(
            imageVector = if (enabled) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
            contentDescription = stringResource(if (enabled) Res.string.torch_off else Res.string.torch_on)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TorchButtonPreview() {
    VorratsheldTheme {
        TorchButton(enabled = true, onToggle = {}, modifier = Modifier.padding(16.dp))
    }
}
