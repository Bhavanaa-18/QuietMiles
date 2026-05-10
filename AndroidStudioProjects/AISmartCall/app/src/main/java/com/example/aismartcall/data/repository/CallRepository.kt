package com.example.aismartcall.data.repository

import com.example.aismartcall.data.dao.CallLogDao
import com.example.aismartcall.data.entity.CallLogEntry

class CallRepository(private val callLogDao: CallLogDao) {

    suspend fun logCall(phoneNumber: String, decision: String) {
        val entry = CallLogEntry(
            phoneNumber = phoneNumber,
            timestamp = System.currentTimeMillis(),
            decision = decision
        )
        callLogDao.insert(entry)
    }

    suspend fun getCallCountInLastMinutes(phoneNumber: String, minutes: Int): Int {
        val startTime = System.currentTimeMillis() - (minutes * 60 * 1000)
        return callLogDao.countCallsFromNumberInTimeWindow(phoneNumber, startTime)
    }

    // Mock function for "Important Contact" - In a real app, this would check a database or Contacts API
    fun isImportantContact(phoneNumber: String): Boolean {
        val importantContacts = listOf("9998887776", "1112223334")
        return phoneNumber in importantContacts
    }
}
