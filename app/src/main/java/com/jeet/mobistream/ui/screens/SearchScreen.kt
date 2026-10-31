package com.jeet.mobistream.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeet.mobistream.core.extension.ExtensionItem
import com.jeet.mobistream.core.extension.ExtensionManager
import com.jeet.mobistream.ui.components.LiquidGlassBadge
import com.jeet.mobistream.ui.components.LiquidGlassSurface
import com.jeet.mobistream.ui.theme.AmoledBlack
import com.jeet.mobistream.ui.theme.CrimsonRed
import com.jeet.mobistream.ui.theme.GlassSurfaceDark
import com.jeet.mobistream.ui.theme.TextDeepDim
import com.jeet.mobistream.ui.theme.TextPureWhite
import com.jeet.mobistream.ui.theme.TextSoftMuted

@Composable
fun SearchScreen() {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val installedProviders = remember { mutableStateListOf<ExtensionItem>() }

    LaunchedEffect(Unit) {
        installedProviders.clear()
        installedProviders.addAll(ExtensionManager.getInstalledExtensions(context))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Text(
            text = "Discover",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPureWhite
        )
        Text(
            text = if (installedProviders.isNotEmpty()) {
                "Search across ${installedProviders.size} installed provider${if (installedProviders.size > 1) "s" else ""}"
            } else {
                "Modular universal media search"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = TextSoftMuted
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Liquid Glass Search Input
        LiquidGlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            tint = Color(0x28202020)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search titles, live feeds, or matches...", color = TextDeepDim, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = CrimsonRed
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextPureWhite,
                    unfocusedTextColor = TextPureWhite,
                    cursorColor = CrimsonRed
                )
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        if (query.trim().isNotEmpty()) {
            // Active Search Query Display
            Text(
                text = "SEARCH RESULTS",
                color = TextSoftMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Querying providers for \"$query\"...",
                        color = TextPureWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (installedProviders.isNotEmpty()) {
                            "Searching: " + installedProviders.joinToString(", ") { it.name }
                        } else {
                            "No active providers installed. Please add an extension in Settings."
                        },
                        color = TextSoftMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Zero hardcoded state: Show active providers or guidance
            if (installedProviders.isNotEmpty()) {
                Text(
                    text = "ACTIVE SEARCH PROVIDERS (${installedProviders.size})",
                    color = TextSoftMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                installedProviders.forEach { ext ->
                    LiquidGlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        specularAlpha = 0.2f
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GlassSurfaceDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ext.name.take(2).uppercase(),
                                        color = CrimsonRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = ext.name,
                                    color = TextPureWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            LiquidGlassBadge(
                                text = "READY",
                                accentColor = Color(0xFF10B981)
                            )
                        }
                    }
                }
            } else {
                LiquidGlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(GlassSurfaceDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Extension,
                                contentDescription = null,
                                tint = TextSoftMuted,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No Extensions Loaded",
                            color = TextPureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "To search movies, sports, or series, go to Settings -> Extension and add a repository (e.g. type 'msr').",
                            color = TextSoftMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}
