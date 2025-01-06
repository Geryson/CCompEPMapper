package com.example.ccompepmapper.ui.screen

import android.content.Context
import android.icu.text.DecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ccompepmapper.EPMapperTopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationMapScreen(
    onNavigateToLocationList: () -> Unit = {},
    locationMapViewModel: LocationMapViewModel = hiltViewModel(),
    @ActivityContext context: Context = LocalContext.current,
    mapBaseId: Int
) {
    val initialGeoPoint = GeoPoint(47.0, 19.0)

    val mapBase: MapBase? by locationMapViewModel.getMapBase(mapBaseId).collectAsState(initial = null)
    val mapLayer: MapLayer? by locationMapViewModel.getMapLayerById(mapBaseId).collectAsState(initial = null)

    var viewerCameraState by remember {
        mutableStateOf(
            CameraState(
                CameraProperty(
                    geoPoint = initialGeoPoint,
                    zoom = 0.0
                )
            )
        )
    }

    var viewerMapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }

    val viewerOverlay by remember {
        mutableStateOf(GroundOverlay())
    }

    val viewerOverlayManagerState = rememberOverlayManagerState()

    SideEffect {
        viewerMapProperties = viewerMapProperties
            .copy(isTilesScaledToDpi = true)
            .copy(zoomButtonVisibility = ZoomButtonVisibility.NEVER)
    }

    LaunchedEffect(viewerCameraState.zoom) {
        val zoom = viewerCameraState.zoom
        val geoPoint = viewerCameraState.geoPoint
        viewerCameraState = CameraState(
            CameraProperty(
                geoPoint = geoPoint,
                zoom = zoom
            )
        )
    }

    val viewerMarkerState = rememberMarkerState(geoPoint = initialGeoPoint)

    if (mapBase != null) {
        viewerCameraState.geoPoint = GeoPoint(mapBase!!.latitude, mapBase!!.longitude)
        viewerCameraState.zoom = mapBase!!.zoomLevel

        viewerMarkerState.geoPoint = GeoPoint(mapBase!!.latitude, mapBase!!.longitude)
    } else {
        // Display loading indicator or placeholder
    }

    if (mapLayer != null) {
        viewerOverlay.transparency = 0.9f
        viewerOverlay.image = context.getDrawable(R.drawable.circles)?.toBitmap(1000, 1000, null)
        viewerOverlay.setPosition(
            GeoPoint(mapLayer!!.upperLeftLatitude, mapLayer!!.upperLeftLongitude),
            GeoPoint(mapLayer!!.lowerRightLatitude, mapLayer!!.lowerRightLongitude))

        viewerOverlayManagerState.overlayManager.add(viewerOverlay)
    } else {
        // Display loading indicator or placeholder
    }

    Scaffold(
        topBar = {
            EPMapperTopAppBar("Location Map")
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxHeight(),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly) {
            OpenStreetMap(
                modifier = Modifier
                    .height(300.dp),
                cameraState = viewerCameraState,
                properties = viewerMapProperties,
                overlayManagerState = viewerOverlayManagerState,
                onFirstLoadListener = {
                    val copyright = CopyrightOverlay(context)
                    viewerOverlayManagerState.overlayManager.add(copyright)
                }
            ) {
                Marker(
                    state = viewerMarkerState
                )
            }

            Row(modifier = Modifier.background(Color.White).fillMaxHeight()
                .fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    if (mapBase != null) {
                        LabelText(text = "Map name")
                        ValueText(text = mapBase!!.name)
                        LabelText(text = "Radius on map")
                        if (mapBase!!.destinationRadius == 0.0) {
                            ValueText(text = "Not set")
                        } else {
                            val destinationValue = mapBase!!.destinationRadius
                            val decimalFormat = DecimalFormat("#.##")
                            val formattedValue = decimalFormat.format(destinationValue)
                            ValueText(text = "$formattedValue km")
                        }
                    }
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
                        Button(onClick = {
                            onNavigateToLocationList()
                        }) {
                            Text(text = "Back")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        fontSize = 30.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun ValueText(text: String) {
    Text(
        text = text,
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF557A00)
    )
}

//@Preview(showBackground = true)
//@Composable
//fun MapDetailsPreview() {
//    LocationMapScreen()
//}