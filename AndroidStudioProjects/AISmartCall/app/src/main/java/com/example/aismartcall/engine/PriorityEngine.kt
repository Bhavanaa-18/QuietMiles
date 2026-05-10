package com.example.aismartcall.engine

import com.example.aismartcall.data.repository.CallRepository

class PriorityEngine(
    private val repository: CallRepository,
    private val emergencyDetector: EmergencyDetector
) {

    suspend fun decide(phoneNumber: String): Decision {
        // 1. Check for Emergency (3 calls in 5 minutes)
        if (emergencyDetector.isEmergency(phoneNumber)) {
            return Decision.ALLOW
        }

        // 2. Check for Important Contact
        if (repository.isImportantContact(phoneNumber)) {
            return Decision.ALLOW
        }

        // 3. Default decision for unknown/non-priority callers
        // You could add logic here to SILENCE or REJECT based on other criteria
        return Decision.REJECT
    }
}
