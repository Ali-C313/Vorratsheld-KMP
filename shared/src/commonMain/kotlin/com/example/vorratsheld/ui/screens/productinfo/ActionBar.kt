package com.example.vorratsheld.ui.screens.productinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_delete
import com.example.vorratsheld.resources.action_edit
import com.example.vorratsheld.resources.info_add_to_list
import org.jetbrains.compose.resources.stringResource

// Löschen und Einkaufsliste als Icons, Bearbeiten als Hauptaktion
@Composable
fun ActionBar(onDeleteClick: () -> Unit, onAddToListClick: () -> Unit, onEditClick: () -> Unit) {
    Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            FilledTonalIconButton(
                onClick = onDeleteClick,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier.size(52.dp)
            ) {
                Icon(Icons.Filled.Delete, contentDescription = stringResource(Res.string.action_delete))
            }
            FilledTonalIconButton(onClick = onAddToListClick, modifier = Modifier.size(52.dp)) {
                Icon(Icons.Filled.AddShoppingCart, contentDescription = stringResource(Res.string.info_add_to_list))
            }
            Button(
                onClick = onEditClick,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(stringResource(Res.string.action_edit), modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
