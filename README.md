# 🏰 Castle Defense Board

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-UI-4285F4?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material-3-757575?style=flat&logo=materialdesign&logoColor=white)](https://m3.material.io)

**Castle Defense Board** is a modern, tactical strategy board game built natively for Android using **Jetpack Compose** and **Material 3**. Defend your castle against waves of incoming enemies by strategically positioning defender units across the tactical grid, managing resources, and utilizing tactical abilities!

---

## ✨ Key Features

- **Tactical Grid Gameplay**: Deploy and position various defender units on the board to counter enemy waves.
- **Dynamic Enemy Waves**: Face diverse enemy units with unique behaviors, stats, and attack patterns.
- **Modern Jetpack Compose UI**: Smooth animations, responsive layouts, and rich Material 3 design system components.
- **Immersive Feedback**: Built-in procedural sound synthesizer (`SoundSynthesizer`) and haptic feedback (`HapticsHelper`) for satisfying tactile and auditory interactions.
- **Game Settings & Preferences**: Customize game options, audio, and difficulty with Jetpack DataStore preferences persistence.
- **Comprehensive Screens**:
  - 🏠 **Home Screen**: Start new games, view stats, and access settings.
  - 🎮 **Game Screen**: Interactive tactical board with real-time wave management and HUD.
  - 📖 **How to Play**: Interactive guide explaining rules, units, and strategies.
  - ⚙️ **Settings**: Configure sound, haptics, and gameplay preferences.
  - ℹ️ **About**: Project details and credits.
  - 🏆 **Result Screen**: Victory or defeat breakdown with score summaries.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) + [Material 3](https://m3.material.io/)
- **Architecture**: MVVM (Model-View-ViewModel) with StateFlow
- **Navigation**: Jetpack Navigation Compose
- **Data Persistence**: Jetpack DataStore Preferences
- **Concurrency**: Kotlin Coroutines & Flow
- **Testing**: JUnit 4, Kotlin Coroutines Test, Compose UI Testing

---

## 📂 Project Structure

```text
com.example.castledefenseboard/
├── audio/          # Sound synthesizer & haptics helper
├── data/
│   ├── model/      # Game state, units, grid positions, and events
│   └── preferences/# Game preferences DataStore
├── domain/         # Core game rules and logic calculations
├── ui/
│   ├── components/ # Tactical board, unit tokens, and HUD widgets
│   ├── navigation/ # NavGraph and screen definitions
│   ├── screens/    # Home, Game, Settings, HowToPlay, About, Result, Splash
│   ├── theme/      # M3 Color palette, typography, and theme
│   └── viewmodel/  # GameViewModel and SettingsViewModel
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (Koala / Iguana or newer recommended)
- JDK 11 or higher
- Android SDK (Compile SDK 37, Min SDK 24)

### Building and Running
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/Castle-Defense-Board-.git
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle and run the app on an Android emulator or physical device.

---

## 🧪 Testing

The project includes unit tests for game rules and state management, as well as instrumented Compose UI tests.

Run unit tests via Gradle:
```bash
./gradlew test
```

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

---

Developed with ❤️ using Kotlin and Jetpack Compose.
