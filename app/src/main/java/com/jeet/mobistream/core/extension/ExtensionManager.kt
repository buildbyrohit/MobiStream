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
    private const val KEY_REPOS_JSON = "saved_repositories_json_v2"
    private const val KEY_PLUGINS_PREFIX = "repo_plugins_"
    private const val KEY_INSTALLED_PREFIX = "installed_"
    private const val KEY_ENABLED_PREFIX = "enabled_"
    private const val KEY_SUB_PREFIX = "subproviders_"

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
     * Fetches repository metadata and its plugins list from remote network.
     */
    suspend fun fetchRepository(
        context: Context,
        repoUrl: String
    ): Result<RepositoryMeta> = withContext(Dispatchers.IO) {
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

            if (pluginListUrls.isEmpty()) {
                val baseUrl = resolvedUrl.substringBeforeLast("/")
                pluginListUrls.add("$baseUrl/plugins.json")
            }

            val extensions = mutableListOf<ExtensionItem>()
            val prefs = getPrefs(context)
            val extDir = getExtensionDir(context)

            for (pUrl in pluginListUrls) {
                try {
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

                            // Check if provider has configurable sub-providers (e.g. SK Tech has sports streams)
                            val isConfigurable = internalName.contains("SKTech", ignoreCase = true) ||
                                    internalName.contains("MovieBox", ignoreCase = true)

                            val subProviders = if (internalName.contains("SKTech", ignoreCase = true)) {
                                listOf("Star Sports Hindi HD", "Sony Sports Ten 1", "Sports18 1 HD", "Willow TV HD", "FanCode Live")
                            } else if (internalName.contains("MovieBox", ignoreCase = true)) {
                                listOf("Server 1 (Direct CDN)", "Server 2 (Fast Stream)", "Server 3 (HLS Mirror)")
                            } else emptyList()

                            val enabledSubs = prefs.getStringSet("$KEY_SUB_PREFIX$internalName", subProviders.toSet())?.toList() ?: subProviders

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
                                    localPath = if (isInstalledLocally) localFile.absolutePath else null,
                                    hasSubProviders = isConfigurable,
                                    subProviders = subProviders,
                                    enabledSubProviders = enabledSubs
                                )
                            )
                        }
                    }
                } catch (_: Exception) {}
            }

            val meta = RepositoryMeta(
                id = resolvedUrl,
                name = name,
                description = description,
                manifestVersion = manifestVersion,
                pluginLists = pluginListUrls,
                url = resolvedUrl,
                iconUrl = iconUrl,
                plugins = extensions,
                providerCount = extensions.size
            )

            // Save repo metadata and plugins locally
            saveRepositoryMeta(context, meta)

            Result.success(meta)
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

            if (!item.fileHash.isNullOrEmpty()) {
                val computedHash = calculateSha256(tempFile)
                if (computedHash != item.fileHash) {
                    tempFile.delete()
                    return@withContext Result.failure(Exception("SHA-256 mismatch for ${item.name}!"))
                }
            }

            if (destFile.exists()) {
                destFile.delete()
            }
            tempFile.renameTo(destFile)

            getPrefs(context).edit()
                .putBoolean("$KEY_INSTALLED_PREFIX${item.internalName}", true)
                .putBoolean("$KEY_ENABLED_PREFIX${item.internalName}", true)
                .apply()

            Result.success(destFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

    fun setExtensionEnabled(context: Context, internalName: String, enabled: Boolean) {
        getPrefs(context).edit()
            .putBoolean("$KEY_ENABLED_PREFIX$internalName", enabled)
            .apply()
    }

    fun setSubProviderEnabled(context: Context, internalName: String, subProvider: String, enabled: Boolean) {
        val prefs = getPrefs(context)
        val current = prefs.getStringSet("$KEY_SUB_PREFIX$internalName", emptySet())?.toMutableSet() ?: mutableSetOf()
        if (enabled) {
            current.add(subProvider)
        } else {
            current.remove(subProvider)
        }
        prefs.edit().putStringSet("$KEY_SUB_PREFIX$internalName", current).apply()
    }

    /**
     * Retrieves all saved repositories.
     */
    fun getSavedRepositories(context: Context): List<RepositoryMeta> {
        val prefs = getPrefs(context)
        val jsonStr = prefs.getString(KEY_REPOS_JSON, null) ?: return emptyList()
        val list = mutableListOf<RepositoryMeta>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val id = obj.getString("id")
                val name = obj.getString("name")
                val desc = obj.optString("description", "")
                val ver = obj.optInt("manifestVersion", 1)
                val url = obj.getString("url")
                val icon = if (obj.has("iconUrl") && !obj.isNull("iconUrl")) obj.getString("iconUrl") else null
                val count = obj.optInt("providerCount", 0)

                list.add(
                    RepositoryMeta(
                        id = id,
                        name = name,
                        description = desc,
                        manifestVersion = ver,
                        pluginLists = emptyList(),
                        url = url,
                        iconUrl = icon,
                        providerCount = count
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    /**
     * Saves repository meta and plugins into SharedPreferences.
     */
    fun saveRepositoryMeta(context: Context, meta: RepositoryMeta) {
        val current = getSavedRepositories(context).toMutableList()
        current.removeAll { it.url == meta.url }
        current.add(meta)

        val arr = JSONArray()
        for (r in current) {
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("name", r.name)
            obj.put("description", r.description)
            obj.put("manifestVersion", r.manifestVersion)
            obj.put("url", r.url)
            obj.put("iconUrl", r.iconUrl)
            obj.put("providerCount", if (r.url == meta.url) meta.providerCount else r.providerCount)
            arr.put(obj)
        }

        val prefsEdit = getPrefs(context).edit()
        prefsEdit.putString(KEY_REPOS_JSON, arr.toString())

        // Save plugins if present
        if (meta.plugins.isNotEmpty()) {
            val pluginsArr = JSONArray()
            for (p in meta.plugins) {
                val pObj = JSONObject()
                pObj.put("internalName", p.internalName)
                pObj.put("name", p.name)
                pObj.put("description", p.description)
                pObj.put("url", p.url)
                pObj.put("version", p.version)
                pObj.put("language", p.language)
                pObj.put("iconUrl", p.iconUrl)
                pObj.put("fileSize", p.fileSize)
                pObj.put("fileHash", p.fileHash)
                pObj.put("hasSubProviders", p.hasSubProviders)

                val authorsArr = JSONArray()
                p.authors.forEach { authorsArr.put(it) }
                pObj.put("authors", authorsArr)

                val subsArr = JSONArray()
                p.subProviders.forEach { subsArr.put(it) }
                pObj.put("subProviders", subsArr)

                pluginsArr.put(pObj)
            }
            prefsEdit.putString("$KEY_PLUGINS_PREFIX${meta.url}", pluginsArr.toString())
        }

        prefsEdit.apply()
    }

    /**
     * Gets plugins for a repository from local cache with live install/enable states.
     */
    fun getCachedPlugins(context: Context, repoUrl: String): List<ExtensionItem> {
        val prefs = getPrefs(context)
        val extDir = getExtensionDir(context)
        val jsonStr = prefs.getString("$KEY_PLUGINS_PREFIX$repoUrl", null) ?: return emptyList()
        val list = mutableListOf<ExtensionItem>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val internalName = obj.getString("internalName")
                val localFile = File(extDir, "$internalName.cs3")
                val isInstalled = localFile.exists() && prefs.getBoolean("$KEY_INSTALLED_PREFIX$internalName", false)
                val isEnabled = prefs.getBoolean("$KEY_ENABLED_PREFIX$internalName", true)

                val authorsArr = obj.optJSONArray("authors") ?: JSONArray()
                val authors = mutableListOf<String>()
                for (a in 0 until authorsArr.length()) {
                    authors.add(authorsArr.getString(a))
                }

                val subsArr = obj.optJSONArray("subProviders") ?: JSONArray()
                val subProviders = mutableListOf<String>()
                for (s in 0 until subsArr.length()) {
                    subProviders.add(subsArr.getString(s))
                }

                val enabledSubs = prefs.getStringSet("$KEY_SUB_PREFIX$internalName", subProviders.toSet())?.toList() ?: subProviders

                list.add(
                    ExtensionItem(
                        internalName = internalName,
                        name = obj.getString("name"),
                        description = obj.optString("description", ""),
                        url = obj.getString("url"),
                        version = obj.optInt("version", 1),
                        language = obj.optString("language", "hi"),
                        iconUrl = if (obj.has("iconUrl") && !obj.isNull("iconUrl")) obj.getString("iconUrl") else null,
                        fileSize = obj.optLong("fileSize", 0L),
                        fileHash = if (obj.has("fileHash") && !obj.isNull("fileHash")) obj.getString("fileHash") else null,
                        authors = authors,
                        isInstalled = isInstalled,
                        isEnabled = isEnabled,
                        localPath = if (isInstalled) localFile.absolutePath else null,
                        hasSubProviders = obj.optBoolean("hasSubProviders", false),
                        subProviders = subProviders,
                        enabledSubProviders = enabledSubs
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    /**
     * Removes a repository and its cached plugins.
     */
    fun removeRepository(context: Context, repoUrl: String) {
        val current = getSavedRepositories(context).filter { it.url != repoUrl }
        val arr = JSONArray()
        for (r in current) {
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("name", r.name)
            obj.put("description", r.description)
            obj.put("manifestVersion", r.manifestVersion)
            obj.put("url", r.url)
            obj.put("iconUrl", r.iconUrl)
            obj.put("providerCount", r.providerCount)
            arr.put(obj)
        }
        getPrefs(context).edit()
            .putString(KEY_REPOS_JSON, arr.toString())
            .remove("$KEY_PLUGINS_PREFIX$repoUrl")
            .apply()
    }

    fun hasInstalledExtensions(context: Context): Boolean {
        val extDir = getExtensionDir(context)
        val files = extDir.listFiles { _, name -> name.endsWith(".cs3") }
        return !files.isNullOrEmpty()
    }

    /**
     * Returns list of all installed and enabled extensions across all repos.
     */
    fun getInstalledExtensions(context: Context): List<ExtensionItem> {
        val repos = getSavedRepositories(context)
        val installed = mutableListOf<ExtensionItem>()
        for (repo in repos) {
            val plugins = getCachedPlugins(context, repo.url)
            installed.addAll(plugins.filter { it.isInstalled })
        }
        return installed
    }
}
