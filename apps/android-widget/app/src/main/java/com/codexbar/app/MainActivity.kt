package com.codexbar.app

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.Slider

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("CodexPrefs", Context.MODE_PRIVATE)
        
        // Refresh Interval
        val refreshSlider = findViewById<Slider>(R.id.refresh_slider)
        val refreshText = findViewById<TextView>(R.id.refresh_value)
        
        refreshSlider.value = prefs.getInt("refresh_minutes", 15).toFloat()
        updateRefreshText(refreshText, refreshSlider.value.toInt())

        refreshSlider.addOnChangeListener { _, value, _ ->
            val mins = value.toInt()
            updateRefreshText(refreshText, mins)
            prefs.edit().putInt("refresh_minutes", mins).apply()
        }

        // Thresholds
        val yellowSlider = findViewById<Slider>(R.id.yellow_threshold)
        val redSlider = findViewById<Slider>(R.id.red_threshold)

        yellowSlider.value = prefs.getInt("yellow_pct", 70).toFloat()
        redSlider.value = prefs.getInt("red_pct", 90).toFloat()

        yellowSlider.addOnChangeListener { _, value, _ ->
            prefs.edit().putInt("yellow_pct", value.toInt()).apply()
        }
        redSlider.addOnChangeListener { _, value, _ ->
            prefs.edit().putInt("red_pct", value.toInt()).apply()
        }

        // Manual Refresh Button
        findViewById<FloatingActionButton>(R.id.fab_refresh).setOnClickListener {
            forceWidgetUpdate()
            Toast.makeText(this, "Aggiornamento richiesto...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateRefreshText(tv: TextView, mins: Int) {
        tv.text = if (mins == 1) "Ogni minuto" else "Ogni $mins minuti"
    }

    private fun forceWidgetUpdate() {
        val intent = Intent(this, CodexWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(application)
            .getAppWidgetIds(ComponentName(application, CodexWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }
}
