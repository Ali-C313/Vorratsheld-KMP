package com.example.vorratsheld.ui.help

import com.example.vorratsheld.data.enums.SupportCategory
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.help_category_feature
import com.example.vorratsheld.resources.help_category_problem
import com.example.vorratsheld.resources.help_category_question
import org.jetbrains.compose.resources.StringResource

// Text für die Auswahl-Chips
val SupportCategory.label: StringResource
    get() = when (this) {
        SupportCategory.PROBLEM -> Res.string.help_category_problem
        SupportCategory.FEATURE -> Res.string.help_category_feature
        SupportCategory.QUESTION -> Res.string.help_category_question
    }
