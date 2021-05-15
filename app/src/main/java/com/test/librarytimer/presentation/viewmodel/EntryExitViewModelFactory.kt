package com.test.librarytimer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.test.librarytimer.data.remote.Repository
import com.test.librarytimer.utils.SchedulerProvider
import javax.inject.Inject

class EntryExitViewModelFactory @Inject constructor(
    private val repository: Repository,
    private val schedulerProvider: SchedulerProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EntryExitViewModel::class.java))
            return EntryExitViewModel(repository, schedulerProvider) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}
