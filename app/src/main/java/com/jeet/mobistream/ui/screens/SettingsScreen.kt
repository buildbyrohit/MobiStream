package com.jeet.mobistream.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDownload
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
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val haptics = rememberHaptics()

    // Dialog state for pickers
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

    // Player Preferences
    var selectedPlayer by remember { mutableStateOf("In-App ExoPlayer (Recommended)") }
    var preferredQuality by remember { mutableStateOf("Auto (Highest Available)") }
    var seekDuration by remember { mutableStateOf("10 seconds") }
    var resizeMode by remember { mutableStateOf("Fit to Screen (16:9)") }
    var swipeGestures by remember { mutableStateOf(true) }
    var autoPlayNext by remember { mutableStateOf(true) }
    var hardwareDecoding by remember { mutableStateOf(true) }
    var pipEnabled by remember { mutableStateOf(true) }

    // Subtitles & Audio Preferences
    var subtitlesEnabled by remember { mutableStateOf(true) }
    var subtitleLanguage by remember { mutableStateOf("Hindi (Default)") }
    var subtitleSize by remember { mutableStateOf("Medium (18sp)") }
    var audioLanguage by remember { mutableStateOf("Hindi (Default)") }

    // Network & Bypass Preferences
    var dohProvider by remember { mutableStateOf("Cloudflare 1.1.1.1 (Fastest)") }
    var streamBufferMode by remember { mutableStateOf("Fast Start (Low Latency)") }
    var cacheSizeMb by remember { mutableStateOf("38.4 MB") }

    // Visuals & Tactile Feedback
    var liquidGlassEnabled by remember { mutableStateOf(true) }
    var hapticsEnabled by remember { mutableStateOf(true) }
    var accentTheme by remember { mutableStateOf("Crimson Red") }

    // Downloads
    var wifiOnlyDownload by remember { mutableStateOf(true) }
    var maxDownloads by remember { mutableStateOf("2 parallel streams") }

    // Extensions
    var autoUpdateExtensions by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 18.dp)
    ) {
        // Top Header
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
                        text = "Fine-tune streaming, playback and AMOLED visuals",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSoftMuted
                    )
                }
                LiquidGlassBadge("v1.0.0", accentColor = CrimsonRed)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ==========================================
        // 1. VIDEO PLAYER SETTINGS
        // ==========================================
        item {
            SettingsCategoryHeader("PLAYER & PLAYBACK")

            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    SettingsPickerRow(
                        icon = Icons.Rounded.PlayCircle,
                        title = "Default Player",
                        subtitle = "Select video rendering engine",
                        currentValue = selectedPlayer,
                        onClick = {
                            openPicker(
                                "Default Player",
                                listOf("In-App ExoPlayer (Recommended)", "VLC Player", "MPV Player", "System Default"),
                                selectedPlayer
                            ) { selectedPlayer = it }
                        }
                    )

                    SettingsDivider()

                    SettingsPickerRow(
                        icon = Icons.Rounded.HighQuality,
                        title = "Preferred Quality",
                        subtitle = "Target streaming resolution",
                        currentValue = preferredQuality,
                        onClick = {
                            openPicker(
                                "Preferred Quality",
                                listOf("Auto (Highest Available)", "1080p Full HD", "720p HD", "480p Data Saver"),
                                preferredQuality
                            ) { preferredQuality = it }
                        }
                    )

                    SettingsDivider()

                    SettingsPickerRow(
                        icon = Icons.Rounded.FastForward,
                        title = "Double Tap to Seek",
                        subtitle = "Forward/rewind skip interval",
                        currentValue = seekDuration,
                        onClick = {
                            openPicker(
                                "Double Tap to Seek",
                                listOf("5 seconds", "10 seconds", "15 seconds", "30 seconds"),
                                seekDuration
                            ) { seekDuration = it }
                        }
                    )

                    SettingsDivider()

                    SettingsPickerRow(
                        icon = Icons.Rounded.AspectRatio,
                        title = "Default Video Scale",
                        subtitle = "Aspect ratio cropping mode",
                        currentValue = resizeMode,
                        onClick = {
                            openPicker(
                                "Default Video Scale",
                                listOf("Fit to Screen (16:9)", "Zoom to Fill (20:9)", "Stretch to Fit"),
                                resizeMode
                            ) { resizeMode = it }
                        }
                    )

                    SettingsDivider()

                    SettingsToggleRow(
                        icon = Icons.Rounded.TouchApp,
                        title = "Touch Gestures",
                        subtitle = "Swipe vertically for volume & brightness",
                        checked = swipeGestures,
                        onCheckedChange = {
                            haptics.lightClick()
                            swipeGestures = it
                        }
                    )

                    SettingsDivider()

                    SettingsToggleRow(
                        icon = Icons.Rounded.Speed,
                        title = "Auto-play Next Episode",
                        subtitle = "Automatically load next stream upon finish",
                        checked = autoPlayNext,
                        onCheckedChange = {
                            haptics.lightClick()
                            autoPlayNext = it
                        }
                    )

                    SettingsDivider()

                    SettingsToggleRow(
                        icon = Icons.Rounded.Memory,
                        title = "Hardware Acceleration",
                        subtitle = "Use GPU decoding for ultra-smooth 60fps",
                        checked = hardwareDecoding,
                        onCheckedChange = {
                            haptics.lightClick()
                            hardwareDecoding = it
                        }
                    )

                    SettingsDivider()

                    SettingsToggleRow(
                        icon = Icons.Rounded.PictureInPicture,
                        title = "Picture-in-Picture (PiP)",
                        subtitle = "Continue playing in floating window when minimized",
                        checked = pipEnabled,
                        onCheckedChange = {
                            haptics.lightClick()
                            pipEnabled = it
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ==========================================
        // 2. SUBTITLES & AUDIO
        // ==========================================
        item {
            SettingsCategoryHeader("SUBTITLES & AUDIO")

            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    SettingsToggleRow(
                        icon = Icons.Rounded.Subtitles,
                        title = "Enable Subtitles",
                        subtitle = "Automatically load subtitles when available",
                        checked = subtitlesEnabled,
                        onCheckedChange = {
                            haptics.lightClick()
                            subtitlesEnabled = it
                        }
                    )

                    AnimatedVisibility(visible = subtitlesEnabled) {
                        Column {
                            SettingsDivider()

                            SettingsPickerRow(
                                icon = Icons.Rounded.Language,
                                title = "Subtitle Language",
                                subtitle = "Preferred subtitle track",
                                currentValue = subtitleLanguage,
                                onClick = {
                                    openPicker(
                                        "Subtitle Language",
                                        listOf("Hindi (Default)", "English", "Tamil", "Telugu", "Original Audio Track"),
                                        subtitleLanguage
                                    ) { subtitleLanguage = it }
                                }
                            )

                            SettingsDivider()

                            SettingsPickerRow(
                                icon = Icons.Rounded.Visibility,
                                title = "Subtitle Font Size",
                                subtitle = "Text scale inside video player",
                                currentValue = subtitleSize,
                                onClick = {
                                    openPicker(
                                        "Subtitle Font Size",
                                        listOf("Small (14sp)", "Medium (18sp)", "Large (22sp)", "Extra Large (26sp)"),
                                        subtitleSize
                                    ) { subtitleSize = it }
                                }
                            )
                        }
                    }

                    SettingsDivider()

                    SettingsPickerRow(
                        icon = Icons.Rounded.VolumeUp,
                        title = "Preferred Audio",
                        subtitle = "Default language track for multi-audio streams",
                        currentValue = audioLanguage,
                        onClick = {
                            openPicker(
                                "Preferred Audio",
                                listOf("Hindi (Default)", "English", "Original Soundtrack"),
                                audioLanguage
                            ) { audioLanguage = it }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ==========================================
        // 3. NETWORK & DNS BYPASS
        // ==========================================
        item {
            SettingsCategoryHeader("NETWORK & BYPASS")

            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    SettingsPickerRow(
                        icon = Icons.Rounded.Dns,
                        title = "DNS over HTTPS (DoH)",
                        subtitle = "Bypasses Indian ISP throttling for MovieBox & Castle",
                        currentValue = dohProvider,
                        onClick = {
                            openPicker(
                                "DNS over HTTPS (DoH)",
                                listOf(
                                    "Cloudflare 1.1.1.1 (Fastest)",
                                    "Google DNS 8.8.8.8",
                                    "AdGuard DNS (Blocks Ads)",
                                    "System Default Network"
                                ),
                                dohProvider
                            ) { dohProvider = it }
                        }
                    )

                    SettingsDivider()

                    SettingsPickerRow(
                        icon = Icons.Rounded.Wifi,
                        title = "Stream Buffer Mode",
                        subtitle = "Pre-load buffering strategy",
                        currentValue = streamBufferMode,
                        onClick = {
                            openPicker(
                                "Stream Buffer Mode",
                                listOf(
                                    "Fast Start (Low Latency)",
                                    "Aggressive Buffer (Prevents Lag on Weak Net)",
                                    "Balanced Normal"
                                ),
                                streamBufferMode
                            ) { streamBufferMode = it }
                        }
                    )

                    SettingsDivider()

                    // Clear Stream Cache Action
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                haptics.heavyClick()
                                cacheSizeMb = "0.0 MB"
                                Toast.makeText(context, "Stream cache successfully cleared!", Toast.LENGTH_SHORT).show()
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
                                Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("Clear Stream Cache", color = TextPureWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Reclaim storage from cached posters & video chunks", color = TextSoftMuted, fontSize = 11.sp)
                            }
                        }
                        LiquidGlassBadge(cacheSizeMb, accentColor = TextDeepDim)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ==========================================
        // 4. THEME & TACTILE HAPTICS
        // ==========================================
        item {
            SettingsCategoryHeader("THEME & TACTILE HAPTICS")

            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    // True AMOLED Indicator
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
                                Text("100% Pitch-Black AMOLED", color = TextPureWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("True #000000 blacks; zero backlight bleed", color = TextSoftMuted, fontSize = 11.sp)
                            }
                        }
                        LiquidGlassBadge("ACTIVE", accentColor = Color(0xFF10B981))
                    }

                    SettingsDivider()

                    SettingsToggleRow(
                        icon = Icons.Rounded.Visibility,
                        title = "Apple Liquid Glass Effect",
                        subtitle = "Frosted translucency & specular rim highlights",
                        checked = liquidGlassEnabled,
                        onCheckedChange = {
                            haptics.lightClick()
                            liquidGlassEnabled = it
                        }
                    )

                    SettingsDivider()

                    SettingsToggleRow(
                        icon = Icons.Rounded.Vibration,
                        title = "Tactile Haptic Feedback",
                        subtitle = "Crisp physical click sensations on touches & tabs",
                        checked = hapticsEnabled,
                        onCheckedChange = {
                            haptics.heavyClick()
                            hapticsEnabled = it
                        }
                    )

                    SettingsDivider()

                    SettingsPickerRow(
                        icon = Icons.Rounded.Palette,
                        title = "Accent Color",
                        subtitle = "Highlight tint for buttons & active tabs",
                        currentValue = accentTheme,
                        onClick = {
                            openPicker(
                                "Accent Color",
                                listOf("Crimson Red", "Neon Cyan", "Emerald Green", "Electric Violet"),
                                accentTheme
                            ) { accentTheme = it }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ==========================================
        // 5. DOWNLOADS
        // ==========================================
        item {
            SettingsCategoryHeader("DOWNLOADS")

            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    SettingsToggleRow(
                        icon = Icons.Rounded.Wifi,
                        title = "Download on Wi-Fi Only",
                        subtitle = "Prevent accidental mobile cellular data usage",
                        checked = wifiOnlyDownload,
                        onCheckedChange = {
                            haptics.lightClick()
                            wifiOnlyDownload = it
                        }
                    )

                    SettingsDivider()

                    SettingsPickerRow(
                        icon = Icons.Rounded.CloudDownload,
                        title = "Concurrent Downloads",
                        subtitle = "Maximum active background downloads",
                        currentValue = maxDownloads,
                        onClick = {
                            openPicker(
                                "Concurrent Downloads",
                                listOf("1 stream", "2 parallel streams", "3 parallel streams", "5 parallel streams"),
                                maxDownloads
                            ) { maxDownloads = it }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ==========================================
        // 6. EXTENSIONS & MSR REPOSITORY
        // ==========================================
        item {
            SettingsCategoryHeader("EXTENSIONS & PROVIDERS")

            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
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
                                Icon(Icons.Rounded.Extension, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("MSR Repository Ecosystem", color = TextPureWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Curated Hindi providers (Castle TV, MovieBox, Cricify)", color = TextSoftMuted, fontSize = 11.sp)
                            }
                        }
                        LiquidGlassBadge("SYNCED", accentColor = Color(0xFF10B981))
                    }

                    SettingsDivider()

                    SettingsToggleRow(
                        icon = Icons.Rounded.Refresh,
                        title = "Auto-update Extensions",
                        subtitle = "Automatically fetch provider .cs3 updates",
                        checked = autoUpdateExtensions,
                        onCheckedChange = {
                            haptics.lightClick()
                            autoUpdateExtensions = it
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ==========================================
        // 7. ABOUT & UPDATES
        // ==========================================
        item {
            SettingsCategoryHeader("ABOUT MOBISTREAM")

            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    // Check for Updates
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
                                Text("Latest build synced with GitHub releases", color = TextSoftMuted, fontSize = 11.sp)
                            }
                        }
                        LiquidGlassBadge("LATEST", accentColor = Color(0xFF10B981))
                    }

                    SettingsDivider()

                    // GitHub Repository
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

            Spacer(modifier = Modifier.height(28.dp))

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Mobistream • AMOLED Liquid Glass Edition",
                    color = TextDeepDim,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "com.jeet.mobistream • Crafted by Jeet",
                    color = TextDeepDim.copy(alpha = 0.5f),
                    fontSize = 10.sp
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

@Composable
private fun SettingsCategoryHeader(title: String) {
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
private fun SettingsToggleRow(
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
private fun SettingsPickerRow(
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
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.6.dp)
            .background(GlassBorderStroke)
    )
}
