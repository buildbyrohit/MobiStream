package com.jeet.mobistream.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
fun HomeScreen(
    paddingValues: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    val haptics = rememberHaptics()

    val installedProviders = remember { mutableStateListOf<ExtensionItem>() }
    val feedItems = remember { mutableStateListOf<MediaFeedItem>() }
    var selectedProviderFilter by remember { mutableStateOf("All") }
    var activePlaybackItem by remember { mutableStateOf<MediaFeedItem?>(null) }

    fun refreshContent() {
        installedProviders.clear()
        installedProviders.addAll(ExtensionManager.getInstalledExtensions(context))

        feedItems.clear()
        feedItems.addAll(ExtensionManager.getHomeFeedItems(context))
    }

    LaunchedEffect(Unit) {
        refreshContent()
    }

    // Filter feed items based on selected provider
    val filteredFeeds = remember(feedItems, selectedProviderFilter) {
        if (selectedProviderFilter == "All") feedItems
        else feedItems.filter { it.providerName == selectedProviderFilter || it.providerInternalName == selectedProviderFilter }
    }

    // Group items by category
    val sportsItems = filteredFeeds.filter { it.category.contains("Sports", ignoreCase = true) }
    val moviesItems = filteredFeeds.filter { it.category.contains("Movie", ignoreCase = true) }
    val showsItems = filteredFeeds.filter { it.category.contains("Shows", ignoreCase = true) || it.category.contains("TV", ignoreCase = true) }
    val regionalItems = filteredFeeds.filter { it.category.contains("Regional", ignoreCase = true) || it.category.contains("Channels", ignoreCase = true) }
    val otherItems = filteredFeeds.filter { it !in sportsItems && it !in moviesItems && it !in showsItems && it !in regionalItems }

    val heroItem = sportsItems.firstOrNull() ?: filteredFeeds.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // 1. Top App Bar
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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (installedProviders.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF141414))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${installedProviders.size} Providers",
                                color = Color(0xFF10B981),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    LiquidGlassSurface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        borderWidth = 0.dp,
                        tint = Color(0xFF141414),
                        onClick = {
                            haptics.lightClick()
                            refreshContent()
                            Toast.makeText(context, "Refreshed live feeds", Toast.LENGTH_SHORT).show()
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
        }

        // 2. Provider Filter Chips (CloudStream Multi-Provider Selector)
        if (installedProviders.isNotEmpty()) {
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterOptions = listOf("All") + installedProviders.map { it.name }
                    items(filterOptions) { opt ->
                        val isSelected = selectedProviderFilter == opt
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) CrimsonRed else Color(0xFF141414))
                                .clickable {
                                    haptics.selectionTick()
                                    selectedProviderFilter = opt
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = opt,
                                color = if (isSelected) TextPureWhite else TextSoftMuted,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        if (installedProviders.isEmpty()) {
            // Empty State: Prompt to add extensions
            item {
                Spacer(modifier = Modifier.height(16.dp))
                LiquidGlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF141414)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Extension,
                                contentDescription = null,
                                tint = CrimsonRed,
                                modifier = Modifier.size(28.dp)
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
                            text = "MobiStream is modular and loads streams via extensions.\nAdd a repository like 'msr' to enable live cricket, sports, and movies.",
                            color = TextSoftMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF141414))
                                .padding(14.dp)
                        ) {
                            Text("Quick Setup Guide:", color = TextPureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            StepInstructionRow(step = "1", text = "Tap Settings in the bottom navigation bar")
                            Spacer(modifier = Modifier.height(6.dp))
                            StepInstructionRow(step = "2", text = "Select 'Extension' category")
                            Spacer(modifier = Modifier.height(6.dp))
                            StepInstructionRow(step = "3", text = "Tap '+' and enter shortcode 'msr'")
                            Spacer(modifier = Modifier.height(6.dp))
                            StepInstructionRow(step = "4", text = "Install SK Tech, Castle TV or MovieBox")
                        }
                    }
                }
            }
        } else {
            // 3. Featured Hero Billboard (CloudStream Featured Stream)
            if (heroItem != null) {
                item {
                    LiquidGlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .height(200.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        // Background gradient & art
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF1F080A),
                                            Color(0xFF0F0F0F),
                                            AmoledBlack
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (heroItem.isLive) Color(0xFFEF4444) else Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (heroItem.isLive) "LIVE BROADCAST" else "FEATURED STREAM",
                                        color = if (heroItem.isLive) Color(0xFFEF4444) else Color(0xFF10B981),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                                LiquidGlassBadge(heroItem.quality, accentColor = TextPureWhite)
                            }

                            Column {
                                Text(
                                    text = heroItem.title,
                                    color = TextPureWhite,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Provider: ${heroItem.providerName} • Fast Low-Latency Stream",
                                    color = TextSoftMuted,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Button(
                                        onClick = {
                                            haptics.heavyClick()
                                            activePlaybackItem = heroItem
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = TextPureWhite, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Play Stream", color = TextPureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            haptics.lightClick()
                                            Toast.makeText(context, "Added to Watchlist!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1C1C)),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Icon(Icons.Rounded.Bookmark, contentDescription = null, tint = TextPureWhite, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Saved", color = TextPureWhite, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 4. Row: Live Sports & Cricket Channels
            if (sportsItems.isNotEmpty()) {
                item {
                    ContentRowSection(
                        title = "Live Sports & Stadium Feeds",
                        subtitle = "Cricket, Football, and Events from SK Tech",
                        items = sportsItems,
                        onItemClick = { item ->
                            haptics.mediumClick()
                            activePlaybackItem = item
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // 5. Row: Trending Movies & Blockbusters
            if (moviesItems.isNotEmpty()) {
                item {
                    ContentRowSection(
                        title = "Trending Movies & Blockbusters",
                        subtitle = "Multi-Audio Hindi & English Hits",
                        items = moviesItems,
                        isWidescreen = false,
                        onItemClick = { item ->
                            haptics.mediumClick()
                            activePlaybackItem = item
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // 6. Row: TV & Reality Shows
            if (showsItems.isNotEmpty()) {
                item {
                    ContentRowSection(
                        title = "TV & Reality Shows",
                        subtitle = "Big Boss, Kapil Sharma & 24/7 Feeds",
                        items = showsItems,
                        onItemClick = { item ->
                            haptics.mediumClick()
                            activePlaybackItem = item
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // 7. Row: Regional Channels
            if (regionalItems.isNotEmpty()) {
                item {
                    ContentRowSection(
                        title = "Regional 24/7 Channels",
                        subtitle = "Bengali, Marathi, Tamil, Telugu & Punjabi",
                        items = regionalItems,
                        onItemClick = { item ->
                            haptics.mediumClick()
                            activePlaybackItem = item
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // 8. Other active items
            if (otherItems.isNotEmpty()) {
                item {
                    ContentRowSection(
                        title = "Popular Feeds",
                        subtitle = "Active provider feeds",
                        items = otherItems,
                        onItemClick = { item ->
                            haptics.mediumClick()
                            activePlaybackItem = item
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // Playback Preview Dialog / Stream Player Trigger
    if (activePlaybackItem != null) {
        val media = activePlaybackItem!!
        AlertDialog(
            onDismissRequest = { activePlaybackItem = null },
            containerColor = Color(0xFF111111),
            shape = RoundedCornerShape(18.dp),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = media.title,
                        color = TextPureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(onClick = { activePlaybackItem = null }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = TextSoftMuted, modifier = Modifier.size(20.dp))
                    }
                }
            },
            text = {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LiquidGlassBadge(media.badge, accentColor = if (media.isLive) Color(0xFFEF4444) else CrimsonRed)
                        LiquidGlassBadge(media.quality, accentColor = Color(0xFF10B981))
                        LiquidGlassBadge(media.providerName, accentColor = Color(0xFF3B82F6))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = media.description,
                        color = TextSoftMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF181818))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("Engine: In-App ExoPlayer (Hardware Decoded)", color = TextPureWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Buffer: Fast Start (DoH: Cloudflare 1.1.1.1)", color = TextSoftMuted, fontSize = 10.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        haptics.heavyClick()
                        activePlaybackItem = null
                        Toast.makeText(context, "Launching stream for ${media.title}...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                    shape = RoundedCornerShape(10.dp)
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
                        Toast.makeText(context, "Added to Watchlist!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Add to Saved", color = TextSoftMuted, fontSize = 12.sp)
                }
            }
        )
    }
}

@Composable
private fun ContentRowSection(
    title: String,
    subtitle: String,
    items: List<MediaFeedItem>,
    isWidescreen: Boolean = true,
    onItemClick: (MediaFeedItem) -> Unit
) {
    Column {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = title,
                color = TextPureWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextSoftMuted,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items, key = { it.id }) { item ->
                LiquidGlassSurface(
                    modifier = Modifier
                        .width(if (isWidescreen) 170.dp else 125.dp)
                        .height(if (isWidescreen) 115.dp else 180.dp),
                    shape = RoundedCornerShape(14.dp),
                    onClick = { onItemClick(item) }
                ) {
                    // Card background with subtle gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF1E1E1E),
                                        Color(0xFF121212),
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
        }
    }
}

@Composable
private fun StepInstructionRow(step: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(18.dp)
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
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = TextPureWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
