# CartFinder ⛳ 📍

> **Never lose track of your golf cart again.** A high-precision GPS locator and compass guide built for golfers, featuring senior-friendly high-contrast readability, zero background battery drain, and complete privacy.

---

## 🌟 Key Features

- **🎯 One-Tap GPS Parking Lock**  
  Quickly pin your cart's exact coordinates using Android's `FusedLocationProviderClient` with high-accuracy GPS (`Priority.PRIORITY_HIGH_ACCURACY`). Add optional custom spot labels and notes (e.g., *"Near Hole 9 turn"*, *"Cart #24 with blue bag"*).

- **🗺️ Interactive Map with Clear Cart Pin**  
  Powered by OpenStreetMap (`osmdroid`):
  - High-visibility custom golf cart pin marker at your exact parking spot.
  - Live user location marker.
  - Real-time walking route polyline connecting you straight to your cart.
  - On-screen controls to zoom, center on your position, center on the cart, or fit both in view.

- **🧭 Live Compass & Walking Guidance HUD**  
  - Real-time rotating needle that dynamically points straight toward your cart as you turn or walk with your phone.
  - Live distance readout with instant toggle between **Yards**, **Meters**, and **Feet**.
  - Directional guidance badges (e.g., *"Turn right 35°"*, *"Head straight forward"*, *"You are at your golf cart!"*).

- **🔋 Zero Background Battery Drain**  
  Strict lifecycle management ensures that hardware GPS and motion/magnetometer sensors are **immediately powered down** when you switch apps or turn off your screen. The app operates at **0% CPU** and uses zero battery when idle in your pocket or bag.

- **🔒 Zero-History Privacy**  
  The app does **not** keep a log of your past parking locations. Once you tap **"I'm Here — Forget This Spot"** upon arrival, the coordinates are immediately discarded from your device.

- **👓 Senior-Friendly High-Contrast Readability**  
  - Large touch targets (52dp+ height on all primary buttons).
  - Bold, legible typography (16sp–24sp) with deep, crisp contrast (`#111827` and `#1B4332` on clean light surfaces).
  - Solid, easy-to-read dialog and cancellation buttons designed to prevent mis-clicks or eye strain.

- **🚶 Turn-by-Turn Google Maps Integration**  
  One-tap button to launch walking directions directly in Google Maps if you prefer turn-by-turn spoken guidance.

- **🕹️ Built-In Demo Mode**  
  A built-in demo simulation switch lets you test navigation, distance updates, and arrival detection even while stationary or testing in emulator environments.

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture
  - `data/`: Data models and persistent storage repository (`CartLocationRepository`)
  - `location/`: Hardware sensor orientation tracking (`DeviceCompass`) and GPS telemetry (`LocationTracker`)
  - `ui/`: Compose screens, components, view models, and custom fairway design system
- **Mapping:** [osmdroid](https://github.com/osmdroid/osmdroid) (OpenStreetMap for Android)
- **Location Services:** Google Play Services Location (`com.google.android.gms.location`)
- **Testing:** Local JVM testing with JUnit 4 and [Robolectric](https://robolectric.org/)

---

## 📂 Project Structure

```text
app/src/
├── main/
│   ├── java/com/example/
│   │   ├── MainActivity.kt               # Lifecycle-aware entry activity
│   │   ├── data/
│   │   │   ├── model/GolfCartLocation.kt # Data class & distance units
│   │   │   └── repository/               # Active spot storage (zero-history)
│   │   ├── location/
│   │   │   ├── DeviceCompass.kt          # Hardware rotation vector & magnetometer
│   │   │   └── LocationTracker.kt        # Fused location provider & geodesic math
│   │   └── ui/
│   │       ├── components/
│   │       │   ├── ArrivalCard.kt        # Arrival detection & forget spot button
│   │       │   ├── CompassBearingHud.kt  # Circular dial, needle, and distance HUD
│   │       │   ├── GolfCartMapView.kt    # Osmdroid MapView with custom pin & polyline
│   │       │   └── ParkCartDialog.kt     # High-contrast park confirmation dialog
│   │       ├── screens/HomeScreen.kt     # Unified main screen
│   │       └── theme/                    # Premier Fairway & Golf Green M3 theme
│   └── res/                              # Vector icons, adaptive launcher icon, strings
└── test/
    └── java/com/example/
        └── ExampleRobolectricTest.kt     # Unit & Robolectric validation tests
```

---

## 🚀 Building & Running

### Prerequisites
- Android Studio Ladybug (2024.2+) or newer
- JDK 17 or higher
- Android SDK 34+ (Android 14+)

### Build the APK
Clone the repository and run:

```bash
# Build the Debug APK
./gradlew assembleDebug

# Output APK location:
# app/build/outputs/apk/debug/app-debug.apk
```

### Run Tests
Execute the unit and Robolectric test suite:

```bash
./gradlew testDebugUnitTest
```

---

## 📱 Permissions

CartFinder requires minimal permissions strictly needed for active on-course navigation:

| Permission | Purpose |
| :--- | :--- |
| `ACCESS_FINE_LOCATION` | Precise GPS coordinates to pinpoint your golf cart |
| `ACCESS_COARSE_LOCATION` | Approximate location fallback |
| `INTERNET` & `ACCESS_NETWORK_STATE` | Fetching OpenStreetMap tiles |
| `VIBRATE` | Gentle haptic feedback when saving or arriving at your cart |

*Note: The app does not request or use background location services (`ACCESS_BACKGROUND_LOCATION`). All location tracking terminates immediately when the app is minimized.*

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
