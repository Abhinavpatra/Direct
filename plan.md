# Nexus Implementation Plan
Last updated: April 19, 2026
Status: Aligned with corrected spec

## 1. Goal

Ship Nexus as a clean, modular, Jetpack Compose Android app with:
- local-first storage
- exact reminder delivery
- Firebase AI Logic for MVP natural-language parsing
- future-ready architecture for on-device AI

This plan is execution-first: setup and scaffold come before feature code.

## 2. Current Delivery Rules

- Build stack must use current stable versions
- Compose-only UI
- KSP over KAPT
- Clean architecture boundaries
- No repeated permission, parsing, time, or scheduling logic
- AlarmManager for exact reminders
- WorkManager only for deferrable background work

## 3. Confirmed Toolchain and Library Baseline

Tooling:
- Android Studio Panda 3 Patch 1 or newer stable
- JDK 17
- Gradle 9.3.1
- AGP 9.1.1
- Kotlin 2.3.20

Primary libraries:
- Compose BOM 2026.03.00
- Navigation Compose 2.9.7
- Room 2.8.4
- WorkManager 2.10.5
- Dagger Hilt 2.52
- AndroidX Hilt Navigation Compose 1.3.0
- Firebase BoM 34.7.0
- Firebase AI Logic `firebase-ai`
- Kotlinx Serialization 1.9.0

## 4. Delivery Order

### Phase 0: Setup First

Objective:
- make repo buildable before any feature work

Tasks:
- verify Gradle wrapper 9.3.1
- create or fix `:app` module
- add `app/build.gradle.kts`
- add Compose compiler plugin setup
- add `AndroidManifest.xml`
- add `NexusApp` and `MainActivity`
- add theme, resources, and package root
- run `assembleDebug`

Exit criteria:
- clean debug build from fresh checkout
- no missing module or Gradle configuration errors

### Phase 1: Foundation and Architecture

Objective:
- establish reusable project structure and shared logic

Tasks:
- add package layout for `data`, `domain`, `ui`, `reminder`, `calendar`, `backup`, `core`
- configure Hilt application graph
- create result wrappers and error model
- centralize time utilities
- centralize permission handling
- create base navigation shell with 3 tabs
- create domain models and mapper conventions

Exit criteria:
- app launches
- tab navigation works
- architecture boundaries exist and compile

### Phase 2: Persistence Layer

Objective:
- establish stable local data model

Tasks:
- create Room database
- implement `TaskEntity` and `WeightEntryEntity`
- add DAO contracts
- add repository implementations
- add unit tests for mappers and validation

Exit criteria:
- tasks and weight entries persist locally
- DAO and repository tests pass

### Phase 3: Input and AI

Objective:
- turn natural language into validated local reminders

Tasks:
- create input screen and state holder
- add AI repository interface
- implement Firebase AI Logic data source
- centralize system prompt
- deserialize strict JSON into typed model
- add reason enforcement flow
- add future-time validation
- persist valid task through use case

Exit criteria:
- user can submit text
- missing reason is rejected cleanly
- valid payload becomes local task

### Phase 4: Reminder Engine

Objective:
- deliver reminders reliably at user-selected time

Tasks:
- implement exact alarm scheduler
- add alarm receiver
- create notification channel and helper
- add action intents for WhatsApp and calendar flow
- add boot completed reschedule path
- isolate reminder logic from UI

Exit criteria:
- exact reminder fires on device
- notification actions work
- reboot recovery works for pending reminders

### Phase 5: Biometrics

Objective:
- support weight tracking without extra graph library

Tasks:
- create biometrics screen
- add weight entry form
- add edit flow
- draw line chart using Compose Canvas
- centralize chart scaling and formatting helpers

Exit criteria:
- weight entry create/edit works
- graph renders correctly from local history

### Phase 6: Agenda and Calendar

Objective:
- merge local tasks and calendar context into one dashboard

Tasks:
- create agenda screen
- group tasks by date
- read calendar events with permission handling
- provide insert/edit event handoff via intent
- keep calendar access behind repository boundary

Exit criteria:
- agenda renders local tasks grouped by date
- calendar read flow works when permission granted

### Phase 7: Backup and Hardening

Objective:
- make app safer, recoverable, and production-ready

Tasks:
- implement export to JSON
- implement validated import
- add empty, loading, and error states
- test permission-denied paths
- test malformed AI output handling
- test backup round-trip
- prepare signed release flow

Exit criteria:
- export/import works
- core error paths handled cleanly
- release build completes

## 5. Recommended Project Structure

```text
app/src/main/java/com/nexus/app/
  di/
  data/
    local/
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
    permission/
    result/
    time/
```

## 6. Build Configuration Targets

Root plugins:
- `com.android.application` 9.1.1
- `org.jetbrains.kotlin.android` 2.3.20
- `org.jetbrains.kotlin.plugin.compose` 2.3.20
- `org.jetbrains.kotlin.plugin.serialization` 2.3.20
- `com.google.devtools.ksp` 2.3.20-1.0.28
- `com.google.dagger.hilt.android` 2.52

Android defaults:
- `compileSdk = 36`
- `minSdk = 26`
- `targetSdk = 36`
- `jvmTarget = 17`

## 7. Non-Negotiable Engineering Standards

- no direct Room calls from composables
- no direct Firebase calls from composables
- no duplicated permission request logic
- no duplicated date/time parsing code
- no duplicated notification construction logic
- no business rules inside UI-only classes
- no exact reminder scheduling through WorkManager
- no unnecessary third-party graph library

## 8. Test Plan

Unit tests:
- parsed payload validation
- future-time validation
- repository mapping
- reminder scheduling inputs
- backup import/export validation

Integration or device tests:
- Room persistence
- notification delivery
- exact-alarm settings flow
- calendar permission flow
- boot reschedule path

Compose UI tests:
- tab navigation
- input submission flow
- biometrics form flow
- agenda grouped rendering

## 9. Risks and Mitigations

Risk:
- malformed AI output
Mitigation:
- strict typed parsing plus validation layer before persistence

Risk:
- user denies exact-alarm access
Mitigation:
- explicit education screen and settings deep link

Risk:
- reminder loss after reboot
Mitigation:
- boot receiver plus repository-driven reschedule

Risk:
- duplicated logic as features expand
Mitigation:
- centralize helpers in `core`, keep screen logic thin

## 10. Immediate Next Steps

1. Make project scaffold buildable.
2. Add missing `app/build.gradle.kts` and base Compose app files.
3. Verify `assembleDebug`.
4. Build foundation layer before any AI or reminder feature code.
