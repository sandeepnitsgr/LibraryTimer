package com.test.librarytimer.di

import com.test.librarytimer.presentation.ui.MainActivity
import dagger.Component

@Component(modules = [RetrofitModule::class])
interface Component {
    fun addInjection(activity: MainActivity)
}