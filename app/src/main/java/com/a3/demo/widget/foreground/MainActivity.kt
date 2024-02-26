package com.a3.demo.widget.foreground

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.a3.demo.widget.foreground.ui.theme.WUFServiceTheme
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startServiceViaWorker()
        setContent {
            WUFServiceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        stopService()
        super.onDestroy()
    }

    fun startService() {
        Log.d(TAG, "startService called")
        if (!MyService.isServiceRunning) {
            val serviceIntent = Intent(this, MyService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }
    }

    fun stopService() {
        Log.d(TAG, "stopService called")
        if (MyService.isServiceRunning) {
            val serviceIntent = Intent(this, MyService::class.java)
            stopService(serviceIntent)
        }
    }

    fun startServiceViaWorker() {
        Log.d(TAG, "startServiceViaWorker called")
        val UNIQUE_WORK_NAME = "StartMyServiceViaWorker"
        val workManager = WorkManager.getInstance(this)

        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes
        // (same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
        val request: PeriodicWorkRequest = PeriodicWorkRequest.Builder(
            MyWorker::class.java,
            16,
            TimeUnit.MINUTES
        )
            .build()

        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
        // do check for AutoStart permission
        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun testWidgetUpdater() {
        val workManager = WorkManager.getInstance(this)
        val request = PeriodicWorkRequest.Builder(
            WidgetUpdater::class.java,
            15,
            TimeUnit.MINUTES
        )
            .build()
        workManager.enqueue(request)
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Column {
            Text(
                text = "Hello $name!",
                modifier = modifier
            )
            Button(onClick = {
                startService()
            }) {
                Text(text = "Start Service")
            }

            Button(onClick = { stopService() }) {
                Text(text = "Stop Service")
            }
            Button(onClick = { testWidgetUpdater() }) {
                Text(text = "Test Widget Updater")
            }
        }
    }

}
