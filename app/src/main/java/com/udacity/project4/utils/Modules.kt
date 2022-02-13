package com.udacity.project4.utils

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    single<ReminderDataSource> { RemindersLocalRepository(get(), get()) }

    //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
    viewModel {
        RemindersListViewModel(
            get(),
            get() as ReminderDataSource
        )
    }
}