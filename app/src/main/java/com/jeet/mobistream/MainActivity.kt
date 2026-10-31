package com.jeet.mobistream

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jeet.mobistream.ui.components.LiquidNavigationBar
import com.jeet.mobistream.ui.components.NavigationTab
import com.jeet.mobistream.ui.screens.ExtensionsScreen
import com.jeet.mobistream.ui.screens.HomeScreen
import com.jeet.mobistream.ui.screens.SearchScreen
import com.jeet.mobistream.ui.screens.SettingsScreen
import com.jeet.mobistream.ui.theme.AmoledBlack
import com.jeet.mobistream.ui.theme.MobistreamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobistreamTheme {
                MainAppScaffold()
            }
        }
    }
}

@Composable
fun MainAppScaffold() {
    var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        // Main Screen Content with smooth fade transition
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Crossfade(
                targetState = selectedTab,
                animationSpec = tween(durationMillis = 260),
                label = "tabCrossfade"
            ) { tab ->
                when (tab) {
                    NavigationTab.HOME -> HomeScreen()
                    NavigationTab.SEARCH -> SearchScreen()
                    NavigationTab.EXTENSIONS -> ExtensionsScreen()
                    NavigationTab.SETTINGS -> SettingsScreen()
                }
            }
        }

        // Floating Apple Liquid Glass Navigation Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            LiquidNavigationBar(
                currentTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                }
            )
        }
    }
}

