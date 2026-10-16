package com.mrx7014.ytdlp.domain

import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidYtDlpEngine : YtDlpEngine {
    override suspend fun inspect(url: String): Result<VideoInfo> = withContext(Dispatchers.IO) { runCatching {
        require(url.startsWith("http://") || url.startsWith("https://")) { "Enter a valid http(s) URL" }
        val raw = YoutubeDL.getInstance().getInfo(url)
        val formats = (raw.formats ?: arrayListOf()).mapNotNull { f ->
            val id = f.formatId ?: return@mapNotNull null
            val video = f.vcodec != null && f.vcodec != "none"
            val audio = f.acodec != null && f.acodec != "none"
            if (!video && !audio) return@mapNotNull null
            val kind = if (video) FormatKind.VIDEO else FormatKind.AUDIO
            val label = if (video) "${f.height.takeIf { it > 0 }?.let { "${it}p" } ?: f.formatNote ?: "video"} • ${f.ext ?: "file"}" else "${f.abr.takeIf { it > 0 } ?: f.tbr} kbps • ${f.ext ?: "audio"}"
            VideoFormat(id, label, kind, f.ext ?: "mp4", f.height.takeIf { it > 0 }, f.fileSize.takeIf { it > 0 })
        }.distinctBy { it.id }.sortedWith(compareByDescending<VideoFormat> { it.kind == FormatKind.VIDEO }.thenByDescending { it.height ?: 0 })
        VideoInfo(raw.title ?: "Untitled", raw.uploader ?: "Unknown uploader", raw.duration.toLong(), raw.thumbnail, formats)
    } }
}
