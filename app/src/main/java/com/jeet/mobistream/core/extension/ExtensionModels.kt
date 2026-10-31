package com.jeet.mobistream.core.extension

data class RepositoryMeta(
    val id: String,
    val name: String,
    val description: String,
    val manifestVersion: Int,
    val pluginLists: List<String> = emptyList(),
    val url: String,
    val iconUrl: String? = null,
    val plugins: List<ExtensionItem> = emptyList(),
    val providerCount: Int = 0
)

data class ExtensionItem(
    val internalName: String,
    val name: String,
    val description: String,
    val url: String, // Remote URL to .cs3 file
    val version: Int,
    val language: String,
    val iconUrl: String? = null,
    val fileSize: Long = 0L,
    val fileHash: String? = null,
    val authors: List<String> = emptyList(),
    val isInstalled: Boolean = false,
    val isEnabled: Boolean = true,
    val localPath: String? = null,
    val hasSubProviders: Boolean = false,
    val subProviders: List<String> = emptyList(),
    val enabledSubProviders: List<String> = emptyList()
)

data class MediaFeedItem(
    val id: String,
    val title: String,
    val providerName: String,
    val providerInternalName: String,
    val posterUrl: String? = null,
    val category: String, // e.g. "Live Sports", "Trending Movies", "TV Shows", "Regional"
    val badge: String,    // e.g. "LIVE", "4K HDR", "1080P"
    val isLive: Boolean = false,
    val streamUrl: String = "",
    val quality: String = "1080p 60fps",
    val description: String = ""
)
