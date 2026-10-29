package com.example.vorratsheld.data.model

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.StringResource

// Inhalt einer Onboarding-Seite, Akzentfarbe färbt Hintergrund-Blob und Buttons
data class OnboardingPage(
    val title: StringResource,
    val text: StringResource,
    val accent: Color
)
