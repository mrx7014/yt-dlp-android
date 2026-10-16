package com.mrx7014.ytdlp.data

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mrx7014.ytdlp.domain.FormatKind
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class YtDlpDownloadWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val db=AppDatabase.get(applicationContext); val dao=db.downloads(); val recordId=inputData.getLong(KEY_RECORD_ID,-1)
        val url=inputData.getString(KEY_URL) ?: return@withContext Result.failure(); val format=inputData.getString(KEY_FORMAT) ?: "best"; val kind=FormatKind.valueOf(inputData.getString(KEY_KIND) ?: "VIDEO")
        val start=inputData.getLong(KEY_START,-1).takeIf{it>=0}; val end=inputData.getLong(KEY_END,-1).takeIf{it>=0}
        val folder=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "YtDlp").apply{mkdirs()}
        try { dao.updateStatus(recordId,"Downloading")
            val out=File(folder,"%(title)s.%(ext)s").absolutePath; val req=YoutubeDLRequest(url).apply { addOption("-o",out); addOption("-f", if(kind==FormatKind.AUDIO) "${format}/bestaudio/best" else "$format/best"); addOption("--merge-output-format","mp4"); addOption("--newline"); if(kind==FormatKind.AUDIO){addOption("-x");addOption("--audio-format","mp3")}; if(start!=null&&end!=null) addOption("--download-sections","*$start-$end") }
            val response=YoutubeDL.getInstance().execute(req, "record-$recordId") { progress,_,_-> setProgressAsync(androidx.work.workDataOf("progress" to progress.toInt())) }
            if(response.exitCode==0){dao.updateStatus(recordId,"Completed",folder.absolutePath); Result.success()} else {dao.updateStatus(recordId,"Failed"); Result.failure()}
        } catch (e: Exception) { dao.updateStatus(recordId,"Failed"); if(runAttemptCount<2) Result.retry() else Result.failure() }
    }
    companion object { const val KEY_RECORD_ID="record_id"; const val KEY_URL="url"; const val KEY_FORMAT="format"; const val KEY_KIND="kind"; const val KEY_START="start"; const val KEY_END="end" }
}
