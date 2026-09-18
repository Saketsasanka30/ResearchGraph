package com.example.researchgraph.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.researchgraph.data.CitationHelper
import com.example.researchgraph.data.GeminiResearchService
import com.example.researchgraph.data.ResearchRepository
import com.example.researchgraph.data.local.AppDatabase
import com.example.researchgraph.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppDestination(val title: String) {
    DASHBOARD("Dashboard"),
    SOURCES("Sources"),
    GRAPH("Knowledge Graph"),
    READER("Reader"),
    NOTES("Notes"),
    PROJECTS("Projects & Collections"),
    QUESTIONS("Research Questions")
}

data class ReadingSettings(
    val fontScale: Float = 1.0f,
    val isSerif: Boolean = true,
    val isDistractionFree: Boolean = false,
    val activeHighlightColor: String = "#FDE047"
)

data class SnackbarAlert(
    val message: String,
    val isError: Boolean = false,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val timestamp: Long = System.currentTimeMillis()
)

class ResearchViewModel(application: Application) : AndroidViewModel(application) {

    // Repository with Room Database integration
    val repository: ResearchRepository = ResearchRepository(
        researchDao = AppDatabase.getInstance(application).researchDao(),
        aiService = GeminiResearchService()
    )

    // Navigation
    private val _currentDestination = MutableStateFlow(AppDestination.DASHBOARD)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    // Global Search & Filtering
    val searchQuery = MutableStateFlow("")
    val activeSourceFilter = MutableStateFlow<SourceType?>(null)
    val activeTagFilter = MutableStateFlow<String?>(null)

    // Data from repository (with Room persistence)
    val papers = repository.papers
    val sources = repository.sources
    val notes = repository.notes
    val topics = repository.topics
    val annotations = repository.annotations
    val projects = repository.projects
    val collections = repository.collections
    val graphNodes = repository.graphNodes
    val graphEdges = repository.graphEdges
    val researchQuestions = repository.researchQuestions
    val selectedPaperId = repository.selectedPaperId

    // Selected Paper (Derived)
    val currentReadingPaper: StateFlow<Paper?> = combine(papers, selectedPaperId) { paperList, id ->
        paperList.find { it.id == id } ?: paperList.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Reading Settings
    val readingSettings = MutableStateFlow(ReadingSettings())

    // Graph UI State
    val selectedGraphNodeId = MutableStateFlow<String?>("paper-transformer")
    val visibleNodeTypes = MutableStateFlow<Set<NodeType>>(NodeType.values().toSet())
    val graphSearchQuery = MutableStateFlow("")

    // AI States & Loading indicators
    val isAiAnalyzing = MutableStateFlow(false)
    val currentAiAnalysis = MutableStateFlow<AIAnalysisResult?>(null)

    val isAiComparing = MutableStateFlow(false)
    val paperComparisonResult = MutableStateFlow<PaperComparison?>(null)

    val isGeneratingLitReview = MutableStateFlow(false)
    val generatedLiteratureReview = MutableStateFlow<String?>(null)

    val isExplainingPassage = MutableStateFlow(false)
    val passageExplanation = MutableStateFlow<String?>(null)

    // AI Research Materials Query State
    val isAiFetchingResearch = MutableStateFlow(false)
    val lastAiResearchResult = MutableStateFlow<AIResearchQueryResult?>(null)
    val showResearchResultDialog = MutableStateFlow(false)

    // Active Modals & Dialogs
    val showAddSourceDialog = MutableStateFlow(false)
    val showAddPaperDialog = MutableStateFlow(false)
    val showAddNoteDialog = MutableStateFlow(false)
    val showAddTopicDialog = MutableStateFlow(false)
    val showCompareDialog = MutableStateFlow(false)
    val showCitationModal = MutableStateFlow(false)
    val showLitReviewDialog = MutableStateFlow(false)
    val showNewQuestionDialog = MutableStateFlow(false)
    val citationPaper = MutableStateFlow<Paper?>(null)
    val selectedCitationFormat = MutableStateFlow(CitationFormat.BIBTEX)

    // Snackbar Alert System (Errors, Network Failures, Confirmations)
    val snackbarAlert = MutableStateFlow<SnackbarAlert?>(null)
    val statusNotification = MutableStateFlow<String?>(null)

    fun showSuccessAlert(message: String) {
        statusNotification.value = message
        snackbarAlert.value = SnackbarAlert(
            message = message,
            isError = false
        )
    }

    fun showErrorAlert(message: String, actionLabel: String? = "Retry", onAction: (() -> Unit)? = null) {
        statusNotification.value = message
        snackbarAlert.value = SnackbarAlert(
            message = message,
            isError = true,
            actionLabel = actionLabel,
            onAction = onAction
        )
    }

    fun clearSnackbarAlert() {
        snackbarAlert.value = null
    }

    fun clearNotification() {
        statusNotification.value = null
    }

    fun navigateTo(destination: AppDestination) {
        _currentDestination.value = destination
    }

    fun openPaperInReader(paperId: String) {
        repository.selectPaper(paperId)
        _currentDestination.value = AppDestination.READER
    }

    fun toggleBookmark(paperId: String) {
        repository.toggleBookmark(paperId)
    }

    fun updateReadingProgress(paperId: String, progress: Float) {
        repository.updateReadingProgress(paperId, progress)
    }

    fun toggleNodeTypeFilter(type: NodeType) {
        val current = visibleNodeTypes.value.toMutableSet()
        if (current.contains(type)) {
            if (current.size > 1) current.remove(type)
        } else {
            current.add(type)
        }
        visibleNodeTypes.value = current
    }

    fun selectGraphNode(nodeId: String?) {
        selectedGraphNodeId.value = nodeId
    }

    // ====================================================================
    // AI Research Materials Query (Keyword Input & Visual Loading Indicator)
    // ====================================================================

    fun queryAiResearchMaterials(keywords: String) {
        val clean = keywords.trim()
        if (clean.isBlank()) {
            showErrorAlert("Please enter research keywords to query.", actionLabel = null)
            return
        }

        viewModelScope.launch {
            isAiFetchingResearch.value = true
            try {
                val result = repository.queryResearchMaterials(clean)
                lastAiResearchResult.value = result
                showResearchResultDialog.value = true
                showSuccessAlert("Synthesized AI research materials for '$clean'")
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: "Network connection or AI service request failed"
                showErrorAlert(
                    message = "Research query failed: $errorMsg",
                    actionLabel = "Retry"
                ) {
                    queryAiResearchMaterials(keywords)
                }
            } finally {
                isAiFetchingResearch.value = false
            }
        }
    }

    fun saveAiResearchResultAsTopic(result: AIResearchQueryResult) {
        val topic = result.generatedTopic ?: ResearchTopic(
            id = "topic-${System.currentTimeMillis()}",
            title = result.title,
            description = result.synthesisOverview.take(200),
            keywords = listOf(result.query),
            paperCount = result.recommendedReadings.size
        )
        repository.addTopic(
            title = topic.title,
            description = topic.description,
            keywords = topic.keywords,
            paperCount = topic.paperCount
        )
        showSuccessAlert("Research topic '${topic.title}' saved to Room database")
    }

    fun saveAiResearchResultAsNote(result: AIResearchQueryResult) {
        val content = buildString {
            append(result.synthesisOverview)
            append("\n\nKey Methodologies:\n")
            result.keyMethodologies.forEach { append("• $it\n") }
            append("\nOpen Research Questions:\n")
            result.openQuestions.forEach { append("• $it\n") }
            append("\nRecommended Literature:\n")
            result.recommendedReadings.forEach { append("• $it\n") }
        }
        val note = repository.addNote(
            title = "AI Synthesis: ${result.title}",
            content = content,
            tags = listOf("AI Synthesis", result.query, "Research Query")
        )
        showSuccessAlert("Saved research note '${note.title}' to Room database")
    }

    // ====================================================================
    // Other AI Operations with Robust Error Handling
    // ====================================================================

    fun triggerAiPaperAnalysis(paper: Paper) {
        viewModelScope.launch {
            isAiAnalyzing.value = true
            try {
                val result = repository.analyzePaperWithAi(paper)
                currentAiAnalysis.value = result
                showSuccessAlert("AI analysis complete for '${paper.title.take(30)}...'")
            } catch (e: Exception) {
                showErrorAlert(
                    message = "AI analysis failed: ${e.localizedMessage ?: "Network error"}",
                    actionLabel = "Retry"
                ) {
                    triggerAiPaperAnalysis(paper)
                }
            } finally {
                isAiAnalyzing.value = false
            }
        }
    }

    fun triggerComparePapers(paperA: Paper, paperB: Paper) {
        viewModelScope.launch {
            isAiComparing.value = true
            try {
                val result = repository.comparePapersWithAi(paperA, paperB)
                paperComparisonResult.value = result
                showSuccessAlert("Papers compared successfully")
            } catch (e: Exception) {
                showErrorAlert(
                    message = "Paper comparison failed: ${e.localizedMessage ?: "Network error"}",
                    actionLabel = "Retry"
                ) {
                    triggerComparePapers(paperA, paperB)
                }
            } finally {
                isAiComparing.value = false
            }
        }
    }

    fun triggerLiteratureReview(topic: String) {
        viewModelScope.launch {
            isGeneratingLitReview.value = true
            try {
                val result = repository.generateLiteratureReview(papers.value, topic)
                generatedLiteratureReview.value = result
                showSuccessAlert("Literature review generated")
            } catch (e: Exception) {
                showErrorAlert(
                    message = "Literature review failed: ${e.localizedMessage ?: "Network error"}",
                    actionLabel = "Retry"
                ) {
                    triggerLiteratureReview(topic)
                }
            } finally {
                isGeneratingLitReview.value = false
            }
        }
    }

    fun explainReadingPassage(paperTitle: String, passage: String, mode: String = "explain") {
        viewModelScope.launch {
            isExplainingPassage.value = true
            try {
                val explanation = repository.explainPassage(paperTitle, passage, mode)
                passageExplanation.value = explanation
            } catch (e: Exception) {
                showErrorAlert(
                    message = "Passage query failed: ${e.localizedMessage ?: "Network error"}",
                    actionLabel = "Retry"
                ) {
                    explainReadingPassage(paperTitle, passage, mode)
                }
            } finally {
                isExplainingPassage.value = false
            }
        }
    }

    // ====================================================================
    // Room-persisted Topic & Note Actions
    // ====================================================================

    fun addTopic(
        title: String,
        description: String,
        keywords: List<String> = emptyList(),
        paperCount: Int = 0
    ) {
        val topic = repository.addTopic(title, description, keywords, paperCount)
        showAddTopicDialog.value = false
        showSuccessAlert("Topic '${topic.title}' persisted to device Room database")
    }

    fun deleteTopic(topicId: String) {
        repository.deleteTopic(topicId)
        showSuccessAlert("Topic deleted from Room database")
    }

    fun toggleTopicPin(topicId: String) {
        repository.toggleTopicPin(topicId)
    }

    fun addNote(
        title: String,
        content: String,
        tags: List<String>,
        paperId: String? = null,
        sectionTitle: String? = null,
        highlightColor: String? = null
    ) {
        val n = repository.addNote(title, content, tags, paperId, sectionTitle, highlightColor)
        showAddNoteDialog.value = false
        showSuccessAlert("Note '${n.title}' persisted to device Room database")
    }

    fun deleteNote(noteId: String) {
        repository.deleteNote(noteId)
        showSuccessAlert("Note deleted from Room database")
    }

    fun toggleNotePin(noteId: String) {
        repository.toggleNotePin(noteId)
    }

    // ====================================================================
    // Workspace Content Actions
    // ====================================================================

    fun addSource(
        title: String,
        type: SourceType,
        author: String,
        url: String,
        summary: String,
        tags: List<String>,
        collectionId: String? = null,
        projectId: String? = null
    ) {
        val src = repository.addSource(title, type, author, url, summary, tags, collectionId, projectId)
        showAddSourceDialog.value = false
        showSuccessAlert("Source '${src.title}' added to workspace")
    }

    fun addPaper(
        title: String,
        authors: List<String>,
        venue: String,
        year: Int,
        abstractText: String,
        tags: List<String>,
        url: String = "",
        doi: String = ""
    ) {
        val p = repository.addPaper(title, authors, venue, year, abstractText, tags, url, doi)
        showAddPaperDialog.value = false
        showSuccessAlert("Paper '${p.title}' added to knowledge graph")
    }

    fun addAnnotation(
        paperId: String,
        sectionId: String,
        selectedText: String,
        note: String,
        colorHex: String
    ) {
        repository.addAnnotation(paperId, sectionId, selectedText, note, colorHex)
        showSuccessAlert("Annotation added")
    }

    fun addResearchQuestion(
        question: String,
        context: String,
        sourcePaperIds: List<String>,
        hypothesis: String,
        method: String
    ) {
        val rq = repository.addResearchQuestion(question, context, sourcePaperIds, hypothesis, method)
        showNewQuestionDialog.value = false
        showSuccessAlert("Research hypothesis logged")
    }

    fun addProject(name: String, description: String, colorHex: String) {
        repository.addProject(name, description, colorHex)
        showSuccessAlert("Project '$name' created")
    }

    fun openCitationModal(paper: Paper) {
        citationPaper.value = paper
        showCitationModal.value = true
    }

    fun getFormattedCitation(paper: Paper, format: CitationFormat): String {
        return CitationHelper.formatCitation(paper, format)
    }

    // Network error simulation toggle for testing & user demonstration
    fun toggleSimulateNetworkFailure(): Boolean {
        val current = repository.isSimulatingNetworkFailure()
        val next = !current
        repository.setSimulateNetworkFailure(next)
        if (next) {
            showErrorAlert("Network simulation activated: Network requests and AI queries will fail.")
        } else {
            showSuccessAlert("Network simulation deactivated: Live connection restored.")
        }
        return next
    }

    fun isSimulatingNetworkFailure(): Boolean = repository.isSimulatingNetworkFailure()
}
