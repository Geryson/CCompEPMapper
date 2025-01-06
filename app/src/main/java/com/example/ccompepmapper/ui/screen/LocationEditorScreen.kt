package com.example.ccompepmapper.ui.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ccompepmapper.R
import com.example.ccompepmapper.data.MapBase
import com.example.ccompepmapper.data.MapLayer
import com.utsman.osmandcompose.CameraProperty
import com.utsman.osmandcompose.CameraState
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberMarkerState
import com.utsman.osmandcompose.rememberOverlayManagerState
import dagger.hilt.android.qualifiers.ActivityContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.GroundOverlay
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationEditorScreen(
    onNavigateToLocationList: () -> Unit,
    locationEditorViewModel: LocationEditorViewModel = hiltViewModel(),
    @ActivityContext context: Context = LocalContext.current,
    mapBaseId: Int
) {
    val initialGeoPoint by remember { mutableStateOf(GeoPoint(47.0, 19.0)) }
    var initialZoomLevel by remember { mutableFloatStateOf(5f) }

    var layerNWLatitude by remember { mutableDoubleStateOf(0.0) }
    var layerNWLongitude by remember { mutableDoubleStateOf(0.0) }
    var layerSELatitude by remember { mutableDoubleStateOf(0.0) }
    var layerSELongitude by remember { mutableDoubleStateOf(0.0) }

    val mapBase: MapBase? by locationEditorViewModel.getMapBase(mapBaseId)
        .collectAsState(initial = null)
    val mapLayer: MapLayer? by locationEditorViewModel.getMapLayerById(mapBaseId)
        .collectAsState(initial = null)


    var name by remember { mutableStateOf("") }
    var zoomLevel by remember { mutableFloatStateOf(5f) }
    var destinationRadius by remember { mutableFloatStateOf(50f) }
    var createMapLayer by remember { mutableStateOf(false) }

    var isNameError by remember { mutableStateOf(false) }

    val editorCameraState by remember {
        mutableStateOf(
            CameraState(
                CameraProperty(
                    geoPoint = initialGeoPoint,
                    zoom = initialZoomLevel.toDouble()
                )
            )
        )
    }

    var editorMapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }

    val editorOverlay by remember {
        mutableStateOf(GroundOverlay())
    }

    val editorOverlayManagerState = rememberOverlayManagerState()

    SideEffect {
        editorMapProperties = editorMapProperties
            .copy(isTilesScaledToDpi = true)
            .copy(zoomButtonVisibility = ZoomButtonVisibility.NEVER)
    }

//    LaunchedEffect(editorCameraState.zoom) {
//        val zoom = editorCameraState.zoom
//        val geoPoint = editorCameraState.geoPoint
//        editorCameraState = CameraState(
//            CameraProperty(
//                geoPoint = geoPoint,
//                zoom = zoom
//            )
//        )
//    }

//    LaunchedEffect(zoomLevel) {
//        editorCameraState.zoom = zoomLevel.toDouble()
//        editorCameraState = CameraState(
//            CameraProperty(
//                geoPoint = editorCameraState.geoPoint,
//                zoom = zoomLevel.toDouble()
//            )
//        )
//    }

    val editorMarkerState = rememberMarkerState(geoPoint = initialGeoPoint)

    if (mapBase != null) {
        editorCameraState.geoPoint = GeoPoint(mapBase!!.latitude, mapBase!!.longitude)
        editorCameraState.zoom = mapBase!!.zoomLevel

        editorMarkerState.geoPoint = GeoPoint(mapBase!!.latitude, mapBase!!.longitude)
        name = mapBase!!.name
        zoomLevel = mapBase!!.zoomLevel.toFloat()
        destinationRadius = mapBase!!.destinationRadius.toFloat()
        createMapLayer = mapLayer != null
    } else {
        // Display loading indicator or placeholder
    }

    if (mapLayer != null) {
        layerNWLatitude = mapLayer!!.upperLeftLatitude
        layerNWLongitude = mapLayer!!.upperLeftLongitude
        layerSELatitude = mapLayer!!.lowerRightLatitude
        layerSELongitude = mapLayer!!.lowerRightLongitude

        editorOverlay.transparency = 0.9f
        editorOverlay.image = context.getDrawable(R.drawable.circles)?.toBitmap(1000, 1000, null)
        editorOverlay.setPosition(
            GeoPoint(layerNWLatitude, layerNWLongitude),
            GeoPoint(layerSELatitude, layerSELongitude)
        )
        editorOverlayManagerState.overlayManager.add(editorOverlay)
    } else {
        // Display loading indicator or placeholder
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Location Editor") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight(),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
        ) {
            OpenStreetMap(
                modifier = Modifier
                    .height(300.dp),
                cameraState = editorCameraState,
                properties = editorMapProperties,
                overlayManagerState = editorOverlayManagerState,
                onFirstLoadListener = {
                    val copyright = CopyrightOverlay(context)
                    editorOverlayManagerState.overlayManager.add(copyright)
                },
                onMapClick = {
                    editorMarkerState.geoPoint = it
                    editorCameraState.geoPoint = it
                    changeVisibleRadius(
                        editorCameraState,
                        GeoPoint(layerNWLatitude, layerNWLongitude),
                        GeoPoint(layerSELatitude, layerSELongitude),
                        editorOverlay,
                        destinationRadius.toDouble()
                    )
                }
            ) {
                Marker(
                    state = editorMarkerState
                )
            }
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            isNameError = !locationEditorViewModel.isNameValid(name)
                        },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = isNameError
                    )
                    if (isNameError) {
                        Text(
                            text = "Name must be at least 3 characters long",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Slider(
                        value = zoomLevel,
                        onValueChange = {
                            zoomLevel = it
                            initialZoomLevel = it
                            editorCameraState.zoom = zoomLevel.toDouble()
                        },
                        valueRange = 5f..18f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Zoom Level: ${zoomLevel.toInt()}")

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = createMapLayer,
                            onCheckedChange = {
                                createMapLayer = it
                                if (!it) {
                                    destinationRadius = 0.0F
                                    editorOverlayManagerState.overlayManager.remove(editorOverlay)
                                } else {
                                    destinationRadius =
                                        if (mapBase != null) destinationRadius else 50.0F

                                    editorOverlay.transparency = 0.9f
                                    editorOverlay.image = context.getDrawable(R.drawable.circles)
                                        ?.toBitmap(1000, 1000, null)
                                    changeVisibleRadius(
                                        editorCameraState,
                                        GeoPoint(layerNWLatitude, layerNWLongitude),
                                        GeoPoint(layerSELatitude, layerSELongitude),
                                        editorOverlay,
                                        destinationRadius.toDouble()
                                    )
                                    editorOverlayManagerState.overlayManager.add(editorOverlay)
                                }
                            }
                        )
                        Text("Create Map Layer")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Slider(
                        value = destinationRadius,
                        onValueChange = {
                            destinationRadius = it

                            if (destinationRadius > 0) {
                                changeVisibleRadius(
                                    editorCameraState,
                                    GeoPoint(layerNWLatitude, layerNWLongitude),
                                    GeoPoint(layerSELatitude, layerSELongitude),
                                    editorOverlay,
                                    destinationRadius.toDouble()
                                )
                            }
                        },
                        valueRange = 0f..100f,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = createMapLayer
                    )
                    Text("Destination Radius: ${destinationRadius.toInt()}")

                    Button(enabled = !isNameError,
                        onClick = {
                            val newBorderPoints = calculateDestinationPoints(
                                GeoPoint(
                                    editorCameraState.geoPoint.latitude,
                                    editorCameraState.geoPoint.longitude
                                ), destinationRadius.toDouble()
                            )
                            var newMapLayer: MapLayer? = null
                            if (createMapLayer) {
                                if (mapLayer != null && mapLayer!!.mapLayerId != -1) {
                                    newMapLayer = MapLayer(
                                        mapLayerId = mapLayer!!.mapLayerId,
                                        upperLeftLatitude = newBorderPoints.first.latitude,
                                        upperLeftLongitude = newBorderPoints.first.longitude,
                                        lowerRightLatitude = newBorderPoints.second.latitude,
                                        lowerRightLongitude = newBorderPoints.second.longitude
                                    )
                                } else {
                                    newMapLayer = MapLayer(
                                        upperLeftLatitude = newBorderPoints.first.latitude,
                                        upperLeftLongitude = newBorderPoints.first.longitude,
                                        lowerRightLatitude = newBorderPoints.second.latitude,
                                        lowerRightLongitude = newBorderPoints.second.longitude
                                    )
                                }
                            }
                            var updatedMapLayerId = mapLayer?.mapLayerId
                            if (newMapLayer == null && updatedMapLayerId != null) {
                                locationEditorViewModel.deleteMapLayerById(updatedMapLayerId)
                                updatedMapLayerId = null
                            }

                            if (mapBaseId != -1) {
                                locationEditorViewModel.updateMapBaseAndMapLayer(
                                    newMapLayer,
                                    MapBase(
                                        mapBaseId = mapBaseId,
                                        mapLayerId = updatedMapLayerId,
                                        name = name,
                                        latitude = editorCameraState.geoPoint.latitude,
                                        longitude = editorCameraState.geoPoint.longitude,
                                        zoomLevel = zoomLevel.toDouble(),
                                        destinationRadius = destinationRadius.toDouble()
                                    )
                                )
                            } else {
                                locationEditorViewModel.addNewMapBaseAndMapLayer(
                                    newMapLayer,
//                    null,
                                    MapBase(
                                        mapLayerId = updatedMapLayerId,
                                        name = name,
                                        latitude = editorCameraState.geoPoint.latitude,
                                        longitude = editorCameraState.geoPoint.longitude,
                                        zoomLevel = zoomLevel.toDouble(),
                                        destinationRadius = destinationRadius.toDouble()
                                    )
                                )
                            }
                            onNavigateToLocationList()
                        }) {
                        Text(text = "Save map")
                    }
                    Button(onClick = {
                        onNavigateToLocationList()
                    }) {
                        Text("Back")
                    }
                }
            }
        }
    }
}

fun changeVisibleRadius(
    cameraState: CameraState,
    nwPoint: GeoPoint,
    sePoint: GeoPoint,
    overlay: GroundOverlay,
    destinationRadius: Double
) {
    val newBorderPoints = calculateDestinationPoints(
        GeoPoint(
            cameraState.geoPoint.latitude,
            cameraState.geoPoint.longitude
        ), destinationRadius
    )
    nwPoint.latitude = newBorderPoints.first.latitude
    nwPoint.longitude = newBorderPoints.first.longitude
    sePoint.latitude = newBorderPoints.second.latitude
    sePoint.longitude = newBorderPoints.second.longitude
    overlay.setPosition(
        GeoPoint(nwPoint.latitude, nwPoint.longitude),
        GeoPoint(sePoint.latitude, sePoint.longitude)
    )
}

fun calculateDestinationPoints(origin: GeoPoint, distanceKm: Double): Pair<GeoPoint, GeoPoint> {
    val earthRadiusKm = 6371.0 // Earth's radius in kilometers

    // Convert distance to radians
    val distanceRad = (distanceKm * 2) / earthRadiusKm

    // Calculate diagonal distance (radius of circumscribed circle)
    val diagonalDistanceRad = distanceRad / sqrt(2.0)

    // Calculate bearing for NW (315 degrees) and SE (135 degrees)
    val bearingNW = Math.toRadians(315.0)
    val bearingSE = Math.toRadians(135.0)

    // Calculate destination latitudes and longitudes using diagonal distance
    val latNW = asin(
        sin(Math.toRadians(origin.latitude)) * cos(diagonalDistanceRad) +
                cos(Math.toRadians(origin.latitude)) * sin(diagonalDistanceRad) * cos(bearingNW)
    )
    val lonNW = Math.toRadians(origin.longitude) + atan2(
        sin(bearingNW) * sin(diagonalDistanceRad) * cos(Math.toRadians(origin.latitude)),
        cos(diagonalDistanceRad) - sin(Math.toRadians(origin.latitude)) * sin(latNW)
    )

    val latSE = asin(
        sin(Math.toRadians(origin.latitude)) * cos(diagonalDistanceRad) +
                cos(Math.toRadians(origin.latitude)) * sin(diagonalDistanceRad) * cos(bearingSE)
    )
    val lonSE = Math.toRadians(origin.longitude) + atan2(
        sin(bearingSE) * sin(diagonalDistanceRad) * cos(Math.toRadians(origin.latitude)),
        cos(diagonalDistanceRad) - sin(Math.toRadians(origin.latitude)) * sin(latSE)
    )

    // Convert back to degrees
    return Pair(
        GeoPoint(Math.toDegrees(latNW), Math.toDegrees(lonNW)),
        GeoPoint(Math.toDegrees(latSE), Math.toDegrees(lonSE))
    )
}