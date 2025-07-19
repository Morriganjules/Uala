package com.example.uala.viewmodel

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uala.model.City
import com.example.uala.model.CityUiModel
import com.example.uala.service.citiesModule.CitiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val repository: CitiesRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val FAVORITES_KEY = stringSetPreferencesKey("favorites_key")

    private val _favorites = MutableStateFlow<Set<Long>>(emptySet())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedCity = MutableStateFlow<CityUiModel?>(null)
    val selectedCity: StateFlow<CityUiModel?> = _selectedCity

    val favoritesFromDataStore: Flow<Set<Long>> = dataStore.data
        .map { preferences ->
            preferences[FAVORITES_KEY]?.mapNotNull { it.toLongOrNull() }?.toSet() ?: emptySet()
        }
        .distinctUntilChanged()

    private val _cities = MutableStateFlow<List<City>>(emptyList())
    private val _filteredCities = MutableStateFlow<List<CityUiModel>>(emptyList())
    val filteredCities: StateFlow<List<CityUiModel>> = _filteredCities

    init {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _cities.value = repository.getCities()
            } catch (e: Exception) {
                Log.e("CitiesViewModel", "Error loading cities", e)
            } finally {
                _isLoading.value = false
            }
        }

        viewModelScope.launch {
            favoritesFromDataStore.collect { favSet ->
                _favorites.value = favSet
            }
        }

        viewModelScope.launch {
            combine(
                _cities,
                _query.debounce(300),
                _favorites
            ) { cities, query, favorites ->
                Triple(cities, query, favorites)
            }.collectLatest {
                updateFilteredCities()
            }
        }

        viewModelScope.launch {
            _showOnlyFavorites
                .collectLatest {
                    _isLoading.value = true
                    updateFilteredCities()
                    _isLoading.value = false
                }
        }
    }

    private suspend fun updateFilteredCities() {
        val cities = _cities.value
        val query = _query.value
        val favorites = _favorites.value
        val onlyFavs = _showOnlyFavorites.value

        val result = withContext(Dispatchers.Default) {
            val filtered = if (query.isBlank()) {
                cities
            } else {
                cities.filter {
                    val fullName = "${it.name}, ${it.country}"
                    fullName.startsWith(query, ignoreCase = true)
                }
            }

            filtered
                .sortedWith(compareBy({ it.name.lowercase() }, { it.country.lowercase() }))
                .map { city ->
                    val isFav = city.id in favorites
                    CityUiModel(city, isFav)
                }
                .filter { if (onlyFavs) it.isFavorite else true }
        }

        _filteredCities.value = result
    }

    fun toggleFavorite(cityId: Long) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                val currentSet = preferences[FAVORITES_KEY] ?: emptySet()
                val newSet = if (currentSet.contains(cityId.toString())) {
                    currentSet - cityId.toString()
                } else {
                    currentSet + cityId.toString()
                }
                preferences[FAVORITES_KEY] = newSet
            }
        }
    }

    fun setSelectedCity(city: CityUiModel) {
        _selectedCity.value = city
    }

    fun updateQuery(query: String) {
        _query.value = query
    }

    fun toggleShowOnlyFavorites() {
        _showOnlyFavorites.value = !_showOnlyFavorites.value
    }
}
