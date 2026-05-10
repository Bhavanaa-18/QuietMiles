package com.example.drivingapp

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.drivingapp.navigation.AppNavigation
import com.example.drivingapp.service.DrivingDetectionService
import com.example.drivingapp.ui.theme.DrivingAppTheme

class MainActivity : ComponentActivity() {

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.any { it }) DrivingDetectionService.startService(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrivingAppTheme { AppNavigation() }
        }
        requestPermissionsAndStartService()
    }

    private fun requestPermissionsAndStartService() {
        if (hasLocationPermission()) DrivingDetectionService.startService(this)
        else permissionLauncher.launch(locationPermissions)
    }

    private fun hasLocationPermission() = locationPermissions.all {
        checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        DrivingDetectionService.stopService(this)
    }
}