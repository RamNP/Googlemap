package com.ingskill.googlempas

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
//Extract Screen
@SuppressLint("MissingPermission")
@Composable
fun MapWithMarker() {
    var markers by remember { mutableStateOf(emptyList<LatLng>()) }
    var lat by remember { mutableStateOf("") }
    var lng by remember { mutableStateOf("") }
    var mapView: MapView? by remember { mutableStateOf(null) }
    var googleMap: GoogleMap? by remember { mutableStateOf(null) }
    val database = Firebase.database
    val databaseRef = database.getReference("BusLocation")

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
                markers.forEach { marker ->
                    googleMap?.addMarker(MarkerOptions().position(marker))
                }
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
                    addMarker(MarkerOptions().position(coordinates))
                    moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10f))
                }
                markers = markers + listOf(coordinates)
                val markersData = markers.mapIndexed { index, latLng ->
                    "Marker${index + 1}" to hashMapOf(
                        "latitude" to latLng.latitude,
                        "longitude" to latLng.longitude
                    )
                }.toMap()
                databaseRef.setValue(markersData)
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

@SuppressLint("MissingPermission")
@Composable
fun NewMapWithMarkers() {
    var markers by remember { mutableStateOf(emptyList<LatLng>()) }
    var lat by remember { mutableStateOf("") }
    var lng by remember { mutableStateOf("") }
    var mapView: MapView? by remember { mutableStateOf(null) }
    var googleMap: GoogleMap? by remember { mutableStateOf(null) }
    val database = Firebase.database
    val databaseRef = database.getReference("BusLocation")

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
                markers.forEach { marker ->
                    googleMap?.addMarker(MarkerOptions().position(marker))
                }
//                focusCameraOnLatestMarker()
            }
            mapView?.getMapAsync(callback)
            callback
        }

        onDispose {
            callback?.let { mapView?.getMapAsync { googleMap?.clear() } }
        }
    }

    fun focusCameraOnLatestMarker() {
        if (markers.isNotEmpty()) {
            val latestMarker = markers.last()
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latestMarker, 10f))
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
                    addMarker(MarkerOptions().position(coordinates))
                }
                markers = markers + listOf(coordinates)
                focusCameraOnLatestMarker()
                val markersData = markers.mapIndexed { index, latLng ->
                    "Marker${index + 1}" to hashMapOf(
                        "latitude" to latLng.latitude,
                        "longitude" to latLng.longitude
                    )
                }.toMap()
                databaseRef.setValue(markersData)
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


//klsalaks

@SuppressLint("MissingPermission")
@Composable
fun Map() {
    var markers by remember { mutableStateOf(emptyList<LatLng>()) }
    var lat by remember { mutableStateOf("") }
    var lng by remember { mutableStateOf("") }
    var mapView: MapView? by remember { mutableStateOf(null) }
    var googleMap: GoogleMap? by remember { mutableStateOf(null) }
    val database = Firebase.database
    val databaseRef = database.getReference("BusLocation")

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
                markers.forEach { marker ->
                    googleMap?.addMarker(MarkerOptions().position(marker))
                }
//                drawPolyline()
            }
            mapView?.getMapAsync(callback)
            callback
        }

        onDispose {
            callback?.let { mapView?.getMapAsync { googleMap?.clear() } }
        }
    }

    fun drawPolyline() {
        if (markers.size >= 2) {
            val polylineOptions = PolylineOptions().apply {
                color(Color.RED)
                width(5f)
                markers.forEach { marker ->
                    add(marker)
                }
            }
            googleMap?.addPolyline(polylineOptions)
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
                    addMarker(MarkerOptions().position(coordinates))
                }
                markers = markers + listOf(coordinates)
                drawPolyline()
                val markersData = markers.mapIndexed { index, latLng ->
                    "Marker${index + 1}" to hashMapOf(
                        "latitude" to latLng.latitude,
                        "longitude" to latLng.longitude
                    )
                }.toMap()
                databaseRef.setValue(markersData)
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


