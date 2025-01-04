package com.example.ccompepmapper.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ccompepmapper.R
import com.example.ccompepmapper.data.MapBase
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

@Composable
fun LocationMapScreen(
    onNavigateToLocationList: () -> Unit = {},
    locationMapViewModel: LocationMapViewModel = hiltViewModel(),
    @ActivityContext context: Context = LocalContext.current,
    mapBaseId: Int
) {
    val initialGeoPoint = GeoPoint(47.0, 19.0)

    val mapBase: MapBase? by locationMapViewModel.getMapBase(mapBaseId).collectAsState(initial = null)

    var solidCameraState by remember {
        mutableStateOf(
            CameraState(
                CameraProperty(
                    geoPoint = initialGeoPoint,
                    zoom = 0.0
                )
            )
        )
    }

    var mapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }

    val overlay by remember {
        mutableStateOf(GroundOverlay())
    }

    val overlayManagerState = rememberOverlayManagerState()

    SideEffect {
        mapProperties = mapProperties
            .copy(isTilesScaledToDpi = true)
            .copy(isEnableRotationGesture = true)
            .copy(zoomButtonVisibility = ZoomButtonVisibility.SHOW_AND_FADEOUT)
    }

    LaunchedEffect(solidCameraState.zoom) {
        val zoom = solidCameraState.zoom
        val geoPoint = solidCameraState.geoPoint
        solidCameraState = CameraState(
            CameraProperty(
                geoPoint = geoPoint,
                zoom = zoom
            )
        )
    }

    val markerState = rememberMarkerState(geoPoint = initialGeoPoint)

    if (mapBase != null) {
        solidCameraState.geoPoint = GeoPoint(mapBase!!.latitude, mapBase!!.longitude)
        solidCameraState.zoom = mapBase!!.zoomLevel

        markerState.geoPoint = GeoPoint(mapBase!!.latitude, mapBase!!.longitude)

        overlay.transparency = 0.9f
        overlay.image = context.getDrawable(R.drawable.circles)?.toBitmap(1000, 1000, null)
        val borderPoints = calculateDestinationPoints(GeoPoint(mapBase!!.latitude, mapBase!!.longitude), 50.0)
        overlay.setPosition(
            GeoPoint(borderPoints.first.latitude, borderPoints.first.longitude),
            GeoPoint(borderPoints.second.latitude, borderPoints.second.longitude))

        overlayManagerState.overlayManager.add(overlay)
    } else {
        // Display loading indicator or placeholder
    }

    Column {

        OpenStreetMap(
            modifier = Modifier
                .height(500.dp),
            cameraState = solidCameraState,
            properties = mapProperties,
            overlayManagerState = overlayManagerState,
            onFirstLoadListener = {
                val copyright = CopyrightOverlay(context)
                overlayManagerState.overlayManager.add(copyright)
            }
        ) {
            Marker(
                state = markerState
            )
        }

        Text(text = "Go back, Traveler!")
        Row {
            Button(onClick = {
                onNavigateToLocationList()
            }) {
                Text(text = "Back")
            }

            Button(onClick = {
                solidCameraState.geoPoint = GeoPoint(
                    47.5061, 19.1758
                )
            }
            ) {
                Text(text = "Go to EP")
            }
            Button(onClick = {
                overlay.transparency = 0f
            }) {
                Text(text = "1f")
            }
            Button(onClick = {
                locationMapViewModel.addMapBase(
                    MapBase(
                        mapLayerId = null,
                        name = "EP",
                        latitude = solidCameraState.geoPoint.latitude,
                        longitude = solidCameraState.geoPoint.longitude,
                        zoomLevel = solidCameraState.zoom
                    )
                )
            }) {
                Text(text = "Save MapBase")
            }
        }
    }
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

//@Preview(showBackground = true)
//@Composable
//fun MapDetailsPreview() {
//    LocationMapScreen()
//}