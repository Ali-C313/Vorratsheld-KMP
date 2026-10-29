package com.example.vorratsheld.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.settings_profile_incomplete
import com.example.vorratsheld.ui.components.ProfileAvatar
import com.example.vorratsheld.ui.editprofile.ProfileUiState
import org.jetbrains.compose.resources.stringResource

// Karte mit Foto, Name und E-Mail, öffnet "Profil bearbeiten"
@Composable
fun ProfileCard(state: ProfileUiState, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(photoBase64 = state.photoBase64, name = state.fullName)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = state.fullName.ifEmpty { stringResource(Res.string.settings_profile_incomplete) },
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = state.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}
