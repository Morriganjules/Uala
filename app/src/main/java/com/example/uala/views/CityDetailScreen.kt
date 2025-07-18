package com.example.uala.views

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.uala.model.CityUiModel

@Composable
fun CityDetailScreen(
    city: CityUiModel,
    wikipediaSummary: String?
) {
    val context = LocalContext.current
    val wikipediaUrl = "https://en.wikipedia.org/wiki/${city.city.name}_${city.city.country}"
    val googleUrl = "https://www.google.com/search?q=${city.city.name}+${city.city.country}"

    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Nombre: ${city.city.name}", style = MaterialTheme.typography.headlineSmall)
        Text("País: ${city.city.country}")
        Text("Coordenadas: ${city.city.coord.lat}, ${city.city.coord.lon}")

        Spacer(modifier = Modifier.height(16.dp))

        if (wikipediaSummary == null) {
            CircularProgressIndicator()
        } else {
            Text("More info:", style = MaterialTheme.typography.titleMedium)
            Text(wikipediaSummary)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wikipediaUrl))
                    context.startActivity(intent)
                }) {
                    Text("Wikipedia")
                }

                Button(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleUrl))
                    context.startActivity(intent)
                }) {
                    Text("Google")
                }
            }
        }
    }
}
