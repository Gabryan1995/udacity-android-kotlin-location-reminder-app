package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDB() {
        database.close()
    }

    @Test
    fun saveReminder_getById() = runBlockingTest {

        val reminder = ReminderDTO(
            "title",
            "desc",
            "loc",
            0.0,
            0.0,
        )

        database.reminderDao().saveReminder(reminder)

        val loadedReminder = database.reminderDao().getReminderById(reminder.id)

        assertThat(loadedReminder as ReminderDTO, notNullValue())
        assertThat(loadedReminder.id, `is`(loadedReminder.id))
        assertThat(loadedReminder.title, `is`(loadedReminder.title))
        assertThat(loadedReminder.description, `is`(loadedReminder.description))
        assertThat(loadedReminder.location, `is`(loadedReminder.location))
        assertThat(loadedReminder.latitude, `is`(loadedReminder.latitude))
        assertThat(loadedReminder.longitude, `is`(loadedReminder.longitude))
    }


    @Test
    fun deleteAllReminder_checkIsEmpty() = runBlockingTest {

        val reminder1 = ReminderDTO(
            "title1",
            "desc1",
            "loc1",
            0.0,
            0.0,
        )

        val reminder2 = ReminderDTO(
            "title2",
            "desc2",
            "loc2",
            0.0,
            0.0,
        )

        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)

        database.reminderDao().deleteAllReminders()

        val reminders = database.reminderDao().getReminders()

        assertThat(reminders, `is`(emptyList()))
    }
}