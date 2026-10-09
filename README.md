# Vorratsheld

Vorratsheld behält den Vorrat zu Hause im Blick. Produkte werden per Barcode gescannt, das Mindesthaltbarkeitsdatum liest die Kamera direkt von der Verpackung ab, und kurz vor Ablauf kommt eine Erinnerung. Dazu gibt es eine Einkaufsliste, auf der leere Favoriten automatisch landen.

Die App läuft auf **Android und iOS** mit einer gemeinsamen Codebasis (Kotlin Multiplatform + Compose Multiplatform).

## Funktionen

- **Barcode-Scan** – Name, Marke und Bild kommen von Open Food Facts
- **MHD-Scan** – Texterkennung auf dem Gerät (ML Kit / Apple Vision), Taschenlampe für dunkle Umgebungen
- **Übersicht** – Vorrat mit Ablauf-Farben, nach links wischen zum Bearbeiten oder Löschen, Tippen öffnet die Produktansicht
- **Erinnerungen** – Android über WorkManager, iOS über lokale Benachrichtigungen
- **Einkaufsliste** – Abhaken, Menge per Stepper, Wischen zum Löschen, Fortschritt im Kopf
- **App-Sperre** – Face ID / Touch ID / Fingerabdruck mit eigenem App-PIN als Ersatz, Sperrzeit einstellbar
- **Konto** – Login und Registrierung mit Firebase Auth, Profilbild, E-Mail und Passwort ändern, Konto löschen
- **Hilfe-Formular** – Anfragen landen in Firestore und optional per Mail (EmailJS)
- **10 Sprachen** – Deutsch, Englisch, Türkisch, Arabisch, Französisch, Spanisch, Italienisch, Polnisch, Russisch, Ukrainisch

## Technik

| Bereich | Bibliothek |
|---|---|
| Sprache / UI | Kotlin 2.4, Compose Multiplatform 1.11, Material 3 |
| Architektur | MVVM, Repository-Pattern, Koin |
| Navigation | Navigation Compose (typsichere Routen) |
| Backend | Firebase Auth + Firestore über GitLive |
| Netzwerk | Ktor (Open Food Facts, EmailJS) |
| Bilder | Coil 3, FileKit |
| Kamera | CameraX + ML Kit (Android), AVFoundation + Vision (iOS) |
| Biometrie | androidx.biometric (Android), LocalAuthentication (iOS) |
| Datum | kotlinx-datetime |

Android ab API 24, iOS ab 15.3.

## Projektaufbau

```
androidApp/   Android-Einstieg (MainActivity, Application, Manifest)
iosApp/       Xcode-Projekt, Firebase per Swift Package Manager
shared/       Gemeinsamer Code für beide Plattformen
  commonMain/
    data/
      enums/        Enums, eine pro Datei
      model/        Data Classes, eine pro Datei
      remote/       APIs, Collection-Namen, DTOs
      repository/   Interface + Impl (Firebase/API) + Mock (Previews)
      settings/     Lokale Einstellungen
    di/             Koin-Modul
    platform/       expect-Deklarationen (Kamera, Biometrie, ...)
    ui/
      components/   Wiederverwendbare Composables
      navigation/   NavGraph und Routen
      screens/      Ein Ordner pro Screen
      state/        UiStates
      theme/        Farben, Formen, Schriften
      viewmodel/    ViewModels
    util/           Helper (Datum, Biometrie, MHD-Parser, ...)
  androidMain/      actual-Implementierungen, Worker, Benachrichtigungen
  iosMain/          actual-Implementierungen für iOS
firestore.rules     Sicherheitsregeln für Firestore
```

Jeder Screen ist aufgeteilt in `XScreen` (holt die Daten aus dem ViewModel, navigiert) und `XContent` (reine UI). Alle Screens haben Previews, die mit den Mock-Repositories laufen – dafür braucht es kein Firebase.

## Einrichten

1. Projekt in Android Studio öffnen und Gradle synchronisieren
2. Firebase-Projekt anlegen, `google-services.json` nach `androidApp/` und `GoogleService-Info.plist` nach `iosApp/iosApp/`
3. In Firebase Authentication (E-Mail/Passwort) und Firestore aktivieren, Regeln aus `firestore.rules` veröffentlichen
4. Optional: Zugangsdaten von emailjs.com in `shared/.../data/remote/EmailJsConfig.kt` eintragen – ohne werden Support-Anfragen nur in Firestore gespeichert
5. Android: Run-Konfiguration `androidApp` starten
6. iOS: `iosApp/iosApp.xcodeproj` in Xcode öffnen, Team auswählen und starten

## Firestore-Collections

| Collection | Inhalt |
|---|---|
| `products` | Vorrat, gefiltert nach `userId` |
| `shoppingItems` | Einkaufsliste, gefiltert nach `userId` |
| `users` | Profil, Dokument-ID = uid |
| `supportRequests` | Anfragen aus dem Hilfe-Formular |
