package com.test.librarytimer.presentation.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
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

    // zxing
    private lateinit var qrScan: IntentIntegrator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewsAndPreference()
        initScanner()
        initTimeZone()
        setClickListener()
        if (!::viewModel.isInitialized)
            initViewModel()
        observeLiveDataForTimerUpdate()
        viewModel.startTimer(System.currentTimeMillis())
    }

    private fun initScanner() {
        qrScan = IntentIntegrator(this)
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
            qrScan.initiateScan()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,"scanned result : " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopTimer()
    }
}
