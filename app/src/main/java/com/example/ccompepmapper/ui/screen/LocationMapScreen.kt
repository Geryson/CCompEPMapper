package com.example.ccompepmapper.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ccompepmapper.R
import com.utsman.osmandcompose.CameraProperty
import com.utsman.osmandcompose.CameraState
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberOverlayManagerState
import dagger.hilt.android.qualifiers.ActivityContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.GroundOverlay

@Composable
fun LocationMapScreen(
    onNavigateToLocationList: () -> Unit = {},
    locationMapViewModel: LocationMapViewModel = hiltViewModel(),
    @ActivityContext context: Context = LocalContext.current
) {
    Column {
        var solidCameraState by remember {
            mutableStateOf(
                CameraState(
                    CameraProperty(
                        geoPoint = GeoPoint(-6.1753924, 106.8271528),
                        zoom = 18.0
                    )
                )
            )
        }

        val overlay by remember {
            mutableStateOf(GroundOverlay())
        }
        overlay.transparency = 0.5f
        overlay.image = context.getDrawable(R.drawable.parlamentmap)?.toBitmap(100, 100, null)
        overlay.setPosition(GeoPoint(-6.1753924, 106.8271528), GeoPoint(-6.1733904, 106.8251508))

        var mapProperties by remember {
            mutableStateOf(DefaultMapProperties)
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

        OpenStreetMap(
            modifier = Modifier
                .height(500.dp)
                .padding(30.dp),
            cameraState = solidCameraState,
            properties = mapProperties,
            overlayManagerState = overlayManagerState,
            onFirstLoadListener = {
                val copyright = CopyrightOverlay(context)
                overlayManagerState.overlayManager.add(copyright)
                overlayManagerState.overlayManager.add(overlay)

            }
        )
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
            }){
                Text(text = "1f")
            }
            Button(onClick = {
                locationMapViewModel.addMapBase(
                    com.example.ccompepmapper.data.MapBase(
                        mapLayerId = null,
                        name = "EP",
                        latitude = solidCameraState.geoPoint.latitude,
                        longitude = solidCameraState.geoPoint.longitude,
                        zoomLevel = solidCameraState.zoom
                    )
                )
            }){
                Text(text = "Save MapBase")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapDetailsPreview() {
    LocationMapScreen()
}