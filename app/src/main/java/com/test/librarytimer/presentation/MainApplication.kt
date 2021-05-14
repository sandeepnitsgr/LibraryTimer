package com.test.librarytimer.presentation

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

class MainApplication : Application(), LifecycleObserver {
    private val prefs: SharedPreferences by lazy {
        getSharedPreferences("prefs", MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppInForeground() {
        if (isServiceStarted()) {
            stopTrackerService()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppInBackground() {
        if (shouldStartService())
            startTrackerService()
    }

    private fun startTrackerService() {
        Log.e("sandeep", "start service")
    }

    private fun shouldStartService(): Boolean {
        return true
    }

    private fun isServiceStarted(): Boolean {
        return true
    }

    private fun stopTrackerService() {
        Log.e("sandeep", "stop service")
    }
}