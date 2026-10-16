package com.mrx7014.ytdlp.domain

data class VideoFormat(val id: String, val label: String, val kind: FormatKind, val ext: String, val height: Int? = null, val sizeBytes: Long? = null)
enum class FormatKind { VIDEO, AUDIO }
data class VideoInfo(val title: String, val uploader: String, val durationSeconds: Long, val thumbnail: String?, val formats: List<VideoFormat>)
data class DownloadRequest(val url: String, val formatId: String, val kind: FormatKind, val startSeconds: Long?, val endSeconds: Long?, val outputDirectory: String)
interface YtDlpEngine { suspend fun inspect(url: String): Result<VideoInfo> }
