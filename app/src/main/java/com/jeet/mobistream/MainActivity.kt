package com.jeet.mobistream

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.jeet.mobistream.ui.components.LiquidNavigationBar
import com.jeet.mobistream.ui.components.NavigationTab
import com.jeet.mobistream.ui.screens.HomeScreen
import com.jeet.mobistream.ui.screens.SavedScreen
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
    var isNavbarVisible by remember { mutableStateOf(true) }

    // Scroll listener: hide navbar when scrolling down, show when scrolling up
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta < -8f && isNavbarVisible) {
                    // Scrolling down (content moves up) -> Hide navbar with smooth slide
                    isNavbarVisible = false
                } else if (delta > 8f && !isNavbarVisible) {
                    // Scrolling up (content moves down) -> Show navbar with spring bounce
                    isNavbarVisible = true
                }
                return Offset.Zero
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .nestedScroll(nestedScrollConnection)
    ) {
        // Main Screen Content with smooth fade transition
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Crossfade(
                targetState = selectedTab,
                animationSpec = tween(durationMillis = 240),
                label = "tabCrossfade"
            ) { tab ->
                when (tab) {
                    NavigationTab.HOME -> HomeScreen()
                    NavigationTab.SEARCH -> SearchScreen()
                    NavigationTab.SAVED -> SavedScreen()
                    NavigationTab.SETTINGS -> SettingsScreen()
                }
            }
        }

        // Floating Apple Liquid Glass Navigation Bar with Spring Slide Animation
        AnimatedVisibility(
            visible = isNavbarVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeIn(animationSpec = tween(180)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeOut(animationSpec = tween(140)),
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
