package uk.co.cashfamily.smsresponder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log


class MMSReceiver : BroadcastReceiver() {
    private val TAG = MMSReceiver::class.java.simpleName
    private val PDU_TYPE = "pdus"

    override fun onReceive(context: Context, intent: Intent) {
        /**
        Listen for MMS messages. Send auto-response to those received.
         TODO We need to debug and test this as this is using the SMS PDU.
         **/
        // Get all MMS messages.
        val bundle = intent.extras
        val format = bundle!!.getString("format")
        val pdus = bundle[PDU_TYPE] as Array<*>?

        if (pdus != null) {
            // Iterate pdus, getting messages
            for (pdu in pdus) {
                val msg = SmsMessage.createFromPdu(pdu as ByteArray, format)

                // Message
                val originatingAddress = msg.originatingAddress
                val messageText = msg.messageBody

                // Log
                val strMessage = "MMS from $originatingAddress: $messageText"
                Log.i(TAG, strMessage)

                // Respond
                if (originatingAddress != null) {
                    sendSMSResponse(context, originatingAddress)
                }
            }
        }
    }

    private fun sendSMSResponse(context: Context, phoneNumber: String) {
        val smsManager = SmsManager.getDefault()
        val message = context.getString(R.string.sms_message_blocked)
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

}