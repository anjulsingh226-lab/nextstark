package com.nextstark.app

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private lateinit var listContainer: LinearLayout
    private lateinit var statusText: TextView
    private lateinit var swipe: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 32)
        }

        root.addView(TextView(this).apply {
            text = "NEXT STARK"
            textSize = 26f
            setTypeface(typeface, Typeface.BOLD)
        })
        statusText = TextView(this).apply {
            text = "List load ho rahi hai..."
            textSize = 13f
            setTextColor(Color.GRAY)
            setPadding(0, 8, 0, 24)
        }
        root.addView(statusText)

        listContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(listContainer)

        swipe = SwipeRefreshLayout(this).apply { addView(root) }
        swipe.setOnRefreshListener { fetchFresh(showToastOnFail = true) }
        setContentView(swipe)

        // Pehle cache se turant dikhao (agar pehle se hai), fir fresh fetch karo
        renderList(SiteRepository.loadCached(this))
        fetchFresh(showToastOnFail = false)
    }

    private fun fetchFresh(showToastOnFail: Boolean) {
        swipe.isRefreshing = true
        SiteRepository.refresh(this) { sites, error ->
            swipe.isRefreshing = false
            if (sites != null) {
                statusText.text = "Updated"
                renderList(sites)
            } else {
                statusText.text = "Live list nahi mil payi — internet check karo (purani list dikhayi ja rahi hai)"
                if (showToastOnFail) {
                    android.widget.Toast.makeText(this, "Refresh fail ho gaya", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun renderList(sites: List<Site>) {
        listContainer.removeAllViews()
        if (sites.isEmpty()) {
            listContainer.addView(TextView(this).apply {
                text = "Abhi koi website set up nahi hai."
                setPadding(0, 32, 0, 0)
            })
            return
        }
        for (site in sites) {
            listContainer.addView(Button(this).apply {
                text = site.name
                setOnClickListener {
                    startActivity(Intent(this@MainActivity, WebViewActivity::class.java).apply {
                        putExtra(WebViewActivity.EXTRA_URL, site.url)
                        putExtra(WebViewActivity.EXTRA_TITLE, site.name)
                    })
                }
            }, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 16 })
        }
    }
}
