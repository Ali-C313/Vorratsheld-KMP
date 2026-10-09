package com.example.vorratsheld.ui.screens.productinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_back
import com.example.vorratsheld.resources.favorite
import com.example.vorratsheld.ui.theme.HeaderGreen
import org.jetbrains.compose.resources.stringResource

// Großes Produktbild (oder grüner Verlauf mit Icon), darüber Zurück und Favorit
@Composable
fun InfoHeader(product: Product, onBackClick: () -> Unit, onFavoriteToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(Brush.verticalGradient(HeaderGreen))
    ) {
        if (product.imageUrl != null) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Oben abdunkeln, damit die Buttons auf hellen Bildern sichtbar bleiben
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent)))
            )
        } else {
            Icon(
                Icons.Outlined.Inventory2,
                contentDescription = null,
                tint = Color(0xFFFBF6EA).copy(alpha = 0.9f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(96.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            HeaderButton(Icons.AutoMirrored.Filled.ArrowBack, stringResource(Res.string.action_back), onBackClick)
            Spacer(Modifier.weight(1f))
            HeaderButton(
                icon = if (product.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                description = stringResource(Res.string.favorite),
                onClick = onFavoriteToggle,
                tint = if (product.isFavorite) Color(0xFFFFC94A) else Color.White
            )
        }
    }
}
