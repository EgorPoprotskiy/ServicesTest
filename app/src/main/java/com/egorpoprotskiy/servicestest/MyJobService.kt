package com.egorpoprotskiy.servicestest

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MyJobService : JobService() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        log("onStartCommand")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            coroutineScope.launch {
                var workItem = params?.dequeueWork()
                while (workItem != null) {
                    val page = workItem.intent.getIntExtra(PAGE, 0)
                        for (i in 0 until 5) {
                            delay(1000)
                            log("Timer $i $page")
                        }
                        params?.completeWork(workItem)
                        workItem = params?.dequeueWork()
                    }
                    //вызывается, если требуется перезапуск сервиса, true - требуется, false - не требуется
                    jobFinished(params, false)
                }
            }
        //если сервис закончил работу и он больше не требуется, то true
        //если нам надо, чтобы он продолжил работу, то false
        return true
    }

    //вызывается только если система сама убила сервис
    override fun onStopJob(params: JobParameters?): Boolean {
        log("onStopJob")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "myJobService: $message")
    }

    companion object {
        const val JOB_ID = 111
        private const val PAGE = "page"
        fun newIntent(page: Int): Intent {
            return Intent().apply {
                putExtra(PAGE, page)
            }
        }
    }
}