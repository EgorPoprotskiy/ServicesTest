package com.egorpoprotskiy.servicestest

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class MyWorker(context: Context, private val workerParameters: WorkerParameters): Worker(context, workerParameters) {
    override fun doWork(): Result {
        log("doWork")
        val page = workerParameters.inputData.getInt(PAGE, 0)
        for (i in 0 until 5) {
            Thread.sleep(1000)
            log("Timer $i $page")
        }
        //Метод должен что-то вернуть
        //success - все прошло успешно и сервис завершил свою работу
        //failure - что-то пошло не так(завершился с исключением), перезапущен НЕ будет
        //retry - что-то пошло не так(завершился с исключением), Будет перезапущен
        return Result.success()
    }

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyWorker: $message")
    }

    companion object {
        private const val PAGE = "page"

        const val WORKNAME = "work name"

        fun makeRequet(page: Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<MyWorker>().apply {
                //указываем номер страницы
                setInputData(workDataOf(PAGE to page))
                //Установка ограничений
                setConstraints(makeConstraints())
            }.build()
        }

        private fun makeConstraints(): Constraints = Constraints.Builder()
                //Работает, только если телефон на зарядке
            .setRequiresCharging(true)
            .build()
    }
}