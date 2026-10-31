package com.jeet.mobistream.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Dns
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PictureInPicture
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Subtitles
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeet.mobistream.ui.components.LiquidGlassBadge
import com.jeet.mobistream.ui.components.LiquidGlassSurface
import com.jeet.mobistream.ui.components.rememberHaptics
import com.jeet.mobistream.ui.theme.AmoledBlack
import com.jeet.mobistream.ui.theme.CrimsonRed
import com.jeet.mobistream.ui.theme.GlassBorderStroke
import com.jeet.mobistream.ui.theme.GlassSurfaceDark
import com.jeet.mobistream.ui.theme.TextDeepDim
import com.jeet.mobistream.ui.theme.TextPureWhite
import com.jeet.mobistream.ui.theme.TextSoftMuted

enum class SettingsSubPage {
    MAIN,
    GENERAL,
    PLAYER,
    EXTENSION,
    ABOUT
}

data class InstalledRepo(
    val name: String,
    val url: String,
    val providerCount: Int,
    val isVerified: Boolean = true
)

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val haptics = rememberHaptics()
    var currentPage by remember { mutableStateOf(SettingsSubPage.MAIN) }

    // Picker Dialog State
    var activeDialogTitle by remember { mutableStateOf<String?>(null) }
    var activeDialogOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var activeDialogCurrent by remember { mutableStateOf("") }
    var onDialogSelect by remember { mutableStateOf<(String) -> Unit>({}) }

    fun openPicker(title: String, options: List<String>, current: String, onSelect: (String) -> Unit) {
        haptics.lightClick()
        activeDialogTitle = title
        activeDialogOptions = options
        activeDialogCurrent = current
        onDialogSelect = onSelect
    }

    // 1. General Preferences State
    var dohProvider by remember { mutableStateOf("Cloudflare 1.1.1.1 (Fastest)") }
    var streamBufferMode by remember { mutableStateOf("Fast Start (Low Latency)") }
    var cacheSizeMb by remember { mutableStateOf("38.4 MB") }
    var liquidGlassEnabled by remember { mutableStateOf(true) }
    var hapticsEnabled by remember { mutableStateOf(true) }
    var accentTheme by remember { mutableStateOf("Crimson Red") }
    var wifiOnlyDownload by remember { mutableStateOf(true) }

    // 2. Player Preferences State
    var selectedPlayer by remember { mutableStateOf("In-App ExoPlayer (Recommended)") }
    var preferredQuality by remember { mutableStateOf("Auto (Highest Available)") }
    var seekDuration by remember { mutableStateOf("10 seconds") }
    var resizeMode by remember { mutableStateOf("Fit to Screen (16:9)") }
    var swipeGestures by remember { mutableStateOf(true) }
    var autoPlayNext by remember { mutableStateOf(true) }
    var hardwareDecoding by remember { mutableStateOf(true) }
    var pipEnabled by remember { mutableStateOf(true) }
    var subtitlesEnabled by remember { mutableStateOf(true) }
    var subtitleLanguage by remember { mutableStateOf("Hindi (Default)") }
    var subtitleSize by remember { mutableStateOf("Medium (18sp)") }
    var audioLanguage by remember { mutableStateOf("Hindi (Default)") }

    // 3. Extension Preferences State (Empty by default per user request)
    val installedRepositories = remember { mutableStateListOf<InstalledRepo>() }
    var repoInputText by remember { mutableStateOf("") }
    var autoUpdateExtensions by remember { mutableStateOf(true) }

    AnimatedContent(
        targetState = currentPage,
        transitionSpec = {
            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
        },
        label = "settingsSubPageTransition"
    ) { page ->
        when (page) {
            SettingsSubPage.MAIN -> {
                SettingsMainMenu(
                    installedRepoCount = installedRepositories.size,
                    onNavigate = { subPage ->
                        haptics.mediumClick()
                        currentPage = subPage
                    }
                )
            }
            SettingsSubPage.GENERAL -> {
                GeneralSettingsScreen(
                    dohProvider = dohProvider,
                    onDohClick = {
                        openPicker(
                            "DNS over HTTPS (DoH)",
                            listOf(
                                "Cloudflare 1.1.1.1 (Fastest)",
                                "Google DNS 8.8.8.8",
                                "AdGuard DNS (Blocks Ads)",
                                "System Default"
                            ),
                            dohProvider
                        ) { dohProvider = it }
                    },
                    streamBufferMode = streamBufferMode,
                    onBufferClick = {
                        openPicker(
                            "Stream Buffer Mode",
                            listOf(
                                "Fast Start (Low Latency)",
                                "Aggressive Buffer (Prevents Lag on Weak Net)",
                                "Balanced Normal"
                            ),
                            streamBufferMode
                        ) { streamBufferMode = it }
                    },
                    cacheSize = cacheSizeMb,
                    onClearCache = {
                        haptics.heavyClick()
                        cacheSizeMb = "0.0 MB"
                        Toast.makeText(context, "Stream cache successfully cleared!", Toast.LENGTH_SHORT).show()
                    },
                    liquidGlassEnabled = liquidGlassEnabled,
                    onLiquidGlassChange = { liquidGlassEnabled = it },
                    hapticsEnabled = hapticsEnabled,
                    onHapticsChange = { hapticsEnabled = it },
                    accentTheme = accentTheme,
                    onAccentClick = {
                        openPicker(
                            "Accent Color",
                            listOf("Crimson Red", "Neon Cyan", "Emerald Green", "Electric Violet"),
                            accentTheme
                        ) { accentTheme = it }
                    },
                    wifiOnlyDownload = wifiOnlyDownload,
                    onWifiOnlyChange = { wifiOnlyDownload = it },
                    onBack = {
                        haptics.lightClick()
                        currentPage = SettingsSubPage.MAIN
                    }
                )
            }
            SettingsSubPage.PLAYER -> {
                PlayerSettingsScreen(
                    selectedPlayer = selectedPlayer,
                    onPlayerClick = {
                        openPicker(
                            "Default Player",
                            listOf("In-App ExoPlayer (Recommended)", "VLC Player", "MPV Player", "System Default"),
                            selectedPlayer
                        ) { selectedPlayer = it }
                    },
                    preferredQuality = preferredQuality,
                    onQualityClick = {
                        openPicker(
                            "Preferred Quality",
                            listOf("Auto (Highest Available)", "1080p Full HD", "720p HD", "480p Data Saver"),
                            preferredQuality
                        ) { preferredQuality = it }
                    },
                    seekDuration = seekDuration,
                    onSeekClick = {
                        openPicker(
                            "Double Tap to Seek",
                            listOf("5 seconds", "10 seconds", "15 seconds", "30 seconds"),
                            seekDuration
                        ) { seekDuration = it }
                    },
                    resizeMode = resizeMode,
                    onResizeClick = {
                        openPicker(
                            "Video Scaling",
                            listOf("Fit to Screen (16:9)", "Zoom to Fill (20:9)", "Stretch to Fit"),
                            resizeMode
                        ) { resizeMode = it }
                    },
                    swipeGestures = swipeGestures,
                    onSwipeChange = { swipeGestures = it },
                    autoPlayNext = autoPlayNext,
                    onAutoPlayChange = { autoPlayNext = it },
                    hardwareDecoding = hardwareDecoding,
                    onHardwareChange = { hardwareDecoding = it },
                    pipEnabled = pipEnabled,
                    onPipChange = { pipEnabled = it },
                    subtitlesEnabled = subtitlesEnabled,
                    onSubtitlesChange = { subtitlesEnabled = it },
                    subtitleLanguage = subtitleLanguage,
                    onSubLanguageClick = {
                        openPicker(
                            "Subtitle Language",
                            listOf("Hindi (Default)", "English", "Tamil", "Telugu", "Original Audio Track"),
                            subtitleLanguage
                        ) { subtitleLanguage = it }
                    },
                    subtitleSize = subtitleSize,
                    onSubSizeClick = {
                        openPicker(
                            "Subtitle Font Size",
                            listOf("Small (14sp)", "Medium (18sp)", "Large (22sp)", "Extra Large (26sp)"),
                            subtitleSize
                        ) { subtitleSize = it }
                    },
                    audioLanguage = audioLanguage,
                    onAudioClick = {
                        openPicker(
                            "Preferred Audio",
                            listOf("Hindi (Default)", "English", "Original Soundtrack"),
                            audioLanguage
                        ) { audioLanguage = it }
                    },
                    onBack = {
                        haptics.lightClick()
                        currentPage = SettingsSubPage.MAIN
                    }
                )
            }
            SettingsSubPage.EXTENSION -> {
                ExtensionSettingsScreen(
                    onBack = {
                        haptics.lightClick()
                        currentPage = SettingsSubPage.MAIN
                    }
                )
            }
            SettingsSubPage.ABOUT -> {
                AboutSettingsScreen(
                    onBack = {
                        haptics.lightClick()
                        currentPage = SettingsSubPage.MAIN
                    }
                )
            }
        }
    }

    // Modal Choice Picker Dialog
    if (activeDialogTitle != null) {
        AlertDialog(
            onDismissRequest = { activeDialogTitle = null },
            containerColor = AmoledBlack,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.border(1.dp, GlassBorderStroke, RoundedCornerShape(22.dp)),
            title = {
                Text(
                    text = activeDialogTitle ?: "",
                    color = TextPureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    activeDialogOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    haptics.lightClick()
                                    onDialogSelect(option)
                                    activeDialogTitle = null
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option,
                                color = if (option == activeDialogCurrent) CrimsonRed else TextPureWhite,
                                fontWeight = if (option == activeDialogCurrent) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                            if (option == activeDialogCurrent) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = CrimsonRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        haptics.selectionTick()
                        activeDialogTitle = null
                    }
                ) {
                    Text("Close", color = TextSoftMuted, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

// ====================================================================
// MAIN SETTINGS MENU (Only 4 Clean Categories)
// ====================================================================
@Composable
private fun SettingsMainMenu(
    installedRepoCount: Int,
    onNavigate: (SettingsSubPage) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPureWhite
                    )
                    Text(
                        text = "App configuration & preferences",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSoftMuted
                    )
                }
                LiquidGlassBadge("v1.0.0", accentColor = CrimsonRed)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 1. General Category Card
        item {
            CategoryNavigationCard(
                icon = Icons.Rounded.Tune,
                title = "General",
                subtitle = "AMOLED theme, tactile haptics, DoH DNS & cache",
                badgeText = "System",
                onClick = { onNavigate(SettingsSubPage.GENERAL) }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 2. Player Category Card
        item {
            CategoryNavigationCard(
                icon = Icons.Rounded.PlayCircle,
                title = "Player",
                subtitle = "ExoPlayer engine, streaming resolution, seek & subtitles",
                badgeText = "ExoPlayer",
                onClick = { onNavigate(SettingsSubPage.PLAYER) }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 3. Extension Category Card
        item {
            CategoryNavigationCard(
                icon = Icons.Rounded.Extension,
                title = "Extension",
                subtitle = "Repository manager, shortcodes (msr, jeet, rohit)",
                badgeText = if (installedRepoCount > 0) "$installedRepoCount Active" else "0 Repos",
                badgeColor = if (installedRepoCount > 0) Color(0xFF10B981) else TextDeepDim,
                onClick = { onNavigate(SettingsSubPage.EXTENSION) }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 4. About Category Card
        item {
            CategoryNavigationCard(
                icon = Icons.Rounded.Info,
                title = "About",
                subtitle = "Mobistream v1.0.0, release status & GitHub source",
                badgeText = "buildbyrohit",
                onClick = { onNavigate(SettingsSubPage.ABOUT) }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        // App Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Mobistream • 100% AMOLED Liquid Glass",
                    color = TextDeepDim,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "com.jeet.mobistream • By Jeet",
                    color = TextDeepDim.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun CategoryNavigationCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color = CrimsonRed,
    onClick: () -> Unit
) {
    LiquidGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(GlassSurfaceDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = TextPureWhite, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, color = TextPureWhite, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(subtitle, color = TextSoftMuted, fontSize = 11.sp, lineHeight = 15.sp)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LiquidGlassBadge(badgeText, accentColor = badgeColor)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    tint = TextDeepDim,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// ====================================================================
// 1. GENERAL SETTINGS SUB-SCREEN
// ====================================================================
@Composable
private fun GeneralSettingsScreen(
    dohProvider: String,
    onDohClick: () -> Unit,
    streamBufferMode: String,
    onBufferClick: () -> Unit,
    cacheSize: String,
    onClearCache: () -> Unit,
    liquidGlassEnabled: Boolean,
    onLiquidGlassChange: (Boolean) -> Unit,
    hapticsEnabled: Boolean,
    onHapticsChange: (Boolean) -> Unit,
    accentTheme: String,
    onAccentClick: () -> Unit,
    wifiOnlyDownload: Boolean,
    onWifiOnlyChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 20.dp)
    ) {
        item {
            SubPageTopBar(title = "General Settings", onBack = onBack)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            SubSectionHeader("DISPLAY & THEME")
            LiquidGlassSurface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(GlassSurfaceDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Palette, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("Pitch-Black AMOLED", color = TextPureWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("100% #000000 black canvas; zero backlight bleed", color = TextSoftMuted, fontSize = 11.sp)
                            }
                        }
                        LiquidGlassBadge("ACTIVE", accentColor = Color(0xFF10B981))
                    }

                    SettingsSubDivider()

                    SettingsToggleItem(
                        icon = Icons.Rounded.Visibility,
                        title = "Apple Liquid Glass Effect",
                        subtitle = "Frosted acrylic glass with specular rim highlights",
                        checked = liquidGlassEnabled,
                        onCheckedChange = onLiquidGlassChange
                    )

                    SettingsSubDivider()

                    SettingsToggleItem(
                        icon = Icons.Rounded.Vibration,
                        title = "Tactile Haptic Feedback",
                        subtitle = "Real physical vibration on button & tab clicks",
                        checked = hapticsEnabled,
                        onCheckedChange = onHapticsChange
                    )

                    SettingsSubDivider()

                    SettingsPickerItem(
                        icon = Icons.Rounded.Palette,
                        title = "Accent Color",
                        subtitle = "Primary color tint for tabs & badges",
                        currentValue = accentTheme,
                        onClick = onAccentClick
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            SubSectionHeader("NETWORK & BYPASS")
            LiquidGlassSurface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    SettingsPickerItem(
                        icon = Icons.Rounded.Dns,
                        title = "DNS over HTTPS (DoH)",
                        subtitle = "Bypasses Indian ISP throttling for MovieBox & Castle",
                        currentValue = dohProvider,
                        onClick = onDohClick
                    )

                    SettingsSubDivider()

                    SettingsPickerItem(
                        icon = Icons.Rounded.Wifi,
                        title = "Stream Buffer Mode",
                        subtitle = "Fast start vs aggressive pre-buffering",
                        currentValue = streamBufferMode,
                        onClick = onBufferClick
                    )

                    SettingsSubDivider()

                    SettingsToggleItem(
                        icon = Icons.Rounded.Wifi,
                        title = "Download over Wi-Fi Only",
                        subtitle = "Save mobile cellular data usage",
                        checked = wifiOnlyDownload,
                        onCheckedChange = onWifiOnlyChange
                    )

                    SettingsSubDivider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onClearCache)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(GlassSurfaceDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("Clear Stream Cache", color = TextPureWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Reclaim storage from temporary stream segments", color = TextSoftMuted, fontSize = 11.sp)
                            }
                        }
                        LiquidGlassBadge(cacheSize, accentColor = TextDeepDim)
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// ====================================================================
// 2. PLAYER SETTINGS SUB-SCREEN
// ====================================================================
@Composable
private fun PlayerSettingsScreen(
    selectedPlayer: String,
    onPlayerClick: () -> Unit,
    preferredQuality: String,
    onQualityClick: () -> Unit,
    seekDuration: String,
    onSeekClick: () -> Unit,
    resizeMode: String,
    onResizeClick: () -> Unit,
    swipeGestures: Boolean,
    onSwipeChange: (Boolean) -> Unit,
    autoPlayNext: Boolean,
    onAutoPlayChange: (Boolean) -> Unit,
    hardwareDecoding: Boolean,
    onHardwareChange: (Boolean) -> Unit,
    pipEnabled: Boolean,
    onPipChange: (Boolean) -> Unit,
    subtitlesEnabled: Boolean,
    onSubtitlesChange: (Boolean) -> Unit,
    subtitleLanguage: String,
    onSubLanguageClick: () -> Unit,
    subtitleSize: String,
    onSubSizeClick: () -> Unit,
    audioLanguage: String,
    onAudioClick: () -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 20.dp)
    ) {
        item {
            SubPageTopBar(title = "Player Settings", onBack = onBack)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            SubSectionHeader("PLAYBACK ENGINE")
            LiquidGlassSurface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    SettingsPickerItem(
                        icon = Icons.Rounded.PlayCircle,
                        title = "Default Video Engine",
                        subtitle = "Choose in-app player or external app",
                        currentValue = selectedPlayer,
                        onClick = onPlayerClick
                    )

                    SettingsSubDivider()

                    SettingsPickerItem(
                        icon = Icons.Rounded.HighQuality,
                        title = "Target Quality",
                        subtitle = "Preferred stream resolution",
                        currentValue = preferredQuality,
                        onClick = onQualityClick
                    )

                    SettingsSubDivider()

                    SettingsPickerItem(
                        icon = Icons.Rounded.FastForward,
                        title = "Double Tap Seek",
                        subtitle = "Jump duration on double tap",
                        currentValue = seekDuration,
                        onClick = onSeekClick
                    )

                    SettingsSubDivider()

                    SettingsPickerItem(
                        icon = Icons.Rounded.AspectRatio,
                        title = "Aspect Ratio Cropping",
                        subtitle = "Fit to screen or fill punch-hole/notch",
                        currentValue = resizeMode,
                        onClick = onResizeClick
                    )

                    SettingsSubDivider()

                    SettingsToggleItem(
                        icon = Icons.Rounded.TouchApp,
                        title = "Vertical Swipe Gestures",
                        subtitle = "Swipe left for brightness, right for volume",
                        checked = swipeGestures,
                        onCheckedChange = onSwipeChange
                    )

                    SettingsSubDivider()

                    SettingsToggleItem(
                        icon = Icons.Rounded.Speed,
                        title = "Auto-play Next Stream",
                        subtitle = "Seamlessly play next episode on complete",
                        checked = autoPlayNext,
                        onCheckedChange = onAutoPlayChange
                    )

                    SettingsSubDivider()

                    SettingsToggleItem(
                        icon = Icons.Rounded.Memory,
                        title = "Hardware Acceleration",
                        subtitle = "Use GPU decoding for buttery 60fps",
                        checked = hardwareDecoding,
                        onCheckedChange = onHardwareChange
                    )

                    SettingsSubDivider()

                    SettingsToggleItem(
                        icon = Icons.Rounded.PictureInPicture,
                        title = "Picture-in-Picture (PiP)",
                        subtitle = "Minimize to floating window",
                        checked = pipEnabled,
                        onCheckedChange = onPipChange
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            SubSectionHeader("SUBTITLES & AUDIO TRACKS")
            LiquidGlassSurface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    SettingsToggleItem(
                        icon = Icons.Rounded.Subtitles,
                        title = "Enable Subtitles",
                        subtitle = "Show subtitles when available",
                        checked = subtitlesEnabled,
                        onCheckedChange = onSubtitlesChange
                    )

                    AnimatedVisibility(visible = subtitlesEnabled) {
                        Column {
                            SettingsSubDivider()

                            SettingsPickerItem(
                                icon = Icons.Rounded.Language,
                                title = "Subtitle Language",
                                subtitle = "Default subtitle language track",
                                currentValue = subtitleLanguage,
                                onClick = onSubLanguageClick
                            )

                            SettingsSubDivider()

                            SettingsPickerItem(
                                icon = Icons.Rounded.Visibility,
                                title = "Subtitle Size",
                                subtitle = "Text scale inside video player",
                                currentValue = subtitleSize,
                                onClick = onSubSizeClick
                            )
                        }
                    }

                    SettingsSubDivider()

                    SettingsPickerItem(
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        title = "Preferred Audio Track",
                        subtitle = "Default language for multi-audio streams",
                        currentValue = audioLanguage,
                        onClick = onAudioClick
                    )
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// ====================================================================
// 3. EXTENSION SETTINGS SUB-SCREEN (Real Network Fetch & .cs3 Installer)
// ====================================================================
@Composable
private fun ExtensionSettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val haptics = rememberHaptics()
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    var repoInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    var currentRepoMeta by remember { mutableStateOf<com.jeet.mobistream.core.extension.RepositoryMeta?>(null) }
    val extensionList = remember { mutableStateListOf<com.jeet.mobistream.core.extension.ExtensionItem>() }
    var autoUpdate by remember { mutableStateOf(true) }

    // Downloading item trackers
    val downloadingIds = remember { mutableStateListOf<String>() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 20.dp)
    ) {
        item {
            SubPageTopBar(title = "Extension Settings", onBack = onBack)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Add Repository Section
        item {
            SubSectionHeader("ADD REPOSITORY")
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Enter shortcode or repo manifest URL",
                        color = TextPureWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Supported shortcodes: msr, jeet, rohit, mobistream",
                        color = TextSoftMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = repoInput,
                        onValueChange = { repoInput = it },
                        placeholder = { Text("e.g. msr or https://...", color = TextDeepDim, fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonRed,
                            unfocusedBorderColor = GlassBorderStroke,
                            focusedTextColor = TextPureWhite,
                            unfocusedTextColor = TextPureWhite,
                            cursorColor = CrimsonRed
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val input = repoInput.trim()
                            if (input.isEmpty()) {
                                Toast.makeText(context, "Please enter a shortcode or repo URL", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            haptics.mediumClick()
                            isLoading = true
                            statusMessage = "Fetching repository manifest..."

                            scope.launch {
                                val result = com.jeet.mobistream.core.extension.ExtensionManager.fetchRepository(context, input)
                                isLoading = false
                                statusMessage = null

                                result.onSuccess { (meta, plugins) ->
                                    haptics.heavyClick()
                                    currentRepoMeta = meta
                                    extensionList.clear()
                                    extensionList.addAll(plugins)
                                    Toast.makeText(context, "Loaded ${meta.name} (${plugins.size} Providers)", Toast.LENGTH_SHORT).show()
                                }.onFailure { err ->
                                    Toast.makeText(context, "Failed: ${err.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Add, contentDescription = null, tint = TextPureWhite, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isLoading) "Connecting to GitHub..." else "Fetch & Add Repository",
                                color = TextPureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Active Repository Card (if loaded)
        if (currentRepoMeta != null) {
            item {
                SubSectionHeader("ACTIVE REPOSITORY")
                LiquidGlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(currentRepoMeta?.name ?: "", color = TextPureWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                text = "${extensionList.size} Curated Hindi Providers • Manifest v${currentRepoMeta?.manifestVersion}",
                                color = TextSoftMuted,
                                fontSize = 11.sp
                            )
                        }
                        Button(
                            onClick = {
                                haptics.mediumClick()
                                scope.launch {
                                    extensionList.forEach { ext ->
                                        if (!ext.isInstalled) {
                                            downloadingIds.add(ext.internalName)
                                            val dlRes = com.jeet.mobistream.core.extension.ExtensionManager.downloadExtension(context, ext)
                                            downloadingIds.remove(ext.internalName)
                                            if (dlRes.isSuccess) {
                                                val idx = extensionList.indexOf(ext)
                                                if (idx != -1) {
                                                    extensionList[idx] = ext.copy(isInstalled = true, isEnabled = true)
                                                }
                                            }
                                        }
                                    }
                                    Toast.makeText(context, "All providers installed successfully!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text("Install All", color = TextPureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Available Providers List
        item {
            SubSectionHeader("AVAILABLE PROVIDERS (${extensionList.size})")
        }

        if (extensionList.isEmpty()) {
            item {
                LiquidGlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(GlassSurfaceDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Extension, contentDescription = null, tint = TextSoftMuted, modifier = Modifier.size(26.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No Extensions Loaded", color = TextPureWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No providers are added by default.\nType 'msr' above to fetch the curated 7 Hindi providers.",
                            color = TextSoftMuted,
                            fontSize = 11.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        } else {
            items(extensionList, key = { it.internalName }) { ext ->
                val isDownloading = downloadingIds.contains(ext.internalName)

                LiquidGlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Provider icon + details
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GlassSurfaceDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ext.name.take(2).uppercase(),
                                    color = CrimsonRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(ext.name, color = TextPureWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    LiquidGlassBadge(ext.language.uppercase(), accentColor = CrimsonRed)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "v${ext.version} • ${ext.authors.firstOrNull() ?: "buildbyrohit"}",
                                    color = TextSoftMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Right actions (Install button vs Installed toggles)
                        if (!ext.isInstalled) {
                            Button(
                                onClick = {
                                    haptics.lightClick()
                                    downloadingIds.add(ext.internalName)
                                    scope.launch {
                                        val dlRes = com.jeet.mobistream.core.extension.ExtensionManager.downloadExtension(context, ext)
                                        downloadingIds.remove(ext.internalName)
                                        if (dlRes.isSuccess) {
                                            haptics.heavyClick()
                                            val idx = extensionList.indexOf(ext)
                                            if (idx != -1) {
                                                extensionList[idx] = ext.copy(isInstalled = true, isEnabled = true)
                                            }
                                            Toast.makeText(context, "${ext.name} installed!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Install failed: ${dlRes.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                enabled = !isDownloading,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                            ) {
                                Text(
                                    text = if (isDownloading) "Downloading..." else "Install",
                                    color = TextPureWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = ext.isEnabled,
                                    onCheckedChange = { isChecked ->
                                        haptics.lightClick()
                                        com.jeet.mobistream.core.extension.ExtensionManager.setExtensionEnabled(context, ext.internalName, isChecked)
                                        val idx = extensionList.indexOf(ext)
                                        if (idx != -1) {
                                            extensionList[idx] = ext.copy(isEnabled = isChecked)
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = TextPureWhite,
                                        checkedTrackColor = Color(0xFF10B981),
                                        uncheckedThumbColor = TextDeepDim,
                                        uncheckedTrackColor = GlassSurfaceDark
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        haptics.lightClick()
                                        com.jeet.mobistream.core.extension.ExtensionManager.uninstallExtension(context, ext.internalName)
                                        val idx = extensionList.indexOf(ext)
                                        if (idx != -1) {
                                            extensionList[idx] = ext.copy(isInstalled = false, isEnabled = false)
                                        }
                                        Toast.makeText(context, "${ext.name} uninstalled", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Rounded.DeleteOutline, contentDescription = "Uninstall", tint = TextDeepDim, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            item { Spacer(modifier = Modifier.height(10.dp)) }
        }

        // Auto-update Switch
        item {
            SubSectionHeader("AUTOMATION")
            LiquidGlassSurface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                SettingsToggleItem(
                    icon = Icons.Rounded.Refresh,
                    title = "Auto-update Extensions",
                    subtitle = "Silently fetch provider updates in background",
                    checked = autoUpdate,
                    onCheckedChange = { autoUpdate = it }
                )
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// ====================================================================
// 4. ABOUT SETTINGS SUB-SCREEN
// ====================================================================
@Composable
private fun AboutSettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val haptics = rememberHaptics()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 20.dp)
    ) {
        item {
            SubPageTopBar(title = "About Mobistream", onBack = onBack)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // App Identity Hero Card
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(GlassSurfaceDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.PlayCircle, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(42.dp))
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Mobistream", color = TextPureWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("100% AMOLED Liquid Glass Edition", color = TextSoftMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LiquidGlassBadge("v1.0.0 (Release)", accentColor = Color(0xFF10B981))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            SubSectionHeader("APPLICATION DETAILS")
            LiquidGlassSurface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    InfoRow("Package Name", "com.jeet.mobistream")
                    SettingsSubDivider()
                    InfoRow("Architect / Developer", "Jeet (buildbyrohit)")
                    SettingsSubDivider()
                    InfoRow("Theme", "100% Pitch-Black AMOLED (#000000)")
                    SettingsSubDivider()
                    InfoRow("UI Paradigm", "Apple Liquid Glass (Frosted Specular)")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            SubSectionHeader("LINKS & UPDATES")
            LiquidGlassSurface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                haptics.mediumClick()
                                Toast.makeText(context, "Mobistream v1.0.0 is up to date!", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(GlassSurfaceDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Refresh, contentDescription = null, tint = TextPureWhite, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("Check for Updates", color = TextPureWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Sync releases with GitHub repository", color = TextSoftMuted, fontSize = 11.sp)
                            }
                        }
                        LiquidGlassBadge("LATEST", accentColor = Color(0xFF10B981))
                    }

                    SettingsSubDivider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(GlassSurfaceDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Info, contentDescription = null, tint = TextSoftMuted, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("GitHub Repository", color = TextPureWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("buildbyrohit/MobiStream", color = TextSoftMuted, fontSize = 11.sp)
                            }
                        }
                        Text("OPEN", color = CrimsonRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// ====================================================================
// SHARED REUSABLE COMPONENTS
// ====================================================================
@Composable
private fun SubPageTopBar(title: String, onBack: () -> Unit) {
    Spacer(modifier = Modifier.height(20.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(GlassSurfaceDark)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = TextPureWhite,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPureWhite
        )
    }
}

@Composable
private fun SubSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextDeepDim,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GlassSurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = TextPureWhite, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, color = TextPureWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, color = TextSoftMuted, fontSize = 11.sp, lineHeight = 14.sp)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextPureWhite,
                checkedTrackColor = CrimsonRed,
                uncheckedThumbColor = TextDeepDim,
                uncheckedTrackColor = GlassSurfaceDark
            )
        )
    }
}

@Composable
private fun SettingsPickerItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    currentValue: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GlassSurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = TextPureWhite, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, color = TextPureWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, color = TextSoftMuted, fontSize = 11.sp, lineHeight = 14.sp)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        LiquidGlassBadge(
            text = currentValue.take(18) + if (currentValue.length > 18) "…" else "",
            accentColor = CrimsonRed
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSoftMuted, fontSize = 13.sp)
        Text(value, color = TextPureWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SettingsSubDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.6.dp)
            .background(GlassBorderStroke)
    )
}
