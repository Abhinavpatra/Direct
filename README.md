# Nexus

Local-first Android command center. Kotlin + Jetpack Compose.

## Quick Install

1. **Phone**: Enable **Developer Options** > **USB Debugging**.
2. **PC**: Connect phone via USB.
3. **Run**:
   ```powershell
   ./gradlew installDebug
   ```

## ## FOR YOU TO DO (Mandatory Setup)
The app requires Firebase for AI features. Follow these steps to make it work:

1. **Firebase Project**: Create a project in [Firebase Console](https://console.firebase.google.com/).
2. **Add Android App**: Use package name `com.nexus.app`.
3. **Config File**: Download `google-services.json` and place it in the `app/` folder.
4. **Enable Vertex AI**:
   - Go to the Firebase Console.
   - Select **Build > Vertex AI**.
   - Enable the **Vertex AI for Firebase** (AI Logic) SDK.
   - Note: This typically requires the **Blaze (Pay as you go) plan**, though it has a generous free tier.
5. **On-Device AI**: Currently, the app uses a basic pattern-matching fallback when offline. To use **Gemini Nano**, you must use a supported device (e.g., Pixel 8+) and configure the [Google AI Edge SDK](https://ai.google.dev/edge/gemini-nano).



once done : 
run 
./gradlew assembleDebug

## Features
- AI Natural Language Input (Online + Offline Fallback)
- Personal Agenda + Calendar Integration
- Weight & Biometrics Tracking (Canvas Charts)
- Local JSON Backup/Restore
- Modern Onboarding Flow

## Project Status
- [x] **Onboarding Flow**: Completed.
- [x] **Dark Mode**: Refined contrast and depth.
- [x] **Offline AI**: Implemented basic functional fallback.
- [x] **Hybrid AI Repository**: Automatically switches to local when remote fails.

---
*Privacy-first. Local-only storage. Smart assistance.*
