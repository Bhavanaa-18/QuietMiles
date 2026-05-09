package com.example.drivingapp.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Centralized permission manager.
 *
 * Usage:
 *   val manager = PermissionManager(activity)
 *   manager.requestAllPermissions()
 *   manager.permissionState.collect { state -> ... }
 *
 * Must be created BEFORE onStart() — pass activity in constructor.
 */
class PermissionManager(private val activity: ComponentActivity) {

    // ── Public state ──────────────────────────────────────────────────────────
    private val _permissionState = MutableStateFlow(PermissionState())
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    // ── Permission launchers (must register before onStart) ───────────────────
    private val fineLocationLauncher: ActivityResultLauncher<Array<String>> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            handleFineLocationResult(results)
        }

    private val backgroundLocationLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            handleBackgroundLocationResult(granted)
        }

    private val activityRecognitionLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            handleActivityRecognitionResult(granted)
        }

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /** Call once on screen load to sync current grant status */
    fun syncPermissionState() {
        _permissionState.update {
            PermissionState(
                fineLocation        = checkFineLocation(),
                backgroundLocation  = checkBackgroundLocation(),
                activityRecognition = checkActivityRecognition()
            )
        }
    }

    /**
     * Step 1 of the permission flow.
     * Always request fine + coarse together per Google guidelines.
     * Background location is requested AFTER fine location is granted.
     */
    fun requestFineLocation() {
        fineLocationLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    /**
     * Step 2 — only call AFTER fine location is granted.
     * Android 10+ requires this to be a separate request dialog.
     */
    fun requestBackgroundLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocationLauncher.launch(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            // Pre-Android 10: background location is included with fine location
            _permissionState.update {
                it.copy(backgroundLocation = PermissionState.Status.GRANTED)
            }
        }
    }

    /**
     * Step 3 — Activity Recognition (Android 10+ only).
     * Required for detecting physical activity via sensor fusion.
     */
    fun requestActivityRecognition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activityRecognitionLauncher.launch(
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        } else {
            _permissionState.update {
                it.copy(activityRecognition = PermissionState.Status.GRANTED)
            }
        }
    }

    /**
     * Convenience: request all permissions in the correct sequence.
     * The result callbacks chain them automatically.
     */
    fun requestAllPermissions() {
        when {
            checkFineLocation() != PermissionState.Status.GRANTED -> requestFineLocation()
            checkBackgroundLocation() != PermissionState.Status.GRANTED -> requestBackgroundLocation()
            checkActivityRecognition() != PermissionState.Status.GRANTED -> requestActivityRecognition()
        }
    }

    /** Navigate to system settings when permanently denied */
    fun openAppSettings() {
        activity.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", activity.packageName, null)
            }
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Result handlers
    // ─────────────────────────────────────────────────────────────────────────

    private fun handleFineLocationResult(results: Map<String, Boolean>) {
        val granted = results[Manifest.permission.ACCESS_FINE_LOCATION] == true

        _permissionState.update {
            it.copy(
                fineLocation = if (granted) {
                    PermissionState.Status.GRANTED
                } else if (activity.shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    PermissionState.Status.DENIED
                } else {
                    PermissionState.Status.PERMANENTLY_DENIED
                }
            )
        }

        // Auto-chain: if fine location granted, move to background location
        if (granted) requestBackgroundLocation()
    }

    private fun handleBackgroundLocationResult(granted: Boolean) {
        _permissionState.update {
            it.copy(
                backgroundLocation = if (granted) {
                    PermissionState.Status.GRANTED
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    activity.shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    PermissionState.Status.DENIED
                } else {
                    PermissionState.Status.PERMANENTLY_DENIED
                }
            )
        }

        // Auto-chain: move to activity recognition
        requestActivityRecognition()
    }

    private fun handleActivityRecognitionResult(granted: Boolean) {
        _permissionState.update {
            it.copy(
                activityRecognition = if (granted) {
                    PermissionState.Status.GRANTED
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    activity.shouldShowRequestPermissionRationale(
                        Manifest.permission.ACTIVITY_RECOGNITION)) {
                    PermissionState.Status.DENIED
                } else {
                    PermissionState.Status.PERMANENTLY_DENIED
                }
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private check helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun checkFineLocation(): PermissionState.Status =
        if (activity.isGranted(Manifest.permission.ACCESS_FINE_LOCATION))
            PermissionState.Status.GRANTED
        else PermissionState.Status.NOT_REQUESTED

    private fun checkBackgroundLocation(): PermissionState.Status {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            return PermissionState.Status.GRANTED
        return if (activity.isGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
            PermissionState.Status.GRANTED
        else PermissionState.Status.NOT_REQUESTED
    }

    private fun checkActivityRecognition(): PermissionState.Status {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            return PermissionState.Status.GRANTED
        return if (activity.isGranted(Manifest.permission.ACTIVITY_RECOGNITION))
            PermissionState.Status.GRANTED
        else PermissionState.Status.NOT_REQUESTED
    }

    private fun Context.isGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED
}