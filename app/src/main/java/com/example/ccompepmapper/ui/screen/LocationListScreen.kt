package com.example.ccompepmapper.ui.screen

import android.icu.text.DecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ccompepmapper.ui.theme.CCompEPMapperTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationListScreen(
    onNavigateToLocationMap: (Int) -> Unit = {},
    onNavigateToLocationEditor: (Int) -> Unit = {},
    locationListViewModel: LocationListViewModel = hiltViewModel()
) {
    val mapsList by locationListViewModel.getMapBases().collectAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Location List") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onNavigateToLocationEditor(-1)
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (mapsList.isEmpty()) {
                Text(text = "No maps created...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center)
            } else {
                LazyColumn {
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.padding(32.dp)) {
                                    Text(text = mapBase.name,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 40.sp,
                                        color = Color(0xFF557A00)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val destinationValue = mapBase.destinationRadius
                                        val decimalFormat = DecimalFormat("#.##")
                                        val formattedValue = decimalFormat.format(destinationValue)
                                        Box(
                                            modifier = Modifier
                                                .size(AssistChipDefaults.IconSize * 2)
                                                .background(
                                                    Color(0xFFF44336),
                                                    shape = CircleShape
                                                ) // Holo red light
                                                .border(
                                                    10.dp,
                                                    Color(0xFFB71C1C),
                                                    shape = CircleShape
                                                ) // Holo red dark
                                        )
                                        Text(text = "$formattedValue km",
                                            modifier = Modifier.padding(start = 8.dp),
                                            fontSize = 20.sp)
                                    }
                                }
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Button(
                                        onClick = {
                                            onNavigateToLocationEditor(mapBase.mapBaseId)
                                        },
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Text(text = "Edit")
                                    }
                                    Button(
                                        onClick = {
                                            locationListViewModel.deleteMapBase(mapBase)
                                        },
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Text(text = "Delete")
                                    }
                                }
                            }
                        }
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