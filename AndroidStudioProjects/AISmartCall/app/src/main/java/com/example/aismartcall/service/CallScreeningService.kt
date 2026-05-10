package com.example.aismartcall.service

import android.content.Intent
import android.telecom.Call
import android.telecom.CallScreeningService
import com.example.aismartcall.data.AppDatabase
import com.example.aismartcall.data.repository.CallRepository
import com.example.aismartcall.engine.Decision
import com.example.aismartcall.engine.EmergencyDetector
import com.example.aismartcall.engine.PriorityEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CallScreeningService : CallScreeningService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private lateinit var repository: CallRepository
    private lateinit var priorityEngine: PriorityEngine

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getDatabase(applicationContext)
        repository = CallRepository(database.callLogDao())
        val emergencyDetector = EmergencyDetector(repository)
        priorityEngine = PriorityEngine(repository, emergencyDetector)
    }

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: "Unknown"

        serviceScope.launch {
            // 1. Log every call attempt immediately
            repository.logCall(phoneNumber, "ATTEMPT")

            // 2. Ask PriorityEngine for a decision
            val decision = priorityEngine.decide(phoneNumber)

            // 3. Respond to the Android system
            val response = when (decision) {
                Decision.ALLOW -> {
                    CallResponse.Builder()
                        .setDisallowCall(false)
                        .setRejectCall(false)
                        .setSkipCallLog(false)
                        .setSkipNotification(false)
                        .build()
                }
                Decision.SILENCE -> {
                    // Note: setSilenceCall might not be available on all API levels, 
                    // usually we disallow the call or let it ring with setDisallowCall(false)
                    CallResponse.Builder()
                        .setDisallowCall(false)
                        .setSkipNotification(true)
                        .build()
                }
                Decision.REJECT -> {
                    CallResponse.Builder()
                        .setDisallowCall(true)
                        .setRejectCall(true)
                        .build()
                }
            }

            respondToCall(callDetails, response)

            // 4. Trigger SmsAutoResponderService if needed
            if (decision == Decision.SILENCE || decision == Decision.REJECT) {
                triggerAutoResponder(phoneNumber)
            }
            
            // 5. Update the log with the final decision
            repository.logCall(phoneNumber, decision.name)
        }
    }

    private fun triggerAutoResponder(phoneNumber: String) {
        val intent = Intent(this, SmsAutoResponderService::class.java).apply {
            putExtra(SmsAutoResponderService.EXTRA_PHONE_NUMBER, phoneNumber)
        }
        startService(intent)
    }
}
