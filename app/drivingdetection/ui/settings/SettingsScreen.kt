package com.example.drivingapp.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivingapp.R

@Composable
fun SettingsScreen(
    innerPadding: PaddingValues,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        SettingsSectionHeader(stringResource(R.string.settings_section_sms))
        SettingsToggleItem(
            stringResource(R.string.settings_auto_reply),
            stringResource(R.string.settings_auto_reply_desc),
            uiState.autoReplySmsEnabled, viewModel::toggleAutoReplySms
        )
        SettingsToggleItem(
            stringResource(R.string.settings_notify_contacts),
            stringResource(R.string.settings_notify_contacts_desc),
            uiState.notifyContactsOnTrip, viewModel::toggleNotifyContacts
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        SettingsSectionHeader(stringResource(R.string.settings_section_speed))
        SettingsToggleItem(
            stringResource(R.string.settings_high_speed_alert),
            stringResource(R.string.settings_high_speed_alert_desc),
            uiState.highSpeedAlertEnabled, viewModel::toggleHighSpeedAlert
        )
        SettingsToggleItem(stringResource(R.string.settings_threshold_60),
            checked = uiState.speedThreshold60, onCheckedChange = viewModel::toggleThreshold60)
        SettingsToggleItem(stringResource(R.string.settings_threshold_80),
            checked = uiState.speedThreshold80, onCheckedChange = viewModel::toggleThreshold80)
        SettingsToggleItem(stringResource(R.string.settings_threshold_100),
            checked = uiState.speedThreshold100, onCheckedChange = viewModel::toggleThreshold100)
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = subtitle?.let { { Text(it) } },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) }
    )
}