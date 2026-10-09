import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    // expect/actual für Objekte (LocalAppLocale) ohne Warnung
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    // Android-Teil des Moduls (neues KMP-Plugin ab AGP 9)
    android {
        namespace = "com.example.vorratsheld.shared"
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
        minSdk = libs.versions.androidMinSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }

        // Nötig, damit die Übersetzungen (composeResources) mit in die App kommen
        androidResources {
            enable = true
        }
    }

    // iOS: echtes Gerät + Simulator auf Apple-Silicon-Macs
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // UI
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.components.resources)

            // ViewModel, Lifecycle, Navigation
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.navigation.compose)

            // DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)

            // Netzwerk + Bilder
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // Firebase + Datum
            implementation(libs.gitlive.firebase.auth)
            implementation(libs.gitlive.firebase.firestore)
            implementation(libs.kotlinx.datetime)

            // Profilbild auswählen + verkleinern
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs.compose)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.activity.compose)

            // Barcode-Scanner
            implementation(libs.androidx.camera.camera2)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)
            implementation(libs.mlkit.barcode.scanning)
            implementation(libs.mlkit.text.recognition)
            implementation(libs.androidx.biometric)

            // Täglicher Ablauf-Check im Hintergrund
            implementation(libs.androidx.work.runtime)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

// Texte in allen Sprachen liegen in src/commonMain/composeResources
compose.resources {
    packageOfResClass = "com.example.vorratsheld.resources"
    generateResClass = always
}

dependencies {
    // Damit @Preview in Android Studio etwas anzeigt
    androidRuntimeClasspath(libs.compose.ui.tooling)
}
