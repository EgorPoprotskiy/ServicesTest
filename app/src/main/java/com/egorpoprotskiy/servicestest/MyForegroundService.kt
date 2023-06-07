package com.egorpoprotskiy.servicestest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MyForegroundService: Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")
        coroutineScope.launch {
            for (i in 0 until 100) {
                delay(1000)
                log("Timer $i")
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyForegroundService: $message")
    }
    //Создание канала для уведомления
    private fun createNotificationChannel() {
        //получение экземпляра для получения уведомления.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //Создание канала для Андроид с API>=26
        //проверка, если версия андроид>8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                //Приоритет уведомления
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
    //Создание уведомления
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            //Установка Заголовока
            .setContentTitle("Title")
            //Установка Текста
            .setContentText("Text")
            //Установка иконки
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MyForegroundService::class.java)
        }

        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
        private const val NOTIFICATION_ID = 1
    }

}