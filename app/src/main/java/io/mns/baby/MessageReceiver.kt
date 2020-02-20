package io.mns.baby

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage


class MessageReceiver : BroadcastReceiver() {
    @Suppress("DEPRECATION")
    override fun onReceive(context: Context, intent: Intent) {
        val data = intent.extras
        val pdus = data!!["pdus"] as Array<*>?
        for (i in pdus!!.indices) {
            val smsMessage =
                SmsMessage.createFromPdu(pdus[i] as ByteArray)
            mListenerService?.messageReceived(smsMessage.messageBody)
            mListenerActivity?.messageReceived(smsMessage.messageBody)
        }
    }

    companion object {
        private var mListenerService: MessageListener? = null
        private var mListenerActivity: MessageListener? = null
        fun bindListener(listener: MessageListener?) {
            mListenerService = listener
        }
        fun bindListenerActivity(listener: MessageListener?) {
            mListenerActivity = listener
        }
    }
    interface MessageListener {
        fun messageReceived(message: String)
    }
}