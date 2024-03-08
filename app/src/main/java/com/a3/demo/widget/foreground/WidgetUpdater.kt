package com.a3.demo.widget.foreground

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WidgetUpdater(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(
    context,
    params
) {
    override suspend fun doWork(): Result {
        if (instances > 0) {
            Log.d(TAG, "Another instance of WidgetUpdater is already running")
            return Result.success()
        }
        instances++
        while (true) {
            Log.d(TAG, "WidgetUpdater instances: $instances")
            isWidgetUpdating = true
            val widgetIntent = Intent(context, CounterWidget::class.java)
            widgetIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, CounterWidget::class.java))
            widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(widgetIntent)
            Log.d(TAG, "WidgetUpdater: updating widget")
            Thread.sleep(1000)
        }
        return Result.success()
    }



    companion object {
        private const val TAG = "WidgetUpdater"
        var isWidgetUpdating = false
        var instances: Int = 0
    }


}