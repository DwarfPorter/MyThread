package ru.geekbrains.mythread.ui.main

import android.app.IntentService
import android.content.Intent
import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit

class MyIntentService(name: String = "MyIntentService") : IntentService(name) {
    override fun onHandleIntent(intent: Intent?) {
        val arg = intent?.extras?.getInt(DURATION) ?: 10
        Log.d("MyIntentService", "Start service with duration: " + arg)
        startCalculations(arg)
        val broadcastIntent = Intent(TEST_BROADCAST_INTENT_FILTER)
        broadcastIntent.putExtra(THREADS_FRAGMENT_BROADCAST_EXTRA, "Service take " + arg + "sec")
        sendBroadcast(broadcastIntent)
        Log.d("MyIntentService", "Complete service")
    }

    private fun startCalculations(seconds: Int): String {
        val date = Date()
        var diffInSec: Long
        do {
            val currentDate = Date()
            val diffInMs: Long = currentDate.time - date.time
            diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs)
        } while (diffInSec < seconds)
        return diffInSec.toString()
    }
}