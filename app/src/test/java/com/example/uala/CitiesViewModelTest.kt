package com.example.uala

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringSetPreferencesKey
import app.cash.turbine.test
import com.example.uala.model.City
import com.example.uala.model.Coord
import com.example.uala.service.citiesModule.CitiesRepository
import com.example.uala.viewmodel.CitiesViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CitiesViewModelTest {

    private lateinit var repository: CitiesRepository
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var viewModel: CitiesViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        dataStore = mockk()
        val fakePrefsFlow = flowOf(
            preferencesOf(
                stringSetPreferencesKey("favorites_key") to setOf("1", "2")
            )
        )
        every { dataStore.data } returns fakePrefsFlow
    }

    @Test
    fun `filteredCities emits expected result after loading`() = runTest {
        val mockCities = listOf(
            City(id = 1L, name = "Berlin", country = "Germany", coord = Coord(0.0,0.0)),
            City(id = 2L, name = "Tokyo", country = "Japan", coord = Coord(0.0,0.0)),
            City(id = 3L, name = "Buenos Aires", country = "Argentina", coord = Coord(0.0,0.0))
        )
        coEvery { repository.getCities() } returns mockCities

        viewModel = CitiesViewModel(repository, dataStore)
        viewModel.filteredCities.test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertTrue(result.first { it.city.id == 1L }.isFavorite)
            assertTrue(result.first { it.city.id == 2L }.isFavorite)
            assertFalse(result.first { it.city.id == 3L }.isFavorite)
        }
    }

    @Test
    fun `toggleFavorite adds city id when not present and removes when present`() = runTest {
        val mockCities = listOf(
            City(id = 1L, name = "Berlin", country = "Germany", coord = Coord(0.0,0.0)),
            City(id = 2L, name = "Tokyo", country = "Japan", coord = Coord(0.0,0.0)),
            City(id = 3L, name = "Buenos Aires", country = "Argentina", coord = Coord(0.0,0.0))
        )
        coEvery { repository.getCities() } returns mockCities

        viewModel = CitiesViewModel(repository, dataStore)
        viewModel.toggleShowOnlyFavorites()
        viewModel.filteredCities.test {
            val result = awaitItem()
            assertEquals(2, result.size)
        }
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
