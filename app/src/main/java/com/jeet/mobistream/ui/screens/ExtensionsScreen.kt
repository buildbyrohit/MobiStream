package com.jeet.mobistream.ui.screens

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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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

data class ProviderState(
    val name: String,
    val type: String,
    val desc: String,
    var isEnabled: Boolean = true
)

@Composable
fun ExtensionsScreen() {
    val haptics = rememberHaptics()
    var shortcodeText by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("MSR Active (buildbyrohit/msr)") }

    val providers = remember {
        mutableStateListOf(
            ProviderState("Castle TV", "Cinema & Series", "Multi-Audio (Hindi, English, South) with fast HLS"),
            ProviderState("MSR Cinema", "OTT Mirror", "Netflix, Prime Video, Disney+ Hotstar in HD"),
            ProviderState("MSR Mobile", "Fast Mobile", "Ultra-low buffering mobile NetMirror streams"),
            ProviderState("Cricify", "Live Cricket", "Live Cricket (IPL/ICC) with Hindi/English Audio"),
            ProviderState("MovieBox Global", "Global Cinema", "Hollywood and international cinema catalog"),
            ProviderState("MovieBox India", "Bollywood", "Bollywood, Hindi Dubbed and South Indian Hits"),
            ProviderState("SK Tech", "Live Sports", "Live sports events and 24/7 TV channels")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 120.dp)
    ) {
        item {
            Text(
                text = "Extensions Manager",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPureWhite
            )
            Text(
                text = "Manage high-performance streaming scrapers and repositories",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSoftMuted
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Active Repository Liquid Glass Card
        item {
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                specularAlpha = 0.3f
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(CrimsonRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudDone,
                            contentDescription = "Active",
                            tint = CrimsonRed,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "MSR (MobiStream Repo)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPureWhite
                        )
                        Text(
                            text = statusMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSoftMuted
                        )
                    }

                    LiquidGlassBadge(text = "7 READY", accentColor = Color(0xFF10B981))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Quick Shortcode Add Box
        item {
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                tint = Color(0x20202020)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Add Repository via Shortcode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPureWhite
                    )
                    Text(
                        text = "Type msr, jeet, or rohit to link extension catalog",
                        fontSize = 11.sp,
                        color = TextDeepDim
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = shortcodeText,
                            onValueChange = { shortcodeText = it },
                            placeholder = { Text("Enter shortcode (e.g. msr, jeet)", color = TextDeepDim, fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CrimsonRed,
                                unfocusedBorderColor = GlassBorderStroke,
                                focusedTextColor = TextPureWhite,
                                unfocusedTextColor = TextPureWhite,
                                cursorColor = CrimsonRed
                            )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = {
                                val code = shortcodeText.trim().lowercase()
                                haptics.heavyClick()
                                if (code in setOf("msr", "jeet", "rohit", "mobistream")) {
                                    statusMessage = "Loaded via '$code' • 7 Providers Active"
                                    shortcodeText = ""
                                } else {
                                    statusMessage = "Repository link updated!"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CrimsonRed,
                                contentColor = TextPureWhite
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.height(50.dp)
                        ) {
                            Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Installed Providers (${providers.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPureWhite,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Providers List
        items(providers) { provider ->
            LiquidGlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                shape = RoundedCornerShape(18.dp),
                specularAlpha = 0.22f
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x20FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Extension,
                            contentDescription = null,
                            tint = if (provider.isEnabled) CrimsonRed else TextDeepDim,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = provider.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPureWhite
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            LiquidGlassBadge(text = "HINDI", accentColor = Color.White)
                        }
                        Text(
                            text = provider.desc,
                            fontSize = 11.sp,
                            color = TextSoftMuted,
                            maxLines = 1
                        )
                    }

                    Switch(
                        checked = provider.isEnabled,
                        onCheckedChange = {
                            haptics.lightClick()
                            provider.isEnabled = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextPureWhite,
                            checkedTrackColor = CrimsonRed,
                            uncheckedThumbColor = TextDeepDim,
                            uncheckedTrackColor = GlassSurfaceDark
                        )
                    )
                }
            }
        }
    }
}

