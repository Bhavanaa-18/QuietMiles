package com.example.drivingapp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property — one DataStore instance per app
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "driving_preferences"
)

/**
 * Persistent preferences using Jetpack DataStore.
 * Survives process death — SharedPreferences replacement.
 */
class DrivingPreferences(private val context: Context) {

    companion object {
        // Keys
        private val KEY_MANUAL_OVERRIDE_ACTIVE = booleanPreferencesKey("manual_override_active")
        private val KEY_MANUAL_OVERRIDE_OFF     = booleanPreferencesKey("manual_override_off")
        private val KEY_OVERRIDE_TIMESTAMP      = longPreferencesKey("override_timestamp")
        private val KEY_AUTO_DETECT_ENABLED     = booleanPreferencesKey("auto_detect_enabled")
        private val KEY_SMS_AUTO_REPLY          = booleanPreferencesKey("sms_auto_reply")
        private val KEY_NOTIFY_CONTACTS         = booleanPreferencesKey("notify_contacts")
        private val KEY_SPEED_THRESHOLD         = booleanPreferencesKey("high_speed_alert")
    }

    // ── Flows (observed by Repository) ───────────────────────────────────────

    val isManualOverrideOff: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_MANUAL_OVERRIDE_OFF] ?: false }

    val overrideTimestamp: Flow<Long> = context.dataStore.data
        .map { it[KEY_OVERRIDE_TIMESTAMP] ?: 0L }

    val isAutoDetectEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_AUTO_DETECT_ENABLED] ?: true }

    val isSmsAutoReplyEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_SMS_AUTO_REPLY] ?: true }

    val isNotifyContactsEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_NOTIFY_CONTACTS] ?: false }

    val isHighSpeedAlertEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_SPEED_THRESHOLD] ?: true }

    // ── Write operations ──────────────────────────────────────────────────────

    suspend fun setManualOverrideOff(isOff: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MANUAL_OVERRIDE_OFF] = isOff
            prefs[KEY_OVERRIDE_TIMESTAMP]  = System.currentTimeMillis()
        }
    }

    suspend fun clearManualOverride() {
        context.dataStore.edit { prefs ->
            prefs[KEY_MANUAL_OVERRIDE_OFF] = false
            prefs[KEY_OVERRIDE_TIMESTAMP]  = 0L
        }
    }

    suspend fun setAutoDetectEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_DETECT_ENABLED] = enabled }
    }

    suspend fun setSmsAutoReply(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SMS_AUTO_REPLY] = enabled }
    }

    suspend fun setNotifyContacts(enabled: Boolean) {
        context.dataStore.edit { it[KEY_NOTIFY_CONTACTS] = enabled }
    }

    suspend fun setHighSpeedAlert(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SPEED_THRESHOLD] = enabled }
    }
}