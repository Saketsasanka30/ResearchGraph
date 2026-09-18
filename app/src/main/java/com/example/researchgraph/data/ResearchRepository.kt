package com.example.researchgraph.data

import com.example.researchgraph.data.local.ResearchDao
import com.example.researchgraph.data.local.ResearchTopicEntity
import com.example.researchgraph.data.local.SavedNoteEntity
import com.example.researchgraph.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ResearchRepository(
    private val researchDao: ResearchDao? = null,
    private val aiService: GeminiResearchService = GeminiResearchService(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    private val _papers = MutableStateFlow<List<Paper>>(DemoData.papers)
    val papers: StateFlow<List<Paper>> = _papers.asStateFlow()

    private val _sources = MutableStateFlow<List<Source>>(DemoData.sources)
    val sources: StateFlow<List<Source>> = _sources.asStateFlow()

    private val _notes = MutableStateFlow<List<ResearchNote>>(DemoData.notes)
    val notes: StateFlow<List<ResearchNote>> = _notes.asStateFlow()

    private val _topics = MutableStateFlow<List<ResearchTopic>>(emptyList())
    val topics: StateFlow<List<ResearchTopic>> = _topics.asStateFlow()

    private val _annotations = MutableStateFlow<List<PaperAnnotation>>(DemoData.annotations)
    val annotations: StateFlow<List<PaperAnnotation>> = _annotations.asStateFlow()

    private val _projects = MutableStateFlow<List<Project>>(DemoData.projects)
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _collections = MutableStateFlow<List<PaperCollection>>(DemoData.collections)
    val collections: StateFlow<List<PaperCollection>> = _collections.asStateFlow()

    private val _graphNodes = MutableStateFlow<List<GraphNode>>(DemoData.graphNodes)
    val graphNodes: StateFlow<List<GraphNode>> = _graphNodes.asStateFlow()

    private val _graphEdges = MutableStateFlow<List<GraphEdge>>(DemoData.graphEdges)
    val graphEdges: StateFlow<List<GraphEdge>> = _graphEdges.asStateFlow()

    private val _researchQuestions = MutableStateFlow<List<ResearchQuestion>>(DemoData.researchQuestions)
    val researchQuestions: StateFlow<List<ResearchQuestion>> = _researchQuestions.asStateFlow()

    private val _selectedPaperId = MutableStateFlow<String?>(DemoData.papers.firstOrNull()?.id)
    val selectedPaperId: StateFlow<String?> = _selectedPaperId.asStateFlow()

    init {
        // Wire Room Database synchronization if DAO is provided
        if (researchDao != null) {
            scope.launch {
                researchDao.getAllSavedNotes().collect { entities ->
                    if (entities.isNotEmpty()) {
                        _notes.value = entities.map { it.toDomainNote() }
                    } else {
                        // Seed default notes into Room
                        val defaultEntities = DemoData.notes.map { it.toEntity() }
                        researchDao.insertNotes(defaultEntities)
                    }
                }
            }

            scope.launch {
                researchDao.getAllTopics().collect { entities ->
                    if (entities.isNotEmpty()) {
                        _topics.value = entities.map { it.toDomainTopic() }
                    } else {
                        // Seed initial default research topics into Room
                        val initialTopics = listOf(
                            ResearchTopicEntity(
                                id = "topic-attention",
                                title = "Transformer Attention Mechanics",
                                description = "Investigation of multi-head self-attention, KV-cache caching, sparse approximations, and algorithmic scaling.",
                                keywords = "Self-Attention, O(N^2), FlashAttention, Transformers",
                                paperCount = 3,
                                isPinned = true
                            ),
                            ResearchTopicEntity(
                                id = "topic-gnn",
                                title = "Graph Neural Networks & Relational Topology",
                                description = "Message-passing schemes, Weisfeiler-Lehman bounds, oversmoothing mitigation, and non-Euclidean representation learning.",
                                keywords = "Message Passing, Weisfeiler-Lehman, GAT, Node Embeddings",
                                paperCount = 2,
                                isPinned = false
                            ),
                            ResearchTopicEntity(
                                id = "topic-rag",
                                title = "Retrieval-Augmented Generation (RAG)",
                                description = "Dense vector indexing, dual bi-encoders, approximate nearest-neighbor search, and hallucination reduction.",
                                keywords = "Dense Retrieval, DPR, HNSW, Reciprocal Rank Fusion",
                                paperCount = 4,
                                isPinned = true
                            )
                        )
                        researchDao.insertTopics(initialTopics)
                    }
                }
            }
        } else {
            // Memory fallback for topics
            _topics.value = listOf(
                ResearchTopic(
                    id = "topic-attention",
                    title = "Transformer Attention Mechanics",
                    description = "Investigation of multi-head self-attention, KV-cache caching, sparse approximations, and algorithmic scaling.",
                    keywords = listOf("Self-Attention", "O(N^2)", "FlashAttention", "Transformers"),
                    paperCount = 3,
                    isPinned = true
                ),
                ResearchTopic(
                    id = "topic-gnn",
                    title = "Graph Neural Networks & Relational Topology",
                    description = "Message-passing schemes, Weisfeiler-Lehman bounds, oversmoothing mitigation, and non-Euclidean representation learning.",
                    keywords = listOf("Message Passing", "Weisfeiler-Lehman", "GAT", "Node Embeddings"),
                    paperCount = 2,
                    isPinned = false
                ),
                ResearchTopic(
                    id = "topic-rag",
                    title = "Retrieval-Augmented Generation (RAG)",
                    description = "Dense vector indexing, dual bi-encoders, approximate nearest-neighbor search, and hallucination reduction.",
                    keywords = listOf("Dense Retrieval", "DPR", "HNSW", "Reciprocal Rank Fusion"),
                    paperCount = 4,
                    isPinned = true
                )
            )
        }
    }

    fun selectPaper(paperId: String) {
        _selectedPaperId.value = paperId
    }

    fun toggleBookmark(paperId: String) {
        _papers.value = _papers.value.map { paper ->
            if (paper.id == paperId) {
                paper.copy(isBookmarked = !paper.isBookmarked)
            } else paper
        }
    }

    fun updateReadingProgress(paperId: String, progress: Float) {
        _papers.value = _papers.value.map { paper ->
            if (paper.id == paperId) {
                paper.copy(readPercentage = progress.coerceIn(0f, 1f))
            } else paper
        }
    }

    fun addSource(
        title: String,
        type: SourceType,
        author: String,
        url: String,
        summary: String,
        tags: List<String>,
        collectionId: String? = null,
        projectId: String? = null
    ): Source {
        val newSource = Source(
            id = "src-${UUID.randomUUID().toString().take(8)}",
            title = title,
            type = type,
            authorsOrSource = author,
            url = url,
            summary = summary,
            tags = tags,
            dateAdded = System.currentTimeMillis(),
            collectionId = collectionId,
            projectId = projectId
        )
        _sources.value = listOf(newSource) + _sources.value

        // Also add a node to the knowledge graph
        val nodeType = when (type) {
            SourceType.PAPER, SourceType.PDF -> NodeType.PAPER
            SourceType.NOTE -> NodeType.NOTE
            SourceType.REFERENCE -> NodeType.CITATION
            else -> NodeType.TOPIC
        }
        val newNode = GraphNode(
            id = newSource.id,
            label = title.take(24),
            subLabel = author.take(20),
            type = nodeType,
            x = (200..600).random().toFloat(),
            y = (200..600).random().toFloat(),
            radius = 26f
        )
        _graphNodes.value = _graphNodes.value + newNode

        return newSource
    }

    fun addPaper(
        title: String,
        authors: List<String>,
        venue: String,
        year: Int,
        abstractText: String,
        tags: List<String>,
        url: String = "",
        doi: String = "",
        projectId: String? = null
    ): Paper {
        val newPaper = Paper(
            id = "paper-${UUID.randomUUID().toString().take(8)}",
            title = title,
            authors = authors,
            publicationVenue = venue,
            year = year,
            abstractText = abstractText,
            sections = listOf(
                Section(
                    id = "sec-new-1",
                    title = "Abstract",
                    content = abstractText,
                    sectionType = "Abstract"
                ),
                Section(
                    id = "sec-new-2",
                    title = "1. Introduction & Background",
                    content = "This paper introduces new findings regarding $title. Authored by ${authors.joinToString(", ")}, the methodology addresses key empirical benchmarks.",
                    sectionType = "Introduction"
                ),
                Section(
                    id = "sec-new-3",
                    title = "2. Methods & Discussion",
                    content = "The core experimental evaluation confirms robust convergence and stability across tested parameters.",
                    sectionType = "Methodology"
                )
            ),
            tags = tags,
            url = url,
            doi = doi,
            projectId = projectId,
            citationCount = 1,
            isBookmarked = true,
            readPercentage = 0.05f
        )
        _papers.value = listOf(newPaper) + _papers.value

        // Add to Knowledge Graph
        val paperNode = GraphNode(
            id = newPaper.id,
            label = newPaper.title.take(24),
            subLabel = "${authors.firstOrNull()} ($year)",
            type = NodeType.PAPER,
            x = (250..550).random().toFloat(),
            y = (250..550).random().toFloat(),
            radius = 30f,
            relatedPaperId = newPaper.id
        )
        _graphNodes.value = _graphNodes.value + paperNode

        // Add author nodes if needed
        authors.firstOrNull()?.let { authorName ->
            val authorId = "author-${authorName.lowercase().replace(" ", "-").take(12)}"
            val authorNode = GraphNode(
                id = authorId,
                label = authorName,
                type = NodeType.AUTHOR,
                x = paperNode.x + 80f,
                y = paperNode.y - 70f,
                radius = 22f,
                relatedPaperId = newPaper.id
            )
            val edge = GraphEdge(
                id = "e-${UUID.randomUUID().toString().take(6)}",
                fromNodeId = newPaper.id,
                toNodeId = authorId,
                relationship = "written_by"
            )
            _graphNodes.value = _graphNodes.value + authorNode
            _graphEdges.value = _graphEdges.value + edge
        }

        return newPaper
    }

    fun addNote(
        title: String,
        content: String,
        tags: List<String> = emptyList(),
        paperId: String? = null,
        sectionTitle: String? = null,
        highlightColor: String? = null
    ): ResearchNote {
        val paperTitle = paperId?.let { id -> _papers.value.find { it.id == id }?.title }
        val newNote = ResearchNote(
            id = "note-${UUID.randomUUID().toString().take(8)}",
            title = title,
            content = content,
            tags = tags,
            paperId = paperId,
            paperTitle = paperTitle,
            sectionTitle = sectionTitle,
            highlightColor = highlightColor,
            createdAt = System.currentTimeMillis(),
            isPinned = false
        )
        _notes.value = listOf(newNote) + _notes.value

        // Persist to Room Database
        researchDao?.let { dao ->
            scope.launch {
                dao.insertNote(newNote.toEntity())
            }
        }

        // Add to graph
        val noteNode = GraphNode(
            id = newNote.id,
            label = title.take(20),
            subLabel = "Research Note",
            type = NodeType.NOTE,
            x = (200..600).random().toFloat(),
            y = (200..600).random().toFloat(),
            radius = 20f
        )
        _graphNodes.value = _graphNodes.value + noteNode

        if (paperId != null) {
            val edge = GraphEdge(
                id = "e-${UUID.randomUUID().toString().take(6)}",
                fromNodeId = paperId,
                toNodeId = newNote.id,
                relationship = "annotated_by"
            )
            _graphEdges.value = _graphEdges.value + edge
        }

        return newNote
    }

    fun deleteNote(noteId: String) {
        _notes.value = _notes.value.filter { it.id != noteId }
        _graphNodes.value = _graphNodes.value.filter { it.id != noteId }
        researchDao?.let { dao ->
            scope.launch {
                dao.deleteNoteById(noteId)
            }
        }
    }

    fun toggleNotePin(noteId: String) {
        _notes.value = _notes.value.map { note ->
            if (note.id == noteId) note.copy(isPinned = !note.isPinned) else note
        }
        researchDao?.let { dao ->
            scope.launch {
                dao.toggleNotePin(noteId)
            }
        }
    }

    fun addTopic(
        title: String,
        description: String,
        keywords: List<String> = emptyList(),
        paperCount: Int = 0
    ): ResearchTopic {
        val topic = ResearchTopic(
            id = "topic-${UUID.randomUUID().toString().take(8)}",
            title = title,
            description = description,
            keywords = keywords,
            paperCount = paperCount,
            createdAt = System.currentTimeMillis(),
            isPinned = false
        )
        _topics.value = listOf(topic) + _topics.value

        // Persist to Room
        researchDao?.let { dao ->
            scope.launch {
                dao.insertTopic(topic.toEntity())
            }
        }

        // Add to graph
        val topicNode = GraphNode(
            id = topic.id,
            label = title.take(22),
            subLabel = "Research Topic",
            type = NodeType.TOPIC,
            x = (200..600).random().toFloat(),
            y = (200..600).random().toFloat(),
            radius = 24f
        )
        _graphNodes.value = _graphNodes.value + topicNode

        return topic
    }

    fun deleteTopic(topicId: String) {
        _topics.value = _topics.value.filter { it.id != topicId }
        _graphNodes.value = _graphNodes.value.filter { it.id != topicId }
        researchDao?.let { dao ->
            scope.launch {
                dao.deleteTopicById(topicId)
            }
        }
    }

    fun toggleTopicPin(topicId: String) {
        _topics.value = _topics.value.map { topic ->
            if (topic.id == topicId) topic.copy(isPinned = !topic.isPinned) else topic
        }
        researchDao?.let { dao ->
            scope.launch {
                dao.toggleTopicPin(topicId)
            }
        }
    }

    fun addAnnotation(
        paperId: String,
        sectionId: String,
        selectedText: String,
        note: String,
        colorHex: String
    ): PaperAnnotation {
        val ann = PaperAnnotation(
            id = "ann-${UUID.randomUUID().toString().take(8)}",
            paperId = paperId,
            sectionId = sectionId,
            selectedText = selectedText,
            note = note,
            colorHex = colorHex,
            createdAt = System.currentTimeMillis()
        )
        _annotations.value = _annotations.value + ann
        return ann
    }

    fun addResearchQuestion(
        question: String,
        context: String,
        sourcePaperIds: List<String>,
        hypothesis: String,
        method: String
    ): ResearchQuestion {
        val rq = ResearchQuestion(
            id = "rq-${UUID.randomUUID().toString().take(8)}",
            question = question,
            context = context,
            sourcePaperIds = sourcePaperIds,
            keyHypothesis = hypothesis,
            suggestedMethod = method,
            status = "Open"
        )
        _researchQuestions.value = listOf(rq) + _researchQuestions.value
        return rq
    }

    fun addProject(name: String, description: String, colorHex: String): Project {
        val proj = Project(
            id = "proj-${UUID.randomUUID().toString().take(8)}",
            name = name,
            description = description,
            colorHex = colorHex
        )
        _projects.value = _projects.value + proj
        return proj
    }

    // AI Delegations
    suspend fun queryResearchMaterials(query: String): AIResearchQueryResult =
        aiService.queryResearchMaterials(query)

    suspend fun analyzePaperWithAi(paper: Paper): AIAnalysisResult =
        aiService.analyzePaper(paper)

    suspend fun comparePapersWithAi(paperA: Paper, paperB: Paper): PaperComparison =
        aiService.comparePapers(paperA, paperB)

    suspend fun generateLiteratureReview(papers: List<Paper>, topic: String): String =
        aiService.generateLiteratureReview(papers, topic)

    suspend fun explainPassage(paperTitle: String, passage: String, mode: String): String =
        aiService.explainPassage(paperTitle, passage, mode)

    fun setSimulateNetworkFailure(simulate: Boolean) {
        aiService.simulateNetworkFailure = simulate
    }

    fun isSimulatingNetworkFailure(): Boolean = aiService.simulateNetworkFailure
}

// Helpers for Room mapping
fun SavedNoteEntity.toDomainNote(): ResearchNote {
    return ResearchNote(
        id = id,
        title = title,
        content = content,
        tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() },
        paperId = paperId,
        paperTitle = paperTitle,
        sectionTitle = sectionTitle,
        highlightColor = highlightColor,
        createdAt = createdAt,
        isPinned = isPinned
    )
}

fun ResearchNote.toEntity(): SavedNoteEntity {
    return SavedNoteEntity(
        id = id,
        title = title,
        content = content,
        tags = tags.joinToString(","),
        paperId = paperId,
        paperTitle = paperTitle,
        sectionTitle = sectionTitle,
        highlightColor = highlightColor,
        createdAt = createdAt,
        isPinned = isPinned
    )
}

fun ResearchTopicEntity.toDomainTopic(): ResearchTopic {
    return ResearchTopic(
        id = id,
        title = title,
        description = description,
        keywords = if (keywords.isBlank()) emptyList() else keywords.split(",").map { it.trim() },
        paperCount = paperCount,
        createdAt = createdAt,
        isPinned = isPinned
    )
}

fun ResearchTopic.toEntity(): ResearchTopicEntity {
    return ResearchTopicEntity(
        id = id,
        title = title,
        description = description,
        keywords = keywords.joinToString(","),
        paperCount = paperCount,
        createdAt = createdAt,
        isPinned = isPinned
    )
}
