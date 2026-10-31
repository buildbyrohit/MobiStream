package com.jeet.mobistream.core.extension

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

object ExtensionManager {
    private const val PREFS_NAME = "mobistream_extensions_prefs"
    private const val KEY_REPOS = "saved_repositories"
    private const val KEY_INSTALLED_PREFIX = "installed_"
    private const val KEY_ENABLED_PREFIX = "enabled_"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun getExtensionDir(context: Context): File {
        val dir = File(context.filesDir, "extensions")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Resolves shortcodes (msr, jeet, rohit, mobistream) to the official MSR repo manifest.
     */
    fun resolveShortcode(input: String): String {
        val clean = input.trim()
        val lower = clean.lowercase()
        return if (lower in setOf("msr", "jeet", "rohit", "mobistream")) {
            "https://raw.githubusercontent.com/buildbyrohit/msr/builds/repo.json"
        } else if (clean.startsWith("http://") || clean.startsWith("https://")) {
            clean
        } else {
            "https://$clean"
        }
    }

    /**
     * Calculates SHA-256 hash for verifying .cs3 download integrity.
     */
    private fun calculateSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { fis ->
            val buffer = ByteArray(8192)
            var read = fis.read(buffer)
            while (read != -1) {
                digest.update(buffer, 0, read)
                read = fis.read(buffer)
            }
        }
        return "sha256-" + digest.digest().joinToString("") { "%02x".format(it) }
    }

    /**
     * Fetches repository metadata and its plugins list asynchronously.
     */
    suspend fun fetchRepository(
        context: Context,
        repoUrl: String
    ): Result<Pair<RepositoryMeta, List<ExtensionItem>>> = withContext(Dispatchers.IO) {
        try {
            val resolvedUrl = resolveShortcode(repoUrl)
            val request = Request.Builder().url(resolvedUrl).build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to fetch repository: HTTP ${response.code}"))
            }

            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty repository response"))
            val repoJson = JSONObject(body)

            val name = repoJson.optString("name", "Unknown Repository")
            val description = repoJson.optString("description", "")
            val manifestVersion = repoJson.optInt("manifestVersion", 1)
            val iconUrl = if (repoJson.has("iconUrl") && !repoJson.isNull("iconUrl")) repoJson.getString("iconUrl") else null

            val pluginListsArray = repoJson.optJSONArray("pluginLists") ?: JSONArray()
            val pluginListUrls = mutableListOf<String>()
            for (i in 0 until pluginListsArray.length()) {
                pluginListUrls.add(pluginListsArray.getString(i))
            }

            // If pluginLists is empty, try default plugins.json in same base directory
            if (pluginListUrls.isEmpty()) {
                val baseUrl = resolvedUrl.substringBeforeLast("/")
                pluginListUrls.add("$baseUrl/plugins.json")
            }

            val extensions = mutableListOf<ExtensionItem>()
            val prefs = getPrefs(context)
            val extDir = getExtensionDir(context)

            for (pUrl in pluginListUrls) {
                val pluginsReq = Request.Builder().url(pUrl).build()
                val pResponse = httpClient.newCall(pluginsReq).execute()
                if (pResponse.isSuccessful) {
                    val pBody = pResponse.body?.string() ?: continue
                    val pArray = JSONArray(pBody)

                    for (j in 0 until pArray.length()) {
                        val pObj = pArray.getJSONObject(j)
                        val internalName = pObj.optString("internalName", "")
                        if (internalName.isEmpty()) continue

                        val localFile = File(extDir, "$internalName.cs3")
                        val isInstalledLocally = localFile.exists() && prefs.getBoolean("$KEY_INSTALLED_PREFIX$internalName", false)
                        val isEnabled = prefs.getBoolean("$KEY_ENABLED_PREFIX$internalName", true)

                        val authorsArray = pObj.optJSONArray("authors") ?: JSONArray()
                        val authors = mutableListOf<String>()
                        for (a in 0 until authorsArray.length()) {
                            authors.add(authorsArray.getString(a))
                        }

                        extensions.add(
                            ExtensionItem(
                                internalName = internalName,
                                name = pObj.optString("name", internalName),
                                description = pObj.optString("description", ""),
                                url = pObj.optString("url", ""),
                                version = pObj.optInt("version", 1),
                                language = pObj.optString("language", "hi"),
                                iconUrl = if (pObj.has("iconUrl") && !pObj.isNull("iconUrl")) pObj.getString("iconUrl") else null,
                                fileSize = pObj.optLong("fileSize", 0L),
                                fileHash = if (pObj.has("fileHash") && !pObj.isNull("fileHash")) pObj.getString("fileHash") else null,
                                authors = authors,
                                isInstalled = isInstalledLocally,
                                isEnabled = isEnabled,
                                localPath = if (isInstalledLocally) localFile.absolutePath else null
                            )
                        )
                    }
                }
            }

            val meta = RepositoryMeta(
                name = name,
                description = description,
                manifestVersion = manifestVersion,
                pluginLists = pluginListUrls,
                iconUrl = iconUrl
            )

            // Save repo url to preferences
            saveRepositoryUrl(context, resolvedUrl)

            Result.success(Pair(meta, extensions))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Downloads .cs3 binary and verifies SHA-256 hash.
     */
    suspend fun downloadExtension(
        context: Context,
        item: ExtensionItem
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            if (item.url.isEmpty()) {
                return@withContext Result.failure(Exception("Plugin download URL is missing"))
            }

            val extDir = getExtensionDir(context)
            val destFile = File(extDir, "${item.internalName}.cs3")
            val tempFile = File(context.cacheDir, "${item.internalName}.tmp")

            val request = Request.Builder().url(item.url).build()
            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Download failed: HTTP ${response.code}"))
            }

            val responseBody = response.body ?: return@withContext Result.failure(Exception("Empty download body"))

            responseBody.byteStream().use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            // Verify hash if specified
            if (!item.fileHash.isNullOrEmpty()) {
                val computedHash = calculateSha256(tempFile)
                if (computedHash != item.fileHash) {
                    tempFile.delete()
                    return@withContext Result.failure(Exception("SHA-256 mismatch for ${item.name}!"))
                }
            }

            // Move to final location
            if (destFile.exists()) {
                destFile.delete()
            }
            tempFile.renameTo(destFile)

            // Mark installed in preferences
            getPrefs(context).edit()
                .putBoolean("$KEY_INSTALLED_PREFIX${item.internalName}", true)
                .putBoolean("$KEY_ENABLED_PREFIX${item.internalName}", true)
                .apply()

            Result.success(destFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes extension .cs3 binary from local storage.
     */
    fun uninstallExtension(context: Context, internalName: String) {
        val extDir = getExtensionDir(context)
        val file = File(extDir, "$internalName.cs3")
        if (file.exists()) {
            file.delete()
        }
        getPrefs(context).edit()
            .remove("$KEY_INSTALLED_PREFIX$internalName")
            .remove("$KEY_ENABLED_PREFIX$internalName")
            .apply()
    }

    /**
     * Toggles an installed extension between enabled and disabled state.
     */
    fun setExtensionEnabled(context: Context, internalName: String, enabled: Boolean) {
        getPrefs(context).edit()
            .putBoolean("$KEY_ENABLED_PREFIX$internalName", enabled)
            .apply()
    }

    private fun saveRepositoryUrl(context: Context, url: String) {
        val prefs = getPrefs(context)
        val set = prefs.getStringSet(KEY_REPOS, emptySet())?.toMutableSet() ?: mutableSetOf()
        set.add(url)
        prefs.edit().putStringSet(KEY_REPOS, set).apply()
    }

    fun removeRepositoryUrl(context: Context, url: String) {
        val prefs = getPrefs(context)
        val set = prefs.getStringSet(KEY_REPOS, emptySet())?.toMutableSet() ?: mutableSetOf()
        set.remove(url)
        prefs.edit().putStringSet(KEY_REPOS, set).apply()
    }

    fun getSavedRepositoryUrls(context: Context): Set<String> {
        return getPrefs(context).getStringSet(KEY_REPOS, emptySet()) ?: emptySet()
    }
}
