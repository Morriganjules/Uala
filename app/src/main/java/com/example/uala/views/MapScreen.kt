package com.example.uala.views

import android.preference.PreferenceManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(lat: Double, lon: Double, name: String) {
    AndroidView(factory = { context ->
        // Configuración básica de OSMDroid
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = context.packageName

        // Creamos el mapa
        val mapView = MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setBuiltInZoomControls(true)
            setMultiTouchControls(true)

            controller.setZoom(10.0)
            controller.setCenter(GeoPoint(lat, lon))

            // Agregar un marcador
            val marker = Marker(this)
            marker.position = GeoPoint(lat, lon)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = name
            overlays.add(marker)
        }

        mapView
    },
        modifier = Modifier.fillMaxSize()
    )
}
