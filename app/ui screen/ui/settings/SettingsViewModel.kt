package com.example.drivingapp.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val autoReplySmsEnabled: Boolean = true,
    val notifyContactsOnTrip: Boolean = false,
    val highSpeedAlertEnabled: Boolean = true,
    val speedThreshold60: Boolean = true,
    val speedThreshold80: Boolean = false,
    val speedThreshold100: Boolean = false
)

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleAutoReplySms(enabled: Boolean) = _uiState.update { it.copy(autoReplySmsEnabled = enabled) }
    fun toggleNotifyContacts(enabled: Boolean) = _uiState.update { it.copy(notifyContactsOnTrip = enabled) }
    fun toggleHighSpeedAlert(enabled: Boolean) = _uiState.update { it.copy(highSpeedAlertEnabled = enabled) }
    fun toggleThreshold60(enabled: Boolean) = _uiState.update { it.copy(speedThreshold60 = enabled) }
    fun toggleThreshold80(enabled: Boolean) = _uiState.update { it.copy(speedThreshold80 = enabled) }
    fun toggleThreshold100(enabled: Boolean) = _uiState.update { it.copy(speedThreshold100 = enabled) }
}