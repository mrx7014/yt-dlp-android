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

data class HomeUiState(val url="", val info:VideoInfo?=null, val selectedFormat:VideoFormat?=null, val start="", val end="", val loading=false, val progress=0, val error:String?=null, val downloads:List<DownloadRecordEntity> = emptyList())
class MainViewModel(app:Application):AndroidViewModel(app) { private val engine:YtDlpEngine=AndroidYtDlpEngine(); private val dao=AppDatabase.get(app).downloads(); private val wm=WorkManager.getInstance(app); private val _state=MutableStateFlow(HomeUiState()); val state=_state.asStateFlow(); init { viewModelScope.launch { dao.observe().collect{_state.update{ s->s.copy(downloads=it)}} } }
 fun setUrl(v:String)=_state.update{it.copy(url=v,error=null,info=null)}; fun setStart(v:String)=_state.update{it.copy(start=v)}; fun setEnd(v:String)=_state.update{it.copy(end=v)}; fun select(f:VideoFormat)=_state.update{it.copy(selectedFormat=f)}
 fun inspect(){ viewModelScope.launch { val s=_state.value; _state.update{it.copy(loading=true,error=null)}; engine.inspect(s.url).onSuccess{v->_state.update{it.copy(info=v,selectedFormat=v.formats.firstOrNull(),loading=false)}}.onFailure{e->_state.update{it.copy(loading=false,error=e.message ?: "Failed to read video")}} } }
 fun download(){ viewModelScope.launch(Dispatchers.IO) { val s=_state.value; val f=s.selectedFormat ?: return@launch; val st=s.start.toTime(); val en=s.end.toTime(); if(st!=null&&en!=null&&en<=st){_state.update{it.copy(error="End time must be after start time")};return@launch}; val id=dao.insert(DownloadRecordEntity(title=s.info?.title ?: s.url,url=s.url,format=f.label,range=rangeText(st,en),status="Queued",createdAt=System.currentTimeMillis())); val data=workDataOf(YtDlpDownloadWorker.KEY_RECORD_ID to id,YtDlpDownloadWorker.KEY_URL to s.url,YtDlpDownloadWorker.KEY_FORMAT to f.id,YtDlpDownloadWorker.KEY_KIND to f.kind.name,YtDlpDownloadWorker.KEY_START to (st ?: -1L),YtDlpDownloadWorker.KEY_END to (en ?: -1L)); val req=OneTimeWorkRequestBuilder<YtDlpDownloadWorker>().setInputData(data).setBackoffCriteria(BackoffPolicy.EXPONENTIAL,10,TimeUnit.SECONDS).build(); wm.enqueue(req); _state.update{it.copy(error=null)} } }
 private fun String.toTime():Long? = trim().takeIf{it.isNotEmpty()}?.split(":")?.mapNotNull{it.toLongOrNull()}?.let{p->when(p.size){1->p[0];2->p[0]*60+p[1];3->p[0]*3600+p[1]*60+p[2];else->null}}
 private fun rangeText(a:Long?,b:Long?)=if(a==null&&b==null)"Full video" else "${a ?: 0}s → ${b ?: "end"}"
}
