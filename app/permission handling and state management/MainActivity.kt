package com.example.drivingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.drivingapp.data.repository.DrivingStateRepository
import com.example.drivingapp.navigation.AppNavigation
import com.example.drivingapp.permission.PermissionManager
import com.example.drivingapp.service.DrivingDetectionService
import com.example.drivingapp.ui.theme.DrivingAppTheme

class MainActivity : ComponentActivity() {

    // Created before onStart — registers ActivityResult launchers
    lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Init permission manager before setContent
        permissionManager = PermissionManager(this)
        permissionManager.syncPermissionState()

        // Restore persisted override from DataStore
        DrivingStateRepository.restoreFromPreferences()

        setContent {
            DrivingAppTheme {
                // Observe permission state — pass down to screens
                val permissionState by permissionManager
                    .permissionState
                    .collectAsStateWithLifecycle()

                AppNavigation(
                    permissionState = permissionState,
                    onRequestPermission = { permissionManager.requestAllPermissions() },
                    onOpenSettings = { permissionManager.openAppSettings() }
                )
            }
        }

        // Observe permission state to start/stop service
        observePermissionsForService()
    }

    private fun observePermissionsForService() {
        // Start service when minimum permission granted
        permissionManager.permissionState.let { stateFlow ->
            lifecycle.addObserver(
                object : androidx.lifecycle.DefaultLifecycleObserver {
                    override fun onStart(owner: androidx.lifecycle.LifecycleOwner) {
                        permissionManager.syncPermissionState()
                        if (stateFlow.value.canStartDrivingMode) {
                            DrivingDetectionService.startService(this@MainActivity)
                        }
                    }
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DrivingDetectionService.stopService(this)
    }
}