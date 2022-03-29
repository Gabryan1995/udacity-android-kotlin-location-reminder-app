package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDB() {
        database.close()
    }

    @Test
    fun saveReminder_matchesRepository() = runBlocking {

        val reminder = ReminderDTO(
            "title",
            "dec",
            "loc",
            0.0,
            0.0
        )

        repository.saveReminder(reminder)

        val repo = (repository.getReminders() as Result.Success).data

        assertThat(repo[0].id, `is`(reminder.id))
        assertThat(repo[0].title, `is`(reminder.title))
        assertThat(repo[0].description, `is`(reminder.description))
        assertThat(repo[0].latitude, `is`(reminder.latitude))
        assertThat(repo[0].longitude, `is`(reminder.longitude))
        assertThat(repo[0].location, `is`(reminder.location))

    }

    @Test
    fun dataNotFound_errorMessageDisplayed() = runBlocking {

        val reminder = ReminderDTO(
            "title",
            "dec",
            "loc",
            0.0,
            0.0,
            "0"
        )

        val result = (repository.getReminder(reminder.id) as Result.Error).message

        assertThat(result, `is`("Reminder not found!"))

    }

    @Test
    fun deleteAllReminders_checkIsEmpty() = runBlocking {

        val reminder = ReminderDTO(
            "title",
            "dec",
            "loc",
            0.0,
            0.0
        )

        repository.saveReminder(reminder)

        repository.deleteAllReminders()

        val repo = (repository.getReminders() as Result.Success).data

        assertThat(repo, `is`(emptyList()))
    }

}