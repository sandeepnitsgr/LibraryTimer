package com.test.librarytimer.presentation.service

import android.app.*
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.test.librarytimer.*
import com.test.librarytimer.presentation.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class TimerService : Service() {

    private val df = SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH)
    private var notificationBuilder by Delegates.notNull<NotificationCompat.Builder>()
    private var notification by Delegates.notNull<Notification>()
    private val handler = Handler(Looper.getMainLooper())
    private var shouldStop: Boolean = false
    private var startTime by Delegates.notNull<Long>()

    companion object {

        private var sInstance: TimerService? = null
        fun getInstance(): TimerService? {
            return sInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        setTimeZone()

    }

    private fun setTimeZone() {
        df.timeZone = TimeZone.getTimeZone(TIMEZONE_GMT)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            STOP_FOREGROUND_ACTION -> {
                stopForeground(true)
                stopSelf()
            }
            else -> {
                val prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                startTime = prefs.getLong(START_TIME, System.currentTimeMillis())
                startForegroundService()
                startTimer()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startForegroundService() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentTitle(getString(R.string.notification_title))
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(getMainActivityPendingIntent())

        notification = notificationBuilder.build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java),
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private var runnable = object : Runnable {
        override fun run() {
            if (!shouldStop) {
                val timeInMilliseconds = System.currentTimeMillis() - startTime
                NotificationManagerCompat.from(this@TimerService).notify(
                    NOTIFICATION_ID, notificationBuilder
                        .setContentText(df.format(Date(timeInMilliseconds).time))
                        .build()
                )
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun startTimer() {
        shouldStop = false
        handler.postDelayed(runnable, 0)
    }

    private fun stopTimer() {
        shouldStop = true
        handler.removeCallbacks(runnable)
    }

    fun stopNotification() {
        stopTimer()
        stopForeground(true)
        stopSelf()
    }

}
