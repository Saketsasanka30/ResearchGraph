package com.example.researchgraph.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.researchgraph.data.CitationHelper
import com.example.researchgraph.model.CitationFormat
import com.example.researchgraph.viewmodel.ResearchViewModel

@Composable
fun CitationModal(
    viewModel: ResearchViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val paper by viewModel.citationPaper.collectAsState()
    var selectedFormat by remember { mutableStateOf(CitationFormat.BIBTEX) }
    var exportAllCollection by remember { mutableStateOf(false) }
    var copiedFeedback by remember { mutableStateOf(false) }

    if (paper == null) return

    val currentPaper = paper!!
    val formattedText = remember(currentPaper, selectedFormat, exportAllCollection) {
        if (exportAllCollection) {
            CitationHelper.generateBibliography(viewModel.papers.value, selectedFormat)
        } else {
            CitationHelper.formatCitation(currentPaper, selectedFormat)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(8.dp)
                .testTag("citation_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (exportAllCollection) "Bibliography Export" else "Citation Generator",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (exportAllCollection) "All ${viewModel.papers.value.size} papers in collection" else currentPaper.title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle: Single vs Full Bibliography
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !exportAllCollection,
                        onClick = { exportAllCollection = false },
                        label = { Text("Single Paper") }
                    )
                    FilterChip(
                        selected = exportAllCollection,
                        onClick = { exportAllCollection = true },
                        label = { Text("Full Bibliography (${viewModel.papers.value.size})") }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Citation Format Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CitationFormat.values().forEach { format ->
                        FilterChip(
                            selected = selectedFormat == format,
                            onClick = {
                                selectedFormat = format
                                copiedFeedback = false
                            },
                            label = { Text(format.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Formatted Citation Preview
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = formattedText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = if (selectedFormat == CitationFormat.BIBTEX) FontFamily.Monospace else FontFamily.Serif,
                            fontSize = if (selectedFormat == CitationFormat.BIBTEX) 12.sp else 14.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.testTag("citation_preview_text")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions: Copy & Feedback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Citation", formattedText)
                            clipboard.setPrimaryClip(clip)
                            copiedFeedback = true
                            viewModel.statusNotification.value = "Citation copied to clipboard!"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_citation_button")
                    ) {
                        Icon(
                            imageVector = if (copiedFeedback) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (copiedFeedback) "Copied!" else "Copy to Clipboard")
                    }

                    OutlinedButton(
                        onClick = onDismiss
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}
