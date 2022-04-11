package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.koin.core.context.stopKoin


import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var remindersViewModel: SaveReminderViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setupViewModel() {
        stopKoin()
        remindersViewModel =
            SaveReminderViewModel(getApplicationContext(), FakeDataSource())
    }

    @Test
    fun testData_isNullOrEmpty() {

        val reminderList = listOf(
            ReminderDataItem("", "Call the doctor's office", "", 0.0, 0.0),
            ReminderDataItem("Do Laundry", "Get laundry done when home", "", 0.0, 0.0),
            ReminderDataItem("", "Meetup with friends for lunch", "Taco Bell", 0.0, 0.0),
        )

        var actual = false

        for (data in reminderList) {
            if (data.title.isNullOrEmpty() || data.location.isNullOrEmpty()) {
                actual = true
                break
            }
        }

        assertThat(actual, `is`(true))
    }

    @Test
    fun onClear_returnsNull() = runBlockingTest {

        val reminderList = listOf(
            remindersViewModel.reminderTitle.value,
            remindersViewModel.reminderDescription.value,
            remindersViewModel.reminderSelectedLocationStr.value,
            remindersViewModel.latitude.value,
            remindersViewModel.longitude.value
        )

        remindersViewModel.onClear()

        var nullCount = 0
        for (i in reminderList) if (i == null) nullCount++

        val actual = nullCount == reminderList.size

        assertThat(actual, Matchers.`is`(true))
    }

    @Test
    fun validateEnteredData_titleOrLocationIsEmptyOrNull_returnsTrue() = runBlockingTest {

        val reminderList = listOf(
            ReminderDataItem("", "Pay electric bill by 5 PM", "", 0.0, 0.0),
            ReminderDataItem("Buy groceries", "Eggs, Milk, and Bread", "", 0.0, 0.0),
            ReminderDataItem("", "Do laundry", "Home", 0.0, 0.0),
            ReminderDataItem(null, "Pay rent bill by Friday", null, 0.0, 0.0),
            ReminderDataItem("Take Trash Out", "Take out the trash tonight", null, 0.0, 0.0),
            ReminderDataItem(null, "Walk the dog", "Home", 0.0, 0.0)
        )

        var nullAndEmptyCount = 0

        for (data in reminderList) {
            if (!remindersViewModel.validateEnteredData(data)) nullAndEmptyCount++

        }

        val actual = nullAndEmptyCount == reminderList.size

        assertThat(actual, `is`(true))
    }

    @Test
    fun validateEnteredData_showSnackBarIntValue_returnsTrue() = runBlockingTest {

        val reminderList = listOf(
            ReminderDataItem("Buy groceries", "Eggs, Milk, and Bread", "", 0.0, 0.0),
            ReminderDataItem("", "Do laundry", "Home", 0.0, 0.0),
            ReminderDataItem("Take Trash Out", "Take out the trash tonight", null, 0.0, 0.0),
            ReminderDataItem(null, "Walk the dog", "Home", 0.0, 0.0)
        )

        val snackBarList = mutableListOf<Int>()

        for (data in reminderList) {
            remindersViewModel.validateEnteredData(data)
            remindersViewModel.showSnackBarInt.value?.let { snackBarList.add(it) }
        }

        val expectedErrors = listOf(
            R.string.err_select_location,
            R.string.err_enter_title,
            R.string.err_select_location,
            R.string.err_enter_title,
        )

        val actual = expectedErrors == snackBarList

        assertThat(actual, `is`(true))
    }

    @Test
    fun saveReminder_showLoading() = runBlockingTest {

        val reminder = ReminderDataItem(
            "Do Laundry",
            "Get laundry done today",
            "Home",
            0.0,
            0.0
        )

        mainCoroutineRule.pauseDispatcher()

        remindersViewModel.saveReminder(reminder)

        assertThat(remindersViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(remindersViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun saveReminder_showToastValue_returnsTrue() = runBlockingTest{

        val reminder = ReminderDataItem(
            "Meet with Friends",
            "Meetup with friends for coffee",
            "Starbucks",
            1.0,
            2.0
        )

        remindersViewModel.saveReminder(reminder)

        assertThat(remindersViewModel.showToast.getOrAwaitValue(), `is`(getApplicationContext<Context>().getString(R.string.reminder_saved)))
    }
}