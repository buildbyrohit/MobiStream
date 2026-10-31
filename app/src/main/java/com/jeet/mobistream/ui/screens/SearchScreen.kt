package com.jeet.mobistream.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeet.mobistream.core.extension.ExtensionItem
import com.jeet.mobistream.core.extension.ExtensionManager
import com.jeet.mobistream.core.extension.MediaFeedItem
import com.jeet.mobistream.ui.components.LiquidGlassBadge
import com.jeet.mobistream.ui.components.LiquidGlassSurface
import com.jeet.mobistream.ui.components.rememberHaptics
import com.jeet.mobistream.ui.theme.AmoledBlack
import com.jeet.mobistream.ui.theme.CrimsonRed
import com.jeet.mobistream.ui.theme.TextDeepDim
import com.jeet.mobistream.ui.theme.TextPureWhite
import com.jeet.mobistream.ui.theme.TextSoftMuted

@Composable
fun SearchScreen() {
    val context = LocalContext.current
    val haptics = rememberHaptics()

    var query by remember { mutableStateOf("") }
    val installedProviders = remember { mutableStateListOf<ExtensionItem>() }
    var selectedProviderFilter by remember { mutableStateOf("All") }
    var activePlaybackItem by remember { mutableStateOf<MediaFeedItem?>(null) }

    LaunchedEffect(Unit) {
        installedProviders.clear()
        installedProviders.addAll(ExtensionManager.getInstalledExtensions(context))
    }

    // Dynamic search across all active extension feeds
    val searchResults = remember(query, selectedProviderFilter, installedProviders.size) {
        ExtensionManager.searchMedia(context, query, selectedProviderFilter)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        // Search Input Top Section
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
            Text(
                text = "Discover",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPureWhite
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Minimalist Outlined Search Field
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search sports, movies, channels...", color = TextDeepDim, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = if (query.isNotEmpty()) CrimsonRed else TextSoftMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = TextSoftMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CrimsonRed,
                    unfocusedBorderColor = Color(0xFF222222),
                    focusedContainerColor = Color(0xFF101010),
                    unfocusedContainerColor = Color(0xFF101010),
                    focusedTextColor = TextPureWhite,
                    unfocusedTextColor = TextPureWhite,
                    cursorColor = CrimsonRed
                )
            )
        }

        // Provider Filter Chips (CloudStream Multi-Provider Toggle)
        if (installedProviders.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("All") + installedProviders.map { it.name }
                items(filters) { filterName ->
                    val isSelected = selectedProviderFilter == filterName
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CrimsonRed else Color(0xFF141414))
                            .clickable {
                                haptics.selectionTick()
                                selectedProviderFilter = filterName
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filterName,
                            color = if (isSelected) TextPureWhite else TextSoftMuted,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Quick Category Suggestions Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val suggestions = listOf("Cricket Live", "Live Sports", "Movies", "Reality Shows", "Regional", "HD Feeds")
            items(suggestions) { tag ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF161616))
                        .clickable {
                            haptics.selectionTick()
                            query = if (tag == "Cricket Live") "cric" else tag
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(text = tag, color = TextSoftMuted, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content Area: Search Results or Category Results
        if (installedProviders.isEmpty()) {
            // No extensions empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF141414)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Extension, contentDescription = null, tint = TextSoftMuted, modifier = Modifier.size(26.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("No Extensions Loaded", color = TextPureWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "To search channels and movies, add an extension repository in Settings -> Extension.",
                    color = TextSoftMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else if (searchResults.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Rounded.Search, contentDescription = null, tint = TextDeepDim, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("No results for \"$query\"", color = TextPureWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Try searching with different keywords or switch provider filter above.", color = TextSoftMuted, fontSize = 11.sp, textAlign = TextAlign.Center)
            }
        } else {
            // Results Grid in 2 columns
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                item {
                    Text(
                        text = if (query.isNotEmpty()) "RESULTS (${searchResults.size})" else "EXPLORE FEEDS (${searchResults.size})",
                        color = TextSoftMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Chunked in pairs for 2-column grid
                items(searchResults.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (item in pair) {
                            Box(modifier = Modifier.weight(1f)) {
                                SearchResultCard(
                                    item = item,
                                    onClick = {
                                        haptics.mediumClick()
                                        activePlaybackItem = item
                                    }
                                )
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    // Playback dialog
    if (activePlaybackItem != null) {
        val media = activePlaybackItem!!
        AlertDialog(
            onDismissRequest = { activePlaybackItem = null },
            containerColor = Color(0xFF111111),
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(
                    text = media.title,
                    color = TextPureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            text = {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        LiquidGlassBadge(media.badge, accentColor = if (media.isLive) Color(0xFFEF4444) else CrimsonRed)
                        LiquidGlassBadge(media.quality, accentColor = Color(0xFF10B981))
                        LiquidGlassBadge(media.providerName, accentColor = Color(0xFF3B82F6))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(media.description, color = TextSoftMuted, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        haptics.heavyClick()
                        activePlaybackItem = null
                        Toast.makeText(context, "Streaming ${media.title}...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = TextPureWhite, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Play Now", color = TextPureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        haptics.lightClick()
                        activePlaybackItem = null
                    }
                ) {
                    Text("Cancel", color = TextSoftMuted, fontSize = 12.sp)
                }
            }
        )
    }
}

@Composable
private fun SearchResultCard(
    item: MediaFeedItem,
    onClick: () -> Unit
) {
    LiquidGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1C1C1C),
                            Color(0xFF111111),
                            AmoledBlack
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.isLive) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(item.badge, color = Color(0xFFEF4444), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    LiquidGlassBadge(item.badge, accentColor = CrimsonRed)
                }
            }

            Column {
                Text(
                    text = item.title,
                    color = TextPureWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.providerName,
                    color = TextSoftMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
