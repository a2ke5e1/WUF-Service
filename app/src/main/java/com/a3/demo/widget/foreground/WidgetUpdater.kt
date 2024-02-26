package com.a3.demo.widget.foreground

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
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
        while (true) {
            isWidgetUpdating = true
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, CounterWidget::class.java)
            )

            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
            Thread.sleep(1000)
        }
        return Result.success()
    }

    companion object {
        private const val TAG = "WidgetUpdater"
        var isWidgetUpdating = false
    }


}