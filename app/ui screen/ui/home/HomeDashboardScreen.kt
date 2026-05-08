package com.example.drivingapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivingapp.R

@Composable
fun HomeDashboardScreen(
    innerPadding: PaddingValues,
    viewModel: HomeDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineMedium
        )

        // Driving Mode Status Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (uiState.isDrivingModeEnabled)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.driving_mode_label),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = if (uiState.isDrivingModeEnabled)
                                stringResource(R.string.status_active)
                            else
                                stringResource(R.string.status_inactive),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (uiState.isDrivingModeEnabled)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.isDrivingModeEnabled,
                        onCheckedChange = { viewModel.toggleDrivingMode(it) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.current_speed_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = uiState.currentSpeed,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Speed Input (simulating speed update)
        OutlinedTextField(
            value = uiState.currentSpeed,
            onValueChange = { viewModel.updateSpeed(it) },
            label = { Text(stringResource(R.string.speed_input_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
