package com.example.vorratsheld.ui.screens.legal

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.enums.LegalType
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_back
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import org.jetbrains.compose.resources.stringResource

// Zeigt AGB, Datenschutz oder Impressum
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(
    navController: NavHostController,
    type: LegalType
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(type.title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Text(
            text = stringResource(type.text),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LegalScreenPreview() {
    VorratsheldTheme {
        LegalScreen(navController = rememberNavController(), type = LegalType.IMPRINT)
    }
}
