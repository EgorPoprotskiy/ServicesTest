package com.egorpoprotskiy.servicestest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.egorpoprotskiy.servicestest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var page = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.simpleService.setOnClickListener {
            //остановка сервиса снаружи(по нажатию кнопки)
            stopService(MyForegroundService.newIntent(this))
            //функция запуска сервиса
            startService(MyService.newIntent(this, 25))
        }
        binding.foregroundService.setOnClickListener {
            //вызов функции для показа уведомлений
//            showNotification()
            //ContextCompat - проверяет версию API(если >=26, то вызывает функцию startForegroundService, если <26, то функия startService)
            ContextCompat.startForegroundService(this, MyForegroundService.newIntent(this))
        }
        binding.intentService.setOnClickListener {
            ContextCompat.startForegroundService(this, MyIntentService.newIntent(this))
        }
        binding.jobScheduler.setOnClickListener {
            val componentName = ComponentName(this, MyJobService::class.java)
            val jobInfo = JobInfo.Builder(MyJobService.JOB_ID, componentName)
                    //Условие, работать только на зарядке
                .setRequiresCharging(true)
                    //Условие, только при работе Wi-Fi
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build()
            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            //проверка на версию API
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = MyJobService.newIntent(page++)
                jobScheduler.enqueue(jobInfo, JobWorkItem(intent))
            }
        }
    }
//    //Уведомление пользователю
//    private fun showNotification() {
//        //получение экземпляра для получения уведомления.
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        //Создание канала для Андроид с API>=26
//        //проверка, если версия андроид>8
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationChannel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                //Приоритет уведомления
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//                //Установка Заголовока
//            .setContentTitle("Title")
//                //Установка Текста
//            .setContentText("Text")
//                //Установка иконки
//            .setSmallIcon(R.drawable.ic_launcher_background)
//            .build()
//        //Отображение уведомления, через созданный экземпляр(Только для API>=26)(Добавить строку в манифест при версии Android>13)
//        //<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
//        //id - для показа количества уведомлений.
//        notificationManager.notify(1, notification)
//    }
//    companion object {
//        private const val CHANNEL_ID = "channel_id"
//        private const val CHANNEL_NAME = "channel_name"
//    }
}