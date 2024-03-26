package com.ingskill.googlempas

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ingskill.googlempas.ui.theme.GoogleMpasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleMpasTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MapWithMarkers()
                }
            }
        }
    }
}


@SuppressLint("MissingPermission")
@Composable
fun MapWithMarkers() {
    var lat by remember { mutableStateOf("") }
    var lng by remember { mutableStateOf("") }
    val context = LocalContext.current
    var mapView: MapView? by remember { mutableStateOf(null) }
    var googleMap: GoogleMap? by remember { mutableStateOf(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
//    val database = Firebase.database
//    val databaseRef = database.getReference("busLocation")

    fun updateMarkerPosition(latitude: Double, longitude: Double) {
        googleMap?.apply {
            clear()
            val coordinates = LatLng(latitude, longitude)
            addMarker(MarkerOptions().position(coordinates))
            moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10f))
        }
    }




    DisposableEffect(Unit) {
        val callback = mapView?.let {
            val callback = OnMapReadyCallback { map ->
                googleMap = map
                map.setOnCameraIdleListener {
                    val cameraPosition = map.cameraPosition
                    lat = cameraPosition.target.latitude.toString()
                    lng = cameraPosition.target.longitude.toString()
                }
                map.uiSettings.isZoomControlsEnabled = true
                map.uiSettings.isMapToolbarEnabled = true

            }
            mapView?.getMapAsync(callback)
            callback
        }

        onDispose {
            callback?.let { mapView?.getMapAsync { googleMap?.clear() } }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = "Latitude:${lat}",
                onValueChange = { lat = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(16.dp))
            BasicTextField(
                value = "Longitude:${lng}",
                onValueChange = { lng = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val latDouble = lat.toDoubleOrNull()
            val lngDouble = lng.toDoubleOrNull()
            if (latDouble != null && lngDouble != null) {
                val coordinates = LatLng(latDouble, lngDouble)
                googleMap?.apply {
                    clear()
                    addMarker(MarkerOptions().position(coordinates))
                    moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10f))
                }
                val coordinateData = hashMapOf(
                    "latitude" to latDouble,
                    "longitude" to lngDouble
                )
//                databaseRef.setValue(coordinateData)

            }
        }) {
            Text(text = "Update Map")
        }
        Spacer(modifier = Modifier.height(16.dp))
        AndroidView(
            { context ->
                MapView(context).apply {
                    mapView = this
                    onCreate(null)
                }
            }, modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp)
        )
    }
}