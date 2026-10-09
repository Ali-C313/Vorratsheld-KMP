package com.example.vorratsheld.ui.screens.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.data.enums.SupportCategory
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_back
import com.example.vorratsheld.resources.help_category
import com.example.vorratsheld.resources.help_intro
import com.example.vorratsheld.resources.help_message
import com.example.vorratsheld.resources.help_reply_email
import com.example.vorratsheld.resources.help_send
import com.example.vorratsheld.resources.help_sent
import com.example.vorratsheld.resources.help_subject
import com.example.vorratsheld.resources.help_title
import com.example.vorratsheld.ui.state.SupportUiState
import org.jetbrains.compose.resources.stringResource

// Hilfe-Formular: Kategorie, Betreff, Nachricht, Antwort-Adresse
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun HelpContent(
    uiState: SupportUiState,
    onBackClick: () -> Unit,
    onCategoryChange: (SupportCategory) -> Unit,
    onSubjectChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onReplyEmailChange: (String) -> Unit,
    onSend: () -> Unit,
    onErrorShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onErrorShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.help_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Nach dem Absenden kurze Bestätigung über dem (wieder leeren) Formular
            if (uiState.isSent) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = stringResource(Res.string.help_sent),
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }

            Text(text = stringResource(Res.string.help_intro), style = MaterialTheme.typography.bodyMedium)

            Text(text = stringResource(Res.string.help_category), style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SupportCategory.entries.forEach { category ->
                    FilterChip(
                        selected = uiState.category == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(stringResource(category.label)) }
                    )
                }
            }

            OutlinedTextField(
                value = uiState.subject,
                onValueChange = onSubjectChange,
                label = { Text(stringResource(Res.string.help_subject)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.message,
                onValueChange = onMessageChange,
                label = { Text(stringResource(Res.string.help_message)) },
                minLines = 6,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.replyEmail,
                onValueChange = onReplyEmailChange,
                label = { Text(stringResource(Res.string.help_reply_email)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onSend,
                enabled = !uiState.isSending,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(Res.string.help_send))
                }
            }
        }
    }
}
