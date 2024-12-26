package com.example.ccompepmapper.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.ccompepmapper.ui.theme.CCompEPMapperTheme

@Composable
fun LocationListScreen(
    onNavigateToLocationMap: () -> Unit = {}
) {
    Column {
        Text(text = "Hello Traveler!")
        Button(onClick = {
            onNavigateToLocationMap()
        }) {
            Text(text = "Click Me")
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