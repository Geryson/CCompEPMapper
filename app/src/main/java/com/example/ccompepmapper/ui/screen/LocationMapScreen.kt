package com.example.ccompepmapper.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LocationMapScreen(
    onNavigateToLocationList: () -> Unit = {}
) {
    Column {
        Text(text = "Go back, Traveler!")
        Button(onClick = {
            onNavigateToLocationList()
        }) {
            Text(text = "Back")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapDetailsPreview() {
    LocationMapScreen()
}