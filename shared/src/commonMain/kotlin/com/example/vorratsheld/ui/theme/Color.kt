package com.example.vorratsheld.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Helles Farbschema: frisches Salbeigrün + warme Terrakotta-Akzente
// (passt zum Thema "Vorrat/Küche", statt der Standard-Lila-Palette)
val md_light_primary = Color(0xFF3F6B3F)
val md_light_onPrimary = Color(0xFFFFFFFF)
val md_light_primaryContainer = Color(0xFFC0EFB4)
val md_light_onPrimaryContainer = Color(0xFF012201)

val md_light_secondary = Color(0xFF8B5E34)
val md_light_onSecondary = Color(0xFFFFFFFF)
val md_light_secondaryContainer = Color(0xFFFFDCC0)
val md_light_onSecondaryContainer = Color(0xFF2D1600)

val md_light_tertiary = Color(0xFF3A6470)
val md_light_onTertiary = Color(0xFFFFFFFF)

val md_light_background = Color(0xFFFBFDF7)
val md_light_surface = Color(0xFFFBFDF7)
val md_light_onSurface = Color(0xFF1A1C18)
val md_light_surfaceVariant = Color(0xFFDEE5D6)
val md_light_onSurfaceVariant = Color(0xFF424940)
val md_light_outlineVariant = Color(0xFFC2C9BC)

val md_light_error = Color(0xFFBA1A1A)
val md_light_errorContainer = Color(0xFFFFDAD6)
val md_light_onErrorContainer = Color(0xFF410002)

// Dunkles Farbschema: gleiche Farbfamilie, invertierte Tonwerte
val md_dark_primary = Color(0xFFA5D395)
val md_dark_onPrimary = Color(0xFF0F3D0F)
val md_dark_primaryContainer = Color(0xFF255128)
val md_dark_onPrimaryContainer = Color(0xFFC0EFB4)

val md_dark_secondary = Color(0xFFFFB77C)
val md_dark_onSecondary = Color(0xFF4A2900)
val md_dark_secondaryContainer = Color(0xFF693F12)
val md_dark_onSecondaryContainer = Color(0xFFFFDCC0)

val md_dark_tertiary = Color(0xFFA2CDDA)
val md_dark_onTertiary = Color(0xFF00363F)

val md_dark_background = Color(0xFF1A1C18)
val md_dark_surface = Color(0xFF1A1C18)
val md_dark_onSurface = Color(0xFFE3E3DC)
val md_dark_surfaceVariant = Color(0xFF44483E)
val md_dark_onSurfaceVariant = Color(0xFFC2C9BC)
val md_dark_outlineVariant = Color(0xFF44483E)

val md_dark_error = Color(0xFFFFB4AB)
val md_dark_errorContainer = Color(0xFF93000A)
val md_dark_onErrorContainer = Color(0xFFFFDAD6)
// App-Farben außerhalb des Material-Schemas (Kopfbereiche, Sperrbildschirm, Splash)
val Cream = Color(0xFFFBF6EA)
val GreenLight = Color(0xFF5E8C55)
val GreenDark = Color(0xFF2F5530)
val HeaderGreen = listOf(GreenLight, GreenDark)
val LockGradient = Brush.verticalGradient(HeaderGreen)

val SplashGreenLight = Color(0xFF5E8C55)
val SplashGreen = Color(0xFF3F6B3F)
val SplashGreenDark = Color(0xFF2A4A2B)

// Farben des App-Logos
val LogoCapeRed = Color(0xFFB8432F)
val LogoCapeShadow = Color(0xFF8F3122)
val LogoLidColor = Color(0xFFC07A45)
val LogoLidShadow = Color(0xFF8B5E34)
val LogoNeckColor = Color(0xFFEADFC8)
val LogoJarColor = Color(0xFFFBF6EA)
val LogoLabelColor = Color(0xFFE3EFD9)
val LogoStarColor = Color(0xFFE8A93C)
