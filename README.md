<div align="center">

<img src="docs/banner.svg" alt="Vorratsheld" width="100%"/>

<br/>

![Kotlin](https://img.shields.io/badge/Kotlin-2.4-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.11-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Android](https://img.shields.io/badge/Android-24+-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-15.3+-000000?style=for-the-badge&logo=apple&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth_%26_Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

**Eine App, zwei Plattformen: Lebensmittel scannen, Haltbarkeit im Blick behalten und weniger wegwerfen.**

[Funktionen](#funktionen) · [Screens](#screens) · [Architektur](#architektur) · [Tech-Stack](#tech-stack) · [Installation](#installation)

</div>

---

## Die Idee

**Vorratsheld** ist eine Vorrats-App für den eigenen Haushalt. Sie zeigt, was zu Hause ist und was bald abläuft.

Produkte werden per Barcode gescannt. Das Mindesthaltbarkeitsdatum liest die Kamera direkt von der Verpackung ab. Kurz bevor etwas abläuft, kommt eine Erinnerung, und was aufgebraucht ist, landet automatisch auf der Einkaufsliste.

Android und iOS teilen sich **eine gemeinsame Codebasis**: Logik, Daten und Oberfläche sind mit Kotlin Multiplatform und Compose Multiplatform geschrieben. Nur Kamera, Biometrie und Benachrichtigungen sind nativ für die jeweilige Plattform umgesetzt.

## Ablauf

<img src="docs/flow.svg" alt="Ablauf: Scannen, MHD lesen, Vorrat, Erinnern, Einkaufen" width="100%"/>

## Funktionen

<table>
<tr>
<td width="50%" valign="top">

### Scannen
- Barcode-Scan mit automatischem Abruf von Name, Marke und Bild über **Open Food Facts**
- **MHD-Erkennung** per Texterkennung direkt auf dem Gerät, ohne Cloud
- Taschenlampe für dunkle Küchen und spiegelnde Verpackungen

</td>
<td width="50%" valign="top">

### Vorrat
- Übersicht mit Ablauf-Farben: frisch, bald fällig, abgelaufen
- Nach links wischen zum **Bearbeiten oder Löschen**, mit Rückgängig-Funktion
- Detailansicht mit Menge, Lagerort, MHD und Favoriten

</td>
</tr>
<tr>
<td width="50%" valign="top">

### Erinnerungen
- Hinweis **3 Tage vor Ablauf**
- Android: täglicher Hintergrund-Check mit WorkManager
- iOS: geplante lokale Benachrichtigungen

</td>
<td width="50%" valign="top">

### Einkaufsliste
- Abhaken, Menge per Stepper, Wischen zum Löschen
- Fortschrittsbalken: wie viel schon im Korb ist
- Aufgebrauchte Favoriten landen automatisch auf der Liste

</td>
</tr>
<tr>
<td width="50%" valign="top">

### Sicherheit
- App-Sperre mit **Face ID, Touch ID oder Fingerabdruck**
- Eigener App-PIN als Ausweichlösung
- Sperrzeit einstellbar: sofort, nach 1 oder 5 Minuten, nur beim Start

</td>
<td width="50%" valign="top">

### Sprachen & Profil
- **10 Sprachen**, direkt in der App umschaltbar
- Animierter Splash-Screen und Onboarding
- Profil mit Foto, Hilfe-Formular, Rechtstexte

</td>
</tr>
</table>

## Screens

<div align="center">
<img src="docs/screens.svg" alt="Übersicht, Produktansicht, Einkaufsliste und App-Sperre" width="100%"/>
</div>

## Architektur

Die App folgt **MVVM** mit dem **Repository-Pattern**. Jedes Feature hat einen eigenen Ordner mit Screen, ViewModel und UiState. Jedes Repository hat ein Interface und zwei Umsetzungen: eine echte mit Firebase oder API und eine Mock-Version mit Testdaten. Koin verbindet alles. Dadurch laufen alle Compose-Previews ohne Internet und ohne Firebase.

```mermaid
flowchart LR
    subgraph UI["UI · Compose Multiplatform"]
        S[Screens] --> C[Components]
    end
    subgraph VM["ViewModel"]
        V["ViewModels<br/>StateFlow + UiState"]
    end
    subgraph DATA["Data"]
        R{{Repository-Interfaces}}
        I["*RepositoryImpl<br/>Firebase · Ktor"]
        M["*RepositoryMock<br/>Testdaten für Previews"]
    end
    subgraph EXT["Extern"]
        F[(Firestore)]
        A[Firebase Auth]
        O[Open Food Facts]
    end

    S -- Events --> V
    V -- State --> S
    V --> R
    R -.-> I
    R -.-> M
    I --> F & A & O

    classDef ui fill:#E3EFD9,stroke:#5E8C55,color:#1A1C18
    classDef vm fill:#FFF3CD,stroke:#E8A93C,color:#1A1C18
    classDef data fill:#FBF6EA,stroke:#C07A45,color:#1A1C18
    classDef ext fill:#F0F0F0,stroke:#9AA096,color:#1A1C18
    class S,C ui
    class V vm
    class R,I,M data
    class F,A,O ext
```

### Plattform-Code mit `expect` / `actual`

| Funktion | Android | iOS |
|---|---|---|
| Kamera | CameraX | AVFoundation |
| Barcode | ML Kit Barcode Scanning | AVFoundation Metadata |
| Texterkennung (MHD) | ML Kit Text Recognition | Apple Vision |
| Biometrie | BiometricPrompt | LocalAuthentication |
| Erinnerungen | WorkManager + Notifications | UserNotifications |
| Lokaler Speicher | SharedPreferences | NSUserDefaults |

## Tech-Stack

| Bereich | Technologie |
|---|---|
| **Sprache** | Kotlin 2.4 (Multiplatform) |
| **UI** | Compose Multiplatform 1.11, Material 3 |
| **Architektur** | MVVM, Repository-Pattern, `StateFlow` |
| **Dependency Injection** | Koin |
| **Navigation** | Navigation Compose mit typsicheren Routen |
| **Backend** | Firebase Auth + Cloud Firestore (GitLive SDK) |
| **Netzwerk** | Ktor Client + kotlinx.serialization |
| **Bilder** | Coil 3, FileKit |
| **Datum** | kotlinx-datetime |

## Projektstruktur

```
Vorratsheld/
├── androidApp/            Android-Einstieg (Activity, Application, Manifest)
├── iosApp/                Xcode-Projekt, Firebase per Swift Package Manager
├── shared/
│   └── src/
│       ├── commonMain/    Gemeinsamer Code für Android und iOS
│       │   ├── data/
│       │   │   ├── enums/        Enums, eine Datei pro Enum
│       │   │   ├── model/        Data Classes, eine Datei pro Model
│       │   │   ├── remote/       APIs, Collection-Namen, DTOs
│       │   │   ├── repository/   Interface · Impl · Mock
│       │   │   └── settings/     Lokale Einstellungen
│       │   ├── di/               Koin-Modul
│       │   ├── platform/         expect-Deklarationen
│       │   ├── ui/
│       │   │   ├── components/   Wiederverwendbare Composables
│       │   │   ├── navigation/   NavGraph und Routen
│       │   │   ├── theme/        Farben, Formen, Schriften
│       │   │   └── <feature>/    Pro Feature Screen, ViewModel, UiState
│       │   │                     (overview, shoppinglist, productinfo, ...)
│       │   └── util/             Helper (Datum, Biometrie, MHD-Parser)
│       ├── androidMain/   actual-Code für Android
│       └── iosMain/       actual-Code für iOS
└── firestore.rules        Sicherheitsregeln für Firestore
```

## Datenschutz & Sicherheit

- **Texterkennung offline:** Fotos der Verpackung verlassen das Gerät nicht.
- **PIN nie im Klartext:** Gespeichert wird nur ein gesalzener SHA-256-Hash.
- **Eigene Daten bleiben privat:** Die Firestore-Regeln erlauben jedem Nutzer nur den Zugriff auf seine eigenen Produkte, Listen und sein Profil.

## Installation

<details>
<summary><b>Voraussetzungen</b></summary>

<br/>

- Android Studio mit dem Kotlin-Multiplatform-Plugin
- Xcode (für iOS, nur auf dem Mac)
- JDK 17 oder neuer
- Ein eigenes Firebase-Projekt

</details>

<details>
<summary><b>Firebase einrichten</b></summary>

<br/>

1. In der [Firebase Console](https://console.firebase.google.com) ein Projekt anlegen
2. Eine Android-App (`com.example.vorratsheld`) hinzufügen und die `google-services.json` nach `androidApp/` legen
3. Eine iOS-App mit derselben Bundle-ID hinzufügen und die `GoogleService-Info.plist` nach `iosApp/iosApp/` legen
4. **Authentication** mit E-Mail/Passwort aktivieren
5. **Firestore** anlegen und die Regeln aus [`firestore.rules`](firestore.rules) veröffentlichen

</details>

<details>
<summary><b>App starten</b></summary>

<br/>

```bash
git clone https://github.com/Ali-C313/Vorratsheld.git
cd Vorratsheld
```

- **Android:** Projekt in Android Studio öffnen, Gradle synchronisieren und die Run-Konfiguration `androidApp` starten
- **iOS:** `iosApp/iosApp.xcodeproj` in Xcode öffnen, unter *Signing* das eigene Team wählen und starten

</details>

<details>
<summary><b>Optional: Support-Mails per EmailJS</b></summary>

<br/>

Anfragen aus dem Hilfe-Formular werden immer in Firestore gespeichert. Damit sie zusätzlich als Mail ankommen, die Zugangsdaten von [emailjs.com](https://www.emailjs.com) in `shared/src/commonMain/kotlin/com/example/vorratsheld/data/remote/EmailJsConfig.kt` eintragen.

</details>

## Roadmap

- [x] Kotlin Multiplatform für Android und iOS
- [x] Barcode- und MHD-Scan mit Taschenlampe
- [x] Einkaufsliste mit Stepper und Wisch-Gesten
- [x] App-Sperre mit Biometrie und PIN
- [x] 10 Sprachen
- [ ] Supabase als alternatives Backend
- [ ] Unit- und UI-Tests
- [ ] Veröffentlichung im Play Store und App Store

## Autor

<table>
<tr>
<td>

**Ali Chehab**<br/>
App-Entwicklung (Android & iOS) · Syntax Institut

[![GitHub](https://img.shields.io/badge/GitHub-Ali--C313-181717?style=flat-square&logo=github)](https://github.com/Ali-C313)

</td>
</tr>
</table>
