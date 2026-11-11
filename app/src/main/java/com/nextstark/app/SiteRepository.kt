package com.nextstark.app

import android.content.Context
import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

data class Site(val name: String, val url: String)

/**
 * Website list is NOT hardcoded in the app. It is fetched every time the app
 * opens from a JSON file you control on GitHub (CONFIG_URL below). Add or
 * remove a site there, commit, and every user's app shows the updated list
 * next time they open it — no Play Store update, no new APK needed.
 *
 * Expected JSON format at CONFIG_URL:
 * [
 *   { "name": "My Course Site", "url": "https://example.com" },
 *   { "name": "Practice Tests", "url": "https://example.org" }
 * ]
 */
object SiteRepository {

    // TODO: isko apne GitHub repo ke sites.json ke "raw" link se replace karo.
    // Example: https://raw.githubusercontent.com/USERNAME/REPO/main/sites.json
    const val CONFIG_URL = "https://raw.githubusercontent.com/USERNAME/REPO/main/sites.json"

    private const val PREFS = "nextstark"
    private const val KEY_CACHE = "sites_cache_json"

    private val executor = Executors.newSingleThreadExecutor()
    private val main = Handler(Looper.getMainLooper())

    /** Pehle turant cache se dikhao (agar hai), phir background me fresh list fetch karo. */
    fun loadCached(context: Context): List<Site> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_CACHE, null)
            ?: return emptyList()
        return parse(raw)
    }

    fun refresh(context: Context, onResult: (List<Site>?, Throwable?) -> Unit) {
        executor.execute {
            try {
                // cache-busting query param taaki CDN/browser purani list na pakdaye
                val bustUrl = CONFIG_URL + (if (CONFIG_URL.contains("?")) "&" else "?") + "t=" + System.currentTimeMillis()
                val conn = URL(bustUrl).openConnection() as HttpURLConnection
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                conn.setRequestProperty("Cache-Control", "no-cache")
                val text = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                conn.disconnect()

                val sites = parse(text)
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit().putString(KEY_CACHE, text).apply()

                main.post { onResult(sites, null) }
            } catch (e: Exception) {
                main.post { onResult(null, e) }
            }
        }
    }

    private fun parse(json: String): List<Site> {
        val arr = JSONArray(json)
        val out = ArrayList<Site>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val name = obj.optString("name").trim()
            val url = obj.optString("url").trim()
            if (name.isNotEmpty() && url.isNotEmpty()) out.add(Site(name, url))
        }
        return out
    }
}
