package com.test.librarytimer.presentation

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.test.librarytimer.IS_TIMER_RUNNING
import com.test.librarytimer.PREF_NAME
import com.test.librarytimer.presentation.service.TimerService

class MainApplication : Application(), LifecycleObserver {
    private val prefs: SharedPreferences by lazy {
        getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppInForeground() {
        if (isTimerRunning()) {
            stopTrackerService()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppInBackground() {
        if (isTimerRunning())
            startTrackerService()
    }

    private fun startTrackerService() {
        val startTimerServiceIntent = Intent(this, TimerService::class.java)
        ContextCompat.startForegroundService(this, startTimerServiceIntent)
    }

    private fun isTimerRunning(): Boolean {
        return prefs.getBoolean(IS_TIMER_RUNNING, false)
    }

    private fun stopTrackerService() {
        TimerService.getInstance()?.stopNotification()
    }
}