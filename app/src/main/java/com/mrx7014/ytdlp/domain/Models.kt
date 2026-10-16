package com.mrx7014.ytdlp.domain

data class VideoFormat(val id: String, val label: String, val kind: FormatKind, val ext: String)
enum class FormatKind { VIDEO, AUDIO }
data class VideoInfo(val title: String, val uploader: String, val durationSeconds: Long, val thumbnail: String?, val formats: List<VideoFormat>)
data class DownloadRequest(val url: String, val formatId: String, val startSeconds: Long?, val endSeconds: Long?)
data class DownloadRecord(val id: Long, val title: String, val url: String, val format: String, val range: String, val status: String, val createdAt: Long)
interface YtDlpEngine { suspend fun inspect(url: String): Result<VideoInfo>; suspend fun download(request: DownloadRequest, onProgress: (Int) -> Unit): Result<String> }
class MockYtDlpEngine : YtDlpEngine {
    override suspend fun inspect(url: String) = if (url.isBlank()) Result.failure(IllegalArgumentException("URL is empty")) else Result.success(VideoInfo("Sample video", "yt-dlp", 754, null, listOf(VideoFormat("best", "Best available • MP4", FormatKind.VIDEO, "mp4"), VideoFormat("720p", "720p • MP4", FormatKind.VIDEO, "mp4"), VideoFormat("bestaudio", "Best audio • M4A", FormatKind.AUDIO, "m4a"), VideoFormat("128k", "128 kbps • MP3", FormatKind.AUDIO, "mp3")))
    override suspend fun download(request: DownloadRequest, onProgress: (Int) -> Unit): Result<String> { for (p in 0..100 step 10) { kotlinx.coroutines.delay(80); onProgress(p) }; return Result.success("/Downloads/${request.formatId}.${if (request.formatId.contains("audio")) "m4a" else "mp4"}") }
}
