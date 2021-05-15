package com.test.librarytimer.utils

import io.reactivex.Scheduler

interface BaseSchedulerProvider {
    fun io() : Scheduler
    fun computation() : Scheduler
    fun ui() : Scheduler
}