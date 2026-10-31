package com.jeet.mobistream.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FileDownloadDone
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.WatchLater
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

data class SavedMediaItem(
    val id: String,
    val title: String,
    val category: String,
    val duration: String,
    val quality: String,
    val size: String = ""
)

@Composable
fun SavedScreen() {
    val context = LocalContext.current
    val haptics = rememberHaptics()
    var selectedSegment by remember { mutableIntStateOf(0) } // 0 = Watchlist, 1 = Downloads

    // Sample saved watchlist items
    val watchlistItems = remember {
        mutableStateListOf(
            SavedMediaItem("1", "Jawan (Extended Cut)", "Bollywood • Action", "2h 49m", "1080p FHD"),
            SavedMediaItem("2", "Kalki 2898 AD", "Sci-Fi • Epic", "3h 01m", "4K HDR"),
            SavedMediaItem("3", "Mirzapur Season 3", "Series • 10 Episodes", "Hindi", "1080p FHD")
        )
    }

    // Sample downloaded offline items
    val downloadedItems = remember {
        mutableStateListOf(
            SavedMediaItem("4", "Animal", "Bollywood • Action", "3h 24m", "1080p", "1.8 GB"),
            SavedMediaItem("5", "India vs Australia T20 Final", "Live Sports Replay", "3h 15m", "720p 60fps", "1.2 GB")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 20.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Saved",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPureWhite
                    )
                    Text(
                        text = "Your personal watchlist and offline downloads",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSoftMuted
                    )
                }
                LiquidGlassBadge(
                    if (selectedSegment == 0) "${watchlistItems.size} Titles" else "${downloadedItems.size} Files",
                    accentColor = CrimsonRed
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Segmented Tab Selector (Watchlist / Downloads)
        item {
            LiquidGlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Watchlist Segment
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (selectedSegment == 0) CrimsonRed else Color.Transparent)
                            .clickable {
                                haptics.selectionTick()
                                selectedSegment = 0
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Bookmark,
                                contentDescription = null,
                                tint = if (selectedSegment == 0) TextPureWhite else TextSoftMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Watchlist (${watchlistItems.size})",
                                color = if (selectedSegment == 0) TextPureWhite else TextSoftMuted,
                                fontWeight = if (selectedSegment == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Downloads Segment
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (selectedSegment == 1) CrimsonRed else Color.Transparent)
                            .clickable {
                                haptics.selectionTick()
                                selectedSegment = 1
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = null,
                                tint = if (selectedSegment == 1) TextPureWhite else TextSoftMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Downloads (${downloadedItems.size})",
                                color = if (selectedSegment == 1) TextPureWhite else TextSoftMuted,
                                fontWeight = if (selectedSegment == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section Content
        if (selectedSegment == 0) {
            // Watchlist Content
            if (watchlistItems.isEmpty()) {
                item {
                    EmptySavedPlaceholder(
                        icon = Icons.Rounded.BookmarkBorder,
                        title = "Your Watchlist is Empty",
                        subtitle = "Bookmark movies and TV shows from Home or Search to watch them later."
                    )
                }
            } else {
                items(watchlistItems, key = { it.id }) { item ->
                    SavedMediaCard(
                        item = item,
                        isDownload = false,
                        onPlay = {
                            haptics.heavyClick()
                            Toast.makeText(context, "Playing ${item.title}...", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = {
                            haptics.lightClick()
                            watchlistItems.remove(item)
                            Toast.makeText(context, "Removed from Watchlist", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        } else {
            // Downloads Content
            item {
                // Storage Usage Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Storage, contentDescription = null, tint = TextSoftMuted, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Storage: 3.0 GB used", color = TextSoftMuted, fontSize = 12.sp)
                    }
                    Text("52.4 GB free", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (downloadedItems.isEmpty()) {
                item {
                    EmptySavedPlaceholder(
                        icon = Icons.Rounded.Download,
                        title = "No Downloads Found",
                        subtitle = "Download movies or series episodes to watch completely offline without internet."
                    )
                }
            } else {
                items(downloadedItems, key = { it.id }) { item ->
                    SavedMediaCard(
                        item = item,
                        isDownload = true,
                        onPlay = {
                            haptics.heavyClick()
                            Toast.makeText(context, "Playing offline: ${item.title}", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = {
                            haptics.lightClick()
                            downloadedItems.remove(item)
                            Toast.makeText(context, "Download deleted", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp)) // padding for bottom floating navbar
        }
    }
}

@Composable
private fun SavedMediaCard(
    item: SavedMediaItem,
    isDownload: Boolean,
    onPlay: () -> Unit,
    onDelete: () -> Unit
) {
    LiquidGlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Poster Placeholder + Title info
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassSurfaceDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDownload) Icons.Rounded.FileDownloadDone else Icons.Rounded.Bookmark,
                        contentDescription = null,
                        tint = CrimsonRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = item.title,
                        color = TextPureWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${item.category} • ${item.duration}",
                        color = TextSoftMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LiquidGlassBadge(item.quality, accentColor = CrimsonRed)
                        if (item.size.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            LiquidGlassBadge(item.size, accentColor = TextDeepDim)
                        }
                    }
                }
            }

            // Right Actions (Play & Delete)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onPlay,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CrimsonRed)
                ) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        contentDescription = "Play",
                        tint = TextPureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        Icons.Rounded.DeleteOutline,
                        contentDescription = "Delete",
                        tint = TextDeepDim,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySavedPlaceholder(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    LiquidGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(GlassSurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextSoftMuted,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                color = TextPureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = TextSoftMuted,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}
