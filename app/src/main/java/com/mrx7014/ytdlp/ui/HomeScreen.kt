package com.mrx7014.ytdlp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.mrx7014.ytdlp.domain.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: MainViewModel) {
    val s by vm.state.collectAsState()
    var arabic by remember { mutableStateOf(false) }
    CompositionLocalProvider(LocalLayoutDirection provides if (arabic) LayoutDirection.Rtl else LayoutDirection.Ltr) {
        Scaffold(topBar = {
            TopAppBar(title = { Text("YtDlp", fontWeight = FontWeight.Bold) }, actions = {
                TextButton(onClick = { arabic = !arabic }) { Text(if (arabic) "EN" else "عربي") }
                IconButton(onClick = {}) { Icon(Icons.Default.Settings, "Settings") }
            })
        }) { pad ->
            LazyColumn(
                modifier = Modifier.padding(pad).padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(Modifier.height(4.dp))
                    Text(if (arabic) "حمّل بالطريقة التي تريدها" else "Download your way", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(if (arabic) "الصق رابط يوتيوب واختر الجودة والجزء" else "Paste a YouTube link, choose quality and range", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                item {
                    OutlinedTextField(value = s.url, onValueChange = vm::setUrl, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text(if (arabic) "رابط الفيديو" else "Video URL") }, leadingIcon = { Icon(Icons.Default.Link, "URL") }, trailingIcon = { IconButton(onClick = vm::inspect) { Icon(Icons.Default.Search, "Inspect") } })
                }
                if (s.loading && s.info == null) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                s.error?.let { err -> item { Text(err, color = MaterialTheme.colorScheme.error) } }
                s.info?.let { info ->
                    item { VideoCard(info) }
                    item { FormatPicker(info.formats, s.selectedFormat, vm::select) }
                    item { RangeCard(s, arabic, vm) }
                    item {
                        Button(onClick = vm::download, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp), enabled = !s.loading) {
                            Icon(Icons.Default.Download, "Download"); Spacer(Modifier.width(8.dp)); Text(if (arabic) "بدء التحميل" else "Start download")
                        }
                    }
                }
                item { Text(if (arabic) "السجل" else "History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                if (s.downloads.isEmpty()) item { Text(if (arabic) "لا توجد تنزيلات بعد" else "No downloads yet", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                else items(s.downloads, key = { it.id }) { d ->
                    ListItem(headlineContent = { Text(d.title, maxLines = 1) }, supportingContent = { Text("${d.format} • ${d.range}") }, leadingContent = { Icon(Icons.Default.VideoFile, "Video") }, trailingContent = { Text(d.status, color = MaterialTheme.colorScheme.primary) })
                }
            }
        }
    }
}

@Composable private fun VideoCard(i: VideoInfo) = Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), shape = RoundedCornerShape(24.dp)) {
    Column(Modifier.padding(18.dp)) { Text(i.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("${i.uploader} • ${i.durationSeconds / 60}:${(i.durationSeconds % 60).toString().padStart(2, '0')}", color = MaterialTheme.colorScheme.onPrimaryContainer) }
}

@Composable private fun FormatPicker(fs: List<VideoFormat>, selected: VideoFormat?, onSelect: (VideoFormat) -> Unit) = Column {
    Text("Quality & format", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(8.dp))
    fs.forEach { f -> FilterChip(selected = selected == f, onClick = { onSelect(f) }, label = { Text(f.label) }, leadingIcon = { Icon(if (f.kind == FormatKind.AUDIO) Icons.Default.Audiotrack else Icons.Default.HighQuality, null) }, modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)) }
}

@Composable private fun RangeCard(s: HomeUiState, ar: Boolean, vm: MainViewModel) = Card(shape = RoundedCornerShape(20.dp)) {
    Column(Modifier.padding(16.dp)) { Text(if (ar) "جزء محدد (اختياري)" else "Clip a range (optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(10.dp)); Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { OutlinedTextField(s.start, vm::setStart, Modifier.weight(1f), label = { Text(if (ar) "من 00:00" else "Start 00:00") }, singleLine = true); OutlinedTextField(s.end, vm::setEnd, Modifier.weight(1f), label = { Text(if (ar) "إلى 00:00" else "End 00:00") }, singleLine = true) } }
}
