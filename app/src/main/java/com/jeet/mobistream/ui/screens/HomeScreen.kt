package com.jeet.mobistream.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jeet.mobistream.core.extension.ExtensionItem
import com.jeet.mobistream.core.extension.ExtensionManager
import com.jeet.mobistream.ui.components.LiquidGlassBadge
import com.jeet.mobistream.ui.components.LiquidGlassSurface
import com.jeet.mobistream.ui.components.rememberHaptics
import com.jeet.mobistream.ui.theme.AmoledBlack
import com.jeet.mobistream.ui.theme.CrimsonRed
import com.jeet.mobistream.ui.theme.TextDeepDim
import com.jeet.mobistream.ui.theme.TextPureWhite
import com.jeet.mobistream.ui.theme.TextSoftMuted
import kotlinx.coroutines.launch

/**
 * CloudStream Home Media Item Model
 */
data class CloudStreamMedia(
    val id: String,
    val title: String,
    val posterUrl: String,
    val backdropUrl: String? = null,
    val quality: String = "HD",
    val languageTag: String = "HIN",
    val category: String,
    val isLive: Boolean = false,
    val year: String = "2026",
    val rating: String = "8.8",
    val synopsis: String = "",
    val provider: String = "SK Tech"
)

@Composable
fun HomeScreen(
    paddingValues: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    val haptics = rememberHaptics()
    val scope = rememberCoroutineScope()

    val installedProviders = remember { mutableStateListOf<ExtensionItem>() }
    var selectedProvider by remember { mutableStateOf("All") }
    var showSourceDropdown by remember { mutableStateOf(false) }
    var activeDetailMedia by remember { mutableStateOf<CloudStreamMedia?>(null) }
    var isBookmarkedHero by remember { mutableStateOf(false) }

    // Add Repo Dialog states
    var showAddRepoDialog by remember { mutableStateOf(false) }
    var repoInput by remember { mutableStateOf("") }
    var isAddingRepo by remember { mutableStateOf(false) }

    fun refreshExtensions() {
        installedProviders.clear()
        installedProviders.addAll(ExtensionManager.getInstalledExtensions(context))
    }

    LaunchedEffect(Unit) {
        refreshExtensions()
    }

    // Dynamically derive media feeds strictly from installed and enabled extensions
    val allMediaItems = remember(installedProviders.toList()) {
        buildDynamicMedia(installedProviders)
    }

    // Filter items based on active provider selection ("All" or specific provider name)
    val visibleItems = remember(allMediaItems, selectedProvider) {
        if (selectedProvider == "All") allMediaItems
        else allMediaItems.filter { it.provider.equals(selectedProvider, ignoreCase = true) }
    }

    val featuredHero = remember(visibleItems) {
        visibleItems.firstOrNull { it.backdropUrl != null } ?: visibleItems.firstOrNull()
    }

    val categories = remember(visibleItems) {
        visibleItems.map { it.category }.distinct()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        if (installedProviders.isEmpty() || visibleItems.isEmpty()) {
            // ================================================================
            // AUTHENTIC CLOUDSTREAM EMPTY STATE (WHEN NO EXTENSIONS LOADED)
            // ================================================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AmoledBlack)
            ) {
                // Top Header Overlay
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
                            color = TextPureWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "STREAM",
                            color = CrimsonRed,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                haptics.lightClick()
                                refreshExtensions()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = "Refresh", tint = TextPureWhite, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0x33FFFFFF))
                                .border(1.dp, Color(0x4DFFFFFF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.AccountCircle, contentDescription = "Profile", tint = TextPureWhite, modifier = Modifier.size(22.dp))
                        }
                    }
                }

                // Centered Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp)
                        .padding(bottom = 90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF141414))
                            .border(1.dp, Color(0x22FFFFFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Extension,
                            contentDescription = null,
                            tint = CrimsonRed,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "None of your extensions are loaded",
                        color = TextPureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "MobiStream is modular. Add an extension repository to start watching live sports, movies, and TV shows.",
                        color = TextSoftMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            haptics.heavyClick()
                            repoInput = ""
                            showAddRepoDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, tint = TextPureWhite, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Repository", color = TextPureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Shortcode Setup Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF161616))
                            .border(0.5.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                            .clickable {
                                haptics.mediumClick()
                                scope.launch {
                                    isAddingRepo = true
                                    val res = ExtensionManager.fetchRepository(context, "msr")
                                    isAddingRepo = false
                                    res.onSuccess { meta ->
                                        // Auto install providers if fresh
                                        meta.plugins.forEach { ext ->
                                            ExtensionManager.downloadExtension(context, ext)
                                        }
                                        refreshExtensions()
                                        Toast.makeText(context, "${meta.name} successfully installed!", Toast.LENGTH_SHORT).show()
                                    }.onFailure { err ->
                                        Toast.makeText(context, "Failed: ${err.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isAddingRepo) "Loading MSR..." else "+ Quick Setup: Official MSR Repository",
                            color = Color(0xFF3B82F6),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        } else {
            // ================================================================
            // CLOUDSTREAM FULL HOME FEED WITH CINEMATIC HERO BILLBOARD
            // ================================================================
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // 1. CLOUDSTREAM HERO BANNER (490dp)
                if (featuredHero != null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(490.dp)
                        ) {
                            // Hero Backdrop Poster
                            AsyncImage(
                                model = featuredHero.backdropUrl ?: featuredHero.posterUrl,
                                contentDescription = featuredHero.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // CloudStream Cinematic Gradient Overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                AmoledBlack.copy(alpha = 0.65f),
                                                Color.Transparent,
                                                AmoledBlack.copy(alpha = 0.5f),
                                                AmoledBlack
                                            ),
                                            startY = 0f,
                                            endY = 1300f
                                        )
                                    )
                            )

                            // Top Bar Overlay
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .align(Alignment.TopCenter),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left: Brand / Provider Title with Dropdown
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { showSourceDropdown = true }
                                        .padding(horizontal = 4.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "MOBI",
                                        color = TextPureWhite,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "STREAM",
                                        color = CrimsonRed,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowDropDown,
                                        contentDescription = "Select Source",
                                        tint = TextPureWhite,
                                        modifier = Modifier.size(20.dp)
                                    )

                                    DropdownMenu(
                                        expanded = showSourceDropdown,
                                        onDismissRequest = { showSourceDropdown = false },
                                        modifier = Modifier.background(Color(0xFF141414))
                                    ) {
                                        val sourceOptions = listOf("All") + installedProviders.map { it.name }.distinct()
                                        sourceOptions.forEach { opt ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = opt,
                                                        color = if (selectedProvider == opt) CrimsonRed else TextPureWhite,
                                                        fontWeight = if (selectedProvider == opt) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                },
                                                onClick = {
                                                    haptics.selectionTick()
                                                    selectedProvider = opt
                                                    showSourceDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // Right Action Icons: Refresh, Account Profile
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            haptics.lightClick()
                                            refreshExtensions()
                                            Toast.makeText(context, "Feeds synchronized", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Icon(Icons.Rounded.Refresh, contentDescription = "Refresh", tint = TextPureWhite, modifier = Modifier.size(20.dp))
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x33FFFFFF))
                                            .border(1.dp, Color(0x4DFFFFFF), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Rounded.AccountCircle, contentDescription = "Profile", tint = TextPureWhite, modifier = Modifier.size(22.dp))
                                    }
                                }
                            }

                            // Hero Bottom Content: Tags + Title + CloudStream Action Bar
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Quality & Genre tags
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (featuredHero.isLive) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFEF4444))
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("LIVE NOW", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text("• ${featuredHero.languageTag} • ${featuredHero.quality} • ${featuredHero.category}", color = TextSoftMuted, fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = featuredHero.title,
                                    color = TextPureWhite,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // CloudStream Action Bar (+ Bookmark, Play Button, Info)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1. My List / Bookmark (CloudStream Style)
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clickable {
                                                haptics.lightClick()
                                                isBookmarkedHero = !isBookmarkedHero
                                                Toast.makeText(
                                                    context,
                                                    if (isBookmarkedHero) "Added to My List" else "Removed from My List",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isBookmarkedHero) Icons.Rounded.Check else Icons.Rounded.Add,
                                            contentDescription = "My List",
                                            tint = if (isBookmarkedHero) Color(0xFF10B981) else TextPureWhite,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = if (isBookmarkedHero) "Saved" else "My List",
                                            color = TextPureWhite,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    // 2. CloudStream Play Button (White Button, Bold, Black Text)
                                    Button(
                                        onClick = {
                                            haptics.heavyClick()
                                            activeDetailMedia = featuredHero
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = TextPureWhite,
                                            contentColor = AmoledBlack
                                        ),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.height(42.dp),
                                        contentPadding = PaddingValues(horizontal = 24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.PlayArrow,
                                            contentDescription = "Play",
                                            tint = AmoledBlack,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Play",
                                            color = AmoledBlack,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    // 3. Info Button (CloudStream Style)
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clickable {
                                                haptics.lightClick()
                                                activeDetailMedia = featuredHero
                                            }
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Info,
                                            contentDescription = "Info",
                                            tint = TextPureWhite,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "Info",
                                            color = TextPureWhite,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. CLOUDSTREAM DYNAMIC CATEGORY ROWS (114dp x 175dp Card Layout)
                categories.forEach { categoryName ->
                    val itemsForCategory = visibleItems.filter { it.category == categoryName }
                    if (itemsForCategory.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            CloudStreamSectionHeader(categoryName)
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 14.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(itemsForCategory, key = { it.id }) { item ->
                                    CloudStreamMediaCard(
                                        item = item,
                                        onClick = {
                                            haptics.mediumClick()
                                            activeDetailMedia = item
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // 3. CLOUDSTREAM SOURCE SELECTOR FLOATING ACTION BUTTON (Bottom Right)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 18.dp, bottom = 95.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1A1A1A))
                        .border(0.5.dp, Color(0x33FFFFFF), RoundedCornerShape(24.dp))
                        .clickable {
                            haptics.mediumClick()
                            showSourceDropdown = true
                        }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.FilterList,
                            contentDescription = "Source",
                            tint = TextPureWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Source: $selectedProvider",
                            color = TextPureWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // ================================================================
        // ADD REPOSITORY POPUP DIALOG (SUPPORTS SHORTCODES LIKE 'msr')
        // ================================================================
        if (showAddRepoDialog) {
            AlertDialog(
                onDismissRequest = { if (!isAddingRepo) showAddRepoDialog = false },
                containerColor = Color(0xFF141414),
                shape = RoundedCornerShape(16.dp),
                title = {
                    Text("Add Repository", color = TextPureWhite, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                },
                text = {
                    Column {
                        Text(
                            text = "Enter repository URL or shortcode (e.g., 'msr', 'rohit').",
                            color = TextSoftMuted,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = repoInput,
                            onValueChange = { repoInput = it },
                            placeholder = { Text("Shortcode or URL (e.g. msr)", color = TextDeepDim, fontSize = 13.sp) },
                            singleLine = true,
                            enabled = !isAddingRepo,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CrimsonRed,
                                unfocusedBorderColor = Color(0xFF262626),
                                focusedContainerColor = Color(0xFF0D0D0D),
                                unfocusedContainerColor = Color(0xFF0D0D0D),
                                focusedTextColor = TextPureWhite,
                                unfocusedTextColor = TextPureWhite
                            )
                        )
                        if (isAddingRepo) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(color = CrimsonRed, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Fetching manifest...", color = TextSoftMuted, fontSize = 11.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (repoInput.isNotBlank()) {
                                haptics.heavyClick()
                                scope.launch {
                                    isAddingRepo = true
                                    val res = ExtensionManager.fetchRepository(context, repoInput.trim())
                                    isAddingRepo = false
                                    res.onSuccess { meta ->
                                        // Auto-install plugins on addition
                                        meta.plugins.forEach { ext ->
                                            ExtensionManager.downloadExtension(context, ext)
                                        }
                                        refreshExtensions()
                                        showAddRepoDialog = false
                                        Toast.makeText(context, "${meta.name} successfully installed!", Toast.LENGTH_SHORT).show()
                                    }.onFailure { err ->
                                        Toast.makeText(context, "Error: ${err.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        enabled = !isAddingRepo && repoInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Install", color = TextPureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showAddRepoDialog = false },
                        enabled = !isAddingRepo
                    ) {
                        Text("Cancel", color = TextSoftMuted, fontSize = 12.sp)
                    }
                }
            )
        }

        // ================================================================
        // 4. CLOUDSTREAM MEDIA DETAILS / STREAM BOTTOM SHEET
        // ================================================================
        if (activeDetailMedia != null) {
            val media = activeDetailMedia!!
            AlertDialog(
                onDismissRequest = { activeDetailMedia = null },
                containerColor = Color(0xFF101010),
                shape = RoundedCornerShape(16.dp),
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
                        IconButton(onClick = { activeDetailMedia = null }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Close", tint = TextSoftMuted, modifier = Modifier.size(20.dp))
                        }
                    }
                },
                text = {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Poster Thumbnail
                            AsyncImage(
                                model = media.posterUrl,
                                contentDescription = media.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(115.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(media.year, color = TextPureWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("★ ${media.rating}", color = Color(0xFFF59E0B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    LiquidGlassBadge(media.quality, accentColor = CrimsonRed)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Source: ${media.provider}", color = Color(0xFF3B82F6), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = media.synopsis,
                                    color = TextSoftMuted,
                                    fontSize = 11.sp,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stream Info
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF161616))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Server: Direct CDN Mirror 1 (Fast Low-Latency)", color = TextPureWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("Audio: Hindi Multi-Audio • Subtitles: Embedded", color = TextSoftMuted, fontSize = 10.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            haptics.heavyClick()
                            activeDetailMedia = null
                            Toast.makeText(context, "Streaming ${media.title} in ExoPlayer...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = TextPureWhite, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Play", color = TextPureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                },
                dismissButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(
                            onClick = {
                                haptics.lightClick()
                                Toast.makeText(context, "Added to Watchlist!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("Save", color = TextSoftMuted, fontSize = 12.sp)
                        }
                        TextButton(
                            onClick = {
                                haptics.lightClick()
                                Toast.makeText(context, "Downloading ${media.title}...", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Rounded.Download, contentDescription = null, tint = TextSoftMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Download", color = TextSoftMuted, fontSize = 12.sp)
                        }
                    }
                }
            )
        }
    }
}

/**
 * Derives media items dynamically from installed & enabled extensions.
 */
private fun buildDynamicMedia(installed: List<ExtensionItem>): List<CloudStreamMedia> {
    if (installed.isEmpty()) return emptyList()
    val list = mutableListOf<CloudStreamMedia>()

    for (ext in installed.filter { it.isEnabled }) {
        if (ext.internalName.contains("SKTech", ignoreCase = true)) {
            for (sub in ext.enabledSubProviders) {
                val isSports = sub in listOf(
                    "FC LIVE", "SONY LIVE", "SONY LIVE 2", "JTV+", "JTV", "JTV WW",
                    "SPORTS WORLD", "SPORTS EVENT", "CRIC HD", "JIOSTAR"
                )
                val isShow = sub in listOf(
                    "BIG BOSS", "KAPIL SHOW S4", "Family Full House With Rohit Sharma", "KIDS TV"
                )
                val isLiveEntertainment = sub in listOf(
                    "MOVIES", "SONY-LIV", "SONY-LIV WW", "JIO CINEMA", "SUN NXT", "DISH TV", "WAVES OTT",
                    "ZEE GLOBAL", "ZEE FIVE", "ZEE5_WW", "VZY"
                )

                val category = when {
                    isSports -> "Live Cricket & Sports Feeds"
                    isShow -> "TV & Reality Shows"
                    isLiveEntertainment -> "Live Entertainment & OTT"
                    else -> "Regional & Live Channels"
                }

                val (poster, backdrop) = when (sub) {
                    "FC LIVE" -> Pair(
                        "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=600",
                        "https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?w=1200"
                    )
                    "SONY LIVE" -> Pair(
                        "https://images.unsplash.com/photo-1517649763962-0c623266ddc0?w=600",
                        null
                    )
                    "SONY LIVE 2" -> Pair(
                        "https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=600",
                        null
                    )
                    "JTV+" -> Pair(
                        "https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=600",
                        null
                    )
                    "JIOSTAR" -> Pair(
                        "https://images.unsplash.com/photo-1489944445339-3f309e45549c?w=600",
                        "https://images.unsplash.com/photo-1531415074868-036b107e775a?w=1200"
                    )
                    "CRIC HD" -> Pair(
                        "https://images.unsplash.com/photo-1531415074868-036b107e775a?w=600",
                        null
                    )
                    "SPORTS WORLD" -> Pair(
                        "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=600",
                        null
                    )
                    "BIG BOSS" -> Pair(
                        "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600",
                        null
                    )
                    "KAPIL SHOW S4" -> Pair(
                        "https://images.unsplash.com/photo-1526478806334-5fd488fcaabc?w=600",
                        null
                    )
                    "Family Full House With Rohit Sharma" -> Pair(
                        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600",
                        null
                    )
                    "MOVIES" -> Pair(
                        "https://images.unsplash.com/photo-1485846234645-a62644f84728?w=600",
                        null
                    )
                    else -> Pair(
                        "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600",
                        null
                    )
                }

                val quality = when {
                    sub == "JIOSTAR" -> "4K"
                    isSports -> "LIVE"
                    isShow -> "HD"
                    else -> "FHD"
                }

                list.add(
                    CloudStreamMedia(
                        id = "sk_${sub.replace(" ", "_").lowercase()}",
                        title = sub,
                        posterUrl = poster,
                        backdropUrl = backdrop,
                        quality = quality,
                        languageTag = "HIN",
                        category = category,
                        isLive = isSports || !isShow,
                        rating = if (isSports) "9.5" else "8.8",
                        synopsis = "$sub Live stream feed provided by ${ext.name}.",
                        provider = ext.name
                    )
                )
            }
        } else if (ext.internalName.contains("Castle", ignoreCase = true)) {
            val castleMovies = listOf(
                CloudStreamMedia(
                    id = "c_pushpa",
                    title = "Pushpa 2: The Rule",
                    posterUrl = "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=600",
                    backdropUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=1200",
                    quality = "4K HDR",
                    languageTag = "MULTI",
                    category = "Trending on MobiStream",
                    year = "2026",
                    rating = "9.1",
                    synopsis = "Pushpa Raj expands his sandalwood empire into international waters.",
                    provider = ext.name
                ),
                CloudStreamMedia(
                    id = "c_stree2",
                    title = "Stree 2",
                    posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600",
                    quality = "FHD",
                    languageTag = "HIN",
                    category = "Trending on MobiStream",
                    year = "2024",
                    rating = "8.6",
                    synopsis = "The town of Chanderi is haunted once more by Sarkata.",
                    provider = ext.name
                ),
                CloudStreamMedia(
                    id = "c_singham",
                    title = "Singham Again",
                    posterUrl = "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=600",
                    quality = "FHD",
                    languageTag = "HIN",
                    category = "Bollywood Blockbusters",
                    year = "2024",
                    rating = "8.2",
                    synopsis = "DCP Bajirao Singham leads the cop universe on a high-stakes mission.",
                    provider = ext.name
                ),
                CloudStreamMedia(
                    id = "c_avatar",
                    title = "Avatar: Fire and Ash",
                    posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600",
                    quality = "4K",
                    languageTag = "DUB",
                    category = "Trending on MobiStream",
                    year = "2025",
                    rating = "9.0",
                    synopsis = "Jake Sully and Neytiri encounter the volcanic Ash People.",
                    provider = ext.name
                )
            )
            list.addAll(castleMovies)
        } else if (ext.internalName.contains("MovieBox", ignoreCase = true)) {
            val movieboxMovies = listOf(
                CloudStreamMedia(
                    id = "mb_deadpool",
                    title = "Deadpool & Wolverine",
                    posterUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=600",
                    backdropUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=1200",
                    quality = "4K",
                    languageTag = "DUB",
                    category = "Trending on MobiStream",
                    year = "2024",
                    rating = "8.8",
                    synopsis = "Wade Wilson teams up with an alternate Wolverine.",
                    provider = ext.name
                ),
                CloudStreamMedia(
                    id = "mb_kalki",
                    title = "Kalki 2898 AD",
                    posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600",
                    quality = "4K HDR",
                    languageTag = "MULTI",
                    category = "Trending on MobiStream",
                    year = "2024",
                    rating = "8.7",
                    synopsis = "In a post-apocalyptic future, Ashwatthama protects the unborn avatar Kalki.",
                    provider = ext.name
                ),
                CloudStreamMedia(
                    id = "mb_jawan",
                    title = "Jawan (Extended)",
                    posterUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?w=600",
                    quality = "1080P",
                    languageTag = "HIN",
                    category = "Bollywood Blockbusters",
                    year = "2023",
                    rating = "8.4",
                    synopsis = "A man driven by personal vendetta to rectify corrupt evils.",
                    provider = ext.name
                ),
                CloudStreamMedia(
                    id = "mb_animal",
                    title = "Animal",
                    posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600",
                    quality = "4K",
                    languageTag = "HIN",
                    category = "Bollywood Blockbusters",
                    year = "2023",
                    rating = "8.1",
                    synopsis = "A son undergoes violent conflict for his father.",
                    provider = ext.name
                )
            )
            list.addAll(movieboxMovies)
        } else {
            list.add(
                CloudStreamMedia(
                    id = "gen_${ext.internalName}",
                    title = "${ext.name} Live Stream",
                    posterUrl = ext.iconUrl ?: "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600",
                    quality = "FHD",
                    languageTag = ext.language.uppercase(),
                    category = "Popular Streams",
                    rating = "8.5",
                    synopsis = ext.description.ifEmpty { "Curated streaming provided by ${ext.name}." },
                    provider = ext.name
                )
            )
        }
    }

    return list
}

/**
 * CloudStream Section Header with Title and '>' icon
 */
@Composable
private fun CloudStreamSectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPureWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = "More",
            tint = TextSoftMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * CloudStream Standard Media Card (114dp x 175dp with corner radius 8dp)
 */
@Composable
private fun CloudStreamMediaCard(
    item: CloudStreamMedia,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(114.dp)
            .height(175.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF141414))
            .clickable(onClick = onClick)
    ) {
        // Poster Image
        AsyncImage(
            model = item.posterUrl,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Bottom Gradient Shadow (title_shadow)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            AmoledBlack.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // Top Badges Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quality Badge (text_quality)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (item.isLive) Color(0xFFEF4444) else Color(0xCC000000))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = item.quality,
                    color = TextPureWhite,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Dub / Language Badge (text_is_dub)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xCC000000))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = item.languageTag,
                    color = TextSoftMuted,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Title at the bottom (imageText)
        Text(
            text = item.title,
            color = TextPureWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp)
        )
    }
}
