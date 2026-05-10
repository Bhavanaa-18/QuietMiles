package com.example.aismartcall.engine

import com.example.aismartcall.data.repository.CallRepository

class EmergencyDetector(private val repository: CallRepository) {

    suspend fun isEmergency(phoneNumber: String): Boolean {
        // "3 calls in 5 minutes" rule
        val callCount = repository.getCallCountInLastMinutes(phoneNumber, 5)
        return callCount >= 2 // Current call will be the 3rd if logged immediately after
    }
}
