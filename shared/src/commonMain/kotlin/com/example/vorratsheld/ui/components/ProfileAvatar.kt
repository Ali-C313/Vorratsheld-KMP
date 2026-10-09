package com.example.vorratsheld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import kotlin.io.encoding.Base64

// Rundes Profilbild – ohne Foto die Initialen, ohne Namen ein Personen-Icon
@Composable
fun ProfileAvatar(
    photoBase64: String?,
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    val photoBytes = remember(photoBase64) {
        photoBase64?.let { runCatching { Base64.decode(it) }.getOrNull() }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        val initials = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }

        when {
            photoBytes != null -> AsyncImage(
                model = photoBytes,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size)
            )
            initials.isNotEmpty() -> Text(
                text = initials,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = (size.value * 0.38f).sp,
                style = MaterialTheme.typography.titleLarge
            )
            else -> Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileAvatarPreview() {
    VorratsheldTheme {
        ProfileAvatar(photoBase64 = null, name = "Ali Chehab", modifier = Modifier.padding(16.dp))
    }
}
