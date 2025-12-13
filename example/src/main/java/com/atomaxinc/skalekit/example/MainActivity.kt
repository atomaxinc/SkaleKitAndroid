package com.atomaxinc.skalekit.example

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.atomaxinc.skalekit.SkaleDevicePicker
import com.atomaxinc.skalekit.SkaleKit
import com.atomaxinc.skalekit.example.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SkaleKit.SkaleListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var skaleKit: SkaleKit
    private var devicePicker: SkaleDevicePicker? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            showDevicePicker()
        } else {
            Toast.makeText(this, "Bluetooth permissions required", Toast.LENGTH_SHORT).show()
        }
    }

    private val bluetoothEnableLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (skaleKit.isBluetoothEnabled()) {
            checkPermissionsAndConnect()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        skaleKit = SkaleKit(this)
        skaleKit.setListener(this)

        setupUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        skaleKit.disconnect()
    }

    private fun setupUI() {
        binding.connectButton.setOnClickListener {
            if (!skaleKit.isBluetoothEnabled()) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                bluetoothEnableLauncher.launch(enableBtIntent)
            } else {
                checkPermissionsAndConnect()
            }
        }

        binding.tareButton.setOnClickListener {
            skaleKit.tare()
        }

        binding.disconnectButton.setOnClickListener {
            skaleKit.disconnect()
        }
    }

    private fun checkPermissionsAndConnect() {
        val requiredPermissions = SkaleKit.getRequiredPermissions()
        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            showDevicePicker()
        } else {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    private fun showDevicePicker() {
        devicePicker = SkaleDevicePicker(this, skaleKit)
        devicePicker?.show(object : SkaleDevicePicker.OnDeviceSelectedListener {
            override fun onDeviceSelected(device: BluetoothDevice) {
                skaleKit.connect(device)
            }

            override fun onCancelled() {
                // User cancelled
            }
        })
    }

    private fun updateUIForState(state: SkaleKit.ConnectionState) {
        runOnUiThread {
            when (state) {
                SkaleKit.ConnectionState.DISCONNECTED -> {
                    binding.statusIndicator.setBackgroundResource(R.drawable.status_indicator_disconnected)
                    binding.statusText.text = "Disconnected"
                    binding.connectButton.isVisible = true
                    binding.connectButton.isEnabled = true
                    binding.connectButton.text = "Connect Scale"
                    binding.tareButton.isVisible = false
                    binding.disconnectButton.isVisible = false
                    binding.weightText.text = "0.0 g"
                    binding.batteryText.text = ""
                }
                SkaleKit.ConnectionState.SCANNING -> {
                    binding.statusIndicator.setBackgroundResource(R.drawable.status_indicator_connecting)
                    binding.statusText.text = "Scanning..."
                    binding.connectButton.isEnabled = false
                    binding.connectButton.text = "Scanning..."
                }
                SkaleKit.ConnectionState.CONNECTING -> {
                    binding.statusIndicator.setBackgroundResource(R.drawable.status_indicator_connecting)
                    binding.statusText.text = "Connecting..."
                    binding.connectButton.isEnabled = false
                    binding.connectButton.text = "Connecting..."
                }
                SkaleKit.ConnectionState.CONNECTED -> {
                    binding.statusIndicator.setBackgroundResource(R.drawable.status_indicator_connected)
                    binding.statusText.text = "Connected"
                    binding.connectButton.isVisible = false
                    binding.tareButton.isVisible = true
                    binding.tareButton.isEnabled = true
                    binding.disconnectButton.isVisible = true
                    binding.disconnectButton.isEnabled = true

                    // Request battery level
                    skaleKit.requestBatteryLevel()
                }
            }
        }
    }

    // SkaleKit.SkaleListener implementation

    override fun onConnectionStateChanged(state: SkaleKit.ConnectionState) {
        updateUIForState(state)
    }

    override fun onWeightUpdate(weight: Float) {
        runOnUiThread {
            binding.weightText.text = String.format("%.1f g", weight)
        }
    }

    override fun onButtonClicked(buttonId: Int) {
        runOnUiThread {
            Toast.makeText(this, "Button $buttonId clicked", Toast.LENGTH_SHORT).show()
            if (buttonId == 1) {
                skaleKit.tare()
            }
        }
    }

    override fun onBatteryLevelUpdate(level: Int) {
        runOnUiThread {
            binding.batteryText.text = "Battery: $level%"
        }
    }

    override fun onError(error: SkaleKit.SkaleError) {
        runOnUiThread {
            val message = when (error) {
                is SkaleKit.SkaleError.BluetoothDisabled -> "Bluetooth is disabled"
                is SkaleKit.SkaleError.PermissionDenied -> "Permission denied"
                is SkaleKit.SkaleError.DeviceNotFound -> "No Skale device found"
                is SkaleKit.SkaleError.ConnectionFailed -> "Connection failed"
                is SkaleKit.SkaleError.ConnectionLost -> "Connection lost"
                is SkaleKit.SkaleError.Unknown -> "Error: ${error.message}"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            updateUIForState(SkaleKit.ConnectionState.DISCONNECTED)
        }
    }
}
