package com.jeet.mobistream.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jeet.mobistream.ui.components.LiquidGlassBadge
import com.jeet.mobistream.ui.components.LiquidGlassSurface
import com.jeet.mobistream.ui.components.rememberHaptics
import com.jeet.mobistream.ui.theme.AmoledBlack
import com.jeet.mobistream.ui.theme.CrimsonRed
import com.jeet.mobistream.ui.theme.LiquidCyan
import com.jeet.mobistream.ui.theme.LiquidRuby
import com.jeet.mobistream.ui.theme.TextDeepDim
import com.jeet.mobistream.ui.theme.TextPureWhite
import com.jeet.mobistream.ui.theme.TextSoftMuted

data class MediaItem(
    val id: String,
    val title: String,
    val posterUrl: String,
    val badge: String,
    val badgeColor: Color = CrimsonRed
)

@Composable
fun HomeScreen(
    paddingValues: PaddingValues = PaddingValues()
) {
    val haptics = rememberHaptics()

    val sampleTrending = listOf(
        MediaItem("1", "Stranger Things 5", "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=600", "4K • HINDI", CrimsonRed),
        MediaItem("2", "Deadpool & Wolverine", "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=600", "CINEMA", LiquidRuby),
        MediaItem("3", "Squid Game 2", "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600", "TRENDING", LiquidCyan),
        MediaItem("4", "The Boys S4", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600", "1080P", Color.White)
    )

    val sampleCricket = listOf(
        MediaItem("5", "India vs Australia T20", "https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?w=600", "LIVE NOW", CrimsonRed),
        MediaItem("6", "IPL 2026 Finals", "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=600", "STADIUM FEED", LiquidRuby),
        MediaItem("7", "Champions Trophy HD", "https://images.unsplash.com/photo-1517649763962-0c623266ddc0?w=600", "HINDI COMM", LiquidCyan)
    )

    val sampleBollywood = listOf(
        MediaItem("8", "Singham Again", "https://images.unsplash.com/photo-1485846234645-a62644f84728?w=600", "BLOCKBUSTER", CrimsonRed),
        MediaItem("9", "Pushpa 2: The Rule", "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=600", "MULTI-AUDIO", LiquidRuby),
        MediaItem("10", "Stree 2", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600", "HINDI 4K", Color.White)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack),
        contentPadding = PaddingValues(bottom = 100.dp)
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
                    Spacer(modifier = Modifier.width(12.dp))
                    LiquidGlassBadge(text = "MSR ACTIVE", accentColor = CrimsonRed)
                }

                LiquidGlassSurface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    onClick = { haptics.lightClick() }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = "Filter",
                        tint = TextPureWhite,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }

        // Hero Billboard (Apple Liquid Glass Frame)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                LiquidGlassSurface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(26.dp),
                    specularAlpha = 0.35f
                ) {
                    // Hero Image
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1080",
                        contentDescription = "Featured",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Cinematic Bottom Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0x80000000),
                                        AmoledBlack
                                    )
                                )
                            )
                    )

                    // Billboard Content
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        LiquidGlassBadge(text = "TOP 10 TODAY • 4K ULTRA HD", accentColor = CrimsonRed)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Avatar: Fire and Ash",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPureWhite
                        )
                        Text(
                            text = "Hindi Dubbed • English • Sci-Fi • 3h 12m",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSoftMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { haptics.heavyClick() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TextPureWhite,
                                    contentColor = AmoledBlack
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = "Play",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Play",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            LiquidGlassSurface(
                                modifier = Modifier.height(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                onClick = { haptics.lightClick() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = "List",
                                        tint = TextPureWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "My List",
                                        color = TextPureWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Trending Now
        item {
            MediaRowSection(
                title = "Trending on MobiStream",
                items = sampleTrending
            )
        }

        // Section: Live Cricket & Sports (MSR SK Tech & Cricify)
        item {
            MediaRowSection(
                title = "Live Cricket & Sports Feeds",
                items = sampleCricket,
                isLive = true
            )
        }

        // Section: Bollywood & Hindi Dubbed Blockbusters
        item {
            MediaRowSection(
                title = "Bollywood & South Hindi Hits",
                items = sampleBollywood
            )
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun MediaRowSection(
    title: String,
    items: List<MediaItem>,
    isLive: Boolean = false
) {
    val haptics = rememberHaptics()

    Column(modifier = Modifier.padding(top = 22.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPureWhite
            )
            Text(
                text = "See All",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = CrimsonRed
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                LiquidGlassSurface(
                    modifier = Modifier
                        .width(if (isLive) 200.dp else 135.dp)
                        .height(if (isLive) 130.dp else 200.dp),
                    shape = RoundedCornerShape(16.dp),
                    specularAlpha = 0.25f,
                    onClick = {
                        haptics.mediumClick()
                    }
                ) {
                    AsyncImage(
                        model = item.posterUrl,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        AmoledBlack.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                    ) {
                        LiquidGlassBadge(
                            text = item.badge,
                            accentColor = item.badgeColor
                        )
                    }

                    Text(
                        text = item.title,
                        color = TextPureWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    )
                }
            }
        }
    }
}

