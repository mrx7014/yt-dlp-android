package com.mrx7014.ytdlp

import android.app.Application
import android.util.Log
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL

class YtDlpApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Native initialization may throw UnsatisfiedLinkError (an Error, not an Exception)
        // on devices where the ABI/native loader cannot initialize. Never let that kill startup.
        try {
            YoutubeDL.getInstance().init(this)
        } catch (t: Throwable) {
            Log.e(TAG, "yt-dlp initialization failed", t)
        }

        try {
            FFmpeg.getInstance().init(this)
        } catch (t: Throwable) {
            Log.e(TAG, "FFmpeg initialization failed", t)
        }
    }

    private companion object {
        const val TAG = "YtDlp"
    }
}
