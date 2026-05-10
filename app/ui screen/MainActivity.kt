package com.example.drivingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.drivingapp.navigation.AppNavigation
import com.example.drivingapp.ui.theme.DrivingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrivingAppTheme {
                AppNavigation()
            }
        }
    }
}