package com.example.spotwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * Implementation of App Widget functionality.
 */
class Wledpower : AppWidgetProvider() {

    companion object {
        const val POWER_CLICK = "com.example.spotwidget.POWER_CLICK"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateWledWidget(context, appWidgetManager, appWidgetId)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            POWER_CLICK -> {
                GlobalScope.launch(Dispatchers.IO) {
                    val sharedPreferences = context.getSharedPreferences("WledPrefs", Context.MODE_PRIVATE)
                    val ipAddress = sharedPreferences.getString("WLED_IP", null)
                    val url = "http://$ipAddress/json/state"
                    val json = "{\"on\":\"t\",\"v\":true}"
                    val mediaType = "application/json; charset=utf-8".toMediaType()
                    val requestBody = json.toRequestBody(mediaType)

                    Log.d("url", url)

                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build()

                    try {
                        val response = client.newCall(request).execute()
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            Log.d("response", responseBody)
                        }
                    } catch (e: IOException) {
                        Log.d("response", e.toString())
                    }
                }
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateWledWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.wledpower)

    val intentPower = Intent(context, Wledpower::class.java).apply {
        action = Wledpower.POWER_CLICK
    }
    val pendingIntentPower = PendingIntent.getBroadcast(context, 0, intentPower, PendingIntent.FLAG_IMMUTABLE)
    views.setOnClickPendingIntent(R.id.power, pendingIntentPower)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}