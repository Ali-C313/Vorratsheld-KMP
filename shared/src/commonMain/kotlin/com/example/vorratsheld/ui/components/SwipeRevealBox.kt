package com.example.vorratsheld.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

// Inhalt lässt sich zur Seite wischen, dahinter liegen Aktions-Buttons
@Composable
fun SwipeRevealBox(
    isOpen: Boolean,
    onOpenChange: (Boolean) -> Unit,
    actionsWidth: Dp,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val revealPx = with(LocalDensity.current) { actionsWidth.toPx() }
    // Bei Arabisch liegen die Buttons links, also andersherum wischen
    val direction = if (LocalLayoutDirection.current == LayoutDirection.Rtl) -1f else 1f
    val offset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val springSpec = spring<Float>(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)

    LaunchedEffect(isOpen) {
        offset.animateTo(if (isOpen) -revealPx else 0f, springSpec)
    }

    Box(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.matchParentSize(),
            content = actions
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(offset.value.roundToInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            offset.snapTo((offset.value + delta * direction).coerceIn(-revealPx * 1.2f, 0f))
                        }
                    },
                    onDragStopped = { velocity ->
                        val v = velocity * direction
                        val open = when {
                            v < -800f -> true
                            v > 800f -> false
                            else -> offset.value < -revealPx / 2
                        }
                        if (open == isOpen) {
                            offset.animateTo(if (open) -revealPx else 0f, springSpec)
                        } else {
                            onOpenChange(open)
                        }
                    }
                )
        ) {
            content()
        }
    }
}
