# Nexus Premium
**Direct — You direct your life the way you want.**

Premium, local-first Android command center. High-definition monochrome aesthetic with Gemini AI intelligence.

## Build APK
1. **Clean & Build**:
   ```powershell
   ./gradlew.bat assembleDebug
   ```
2. **APK Location**:
   `app\.build\app\outputs\apk\debug\app-debug.apk`

---

## Setup Gemini AI
Nexus uses the **Google Generative AI SDK** (no Firebase required).

1. **Get API Key**: Go to [Google AI Studio](https://aistudio.google.com/) and create a free Gemini API key.
2. **Input during Onboarding**: Launch the app and paste the key when prompted.
3. **Change Key**: You can also update the key anytime in **Settings**.

---

## Features
- **Deep Monochrome UI**: Custom "Cred/Grok" inspired aesthetic with pure blacks and crisp whites.
- **Gemini Chat**: Natural language capture for reminders, calls, and meetings.
- **Premium Onboarding**: 3-step glassmorphic flow with permission handling and haptic feedback.
- **Advanced Haptics**: Precision-engineered vibration patterns for every interaction.
- **Local-First**: Your data stays on your device. SQLite (Room) for storage.
- **Hybrid Parser**: Intelligent fallback to local parsing if Gemini is offline.

---

## Technical Stack
- **Languages**: Kotlin + Jetpack Compose
- **AI**: Gemini 2.0 Flash (via Google AI SDK)
- **Database**: Room (SQLite)
- **Dependency Injection**: Hilt / Dagger
- **Persistence**: SharedPreferences + DataStore

---

## Build Prerequisites
- **JDK**: 17+
- **Android SDK**: API 36 (target)
- **Gradle**: 9.3.1 (included)

### Rebuild from Scratch
If you encounter build issues, delete the temporary files:
```powershell
Remove-Item -Path ".build" -Recurse -Force
./gradlew.bat assembleDebug
```

---
*Privacy-first. Premium-designed. Direct control.*
