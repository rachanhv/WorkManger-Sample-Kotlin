package com.kumbri.workmangersamplekotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object {
        const val KEY_COUNT_VALUE = "key_count"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        val textView = findViewById<TextView>(R.id.textView)
        button.setOnClickListener() {
            setPeriodicWorkRequest()
            //setOneTimeWorkRequest(textView)
        }
    }

    private fun setOneTimeWorkRequest(v: TextView) {
        val workmanager: WorkManager = WorkManager.getInstance(applicationContext)

        val data: Data = Data.Builder()
            .putInt(KEY_COUNT_VALUE, 125)
            .build()

        val constraints: Constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        val filterRequest = OneTimeWorkRequest.Builder(FilteringWorker::class.java)
            .build()
        val compressRequest = OneTimeWorkRequest.Builder(CompressingWorker::class.java)
            .build()
        val downloadRequest = OneTimeWorkRequest.Builder(DownloadingWorker::class.java)
            .build()
        val paralleWorkers = mutableListOf<OneTimeWorkRequest>()
        paralleWorkers.add(downloadRequest)
        paralleWorkers.add(filterRequest)

        workmanager
            .beginWith(paralleWorkers)
            .then(compressRequest)
            .then(uploadRequest)
            .enqueue()

        workmanager.getWorkInfoByIdLiveData(uploadRequest.id)
            .observe(this, Observer {
                v.text = it.state.name

                if (it.state.isFinished) {
                    val data: Data = it.outputData
                    val message: String? = data.getString(UploadWorker.KEY_WORKER)
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun setPeriodicWorkRequest() {
        val periodicWorkRequest: PeriodicWorkRequest = PeriodicWorkRequest
            .Builder(DownloadingWorker::class.java, 16, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(periodicWorkRequest)
    }

}