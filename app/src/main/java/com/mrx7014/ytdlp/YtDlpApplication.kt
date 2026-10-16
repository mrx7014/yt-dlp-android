package com.mrx7014.ytdlp

import android.app.Application
import android.util.Log
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.FFmpeg

class YtDlpApplication : Application() {
    override fun onCreate() { super.onCreate(); try { YoutubeDL.getInstance().init(this); FFmpeg.getInstance().init(this) } catch (e: Exception) { Log.e("YtDlp", "yt-dlp initialization failed", e) } }
}
