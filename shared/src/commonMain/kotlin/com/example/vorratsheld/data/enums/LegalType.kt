package com.example.vorratsheld.data.enums

import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.legal_imprint_text
import com.example.vorratsheld.resources.legal_privacy_text
import com.example.vorratsheld.resources.legal_terms_text
import com.example.vorratsheld.resources.settings_imprint
import com.example.vorratsheld.resources.settings_privacy
import com.example.vorratsheld.resources.settings_terms
import org.jetbrains.compose.resources.StringResource

// Welche Rechtstexte es gibt, Titel + Inhalt aus den Ressourcen
enum class LegalType(val title: StringResource, val text: StringResource) {
    TERMS(Res.string.settings_terms, Res.string.legal_terms_text),
    PRIVACY(Res.string.settings_privacy, Res.string.legal_privacy_text),
    IMPRINT(Res.string.settings_imprint, Res.string.legal_imprint_text)
}
