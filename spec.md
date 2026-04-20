# Nexus Technical Specification
Last updated: April 19, 2026
Status: Corrected implementation specification

## 1. Product Summary

Nexus is a local-first Android personal command center built with Kotlin and Jetpack Compose.

Primary goals:
- Natural-language task and reminder capture
- Weight and biometrics tracking
- Agenda-style dashboard
- Local persistence with zero custom backend
- Clean, modular, maintainable code

Product constraints:
- Single-user app
- Android phone/tablet target
- Privacy-first design
- Cloud AI allowed for MVP only through official Firebase AI Logic SDK
- Architecture must support later swap to on-device AI without UI rewrite

## 2. Core Product Rules

- UI must use Jetpack Compose only
- Code must follow clean architecture boundaries and avoid repeated logic
- Shared logic belongs in repositories, use cases, or utility modules, not duplicated across screens
- Storage must remain local-first using Room
- Business logic must be testable without UI
- External dependencies must be minimal and official where practical
- Reminder delivery must be reliable; exact reminder time must use AlarmManager, not WorkManager alone

## 3. Build and Tooling Baseline

This project must be set up in this order before feature code.

### 3.1 Install First

Required local tools:
- Android Studio Panda 3 Patch 1 or newer stable
- JDK 17
- Android SDK Platform 36
- Android SDK Build-Tools 36.0.0
- Android Emulator or physical Android device
- Gradle 9.3.1 via wrapper

Important:
- No `npx` setup is needed for this project
- This is a native Android Gradle project, not a Node scaffold

### 3.2 Create or Fix Project Scaffold First

Before feature work, project must contain:
- `:app` Android application module
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `MainActivity`
- application theme
- Compose entry point
- base package structure

If missing, scaffold these first.

### 3.3 Use Current Stable Build Stack

Approved baseline versions:
- Android Gradle Plugin: 9.1.1
- Gradle: 9.3.1
- Kotlin: 2.3.20
- Compose BOM: 2026.03.00
- Navigation Compose: 2.9.7
- Room: 2.8.4
- WorkManager: 2.10.5
- AndroidX Hilt Navigation Compose: 1.3.0
- Dagger Hilt: 2.52
- Firebase Android BoM: 34.7.0
- Firebase AI Logic: `com.google.firebase:firebase-ai`

Compose compiler guidance:
- Because Kotlin is 2.0+, use `org.jetbrains.kotlin.plugin.compose`
- Do not use old `composeOptions.kotlinCompilerExtensionVersion` flow for new setup

## 4. Required Architecture

Use modular, non-repetitive structure with clear ownership.

Recommended package layout:

```text
app/src/main/java/com/nexus/app/
  NexusApp.kt
  MainActivity.kt
  di/
  data/
    local/
      dao/
      entity/
      database/
    remote/
    repository/
    mapper/
  domain/
    model/
    repository/
    usecase/
    validation/
  ui/
    navigation/
    screen/
      input/
      biometrics/
      agenda/
      settings/
    component/
    state/
    theme/
  reminder/
    alarm/
    notification/
    receiver/
  calendar/
  backup/
  core/
    common/
    time/
    permission/
    result/
```

Architecture rules:
- UI layer handles rendering and user events only
- Use cases contain business rules
- Repositories abstract data sources
- Mappers convert between Room entities, DTOs, and domain models
- Time parsing, permission checks, and validation must be centralized
- No screen should directly talk to Room, Firebase, or AlarmManager

## 5. Feature Scope

### 5.1 Input Processor

Responsibilities:
- Single natural-language input flow
- Send text to Firebase AI Logic
- Request strict JSON output
- Validate mandatory fields
- Reject incomplete input when reason is missing
- Save accepted task locally
- Schedule reminder

Required output shape:

```json
{
  "task": "string",
  "time": "ISO-8601 timestamp",
  "reason": "string",
  "actionType": "reminder | call | meeting | other"
}
```

Validation rules:
- `task` must be non-empty
- `time` must parse to a future instant
- `reason` must be non-empty
- `actionType` must match allowed enum values

### 5.2 Biometrics Tracker

Responsibilities:
- Log weight
- Store timestamp and optional note
- Edit prior entries
- Visualize trend using Compose Canvas

Rules:
- Keep graph dependency-free
- Reuse shared date formatting and chart scaling helpers

### 5.3 Agenda Dashboard

Responsibilities:
- Show tasks grouped by date
- Show pending reminders
- Show relevant calendar events
- Support edit actions for local records

Calendar integration rules:
- Read calendar data via `CalendarContract`
- For MVP write flow, use insert/edit intents unless silent provider sync is explicitly required
- If true background calendar writes are needed later, add direct provider write support behind repository boundary

### 5.4 Local Backup

Responsibilities:
- Export Room-backed app data to JSON
- Save export to Downloads or user-selected document location
- Import backup JSON
- Validate schema before import

## 6. Correct Reminder Strategy

Previous spec incorrectly treated WorkManager as exact-alarm scheduling.

Correct rule:
- Use `AlarmManager.setExactAndAllowWhileIdle()` for user-visible exact reminders
- Use `BroadcastReceiver` to receive alarm and trigger notification
- Use WorkManager only for deferrable background work such as backup cleanup, rehydration, or non-exact maintenance tasks

Exact-alarm implications:
- `SCHEDULE_EXACT_ALARM` special access must be checked in code
- App must guide user to exact-alarm settings if not granted
- Reschedule alarms after device reboot if reminder data requires it

## 7. Dependencies

Use latest stable versions listed above. Keep dependency set lean.

Module dependency baseline:

```kotlin
dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.03.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.activity:activity-compose:1.10.1")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.navigation:navigation-compose:2.9.7")

    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    implementation("androidx.work:work-runtime-ktx:2.10.5")

    implementation("com.google.dagger:hilt-android:2.52")
    ksp("com.google.dagger:hilt-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-ai")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
```

Dependency rules:
- Use KSP, not KAPT, for Room and Hilt in new code
- Use Firebase BoM for Firebase libraries
- Use Compose BOM for Compose libraries
- Avoid adding chart libraries for simple weight graph

## 8. Manifest and Permissions

Required manifest permissions:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.READ_CALENDAR" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

Optional, only if direct provider writes are implemented:

```xml
<uses-permission android:name="android.permission.WRITE_CALENDAR" />
```

Runtime flow:
1. Show onboarding with why each permission exists
2. Request notifications permission
3. Request calendar read permission
4. Check exact alarm capability with `AlarmManager.canScheduleExactAlarms()`
5. If unavailable, route user to system settings with clear explanation

## 9. AI Integration

Use Firebase AI Logic as MVP cloud layer.

Requirements:
- Wrap AI access behind a repository interface
- Keep prompt template centralized
- Force JSON output
- Validate and deserialize result before saving
- Handle malformed responses safely
- Do not let UI parse raw JSON

Required prompt behavior:
- If user provides no clear reason, model must ask for reason
- App must not persist task until required fields pass validation

System prompt baseline:

```text
You are a strict personal assistant. Return only valid JSON with these fields:
{
  "task": string,
  "time": string,
  "reason": string,
  "actionType": "reminder" | "call" | "meeting" | "other"
}

Rules:
- If reason is missing or unclear, ask the user why the task matters.
- Do not invent a reason.
- Time must be a valid future ISO-8601 timestamp.
- Return JSON only when all required fields are clear.
```

## 10. Data Model Baseline

### 10.1 Task

```kotlin
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val scheduledAtEpochMillis: Long,
    val reason: String,
    val actionType: String,
    val status: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long
)
```

### 10.2 Weight Entry

```kotlin
@Entity(tableName = "weight_entries")
data class WeightEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightKg: Float,
    val measuredAtEpochMillis: Long,
    val note: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long
)
```

### 10.3 Parsed Command

```kotlin
@kotlinx.serialization.Serializable
data class ParsedTaskPayload(
    val task: String,
    val time: String,
    val reason: String,
    val actionType: String
)
```

## 11. Setup-First Implementation Order

This is the mandatory execution order.

### Phase 0: Tooling and Scaffold
- Install Android Studio, SDK, JDK 17
- Ensure Gradle wrapper is 9.3.1
- Create or fix `:app` module
- Add base Gradle files
- Add Compose app skeleton
- Confirm `assembleDebug` works before feature code

### Phase 1: Foundation
- Add Room database
- Create entities, DAO interfaces, repositories, mappers
- Add Hilt setup
- Add app navigation shell with three tabs
- Add shared design tokens and theme

### Phase 2: Input and AI
- Add input screen
- Add Firebase AI Logic integration
- Add structured response parsing
- Add validation and reason-enforcement flow
- Persist accepted tasks

### Phase 3: Reminder Engine
- Add exact-alarm scheduler
- Add alarm receiver
- Add notification helper and channels
- Add deep links and action intents
- Add boot reschedule support

### Phase 4: Biometrics
- Add weight entry screen
- Add edit flow
- Add Canvas chart
- Add repository-backed history loading

### Phase 5: Agenda and Calendar
- Add grouped agenda screen
- Read calendar events
- Add task editing
- Add optional calendar insert/edit handoff

### Phase 6: Backup and Hardening
- Add export/import JSON
- Add instrumentation and unit tests
- Validate permission flow
- Validate process death and reboot scenarios
- Build debug and release APKs

## 12. Clean Code Standards

Mandatory code quality rules:
- Prefer small focused classes
- One reason to change per class
- No repeated permission logic across screens
- No repeated date parsing or formatting logic
- No repeated JSON parsing logic
- Use sealed results for operation outcomes
- Use immutable UI state
- Prefer explicit names over abbreviations
- Keep ViewModels thin
- Move side effects into use cases or coordinators
- Add tests for validation, repository behavior, and reminder scheduling logic

## 13. Testing Requirements

Minimum required coverage:
- Unit tests for input validation
- Unit tests for time parsing and future-date checks
- Unit tests for AI response mapping
- Room DAO tests
- ViewModel tests
- Compose UI tests for main flows
- Manual device test for notification and exact-alarm behavior

Critical scenarios:
- Missing reason
- Invalid JSON
- Past time
- Exact alarm permission denied
- Calendar permission denied
- Device reboot after reminders created
- Backup export/import with existing data

## 14. Build Outputs

Debug APK:
- `./gradlew assembleDebug`
- Output: `app/build/outputs/apk/debug/app-debug.apk`

Install on connected device:
- `./gradlew installDebug`

Release APK:
- `./gradlew assembleRelease`

## 15. Acceptance Criteria

- App builds cleanly from fresh checkout
- Compose-based 3-tab navigation works
- Input flow enforces reason requirement
- Valid parsed tasks persist to Room
- Exact reminders trigger notifications at scheduled time
- Weight history stores and edits correctly
- Canvas graph renders without third-party chart library
- Agenda view groups local tasks by date
- Calendar read flow works with runtime permission handling
- Backup export/import works
- Architecture supports later AI provider swap without UI rewrite

## 16. Notes for Implementation

- Treat this document as source of truth over earlier drafts
- If implementation conflicts with old plan notes, follow this spec
- Prioritize maintainability and correctness over shortcut integrations
- Keep modules and helpers reusable to avoid repeated code
