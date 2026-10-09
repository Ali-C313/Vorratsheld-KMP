package com.example.vorratsheld

import androidx.compose.ui.window.ComposeUIViewController
import com.example.vorratsheld.di.initKoin
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.apps
import dev.gitlive.firebase.initialize

// Wird von ContentView.swift angezeigt
fun MainViewController() = ComposeUIViewController { App() }

// Einmal beim App-Start aus iOSApp.swift aufrufen (in Swift: MainViewControllerKt.doInitIosApp())
// Firebase liest dabei die GoogleService-Info.plist
fun initIosApp() {
    if (Firebase.apps().isEmpty()) {
        Firebase.initialize()
    }
    initKoin()
}
