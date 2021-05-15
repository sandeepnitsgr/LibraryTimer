package com.test.librarytimer.presentation.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import com.test.librarytimer.*
import com.test.librarytimer.data.model.RequestParam
import com.test.librarytimer.data.model.Response
import com.test.librarytimer.data.model.ScanResult
import com.test.librarytimer.data.model.Status
import com.test.librarytimer.di.Component
import com.test.librarytimer.di.DaggerComponent
import com.test.librarytimer.presentation.viewmodel.EntryExitViewModel
import com.test.librarytimer.presentation.viewmodel.EntryExitViewModelFactory
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var locationIdTv: TextView
    private lateinit var pricePerMinuteTv: TextView
    private lateinit var startTimeTv: TextView
    private lateinit var locationTv: TextView
    private lateinit var timerTv: TextView

    private lateinit var entryFlipper: ViewFlipper
    private lateinit var endTimeTv: TextView
    private lateinit var totalTimeInMinutesTv: TextView
    private lateinit var amountToBePaidTv: TextView

    private lateinit var detailsLayout: ScrollView
    private lateinit var btn: Button

    private lateinit var viewModel: EntryExitViewModel

    private lateinit var prefs: SharedPreferences

    private val df = SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH)
    private val timerFormatter = SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH)

    private val compositeDisposable = CompositeDisposable()

    private val gson = Gson()
    @Inject
    lateinit var factory : EntryExitViewModelFactory
    // zxing
    private lateinit var qrScan: IntentIntegrator

    private var isTimerRunning = false
    private var startTime by Delegates.notNull<Long>()

    lateinit var component:Component
    private lateinit var submitDataObserver : Observer<Response>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initInjector()
        initViewsAndPreference()
        initScanner()
        initTimeZone()
        setClickListener()
        if (!::viewModel.isInitialized)
            initViewModel()
        observeLiveDataForTimerUpdate()
    }

    private fun initInjector() {
        component = DaggerComponent.builder().build()
        component.addInjection(this)
    }

    private fun initViewsAndPreference() {
        viewFlipper = findViewById(R.id.viewFlipper)
        locationIdTv = findViewById(R.id.locationIdValue)
        pricePerMinuteTv = findViewById(R.id.priceValue)
        startTimeTv = findViewById(R.id.startTimeValue)
        locationTv = findViewById(R.id.locationDetailValue)
        timerTv = findViewById(R.id.timerTv)

        entryFlipper = findViewById(R.id.entryFlipper)
        endTimeTv = findViewById(R.id.endTimeValue)
        totalTimeInMinutesTv = findViewById(R.id.totalTimeValue)
        amountToBePaidTv = findViewById(R.id.amountValue)
        detailsLayout = findViewById(R.id.detailsLayout)
        btn = findViewById(R.id.btn)

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

    }

    private fun initScanner() {
        qrScan = IntentIntegrator(this)
    }

    private fun initTimeZone() {
        timerFormatter.timeZone = TimeZone.getTimeZone(TIMEZONE_GMT)
    }

    private fun observeLiveDataForTimerUpdate() {
        viewModel.timerLiveData.observe(this) { timerValue ->
            updateTimerValue(timerFormatter.format(timerValue))
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, factory).get(EntryExitViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        fetchTimerStatusAndUpdateUI()
    }

    override fun onStop() {
        super.onStop()
        saveTimerInfo(null)
    }

    private fun startTimerAndSaveScannedResult(output: String) {
        startTime = System.currentTimeMillis()
        startTimer()
        flipScreenViewTo(ENTRY_DETAIL_VIEW)
        entryFlipper.displayedChild = ENTRY_DETAIL_MAIN_VIEW
        saveTimerInfo(output)
        val disposable = getScannedResultObservable(output)
        compositeDisposable.add(disposable)
    }

    private fun getScannedResultObservable(output: String) = Single.just(output)
        .subscribeOn(Schedulers.computation())
        .map { scanResult ->
            gson.fromJson(scanResult, ScanResult::class.java)
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ item ->
            updateViews(item)
        }) { e ->
            e.printStackTrace()
            showToast(UNKNOWN_ERROR_OCCURRED)

        }

    // ************ UI Operations START ********************
    private fun setClickListener() {
        btn.setOnClickListener {
            qrScan.initiateScan()
        }
    }

    private fun updateTimerValue(timerValue: String?) {
        timerTv.text = timerValue
    }

    private fun flipScreenViewTo(viewId: Int) {
        viewFlipper.displayedChild = viewId
    }

    private fun updateViews(scanResult: ScanResult?) {
        scanResult?.let {
            locationIdTv.text = it.locationId
            locationTv.text = it.locationDetails
            pricePerMinuteTv.text = "${it.pricePerMinute}"
            startTimeTv.text = df.format(Date(startTime).time)
            changeButtonText(getString(R.string.endSession))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun changeButtonText(buttonText: String) {
        btn.text = buttonText
    }

    private fun updateTotalTimeAndCost(endTimeValue: Long) {
        entryFlipper.displayedChild = EXIT_DETAIL_VIEW
        endTimeTv.text = df.format(Date(endTimeValue))
        val totalMinutes = getTotalMinutesSpent()
        totalTimeInMinutesTv.text = "$totalMinutes"
        val totalAmount = totalMinutes * pricePerMinuteTv.text.toString().toFloat()
        amountToBePaidTv.text = "$totalAmount"
        addSubmitDataObserver()
        viewModel.submitSessionData(RequestParam(locationIdTv.text.toString(), totalMinutes, endTimeValue))
    }

    private fun addSubmitDataObserver() {
        submitDataObserver = Observer { response ->
            when(response.status) {
                Status.LOADING -> showToast("Sending data to Server..")
                Status.ERROR -> {
                    showToast("Sending data to server failed!")
                    viewModel.submitDetailLiveData.removeObserver(submitDataObserver)
                }
                else -> {
                    showToast("Data successfully posted!")
                    viewModel.submitDetailLiveData.removeObserver(submitDataObserver)
                }
            }
        }
        viewModel.submitDetailLiveData.observe(this, submitDataObserver)
    }

    // ************ UI operations END ***************************

    //********* SharedPreference operations START *********************
    // *************** saving ***********************
    private fun saveTimerInfo(jsonString: String?) {
        val editor = prefs.edit()
        editor.putLong(START_TIME, startTime)
        editor.putBoolean(IS_TIMER_RUNNING, isTimerRunning)
        jsonString?.let { editor.putString(SCAN_DETAIL, jsonString) }
        editor.apply()
    }

    // *********** fetching **************************
    private fun getSavedData(): String? {
        return prefs.getString(SCAN_DETAIL, null)
    }

    private fun getTimerData() {
        startTime = prefs.getLong(START_TIME, System.currentTimeMillis())
        isTimerRunning = prefs.getBoolean(IS_TIMER_RUNNING, false)
    }

    // ********** removing ****************************
    private fun removeData() {
        prefs.edit()
            .remove(SCAN_DETAIL)
            .remove(START_TIME)
            .remove(IS_TIMER_RUNNING)
            .apply()
    }
    //***** saving into SharedPreference operations END ************

    private fun getTotalMinutesSpent(): Int {
        val completeTimeSplits = timerTv.text.toString().split(":")
        val hour = Integer.parseInt(completeTimeSplits[0])
        val minutes = Integer.parseInt(completeTimeSplits[1])
        val seconds = Integer.parseInt(completeTimeSplits[2])
        return (hour * 60) + minutes + if (seconds > 0) 1 else 0
    }

    private fun fetchTimerStatusAndUpdateUI() {
        getTimerData()
        if (isTimerRunning) {
            startTimer()
            flipScreenViewTo(ENTRY_DETAIL_VIEW)
        }

        if (startTime.compareTo(0) == 0)
            flipScreenViewTo(WELCOME_VIEW)
        val response = getSavedData()
        response?.let {
            val single = getScannedResultObservable(it)
            compositeDisposable.add(single)
        }
    }

    private fun startTimer() {
        viewModel.startTimer(startTime)
        isTimerRunning = true
    }

    private fun stopTimer() {
        viewModel.stopTimer()
        isTimerRunning = false
        updateTotalTimeAndCost(System.currentTimeMillis())
        changeButtonText(getString(R.string.scanNow))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                showToast(RESULT_NOT_FOUND)
            } else {
                var output = result.contents.replace("\\", "")
                output = output.substring(1, output.length - 1)
                val savedData = getSavedData()
                if (savedData == null) {
                    startTimerAndSaveScannedResult(output)
                } else {
                    if (output == savedData) {
                        stopTimer()
                        removeData()
                    } else {
                        showToast(SCAN_CORRECT_BARCODE)
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
