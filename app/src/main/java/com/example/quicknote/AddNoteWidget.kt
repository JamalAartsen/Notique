package com.example.quicknote

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.quicknote.Activity.AddNote

/**
 * Implementation of App Widget functionality.
 */
class AddNoteWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->

            val pendingIntent: PendingIntent = Intent(context, AddNote::class.java).let { intent ->
                PendingIntent.getActivity(context, 0, intent, 0)
            }

            val views: RemoteViews = RemoteViews(context.packageName, R.layout.add_note_widget).apply {
                setOnClickPendingIntent(R.id.add_note_widget_button, pendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}