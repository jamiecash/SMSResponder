package uk.co.cashfamily.smsresponder

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log


class SMSHeadlessSendService : Service() {
    /**
     * Headless SMS service for sending SMS from other apps through this app when set as default
     * TODO Not implemented. implement override methods required to handle other apps SMS messages.
     */
    private val TAG = SMSHeadlessSendService::class.java.simpleName

    override fun onBind(p0: Intent?): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    private fun sendSMS(context: Context, phoneNumber: String, message: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }



}