package com.example.aismartcall.service

import android.app.IntentService
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log

/**
 * An [IntentService] subclass for handling asynchronous SMS sending requests.
 */
class SmsAutoResponderService : IntentService("SmsAutoResponderService") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return

        val phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER)
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "I am currently driving, I will get back to you soon."

        if (phoneNumber != null) {
            sendSms(phoneNumber, message)
        } else {
            Log.e("SmsAutoResponder", "Phone number extra was null")
        }
    }

    private fun sendSms(phoneNumber: String, message: String) {
        try {
            val smsManager: SmsManager = this.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.d("SmsAutoResponder", "SMS sent to $phoneNumber")
        } catch (e: Exception) {
            Log.e("SmsAutoResponder", "Failed to send SMS", e)
        }
    }

    companion object {
        const val EXTRA_PHONE_NUMBER = "com.example.aismartcall.service.extra.PHONE_NUMBER"
        const val EXTRA_MESSAGE = "com.example.aismartcall.service.extra.MESSAGE"
    }
}
