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
