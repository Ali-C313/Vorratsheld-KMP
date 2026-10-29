package com.example.vorratsheld.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.app_name
import com.example.vorratsheld.resources.splash_tagline
import com.example.vorratsheld.ui.components.VorratsheldLogo
import com.example.vorratsheld.ui.theme.Amber
import com.example.vorratsheld.ui.theme.Cream
import com.example.vorratsheld.ui.theme.Green
import com.example.vorratsheld.ui.theme.GreenDark
import com.example.vorratsheld.ui.theme.GreenLight
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import kotlin.math.sin
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/**
 * Animierter Ladescreen: Glas springt rein, Deckel rastet ein, Stern blitzt auf,
 * Umhang weht, dazu Wellenringe und Glitzer. Danach geht's per [onFinished] weiter.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val currentOnFinished by rememberUpdatedState(onFinished)

    val logoScale = remember { Animatable(0f) }
    val lidOffset = remember { Animatable(260f) }
    val starScale = remember { Animatable(0f) }
    val starRotation = remember { Animatable(-180f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleOffset = remember { Animatable(40f) }
    val taglineAlpha = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }

    // Dauerhafte Animationen: Umhang weht, Ringe pulsieren, Glitzer funkelt
    val infinite = rememberInfiniteTransition()
    val capeAngle by infinite.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    val ringProgress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing))
    )
    val sparkle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2 * kotlin.math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing))
    )

    LaunchedEffect(Unit) {
        // 1. Glas federt rein
        launch {
            logoScale.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow))
        }
        delay(350)
        // 2. Deckel fällt drauf
        launch {
            lidOffset.animateTo(0f, spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessMediumLow))
        }
        delay(450)
        // 3. Stern dreht sich rein
        listOf(
            async { starScale.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium)) },
            async { starRotation.animateTo(0f, tween(600, easing = FastOutSlowInEasing)) },
            async {
                delay(150)
                launch { titleAlpha.animateTo(1f, tween(500)) }
                titleOffset.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow))
            }
        ).awaitAll()
        taglineAlpha.animateTo(1f, tween(450))
        delay(700)
        // 4. Sanft ausblenden und weiter
        exitAlpha.animateTo(0f, tween(350))
        currentOnFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(GreenLight, Green, GreenDark),
                    radius = 1400f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(exitAlpha.value)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(260.dp)) {
                // Wellenringe hinter dem Logo
                Canvas(modifier = Modifier.size(260.dp)) {
                    val maxRadius = size.minDimension / 2
                    for (i in 0 until 3) {
                        val p = (ringProgress + i / 3f) % 1f
                        drawCircle(
                            color = Cream.copy(alpha = (1f - p) * 0.35f * logoScale.value.coerceIn(0f, 1f)),
                            radius = maxRadius * (0.45f + 0.55f * p),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                    // Glitzer um das Glas
                    if (starScale.value > 0.5f) {
                        for (i in 0 until 6) {
                            val angle = sparkle + i * (kotlin.math.PI / 3).toFloat()
                            val r = maxRadius * 0.78f
                            val twinkle = (sin(sparkle * 2 + i) + 1f) / 2f
                            drawCircle(
                                color = Amber.copy(alpha = 0.3f + 0.7f * twinkle),
                                radius = (2.5f + 3f * twinkle).dp.toPx(),
                                center = Offset(
                                    center.x + r * kotlin.math.cos(angle),
                                    center.y + r * sin(angle)
                                )
                            )
                        }
                    }
                }

                VorratsheldLogo(
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer {
                            scaleX = logoScale.value
                            scaleY = logoScale.value
                        },
                    capeAngle = capeAngle,
                    lidOffset = lidOffset.value,
                    starScale = starScale.value,
                    starRotation = starRotation.value
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.app_name),
                color = Cream,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha.value
                    translationY = titleOffset.value * density
                }
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.splash_tagline),
                color = Cream.copy(alpha = 0.85f),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    VorratsheldTheme {
        SplashScreen(onFinished = {})
    }
}
