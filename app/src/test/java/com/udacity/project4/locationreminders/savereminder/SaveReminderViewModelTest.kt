package com.udacity.project4.locationreminders.savereminder

import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4ClassRunner::class)
class SaveReminderViewModelTest {

    @Test
    fun addNewReminder_setsNewReminderEvent() {

        // Given a fresh TasksViewModel
        val remindersViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            FakeDataSource()
        )

        // When adding a new task
        remindersViewModel.validateAndSaveReminder(
            ReminderDataItem(
                "Do Laundry",
                "Get laundry done today",
                "Home",
                32.51247857566461,
                -94.77955414486334,
                "123456"
            ))


        // Then the new task event is triggered

    }

    //TODO: provide testing to the SaveReminderView and its live data objects


}