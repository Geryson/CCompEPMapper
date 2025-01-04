package com.example.ccompepmapper.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ccompepmapper.ui.theme.CCompEPMapperTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationListScreen(
    onNavigateToLocationMap: (Int) -> Unit = {},
    locationListViewModel: LocationListViewModel = hiltViewModel()
) {
    val mapsList by locationListViewModel.getMapBases().collectAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Location List") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Handle FAB click */ }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            items(mapsList) { mapBase ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(36.dp),
                    onClick = {
                        onNavigateToLocationMap(mapBase.mapBaseId)
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Name: ${mapBase.name}")
                        Text(text = "Latitude: ${mapBase.latitude}")
                        Text(text = "Longitude: ${mapBase.longitude}")
                        Text(text = "Zoom Level: ${mapBase.zoomLevel}")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CCompEPMapperTheme {
        LocationListScreen()
    }
}