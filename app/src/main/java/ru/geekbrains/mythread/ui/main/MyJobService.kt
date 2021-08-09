package ru.geekbrains.mythread.ui.main

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "MyJobService"
const val DURATION = "DURATION"

class MyJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        val arg = params?.extras?.getInt(DURATION) ?: 10
        Log.d(TAG, "onStartJob start duration: " + arg)
        startCalculations(arg)
        Log.d(TAG, "onStartJob finish")
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStopJob")
        return true
    }

    private fun startCalculations(seconds: Int): String {
        val date = Date()
        var diffInSecs: Long
        do {
            val currentDate = Date()
            val diffInMs: Long = currentDate.time - date.time
            diffInSecs = TimeUnit.MILLISECONDS.toSeconds(diffInMs)
        } while (diffInSecs < seconds)
        return diffInSecs.toString()
    }
}