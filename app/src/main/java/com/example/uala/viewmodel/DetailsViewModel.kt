package com.example.uala.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uala.service.citiesModule.WikipediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: WikipediaRepository
) : ViewModel() {

    private val _details = mutableStateOf<String?>(null)
    val details: State<String?> = _details

    fun fetchSummary(cityName: String) {

        viewModelScope.launch {
            try {
                val response = repository.getCityDetails(cityName)
                _details.value = response.extract
            } catch (e: Exception) {
                _details.value = "No se pudo obtener la información."
            }
        }
    }
}