package com.example.vorratsheld.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.ui.theme.Amber
import com.example.vorratsheld.ui.theme.Brick
import com.example.vorratsheld.ui.theme.Cream
import com.example.vorratsheld.ui.theme.LogoCapeShadow
import com.example.vorratsheld.ui.theme.LogoLabelColor
import com.example.vorratsheld.ui.theme.LogoLidShadow
import com.example.vorratsheld.ui.theme.LogoNeckColor
import com.example.vorratsheld.ui.theme.Terracotta
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Das App-Logo (Einmachglas mit Umhang) als Canvas, damit sich die Teile einzeln animieren lassen.
 * Gezeichnet wird in einem 1024er-Raster und auf die Größe des Modifiers skaliert.
 *
 * @param capeAngle Schwung vom Umhang in Grad
 * @param lidOffset Deckel-Verschiebung nach oben (im 1024er-Raster)
 * @param starScale Größe vom Stern (0 = unsichtbar)
 * @param starRotation Drehung vom Stern in Grad
 */
@Composable
fun VorratsheldLogo(
    modifier: Modifier = Modifier,
    capeAngle: Float = 0f,
    lidOffset: Float = 0f,
    starScale: Float = 1f,
    starRotation: Float = 0f
) {
    Canvas(modifier = modifier) {
        val factor = size.minDimension / 1024f
        // Inhalt ist etwas nach unten verschoben gezeichnet, deshalb leicht nach oben ziehen
        scale(factor, factor, pivot = Offset.Zero) {
            translate(top = -25f) {
                drawCape(capeAngle)
                drawJar(lidOffset)
                drawStar(starScale, starRotation)
            }
        }
    }
}

private fun DrawScope.drawCape(angle: Float) {
    rotate(degrees = angle, pivot = Offset(512f, 360f)) {
        val cape = Path().apply {
            moveTo(400f, 360f)
            lineTo(624f, 360f)
            cubicTo(690f, 470f, 760f, 640f, 820f, 820f)
            quadraticTo(770f, 790f, 730f, 815f)
            quadraticTo(680f, 790f, 640f, 815f)
            quadraticTo(590f, 790f, 545f, 815f)
            quadraticTo(500f, 790f, 455f, 815f)
            quadraticTo(410f, 790f, 365f, 815f)
            quadraticTo(320f, 790f, 284f, 818f)
            cubicTo(300f, 640f, 340f, 470f, 400f, 360f)
            close()
        }
        drawPath(cape, Brick)

        val shadow = Path().apply {
            moveTo(400f, 360f)
            cubicTo(340f, 470f, 300f, 640f, 284f, 818f)
            quadraticTo(320f, 790f, 365f, 815f)
            cubicTo(370f, 650f, 390f, 500f, 430f, 370f)
            close()
        }
        drawPath(shadow, LogoCapeShadow)
    }
}

private fun DrawScope.drawJar(lidOffset: Float) {
    // Glas
    drawRoundRect(Cream, Offset(337f, 353f), Size(350f, 410f), CornerRadius(78f))
    // Glanz
    drawRoundRect(Color.White.copy(alpha = 0.75f), Offset(372f, 395f), Size(30f, 230f), CornerRadius(15f))
    // Etikett
    drawRoundRect(LogoLabelColor, Offset(412f, 463f), Size(200f, 190f), CornerRadius(26f))
    // Hals
    drawRoundRect(LogoNeckColor, Offset(397f, 330f), Size(230f, 34f), CornerRadius(10f))
    // Deckel (kann von oben "einrasten")
    translate(top = -lidOffset) {
        drawRoundRect(Terracotta, Offset(377f, 255f), Size(270f, 78f), CornerRadius(22f))
        drawRoundRect(LogoLidShadow.copy(alpha = 0.55f), Offset(377f, 303f), Size(270f, 30f), CornerRadius(12f))
    }
}

private fun DrawScope.drawStar(starScale: Float, rotation: Float) {
    if (starScale <= 0f) return
    val center = Offset(512f, 562f)
    val star = Path().apply {
        for (i in 0 until 10) {
            val radius = if (i % 2 == 0) 62f else 28f
            val angle = -PI / 2 + i * PI / 5
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    rotate(rotation, pivot = center) {
        scale(starScale, starScale, pivot = center) {
            drawPath(star, Amber)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VorratsheldLogoPreview() {
    VorratsheldTheme {
        VorratsheldLogo(modifier = Modifier.size(160.dp))
    }
}
