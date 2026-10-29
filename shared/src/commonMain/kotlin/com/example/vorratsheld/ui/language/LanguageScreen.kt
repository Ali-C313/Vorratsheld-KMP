package com.example.vorratsheld.ui.language

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.enums.AppLanguage
import com.example.vorratsheld.data.settings.AppSettings
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_back
import com.example.vorratsheld.resources.language_system
import com.example.vorratsheld.resources.language_title
import com.example.vorratsheld.ui.locale.customAppLocale
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import org.jetbrains.compose.resources.stringResource

// Auswahl wird gespeichert und sofort übernommen, die ganze App baut sich in der neuen Sprache neu auf
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(navController: NavHostController) {
    val selectLanguage: (String?) -> Unit = { code ->
        AppSettings.language = code
        customAppLocale = code
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.language_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back))
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                LanguageRow(
                    name = stringResource(Res.string.language_system),
                    selected = customAppLocale == null,
                    onClick = { selectLanguage(null) }
                )
                HorizontalDivider()
            }
            items(AppLanguage.entries) { language ->
                LanguageRow(
                    name = language.nativeName,
                    selected = customAppLocale == language.code,
                    onClick = { selectLanguage(language.code) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LanguageScreenPreview() {
    VorratsheldTheme {
        LanguageScreen(navController = rememberNavController())
    }
}
