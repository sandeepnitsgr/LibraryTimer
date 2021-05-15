package com.test.librarytimer.presentation.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.librarytimer.data.model.RequestParam
import com.test.librarytimer.data.model.Response
import com.test.librarytimer.data.remote.Repository
import com.test.librarytimer.utils.BaseSchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import kotlin.properties.Delegates

class EntryExitViewModel @Inject constructor(
    private val repository: Repository,
    private val schedulerProvider: BaseSchedulerProvider
) : ViewModel() {

    private val disposable = CompositeDisposable()
    val timerLiveData: LiveData<Long>
        get() = _timerLiveData
    private val _timerLiveData = MutableLiveData<Long>()

    val submitDetailLiveData: LiveData<Response>
        get() = _submitDetailLiveData
    private val _submitDetailLiveData = MutableLiveData<Response>()

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

    fun submitSessionData(requestParam: RequestParam) {
        disposable.add(
            repository.submitData(requestParam).subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .doOnSubscribe { _submitDetailLiveData.value = Response.loading() }
                .subscribe(
                    { response ->
                        _submitDetailLiveData.value = Response.success(response)
                    }
                ) { throwable ->
                    _submitDetailLiveData.value = Response.error(throwable)
                }
        )

    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}
