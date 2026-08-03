package com.example.vorratsheld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.vorratsheld.notification.NotificationHelper
import com.example.vorratsheld.ui.navigation.VorratsheldNavGraph
import com.example.vorratsheld.ui.theme.VorratsheldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)

        setContent {
            VorratsheldTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    VorratsheldNavGraph()
                }
            }
        }
    }
}