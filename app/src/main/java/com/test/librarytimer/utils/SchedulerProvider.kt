package com.test.librarytimer.utils
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SchedulerProvider @Inject constructor() : BaseSchedulerProvider {
    /**
     * IO thread pool scheduler
     */
    override fun io(): Scheduler {
        return Schedulers.io()
    }

    /**
     * Computation thread pool scheduler
     */
    override fun computation(): Scheduler {
        return Schedulers.computation()
    }

    /**
     * Main Thread scheduler
     */
    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

}