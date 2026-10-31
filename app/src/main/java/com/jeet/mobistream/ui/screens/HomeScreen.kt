package com.jeet.mobistream.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jeet.mobistream.core.extension.ExtensionItem
import com.jeet.mobistream.core.extension.ExtensionManager
import com.jeet.mobistream.ui.components.LiquidGlassSurface
import com.jeet.mobistream.ui.components.rememberHaptics
import com.jeet.mobistream.ui.theme.AmoledBlack
import com.jeet.mobistream.ui.theme.CrimsonRed
import com.jeet.mobistream.ui.theme.TextPureWhite
import com.jeet.mobistream.ui.theme.TextSoftMuted

@Composable
fun HomeScreen(
    paddingValues: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    val haptics = rememberHaptics()
    val installedProviders = remember { mutableStateListOf<ExtensionItem>() }
    var isChecking by remember { mutableStateOf(true) }

    fun loadActiveProviders() {
        installedProviders.clear()
        installedProviders.addAll(ExtensionManager.getInstalledExtensions(context))
        isChecking = false
    }

    LaunchedEffect(Unit) {
        loadActiveProviders()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Top App Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MOBI",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPureWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "STREAM",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = CrimsonRed,
                        letterSpacing = 1.sp
                    )
                }

                LiquidGlassSurface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    borderWidth = 0.dp,
                    tint = Color(0xFF141414),
                    onClick = {
                        haptics.lightClick()
                        loadActiveProviders()
                        Toast.makeText(context, "Refreshed", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Refresh",
                        tint = TextPureWhite,
                        modifier = Modifier
                            .size(17.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }

        if (installedProviders.isEmpty()) {
            // Minimal Zero-Hardcoded State
            item {
                Spacer(modifier = Modifier.height(10.dp))
                LiquidGlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF141414)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Extension,
                                contentDescription = null,
                                tint = CrimsonRed,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "No Extensions Installed",
                            color = TextPureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "MobiStream is 100% modular with zero bundled media.\nAdd an extension in Settings to stream movies, series, and sports.",
                            color = TextSoftMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF141414))
                                .padding(14.dp)
                        ) {
                            StepInstructionRow(step = "1", text = "Go to Settings tab")
                            Spacer(modifier = Modifier.height(8.dp))
                            StepInstructionRow(step = "2", text = "Select 'Extension' category")
                            Spacer(modifier = Modifier.height(8.dp))
                            StepInstructionRow(step = "3", text = "Tap '+' and enter shortcode 'msr'")
                            Spacer(modifier = Modifier.height(8.dp))
                            StepInstructionRow(step = "4", text = "Install your preferred providers")
                        }
                    }
                }
            }
        } else {
            // Live Providers Dashboard
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INSTALLED PROVIDERS",
                        color = TextSoftMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${installedProviders.size} Active",
                        color = Color(0xFF10B981),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(installedProviders, key = { it.internalName }) { ext ->
                LiquidGlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!ext.iconUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = ext.iconUrl,
                                    contentDescription = ext.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF161616)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ext.name.take(2).uppercase(),
                                        color = CrimsonRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = ext.name,
                                    color = TextPureWhite,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (ext.hasSubProviders) {
                                        "${ext.enabledSubProviders.size} of ${ext.subProviders.size} feeds active"
                                    } else {
                                        "v${ext.version} • Ready"
                                    },
                                    color = TextSoftMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Active",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepInstructionRow(step: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(CrimsonRed),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                color = TextPureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = TextPureWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
