package com.example.uala

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.uala.viewmodel.CitiesViewModel
import com.example.uala.viewmodel.DetailsViewModel
import com.example.uala.views.CitiesScreen
import com.example.uala.views.CityDetailScreen
import com.example.uala.views.MapScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: CitiesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CitiesAppNavHost(viewModel = viewModel)
        }
    }

    @Composable
    fun CitiesAppNavHost(
        viewModel: CitiesViewModel,
        navController: NavHostController = rememberNavController()
    ) {
        val cities by viewModel.filteredCities.collectAsState()
        val onlyFavorites by viewModel.showOnlyFavorites.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val query by viewModel.query.collectAsState()

        val selectedCity by viewModel.selectedCity.collectAsState()

        val isLandscape =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        NavHost(navController = navController, startDestination = "cities") {
            composable("cities") {
                if (isLandscape) {
                    Row(
                        Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.5f)
                                .zIndex(2f)
                                .background(color = Color.White)
                        ) {
                            CitiesScreen(
                                isLoading = isLoading,
                                query = query,
                                cities = cities,
                                onlyFavorites = onlyFavorites,
                                onFavoriteClick = viewModel::toggleFavorite,
                                onToggleShowFavorites = viewModel::toggleShowOnlyFavorites,
                                onQueryChange = viewModel::updateQuery,
                                onCityClick = { city ->
                                    viewModel.setSelectedCity(city)
                                },
                                onDetailClick = { cityId ->
                                    navController.navigate("details/$cityId")
                                }
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(0.5f)
                                .zIndex(1f)
                        ) {
                            selectedCity?.let { city ->
                                key(city) {
                                    MapScreen(
                                        lat = city.city.coord.lat,
                                        lon = city.city.coord.lon,
                                        name = city.city.name
                                    )
                                }
                            } ?: Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Seleccioná una ciudad")
                            }
                        }
                    }
                } else {
                    CitiesScreen(
                        isLoading = isLoading,
                        query = query,
                        cities = cities,
                        onlyFavorites = onlyFavorites,
                        onFavoriteClick = viewModel::toggleFavorite,
                        onToggleShowFavorites = viewModel::toggleShowOnlyFavorites,
                        onQueryChange = viewModel::updateQuery,
                        onCityClick = { city ->
                            val lat = city.city.coord.lat.toString()
                            val lon = city.city.coord.lon.toString()
                            val name = city.city.name
                            navController.navigate("map/$lat/$lon/$name")
                        },
                        onDetailClick = { cityId ->
                            navController.navigate("details/$cityId")
                        }
                    )
                }
            }
            composable(
                "map/{lat}/{lon}/{name}",
                arguments = listOf(
                    navArgument("lat") { type = NavType.StringType },
                    navArgument("lon") { type = NavType.StringType },
                    navArgument("name") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
                val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull() ?: 0.0
                val name = backStackEntry.arguments?.getString("name") ?: ""
                MapScreen(lat, lon, name)
            }

            composable(
                "details/{cityId}",
                arguments = listOf(navArgument("cityId") { type = NavType.LongType })
            ) { backStackEntry ->
                val cityId = backStackEntry.arguments?.getLong("cityId") ?: return@composable
                val city = cities.find { it.city.id == cityId }

                if (city != null) {
                    val detailViewModel: DetailsViewModel = hiltViewModel()

                    LaunchedEffect(city) {
                        detailViewModel.fetchSummary(city.city.name)
                    }

                    val detail by detailViewModel.details

                    CityDetailScreen(
                        city = city,
                        wikipediaSummary = detail
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Ciudad no encontrada")
                    }
                }
            }

        }
    }
}