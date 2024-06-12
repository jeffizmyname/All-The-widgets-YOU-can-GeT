package com.example.spotwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.media.session.MediaController
import android.media.session.MediaSession
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews

class NowPlaying : AppWidgetProvider() {

    companion object {
        private const val TAG = "NowPlayingWidget"
        const val PREV_CLICK = "com.example.spotwidget.PREV_CLICK"
        const val PP_CLICK = "com.example.spotwidget.PP_CLICK"
        const val NEXT_CLICK = "com.example.spotwidget.NEXT_CLICK"
    }

    private var mediaSession: MediaSession? = null

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            PREV_CLICK -> {
                Log.d(TAG, "Previous button clicked")
                mediaSession?.controller?.transportControls?.skipToPrevious()
            }
            PP_CLICK -> {
                Log.d(TAG, "Play/Pause button clicked")
                val controller = mediaSession?.controller
                val playbackState = controller?.playbackState
                if (playbackState != null) {
                    when (playbackState.state) {
                        android.media.session.PlaybackState.STATE_PLAYING -> controller.transportControls.pause()
                        android.media.session.PlaybackState.STATE_PAUSED -> controller.transportControls.play()
                        else -> Log.d(TAG, "Play/Pause button clicked, but not playing or paused")
                    }
                }
            }
            NEXT_CLICK -> {
                Log.d(TAG, "Next button clicked")
                mediaSession?.controller?.transportControls?.skipToNext()
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        initializeMediaSession(context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        mediaSession?.release()
    }

    private fun initializeMediaSession(context: Context) {
        mediaSession = MediaSession(context, "NowPlayingWidget")
        mediaSession?.isActive = true
    }

}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val views = RemoteViews(context.packageName, R.layout.now_playing)

    val intentPrev = Intent(context, NowPlaying::class.java).apply {
        action = NowPlaying.PREV_CLICK
    }
    val pendingIntentPrev = PendingIntent.getBroadcast(context, 0, intentPrev, PendingIntent.FLAG_IMMUTABLE)
    views.setOnClickPendingIntent(R.id.prev, pendingIntentPrev)

    val intentPP = Intent(context, NowPlaying::class.java).apply {
        action = NowPlaying.PP_CLICK
    }
    val pendingIntentPP = PendingIntent.getBroadcast(context, 0, intentPP, PendingIntent.FLAG_IMMUTABLE)
    views.setOnClickPendingIntent(R.id.pp, pendingIntentPP)

    val intentNext = Intent(context, NowPlaying::class.java).apply {
        action = NowPlaying.NEXT_CLICK
    }
    val pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_IMMUTABLE)
    views.setOnClickPendingIntent(R.id.next, pendingIntentNext)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}
