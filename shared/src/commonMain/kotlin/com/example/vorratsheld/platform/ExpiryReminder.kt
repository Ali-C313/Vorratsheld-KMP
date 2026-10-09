package com.example.vorratsheld.platform

import androidx.compose.runtime.Composable
import com.example.vorratsheld.data.model.Product

// Fragt die Benachrichtigungs-Erlaubnis an und plant die Ablauf-Erinnerungen ein
// Android: täglicher WorkManager-Check (products wird nicht gebraucht)
// iOS: lokale Benachrichtigung pro Produkt, 3 Tage vor Ablauf
@Composable
expect fun ExpiryReminderEffect(products: List<Product>)
