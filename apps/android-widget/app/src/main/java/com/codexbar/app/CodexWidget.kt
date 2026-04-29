package com.codexbar.app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.RemoteViews
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class CodexWidget : AppWidgetProvider() {

    // URL AUTENTICATO
    private val SCRIPT_URL = "https://script.google.com/macros/s/AKfycbxSHtPyLdhCq-5wPe1KC3mHNN5enTnhB1KodJSeln4IZeuPY-V3S_ovLvhyJ1I2EhD5/exec?token=bKegWMZ2Tln3rv"

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            fetchData(context, appWidgetManager, appWidgetId)
        }
    }

    private fun fetchData(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(SCRIPT_URL)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("CodexWidget", "Network Error", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: ""
                Log.d("CodexWidget", "Received body: $body")
                
                if (!response.isSuccessful) {
                    Log.e("CodexWidget", "HTTP Error: ${response.code}")
                    return
                }

                try {
                    val snapshot = Gson().fromJson(body, WidgetSnapshot::class.java)
                    if (snapshot.entries.isEmpty()) {
                        Log.w("CodexWidget", "Snapshot entries are empty")
                    }
                    updateUI(context, manager, widgetId, snapshot)
                } catch (e: Exception) {
                    Log.e("CodexWidget", "JSON Parsing Error: ${e.message}", e)
                }
            }
        })
    }

    private fun updateUI(context: Context, manager: AppWidgetManager, widgetId: Int, snapshot: WidgetSnapshot) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        views.removeAllViews(R.id.providers_list)

        val prefs = context.getSharedPreferences("CodexPrefs", Context.MODE_PRIVATE)
        val yellowThr = prefs.getInt("yellow_pct", 70)
        val redThr = prefs.getInt("red_pct", 90)

        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val time = sdf.format(java.util.Date())
        views.setTextViewText(R.id.last_update_text, "Sync: $time")

        for (entry in snapshot.entries) {
            val itemRow = RemoteViews(context.packageName, R.layout.widget_item)
            itemRow.setTextViewText(R.id.provider_name, entry.provider.uppercase())

            // Primary (5h)
            val primaryUsage = entry.primary?.used_percent?.toInt() ?: 0
            itemRow.setProgressBar(R.id.usage_bar_primary, 100, primaryUsage, false)
            itemRow.setTextViewText(R.id.usage_text_primary, "$primaryUsage%")
            itemRow.setTextColor(R.id.usage_text_primary, getStatusColor(primaryUsage, yellowThr, redThr))

            // Secondary (W)
            if (entry.secondary != null) {
                val secondaryUsage = entry.secondary.used_percent.toInt()
                itemRow.setViewVisibility(R.id.secondary_row, android.view.View.VISIBLE)
                itemRow.setProgressBar(R.id.usage_bar_secondary, 100, secondaryUsage, false)
                itemRow.setTextViewText(R.id.usage_text_secondary, "$secondaryUsage%")
                itemRow.setTextColor(R.id.usage_text_secondary, getStatusColor(secondaryUsage, yellowThr, redThr))
            } else {
                itemRow.setViewVisibility(R.id.secondary_row, android.view.View.GONE)
            }

            views.addView(R.id.providers_list, itemRow)
        }

        manager.updateAppWidget(widgetId, views)
    }

    private fun getStatusColor(usage: Int, yellow: Int, red: Int): Int {
        return when {
            usage >= red -> Color.parseColor("#CF6679")
            usage >= yellow -> Color.parseColor("#FBC02D")
            else -> Color.parseColor("#03DAC6")
        }
    }
}
