package com.mrx7014.ytdlp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrx7014.ytdlp.R
import com.mrx7014.ytdlp.data.DownloadRecordEntity
import com.mrx7014.ytdlp.domain.FormatKind
import com.mrx7014.ytdlp.domain.VideoFormat
import com.mrx7014.ytdlp.domain.VideoInfo

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: MainViewModel) {
    val s by vm.state.collectAsState()
    var arabic by remember { mutableStateOf(false) }
    var selectedDownload by remember { mutableStateOf<DownloadRecordEntity?>(null) }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalLayoutDirection provides if (arabic) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.ytdlp_logo),
                                contentDescription = "YtDlp",
                                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("YtDlp", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                                Text("PREMIUM DOWNLOADER", fontSize = 9.sp, color = MaterialTheme.colorScheme.secondary, letterSpacing = 1.sp)
                            }
                        }
                    },
                    actions = {
                        TextButton(onClick = { arabic = !arabic }) {
                            Text(if (arabic) "EN" else "عربي", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        ) { pad ->
            LazyColumn(
                modifier = Modifier.padding(pad).padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { HeroHeader(arabic) }
                item { UrlCard(s.url, arabic, s.loading, vm::setUrl, vm::inspect) }
                if (s.loading && s.info == null) {
                    item {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
                s.error?.let { err -> item { ErrorCard(err) } }
                s.info?.let { info ->
                    item { VideoCard(info, arabic) }
                    item { FormatPicker(info.formats, s.selectedFormat, arabic, vm::select) }
                    item { RangeCard(s, arabic, vm) }
                    item {
                        Button(
                            onClick = vm::download,
                            modifier = Modifier.fillMaxWidth().height(58.dp),
                            shape = RoundedCornerShape(18.dp),
                            enabled = !s.loading && s.selectedFormat != null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(Modifier.width(10.dp))
                            Text(if (arabic) "ابدأ التحميل" else "Start download", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                item { SectionHeader(if (arabic) "سجل التحميل" else "Download history", s.downloads.size) }
                if (s.downloads.isEmpty()) {
                    item { EmptyHistory(arabic) }
                } else {
                    items(s.downloads, key = { it.id }) { d ->
                        DownloadRow(d, onClick = { selectedDownload = d })
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }

    selectedDownload?.let { download ->
        ModalBottomSheet(
            onDismissRequest = { selectedDownload = null },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            DownloadDetailsSheet(download, arabic)
        }
    }
}

@Composable
private fun HeroHeader(arabic: Boolean) {
    Column(modifier = Modifier.padding(top = 10.dp)) {
        Text(
            text = if (arabic) "نزّلها بجودة تستحقها" else "Download it. Own it.",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (arabic) "حمّل فيديوهات YouTube بسرعة وبجودة عالية" else "Your premium way to save YouTube videos in high quality.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(14.dp))
        AssistChip(
            onClick = {},
            label = { Text(if (arabic) "يدعم YouTube" else "BUILT FOR YOUTUBE", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.7.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                leadingIconContentColor = MaterialTheme.colorScheme.primary
            ),
            border = null
        )
    }
}

@Composable
private fun UrlCard(url: String, arabic: Boolean, loading: Boolean, onUrlChange: (String) -> Unit, onInspect: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(if (arabic) "رابط الفيديو" else "VIDEO LINK", color = MaterialTheme.colorScheme.secondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(9.dp))
            OutlinedTextField(
                value = url,
                onValueChange = onUrlChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !loading,
                placeholder = { Text("https://youtube.com/watch...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Icon(Icons.Default.Link, contentDescription = "URL", tint = MaterialTheme.colorScheme.secondary) },
                trailingIcon = {
                    IconButton(onClick = onInspect, enabled = url.isNotBlank() && !loading) {
                        if (loading) {
                            CircularProgressIndicator(modifier = Modifier.size(21.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.secondary)
                        } else {
                            Icon(Icons.Default.Search, contentDescription = "Inspect", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(if (arabic) "الصق الرابط ثم اضغط بحث لتحليل الفيديو" else "Paste a link and tap search to analyze the video.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.45f))
    ) { Text(message, modifier = Modifier.padding(14.dp), color = MaterialTheme.colorScheme.error) }
}

@Composable
private fun SectionHeader(title: String, count: Int) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surfaceVariant) {
            Text("$count", modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun EmptyHistory(arabic: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.fillMaxWidth().padding(vertical = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(30.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(if (arabic) "ستظهر تحميلاتك هنا" else "Your downloads will appear here", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DownloadRow(d: DownloadRecordEntity, onClick: () -> Unit) {
    val active = d.status == "Downloading" || d.status == "Queued"
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        ListItem(
            headlineContent = { Text(d.title, maxLines = 1, fontWeight = FontWeight.SemiBold) },
            supportingContent = {
                Column {
                    Text("${d.format} • ${d.range}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (active) {
                        Spacer(Modifier.height(7.dp))
                        LinearProgressIndicator(progress = { d.progressPercent / 100f }, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.secondary, trackColor = MaterialTheme.colorScheme.surfaceVariant)
                        Text("${d.progressPercent}%${d.speedText?.let { " • $it" } ?: ""}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            },
            leadingContent = { Icon(Icons.Default.VideoFile, contentDescription = "Video", tint = MaterialTheme.colorScheme.primary) },
            trailingContent = { Text(d.status, color = if (d.status == "Failed") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
            colors = androidx.compose.material3.ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
private fun DownloadDetailsSheet(d: DownloadRecordEntity, ar: Boolean) {
    Column(Modifier.padding(24.dp).navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(if (ar) "تفاصيل التحميل" else "Download details", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(d.title, style = MaterialTheme.typography.titleMedium, maxLines = 2)
        LinearProgressIndicator(progress = { d.progressPercent / 100f }, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.secondary, trackColor = MaterialTheme.colorScheme.surfaceVariant)
        Text("${d.progressPercent}%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
        Divider(color = MaterialTheme.colorScheme.outline)
        DetailLine(if (ar) "الحالة" else "Status", d.status)
        DetailLine(if (ar) "السرعة" else "Speed", d.speedText ?: "—")
        DetailLine(if (ar) "الوقت المتبقي" else "Time remaining", d.etaSeconds?.formatEta() ?: "—")
        DetailLine(if (ar) "تم تنزيل" else "Downloaded", listOfNotNull(d.downloadedText, d.totalText?.let { "of $it" }).joinToString(" ").ifBlank { "—" })
        DetailLine(if (ar) "الصيغة" else "Format", d.format)
        DetailLine(if (ar) "المجلد" else "Folder", d.outputPath ?: "—")
        if (!d.currentLine.isNullOrBlank()) Text(d.currentLine!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3)
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        Text(value, fontWeight = FontWeight.SemiBold, maxLines = 2)
    }
}

private fun Long.formatEta(): String {
    val h = this / 3600
    val m = (this % 3600) / 60
    val s = this % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}

@Composable
private fun VideoCard(i: VideoInfo, arabic: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(if (arabic) "تم العثور على الفيديو" else "VIDEO FOUND", color = MaterialTheme.colorScheme.secondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(8.dp))
            Text(i.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, maxLines = 2)
            Spacer(Modifier.height(6.dp))
            Text("${i.uploader} • ${i.durationSeconds / 60}:${(i.durationSeconds % 60).toString().padStart(2, '0')}", color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
private fun FormatPicker(fs: List<VideoFormat>, selected: VideoFormat?, arabic: Boolean, onSelect: (VideoFormat) -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp)) {
            Text(if (arabic) "الجودة والصيغة" else "Quality & format", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(if (arabic) "اختر الجودة المناسبة قبل التحميل" else "Choose the format you want to save.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(10.dp))
            fs.forEach { f ->
                FilterChip(
                    selected = selected == f,
                    onClick = { onSelect(f) },
                    label = { Text(f.label) },
                    leadingIcon = { Icon(if (f.kind == FormatKind.AUDIO) Icons.Default.Audiotrack else Icons.Default.HighQuality, contentDescription = null) },
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun RangeCard(s: HomeUiState, ar: Boolean, vm: MainViewModel) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp)) {
            Text(if (ar) "جزء محدد (اختياري)" else "Clip a range (optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(if (ar) "اترك الحقول فارغة لتحميل الفيديو كاملًا" else "Leave empty to download the full video.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(s.start, vm::setStart, Modifier.weight(1f), label = { Text(if (ar) "من 00:00" else "Start 00:00") }, singleLine = true)
                OutlinedTextField(s.end, vm::setEnd, Modifier.weight(1f), label = { Text(if (ar) "إلى 00:00" else "End 00:00") }, singleLine = true)
            }
        }
    }
}
