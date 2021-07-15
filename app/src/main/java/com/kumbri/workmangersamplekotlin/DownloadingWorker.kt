package com.kumbri.workmangersamplekotlin

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.*

class DownloadingWorker(context:Context,params:WorkerParameters): Worker(context,params) {

    override fun doWork(): Result {
    try {

        for (i: Int in 0..300) {
            Log.i("MYTAG", "Downloading $i")
        }
        val time = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate : String = time.format(Date())
        Log.i("MYTAG", "Completed $currentDate")

        return Result.success()
    }catch (e:Exception){
        return Result.failure()
    }
    }
}