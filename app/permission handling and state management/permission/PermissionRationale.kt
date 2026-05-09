package com.example.drivingapp.permission

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.drivingapp.R

/**
 * Shown when:
 *  - A permission is DENIED (can re-request)
 *  - A permission is PERMANENTLY_DENIED (must go to Settings)
 */
@Composable
fun PermissionRationaleCard(
    permissionState: PermissionState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val isPermanentlyDenied = listOf(
        permissionState.fineLocation,
        permissionState.backgroundLocation,
        permissionState.activityRecognition
    ).any { it == PermissionState.Status.PERMANENTLY_DENIED }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isPermanentlyDenied)
                        Icons.Filled.Warning else Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPermanentlyDenied)
                        stringResource(R.string.permission_permanently_denied_title)
                    else
                        stringResource(R.string.permission_required_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Show which permissions are missing
            PermissionStatusRow(
                label = stringResource(R.string.permission_fine_location),
                status = permissionState.fineLocation
            )
            PermissionStatusRow(
                label = stringResource(R.string.permission_background_location),
                status = permissionState.backgroundLocation
            )
            PermissionStatusRow(
                label = stringResource(R.string.permission_activity_recognition),
                status = permissionState.activityRecognition
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isPermanentlyDenied)
                    stringResource(R.string.permission_permanently_denied_desc)
                else
                    stringResource(R.string.permission_required_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = if (isPermanentlyDenied) onOpenSettings else onRequestPermission,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isPermanentlyDenied)
                        stringResource(R.string.permission_open_settings)
                    else
                        stringResource(R.string.permission_grant)
                )
            }
        }
    }
}

@Composable
private fun PermissionStatusRow(label: String, status: PermissionState.Status) {
    val (icon, color) = when (status) {
        PermissionState.Status.GRANTED ->
            "✓" to MaterialTheme.colorScheme.primary
        PermissionState.Status.PERMANENTLY_DENIED ->
            "✗" to MaterialTheme.colorScheme.error
        else ->
            "○" to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, color = color, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}