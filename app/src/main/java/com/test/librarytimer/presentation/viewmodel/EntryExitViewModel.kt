package com.test.librarytimer.presentation.viewmodel
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.properties.Delegates

class EntryExitViewModel : ViewModel() {

    val timerLiveData: LiveData<Long>
        get() = _timerLiveData

    private val _timerLiveData = MutableLiveData<Long>()

    private val handler = Handler(Looper.getMainLooper())
    private var runnable = object : Runnable {
        override fun run() {
            if (!shouldStop) {
                val timeInMilliseconds = System.currentTimeMillis() - startTime
                _timerLiveData.value = timeInMilliseconds
                handler.postDelayed(this, 1000)
            }
        }
    }
    private var shouldStop: Boolean = false


    private var startTime by Delegates.notNull<Long>()

    fun startTimer(startTime: Long) {
        shouldStop = false
        this.startTime = startTime

        handler.postDelayed(runnable, 0)
    }

    fun stopTimer() {
        shouldStop = true
        handler.removeCallbacks(runnable)
    }
}
