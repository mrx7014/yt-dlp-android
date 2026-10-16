package com.mrx7014.ytdlp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.mrx7014.ytdlp.data.*
import com.mrx7014.ytdlp.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class HomeUiState(
    val url: String = "",
    val info: VideoInfo? = null,
    val selectedFormat: VideoFormat? = null,
    val start: String = "",
    val end: String = "",
    val loading: Boolean = false,
    val progress: Int = 0,
    val error: String? = null,
    val downloads: List<DownloadRecordEntity> = emptyList()
)

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val engine: YtDlpEngine = AndroidYtDlpEngine()
    private val dao = AppDatabase.get(app).downloads()
    private val wm = WorkManager.getInstance(app)
    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    init { viewModelScope.launch { dao.observe().collect { rows -> _state.update { it.copy(downloads = rows) } } } }
    fun setUrl(v: String) = _state.update { it.copy(url = v, error = null, info = null) }
    fun setStart(v: String) = _state.update { it.copy(start = v) }
    fun setEnd(v: String) = _state.update { it.copy(end = v) }
    fun select(f: VideoFormat) = _state.update { it.copy(selectedFormat = f) }

    fun inspect() = viewModelScope.launch {
        val current = _state.value
        _state.update { it.copy(loading = true, error = null) }
        engine.inspect(current.url)
            .onSuccess { info -> _state.update { it.copy(info = info, selectedFormat = info.formats.firstOrNull(), loading = false) } }
            .onFailure { e -> _state.update { it.copy(loading = false, error = e.message ?: "Failed to read video") } }
    }

    fun download() = viewModelScope.launch(Dispatchers.IO) {
        val current = _state.value
        val format = current.selectedFormat ?: return@launch
        val start = current.start.toTime()
        val end = current.end.toTime()
        if (start != null && end != null && end <= start) { _state.update { it.copy(error = "End time must be after start time") }; return@launch }
        val id = dao.insert(DownloadRecordEntity(title = current.info?.title ?: current.url, url = current.url, format = format.label, range = rangeText(start, end), status = "Queued", createdAt = System.currentTimeMillis()))
        val data = workDataOf(YtDlpDownloadWorker.KEY_RECORD_ID to id, YtDlpDownloadWorker.KEY_URL to current.url, YtDlpDownloadWorker.KEY_FORMAT to format.id, YtDlpDownloadWorker.KEY_KIND to format.kind.name, YtDlpDownloadWorker.KEY_START to (start ?: -1L), YtDlpDownloadWorker.KEY_END to (end ?: -1L))
        val request = OneTimeWorkRequestBuilder<YtDlpDownloadWorker>().setInputData(data).setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS).build()
        wm.enqueue(request)
        _state.update { it.copy(error = null) }
    }

    private fun String.toTime(): Long? = trim().takeIf { it.isNotEmpty() }?.split(":")?.mapNotNull { it.toLongOrNull() }?.let { p -> when (p.size) { 1 -> p[0]; 2 -> p[0] * 60 + p[1]; 3 -> p[0] * 3600 + p[1] * 60 + p[2]; else -> null } }
    private fun rangeText(a: Long?, b: Long?) = if (a == null && b == null) "Full video" else "${a ?: 0}s → ${b ?: "end"}"
}
