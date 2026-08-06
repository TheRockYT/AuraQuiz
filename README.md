# AuraQuiz

AuraQuiz is an Android quiz application written in **Kotlin** with **Jetpack Compose** and **Material 3**. It turns your
lock screen into a bite-sized trivia game and manages a spaced-repetition card system (FSRS) so you can learn anything
in the moments before you unlock your phone.

## Features

- **Lock screen trivia** — A transparent overlay activity shows random questions the moment your screen wakes while the
  keyguard is locked. Answer correctly and it auto-advances; swipe away or press "Back to Lock Screen" to dismiss it.
- **Deck & card system** — Create decks and cards with 7 supported card types:
    - Flashcard
    - Sentence Builder
    - Note
    - Multiple Choice
    - Binary Choice (true / false)
    - Fill in the Blank
    - Matching Pairs
- **Spaced repetition (FSRS)** — Card scheduling powered by the [FsrsKt](https://github.com/therockyt/fsrskt) FSRS
  implementation, with configurable maximum new cards per day and fast-skip behavior.
- **Settings** — Theme (system / light / dark), Material You dynamic colors, lock-screen toggle, and scheduler
  configuration.
- **Local persistence** — Room 3 database for decks, cards, review logs, and notes; DataStore Preferences for settings.

## Lock Screen Quiz

The lock screen experience works like this:

1. `LockScreenService` runs as a foreground service and listens for `ACTION_SCREEN_ON`.
2. When the screen turns on while the keyguard is still locked, it launches `LockScreenActivity`.
3. `LockScreenActivity` renders itself *above* the keyguard (`setShowWhenLocked`), blurs the background, and shows
   `QuizScreen` filled from the bundled trivia deck.
4. The activity closes itself automatically when the user unlocks (`ACTION_USER_PRESENT`), turns the screen off, or
   dismisses the quiz.

Because it draws over other apps, it requires the **"Display over other apps"** (`SYSTEM_ALERT_WINDOW`) permission,
which the app requests when you enable the feature in Settings. A `BootReceiver` restarts the service after reboot or
app update.

## Tech Stack

| Area              | Choice                                        |
|-------------------|-----------------------------------------------|
| Language          | Kotlin                                        |
| UI                | Jetpack Compose                               |
| Navigation        | AndroidX Navigation 3                         |
| Database          | Room 3 with bundled SQLite                    |
| Settings          | DataStore Preferences                         |
| Serialization     | kotlinx.serialization (JSON)                  |
| Code generation   | KSP (Room compiler)                           |
| Spaced repetition | [FsrsKt](https://github.com/therockyt/fsrskt) |
| Build             | Gradle with version catalog                   |

Minimum SDK is **24**, target/compile SDK is **37**.

## Getting Started

### Prerequisites

- IntelliJ IDEA with JDK 17+
- Android device/emulator running Android 7.0 (API 24) or newer

### Building

```bash
./gradlew assembleDebug
```

The debug APK is produced at `app/build/outputs/apk/debug/app-debug.apk`. Install it with:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

To test the lock screen quiz, enable **Settings → Show on lockscreen**, grant the overlay permission when prompted, lock
your device, and turn the screen back on.

## Status

The app is under active development.
