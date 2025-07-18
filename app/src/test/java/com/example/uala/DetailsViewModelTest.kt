package com.example.uala

import com.example.uala.model.Details
import com.example.uala.service.citiesModule.WikipediaRepository
import com.example.uala.viewmodel.DetailsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: DetailsViewModel
    private val repository = mockk<WikipediaRepository>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DetailsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchSummary should update details with valid response`() = runTest {

        val cityName = "Paris"
        val fakeResponse = Details(title = "Paris",extract = "Paris is the capital of France.")
        coEvery { repository.getCityDetails(cityName) } returns fakeResponse


        viewModel.fetchSummary(cityName)
        advanceUntilIdle()

        assertEquals("Paris is the capital of France.", viewModel.details.value)
        coVerify(exactly = 1) { repository.getCityDetails(cityName) }
    }

    @Test
    fun `fetchSummary should update details with error message on exception`() = runTest {

        val cityName = "Atlantis"
        coEvery { repository.getCityDetails(cityName) } throws IOException("Network error")

        viewModel.fetchSummary(cityName)
        advanceUntilIdle()

        assertEquals("No se pudo obtener la información.", viewModel.details.value)
        coVerify(exactly = 1) { repository.getCityDetails(cityName) }
    }
}