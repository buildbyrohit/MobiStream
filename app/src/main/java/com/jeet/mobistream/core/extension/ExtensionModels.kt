package com.jeet.mobistream.core.extension

data class RepositoryMeta(
    val name: String,
    val description: String,
    val manifestVersion: Int,
    val pluginLists: List<String>,
    val iconUrl: String? = null
)

data class ExtensionItem(
    val internalName: String,
    val name: String,
    val description: String,
    val url: String, // Remote URL to .cs3 file
    val version: Int,
    val language: String,
    val iconUrl: String?,
    val fileSize: Long,
    val fileHash: String?,
    val authors: List<String>,
    val isInstalled: Boolean = false,
    val isEnabled: Boolean = true,
    val localPath: String? = null
)
