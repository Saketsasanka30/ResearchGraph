package com.example.researchgraph.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.researchgraph.model.PaperAnnotation
import com.example.researchgraph.model.Paper
import com.example.researchgraph.model.Section
import com.example.researchgraph.viewmodel.ResearchViewModel

enum class ReaderSideTab {
    AI_PANEL,
    MARGIN_NOTES,
    SECTIONS_TOC,
    CITATIONS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingScreen(
    viewModel: ResearchViewModel,
    modifier: Modifier = Modifier
) {
    val currentPaper by viewModel.currentReadingPaper.collectAsState()
    val readingSettings by viewModel.readingSettings.collectAsState()
    val annotations by viewModel.annotations.collectAsState()
    val notes by viewModel.notes.collectAsState()

    var activeSideTab by remember { mutableStateOf<ReaderSideTab?>(null) }
    var showHighlightBar by remember { mutableStateOf(false) }
    var selectedExcerpt by remember { mutableStateOf("") }
    var selectedSectionId by remember { mutableStateOf("") }
    var inlineNoteText by remember { mutableStateOf("") }

    val isExplaining by viewModel.isExplainingPassage.collectAsState()
    val explanationText by viewModel.passageExplanation.collectAsState()
    val isAnalyzing by viewModel.isAiAnalyzing.collectAsState()
    val aiAnalysis by viewModel.currentAiAnalysis.collectAsState()

    if (currentPaper == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Select a paper to begin reading")
        }
        return
    }

    val paper = currentPaper!!
    val paperAnnotations = annotations.filter { it.paperId == paper.id }
    val paperNotes = notes.filter { it.paperId == paper.id }

    val highlightPalette = listOf(
        "#FDE047" to "Amber",
        "#99F6E4" to "Mint",
        "#FECDD3" to "Rose",
        "#E9D5FF" to "Lavender",
        "#BAE6FD" to "Sky"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("reading_screen")
    ) {
        // Reader Toolbar Controls
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Table of Contents Toggle
                IconButton(
                    onClick = {
                        activeSideTab = if (activeSideTab == ReaderSideTab.SECTIONS_TOC) null else ReaderSideTab.SECTIONS_TOC
                    },
                    modifier = Modifier.testTag("reader_toc_button")
                ) {
                    Icon(Icons.Default.FormatListBulleted, contentDescription = "Sections TOC")
                }

                // Font Size Toggle (Cycle 0.9f, 1.0f, 1.15f, 1.3f)
                IconButton(
                    onClick = {
                        val nextScale = when (readingSettings.fontScale) {
                            0.9f -> 1.0f
                            1.0f -> 1.15f
                            1.15f -> 1.3f
                            else -> 0.9f
                        }
                        viewModel.readingSettings.value = readingSettings.copy(fontScale = nextScale)
                    },
                    modifier = Modifier.testTag("reader_font_size_button")
                ) {
                    Icon(Icons.Default.FormatSize, contentDescription = "Font Size")
                }

                // Serif / Sans Toggle
                IconButton(
                    onClick = {
                        viewModel.readingSettings.value = readingSettings.copy(isSerif = !readingSettings.isSerif)
                    },
                    modifier = Modifier.testTag("reader_serif_toggle")
                ) {
                    Text(
                        text = if (readingSettings.isSerif) "Serif" else "Sans",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = if (readingSettings.isSerif) FontFamily.Serif else FontFamily.Default
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // AI Panel Toggle
                FilledTonalIconButton(
                    onClick = {
                        activeSideTab = if (activeSideTab == ReaderSideTab.AI_PANEL) null else ReaderSideTab.AI_PANEL
                    },
                    modifier = Modifier.testTag("reader_ai_panel_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI Assistant")
                }

                // Margin Notes Toggle
                IconButton(
                    onClick = {
                        activeSideTab = if (activeSideTab == ReaderSideTab.MARGIN_NOTES) null else ReaderSideTab.MARGIN_NOTES
                    },
                    modifier = Modifier.testTag("reader_notes_panel_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (paperAnnotations.isNotEmpty() || paperNotes.isNotEmpty()) {
                                Badge { Text("${paperAnnotations.size + paperNotes.size}") }
                            }
                        }
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = "Margin Notes")
                    }
                }

                // Bookmark Toggle
                IconButton(
                    onClick = { viewModel.toggleBookmark(paper.id) },
                    modifier = Modifier.testTag("reader_bookmark_button")
                ) {
                    Icon(
                        imageVector = if (paper.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark Paper",
                        tint = if (paper.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Distraction-Free Toggle
                IconButton(
                    onClick = {
                        viewModel.readingSettings.value = readingSettings.copy(
                            isDistractionFree = !readingSettings.isDistractionFree
                        )
                    }
                ) {
                    Icon(
                        imageVector = if (readingSettings.isDistractionFree) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Distraction-Free Mode"
                    )
                }
            }
        }

        // Inline Highlight / Annotate Quick Bar
        AnimatedVisibility(visible = showHighlightBar) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Annotate Excerpt: \"${selectedExcerpt.take(45)}...\"",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Highlight Colors
                        highlightPalette.forEach { (colorHex, _) ->
                            val c = Color(android.graphics.Color.parseColor(colorHex))
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(c)
                                    .border(
                                        if (readingSettings.activeHighlightColor == colorHex) 2.dp else 0.5.dp,
                                        MaterialTheme.colorScheme.onSurface,
                                        CircleShape
                                    )
                                    .clickable {
                                        viewModel.readingSettings.value = readingSettings.copy(activeHighlightColor = colorHex)
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                viewModel.addAnnotation(
                                    paperId = paper.id,
                                    sectionId = selectedSectionId,
                                    selectedText = selectedExcerpt,
                                    note = inlineNoteText.ifBlank { "Highlighted passage" },
                                    colorHex = readingSettings.activeHighlightColor
                                )
                                showHighlightBar = false
                                inlineNoteText = ""
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Highlight", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.explainReadingPassage(paper.title, selectedExcerpt, "explain")
                                activeSideTab = ReaderSideTab.AI_PANEL
                                showHighlightBar = false
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Explain AI", fontSize = 11.sp)
                        }

                        IconButton(onClick = { showHighlightBar = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Split Layout: Reader Body (Left) and Interactive Side Panel (Right / Overlay)
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Main Reader Scrollable Article
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = if (readingSettings.isDistractionFree) 24.dp else 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Academic Paper Header
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = paper.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = if (readingSettings.isSerif) FontFamily.Serif else FontFamily.Default,
                            lineHeight = 32.sp
                        ),
                        modifier = Modifier.testTag("reading_paper_title")
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = paper.authors.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${paper.publicationVenue} • ${paper.year} • DOI: ${paper.doi}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Paper Sections
                itemsIndexed(paper.sections) { index, section ->
                    PaperSectionItem(
                        section = section,
                        paperId = paper.id,
                        annotations = paperAnnotations.filter { it.sectionId == section.id },
                        fontScale = readingSettings.fontScale,
                        isSerif = readingSettings.isSerif,
                        onSelectExcerpt = { excerpt ->
                            selectedExcerpt = excerpt
                            selectedSectionId = section.id
                            showHighlightBar = true
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Side Panel Overlay (AI Assistant / Margin Notes / TOC)
            if (activeSideTab != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.92f)
                        .align(Alignment.CenterEnd)
                        .testTag("reader_side_panel"),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 10.dp,
                    shadowElevation = 12.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Side Panel Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val panelTitle = when (activeSideTab) {
                                ReaderSideTab.AI_PANEL -> "AI Research Companion"
                                ReaderSideTab.MARGIN_NOTES -> "Margin Notes & Highlights"
                                ReaderSideTab.SECTIONS_TOC -> "Table of Contents"
                                ReaderSideTab.CITATIONS -> "Citation & BibTeX"
                                null -> ""
                            }
                            Text(
                                text = panelTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { activeSideTab = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close Panel")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Panel Body
                        when (activeSideTab) {
                            ReaderSideTab.AI_PANEL -> {
                                AiResearchCompanionPanel(
                                    paper = paper,
                                    viewModel = viewModel,
                                    isExplaining = isExplaining,
                                    explanationText = explanationText,
                                    isAnalyzing = isAnalyzing,
                                    aiAnalysis = aiAnalysis
                                )
                            }
                            ReaderSideTab.MARGIN_NOTES -> {
                                MarginNotesPanel(
                                    paper = paper,
                                    annotations = paperAnnotations,
                                    notes = paperNotes,
                                    onAddNote = { viewModel.showAddNoteDialog.value = true }
                                )
                            }
                            ReaderSideTab.SECTIONS_TOC -> {
                                TableOfContentsPanel(
                                    sections = paper.sections,
                                    onSelectSection = { activeSideTab = null }
                                )
                            }
                            ReaderSideTab.CITATIONS -> {
                                // Direct citation button
                                Button(
                                    onClick = {
                                        viewModel.openCitationModal(paper)
                                        activeSideTab = null
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.FormatQuote, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Full Citation Generator")
                                }
                            }
                            null -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaperSectionItem(
    section: Section,
    paperId: String,
    annotations: List<PaperAnnotation>,
    fontScale: Float,
    isSerif: Boolean,
    onSelectExcerpt: (String) -> Unit
) {
    val bodyFontSize = (15 * fontScale).sp
    val lineHeight = (24 * fontScale).sp

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = section.sectionType.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = (17 * fontScale).sp
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Text Content with selection
        SelectionContainer {
            Text(
                text = section.content,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = bodyFontSize,
                    lineHeight = lineHeight,
                    fontFamily = if (isSerif) FontFamily.Serif else FontFamily.Default,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Select first sentence for quick highlighting
                        val firstSentence = section.content.split(".").firstOrNull()?.trim() ?: section.content.take(60)
                        onSelectExcerpt(firstSentence)
                    }
            )
        }

        // Display Attached Annotations for this section
        if (annotations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            annotations.forEach { ann ->
                val bg = Color(android.graphics.Color.parseColor(ann.colorHex)).copy(alpha = 0.35f)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = bg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "\"${ann.selectedText}\"",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        if (ann.note.isNotBlank() && ann.note != "Highlighted passage") {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Note: ${ann.note}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiResearchCompanionPanel(
    paper: Paper,
    viewModel: ResearchViewModel,
    isExplaining: Boolean,
    explanationText: String?,
    isAnalyzing: Boolean,
    aiAnalysis: com.example.researchgraph.model.AIAnalysisResult?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quick AI actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilledTonalButton(
                onClick = { viewModel.triggerAiPaperAnalysis(paper) },
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(6.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Analyze Paper", fontSize = 11.sp)
            }

            OutlinedButton(
                onClick = { viewModel.explainReadingPassage(paper.title, paper.abstractText, "simplify") },
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(6.dp)
            ) {
                Text("Simplify", fontSize = 11.sp)
            }
        }

        // Active Explanation Card
        if (isExplaining) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("AI is reasoning over the passage...", fontSize = 12.sp)
                }
            }
        } else if (explanationText != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "AI Academic Explanation",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = explanationText,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
                    )
                }
            }
        }

        // Comprehensive AI Analysis Results
        if (isAnalyzing) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Extracting findings, limitations & concepts...", fontSize = 12.sp)
                }
            }
        } else if (aiAnalysis != null) {
            // Executive Summary
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Executive Summary", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(aiAnalysis.executiveSummary, style = MaterialTheme.typography.bodySmall)
                }
            }

            // Key Findings
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Key Empirical Findings", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Spacer(modifier = Modifier.height(6.dp))
                    aiAnalysis.keyFindings.forEach { finding ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text("• ", fontWeight = FontWeight.Bold)
                            Text(finding, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Methodology
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Architecture & Methodology", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(aiAnalysis.methodologyOverview, style = MaterialTheme.typography.bodySmall)
                }
            }

            // Novel Research Questions
            if (aiAnalysis.novelResearchQuestions.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Open Research Hypotheses", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary))
                        Spacer(modifier = Modifier.height(6.dp))
                        aiAnalysis.novelResearchQuestions.forEach { q ->
                            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                Text("? ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                Text(q, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // Extracted Concepts
            if (aiAnalysis.extractedConcepts.isNotEmpty()) {
                Text("Extracted Ontology / Concepts:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    aiAnalysis.extractedConcepts.forEach { c ->
                        AssistChip(
                            onClick = { viewModel.graphSearchQuery.value = c },
                            label = { Text(c, fontSize = 10.sp) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarginNotesPanel(
    paper: Paper,
    annotations: List<PaperAnnotation>,
    notes: List<com.example.researchgraph.model.ResearchNote>,
    onAddNote: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onAddNote,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add Note for this Paper")
        }

        if (annotations.isEmpty() && notes.isEmpty()) {
            Text(
                text = "No notes or highlights yet for this paper. Tap any passage to highlight or tap '+ Add Note' above.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        notes.forEach { note ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(note.title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(note.content, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        annotations.forEach { ann ->
            val bg = Color(android.graphics.Color.parseColor(ann.colorHex)).copy(alpha = 0.35f)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = bg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("\"${ann.selectedText}\"", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                    if (ann.note.isNotBlank()) {
                        Text(ann.note, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                }
            }
        }
    }
}

@Composable
fun TableOfContentsPanel(
    sections: List<Section>,
    onSelectSection: (Section) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sections.forEach { section ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectSection(section) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
