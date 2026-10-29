package com.example.vorratsheld.ui.shoppinglist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_add
import com.example.vorratsheld.resources.shopping_add_hint
import org.jetbrains.compose.resources.stringResource

// Eingabezeile unten, rutscht mit der Tastatur nach oben
@Composable
fun AddItemBar(onAddItem: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    val submit = {
        if (text.isNotBlank()) {
            onAddItem(text)
            text = ""
        }
    }

    Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(stringResource(Res.string.shopping_add_hint)) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { submit() }),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.size(10.dp))
            FilledIconButton(
                onClick = { submit() },
                enabled = text.isNotBlank(),
                modifier = Modifier.size(52.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(Res.string.action_add))
            }
        }
    }
}
