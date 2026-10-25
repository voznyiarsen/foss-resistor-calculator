# FOSS Resistor Calculator

A native Android resistor color code calculator. Supports 3, 4, 5, and 6 band
resistors. No ads, no tracking, no internet permission required.

## Features

- 3 / 4 / 5 / 6 band resistor color code calculation
- Live resistor preview composited from the original artwork
- Min / max resistance range from tolerance
- Temperature coefficient (6th band)
- Light / dark / system theme
- Optional haptic feedback
- Edge-to-edge UI

## Build

Requires Android Studio / Gradle with Android SDK 36.

```sh
./gradlew assembleDebug
```

The debug APK is produced at `app/build/outputs/apk/debug/`.

## Install

```sh
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## License

FOSS. See repository for details.
