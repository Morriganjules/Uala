package com.example.uala.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.uala.model.CityUiModel

@Composable
fun CitiesScreen(
    query: String,
    isLoading: Boolean,
    cities: List<CityUiModel>,
    onlyFavorites: Boolean,
    onDetailClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    onToggleShowFavorites: () -> Unit,
    onQueryChange: (String) -> Unit,
    onCityClick: (CityUiModel) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                label = { Text("Search") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Only favorites")
                Switch(
                    checked = onlyFavorites,
                    onCheckedChange = { onToggleShowFavorites() }
                )
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(cities) { cityModel ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = { onDetailClick(cityModel.city.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Más información",
                                    tint = Color.Blue
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onCityClick(cityModel) }
                                    .padding(start = 8.dp) // para separar del ícono
                            ) {
                                Text("${cityModel.city.name}, ${cityModel.city.country}")
                                Text("${cityModel.city.coord.lat}, ${cityModel.city.coord.lon}")
                            }

                            IconButton(onClick = { onFavoriteClick(cityModel.city.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Favorito",
                                    tint = if (cityModel.isFavorite) Color.Yellow else Color.Gray
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }

    }
}
