# SkaleKit Android SDK

Bluetooth SDK for Skale scales.

## Requirements

- Android 5.0 (API 21) or higher
- Bluetooth Low Energy (BLE) support

## Installation

### Download AAR

Download the latest AAR from the [`releases`](releases/) folder and add to your project's `libs` directory.

Then add to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(files("libs/skalekit-1.0.0.aar"))
}
```

## Permissions

The SDK requires Bluetooth permissions. Add to your `AndroidManifest.xml`:

```xml
<!-- For Android 11 and below -->
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" android:maxSdkVersion="30" />

<!-- For Android 12+ -->
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />

<uses-feature android:name="android.hardware.bluetooth_le" android:required="true" />
```

## Usage

### Initialize

```kotlin
val skaleKit = SkaleKit(context)
skaleKit.setListener(object : SkaleKit.SkaleListener {
    override fun onConnectionStateChanged(state: SkaleKit.ConnectionState) {
        // Handle connection state
    }

    override fun onWeightUpdate(weight: Float) {
        // Weight in grams
    }

    override fun onButtonClicked(buttonId: Int) {
        // 1 = circle button, 2 = square button
    }

    override fun onBatteryLevelUpdate(level: Int) {
        // Battery percentage
    }

    override fun onError(error: SkaleKit.SkaleError) {
        // Handle error
    }
})
```

### Check Permissions

```kotlin
// Check if permissions are granted
if (!SkaleKit.hasPermissions(context)) {
    val permissions = SkaleKit.getRequiredPermissions()
    // Request permissions
}
```

### Connect

```kotlin
// Auto-scan and connect to first Skale found
skaleKit.startScan()

// Or use device picker
val picker = SkaleDevicePicker(context, skaleKit)
picker.show(object : SkaleDevicePicker.OnDeviceSelectedListener {
    override fun onDeviceSelected(device: BluetoothDevice) {
        skaleKit.connect(device)
    }

    override fun onCancelled() {
        // User cancelled
    }
})
```

### Tare

```kotlin
skaleKit.tare()
```

### Request Battery Level

```kotlin
skaleKit.requestBatteryLevel()
```

### Disconnect

```kotlin
skaleKit.disconnect()
```

## Example App

See the `example` module for a complete implementation.

## Author

Atomax Inc. - service@atomaxinc.com

Any feedback is welcomed! Please send feedback to service@atomaxinc.com

## License

MIT License - see [LICENSE](LICENSE) for details.
