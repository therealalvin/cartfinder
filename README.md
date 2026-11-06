# CartFinder 🛺 📍

> **Never lose track of your golf cart in The Villages, Florida again.** A high-precision GPS locator and compass guide tailored for golf cart community living — navigating massive parking lots across town squares (Lake Sumter Landing, Spanish Springs, Brownwood), grocery plazas (Publix, Winn-Dixie), restaurants, doctor offices, and street parking. Featuring 5 cheerful Florida daylight themes, optional spot photo capture, senior-friendly high-contrast readability, zero background battery drain, and complete privacy.

---

## 🌟 Key Features

- **🎯 One-Tap GPS Parking Lock**  
  Quickly pin your cart's exact coordinates using Android's `FusedLocationProviderClient` with high-accuracy GPS (`Priority.PRIORITY_HIGH_ACCURACY`). Add optional custom spot labels and cart notes (e.g., *"Row 3 outside Publix, Lake Sumter Square"*, *"Near light pole 12, red rental 4-seater"*).

- **📸 Optional Spot Photo Capture**  
  Snap an optional photo of your parked cart, row sign, or nearby storefront directly when pinning your spot. When navigating back, the photo is displayed right on your Find screen with one-tap fullscreen zoom. When you arrive at your cart, you choose whether to permanently delete the photo (keeping zero history/clutter) or save a copy to your phone's photo gallery.

- **🎨 5 Cheerful Florida Color Themes**  
  Switch instantly between 5 bright, happy, sunlight-optimized themes via a compact palette button (**🎨**) in the top bar:
  1. **🍊 Florida Citrus** *(Default)*: Tangerine Orange (`#EA580C`) & Sun Gold (`#D97706`).
  2. **🌊 Key West Aqua**: Tropical Turquoise (`#0891B2`) & Coral Sunset Pink (`#F43F5E`).
  3. **🌴 Tropical Palms**: Lush Palm Green (`#16A34A`) & Sunshine Yellow (`#CA8A04`).
  4. **🦩 Flamingo Sunset**: Vibrant Flamingo Coral Pink (`#E11D48`) & Sunset Amber (`#D97706`).
  5. **☀️ Daytona Breeze**: Royal Ocean Blue (`#2563EB`) & Sunburst Gold (`#EAB308`).
  *All choices are automatically saved to your device and restored whenever you restart the app.*

- **🗺️ Interactive Map with Clear Cart Pin**  
  Powered by OpenStreetMap (`osmdroid`):
  - High-visibility custom golf cart pin marker at your exact parking spot.
  - Live user location marker.
  - Real-time walking route polyline connecting you straight to your cart across parking lots and town plazas.
  - On-screen controls to zoom, center on your position, center on the cart, or fit both in view.

- **🧭 Live Compass & Walking Guidance HUD**  
  - Real-time rotating needle that dynamically points straight toward your cart as you turn or walk with your phone.
  - Live distance readout with instant toggle between **Feet**, **Yards**, and **Meters**.
  - Directional guidance badges (e.g., *"Turn right 35°"*, *"Head straight forward"*, *"You are at your golf cart!"*).

- **👓 Senior-Friendly Sunlight Readability**  
  - Clean, high-contrast surfaces designed specifically to remain legible in bright Florida sunshine (no dark-on-dark or muddy text).
  - Generous touch targets (52dp–60dp height on all primary buttons).
  - Bold, legible typography (15sp–24sp) with deep contrast to eliminate eye strain.

- **🔋 Zero Background Battery Drain**  
  Strict lifecycle management ensures that hardware GPS and motion/magnetometer sensors are **immediately powered down** when you switch apps or turn off your screen. The app operates at **0% CPU** and uses zero battery when idle in your pocket or bag.

- **🔒 Zero-History Privacy**  
  The app does **not** keep a log of your past parking locations. Once you tap **"I'm Here — Forget This Spot"** upon arrival, the coordinates are immediately discarded from your device.

- **🚶 Turn-by-Turn Google Maps Integration**  
  One-tap button to launch walking directions directly in Google Maps if you prefer turn-by-turn spoken guidance through the square.

- **🕹️ Built-In Demo Mode (The Villages, FL)**  
  A built-in demo simulation switch defaults to Lake Sumter Landing Market Square in The Villages, FL (`28.8950, -81.9705`), allowing seamless demonstration and testing of distance calculation and arrival HUD.

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Image Loading:** [Coil Compose](https://coil-kt.github.io/coil/)
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture
  - `data/`: Data models and persistent storage repository (`CartLocationRepository`)
  - `location/`: Hardware sensor orientation tracking (`DeviceCompass`) and GPS telemetry (`LocationTracker`)
  - `ui/`: Compose screens, components, view models, and the 5-palette Florida theme system
- **Mapping:** [osmdroid](https://github.com/osmdroid/osmdroid) (OpenStreetMap for Android)
- **Location Services:** Google Play Services Location (`com.google.android.gms.location`)
- **Testing:** Local JVM testing with JUnit 4 and [Robolectric](https://robolectric.org/)

---

## 📂 Project Structure

```text
app/src/
├── main/
│   ├── java/com/example/
│   │   ├── MainActivity.kt               # Lifecycle-aware entry activity with dynamic theme binding
│   │   ├── data/
│   │   │   ├── model/GolfCartLocation.kt # Data class, distance units, optional photo URI
│   │   │   └── repository/               # Active spot, distance unit & theme storage
│   │   ├── location/
│   │   │   ├── DeviceCompass.kt          # Hardware rotation vector & magnetometer
│   │   │   └── LocationTracker.kt        # Fused location provider & geodesic math
│   │   └── ui/
│   │       ├── components/
│   │       │   ├── ArrivalCard.kt        # Arrival detection & forget spot button
│   │       │   ├── CompassBearingHud.kt  # Circular dial, needle, and distance HUD
│   │       │   ├── GolfCartMapView.kt    # Osmdroid MapView with custom pin & polyline
│   │       │   ├── ParkCartDialog.kt     # High-contrast park confirmation & optional photo capture
│   │       │   └── ThemeChooserDialog.kt # 5-palette Florida theme selection dialog
│   │       ├── screens/HomeScreen.kt     # Unified main screen & fullscreen photo viewer
│   │       └── theme/                    # 5 Florida theme palettes, M3 ColorScheme & Typography
│   ├── res/
│   │   ├── xml/file_paths.xml            # Secure FileProvider paths for camera photos
│   │   └── ...                           # Vector icons, adaptive launcher icon, strings
└── test/
    └── java/com/example/
        └── ExampleRobolectricTest.kt     # Unit & Robolectric validation tests (zero-history & theme persistence)
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
# Run unit and Robolectric tests
./gradlew testDebugUnitTest
```

---

## 📱 Permissions

CartFinder requires minimal permissions strictly needed for active community parking navigation:

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
