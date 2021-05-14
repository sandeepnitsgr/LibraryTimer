package com.test.librarytimer.presentation.ui

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.lifecycle.ViewModelProvider
import com.test.librarytimer.R
import com.test.librarytimer.presentation.viewmodel.EntryExitViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var locationIdTv: TextView
    private lateinit var pricePerMinuteTv: TextView
    private lateinit var startTimeTv: TextView
    private lateinit var locationTv: TextView
    private lateinit var timerTv: TextView
    private lateinit var btn: Button

    private lateinit var viewModel: EntryExitViewModel

    private lateinit var prefs : SharedPreferences

    private val timerFormatter = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewsAndPreference()
        initTimeZone()
        setClickListener()
        if (!::viewModel.isInitialized)
            initViewModel()
        observeLiveDataForTimerUpdate()
        viewModel.startTimer(System.currentTimeMillis())
    }

    private fun observeLiveDataForTimerUpdate() {
        viewModel.timerLiveData.observe(this) { timerValue ->
            updateTimerValue(timerFormatter.format(timerValue))
        }

    }
    private fun updateTimerValue(timerValue: String?) {
        timerTv.text = timerValue
    }
    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(EntryExitViewModel::class.java)
    }

    private fun setClickListener() {
        btn.setOnClickListener {
            // start scanning
        }
    }

    private fun initTimeZone() {
        timerFormatter.timeZone = TimeZone.getTimeZone("GMT")
    }

    private fun initViewsAndPreference() {
        viewFlipper = findViewById(R.id.viewFlipper)
        locationIdTv = findViewById(R.id.locationIdValue)
        pricePerMinuteTv = findViewById(R.id.priceValue)
        startTimeTv = findViewById(R.id.startTimeValue)
        locationTv = findViewById(R.id.locationDetailValue)
        timerTv = findViewById(R.id.timerTv)
        btn = findViewById(R.id.btn)

        prefs = getSharedPreferences("prefs", MODE_PRIVATE)

    }

    override fun onStop() {
        super.onStop()
        viewModel.stopTimer()
    }
}
