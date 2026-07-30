# Firebase Integration Completion Plan

The project already contains Firebase dependencies and repository code for Authentication and Firestore. However, the essential configuration to make Firebase work on Android is missing.

## User Review Required

> [!IMPORTANT]
> **Missing Configuration File**: You must provide the `google-services.json` file.
> 1. Go to the [Firebase Console](https://console.firebase.google.com/).
> 2. Select your project (or create a new one).
> 3. Add an Android app with the package name `com.example.vorratsheld`.
> 4. Download the `google-services.json` file.
> 5. Place it in the `app/` directory of your project.

## Proposed Changes

### Build Configuration

#### [MODIFY] [root build.gradle.kts](file:///C:/Users/Acheh/Desktop/Android Dev/Vorratsheld/build.gradle.kts)
- Add the Google Services plugin to the `plugins` block.

#### [MODIFY] [app build.gradle.kts](file:///C:/Users/Acheh/Desktop/Android Dev/Vorratsheld/app/build.gradle.kts)
- Apply the Google Services plugin.
- Update Firebase dependencies to use the latest BoM and transition from KTX modules to main modules (as KTX is being deprecated).

## Verification Plan

### Automated Tests
- I will perform a Gradle Sync to ensure the build configuration is valid (note: this will likely fail until `google-services.json` is added).

### Manual Verification
- Once the plugin is applied and `google-services.json` is in place, you should be able to run the app and perform Login/Register actions.
