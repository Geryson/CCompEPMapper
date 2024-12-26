package com.example.ccompepmapper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ccompepmapper.ui.screen.LocationListScreen
import com.example.ccompepmapper.ui.screen.LocationMapScreen
import com.example.ccompepmapper.ui.theme.CCompEPMapperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CCompEPMapperTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EPMapperApp()
                }
            }
        }
    }
}

const val LOCATION_LIST_ROUTE = "location_list"
const val LOCATION_MAP_ROUTE = "location_map"

@Composable
fun EPMapperApp(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = LOCATION_LIST_ROUTE) {
        composable(LOCATION_LIST_ROUTE) {
            LocationListScreen(
                onNavigateToLocationMap = { navController.navigate(LOCATION_MAP_ROUTE)}) }
        composable(LOCATION_MAP_ROUTE) {
            LocationMapScreen(
                onNavigateToLocationList = { navController.navigate(LOCATION_LIST_ROUTE)}) }
    }
}