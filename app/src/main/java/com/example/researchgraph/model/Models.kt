package com.example.researchgraph.model

enum class SourceType(val displayName: String) {
    PAPER("Research Paper"),
    PDF("PDF Document"),
    URL("Web Article / URL"),
    NOTE("Field Note"),
    DOCUMENT("Document"),
    REFERENCE("Citation Reference")
}

enum class NodeType(val displayName: String) {
    PAPER("Paper"),
    AUTHOR("Author"),
    CONCEPT("Concept"),
    TOPIC("Topic"),
    CITATION("Citation"),
    NOTE("Note")
}

data class Section(
    val id: String,
    val title: String,
    val content: String,
    val sectionType: String // "Abstract", "Introduction", "Methodology", "Results", "Discussion", "References"
)

data class Paper(
    val id: String,
    val title: String,
    val authors: List<String>,
    val publicationVenue: String,
    val year: Int,
    val doi: String = "",
    val url: String = "",
    val abstractText: String,
    val sections: List<Section>,
    val tags: List<String>,
    val collectionIds: List<String> = emptyList(),
    val projectId: String? = null,
    val citationCount: Int = 0,
    val isBookmarked: Boolean = false,
    val readPercentage: Float = 0f,
    val dateAdded: Long = System.currentTimeMillis()
)

data class Source(
    val id: String,
    val title: String,
    val type: SourceType,
    val authorsOrSource: String,
    val url: String = "",
    val summary: String = "",
    val tags: List<String> = emptyList(),
    val dateAdded: Long = System.currentTimeMillis(),
    val collectionId: String? = null,
    val projectId: String? = null,
    val linkedPaperId: String? = null
)

data class ResearchNote(
    val id: String,
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
    val paperId: String? = null,
    val paperTitle: String? = null,
    val sectionTitle: String? = null,
    val highlightColor: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

data class PaperAnnotation(
    val id: String,
    val paperId: String,
    val sectionId: String,
    val selectedText: String,
    val note: String,
    val colorHex: String = "#FDE047", // yellow default
    val createdAt: Long = System.currentTimeMillis()
)

data class Project(
    val id: String,
    val name: String,
    val description: String,
    val colorHex: String,
    val paperCount: Int = 0,
    val notesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class PaperCollection(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val colorHex: String,
    val count: Int = 0
)

data class GraphNode(
    val id: String,
    val label: String,
    val subLabel: String? = null,
    val type: NodeType,
    var x: Float = 0f,
    var y: Float = 0f,
    val radius: Float = 26f,
    val relatedPaperId: String? = null,
    val metadata: String? = null
)

data class GraphEdge(
    val id: String,
    val fromNodeId: String,
    val toNodeId: String,
    val relationship: String, // "written_by", "cites", "explores", "references", "note_on"
    val strength: Float = 1.0f
)

enum class CitationFormat {
    BIBTEX,
    APA,
    MLA,
    CHICAGO,
    IEEE,
    HARVARD
}

data class CitationMetadata(
    val key: String,
    val title: String,
    val authors: List<String>,
    val year: Int,
    val journalOrVenue: String,
    val volume: String? = null,
    val issue: String? = null,
    val pages: String? = null,
    val doi: String? = null,
    val publisher: String? = null,
    val url: String? = null
)

data class ResearchQuestion(
    val id: String,
    val question: String,
    val context: String,
    val sourcePaperIds: List<String>,
    val keyHypothesis: String,
    val suggestedMethod: String,
    val status: String = "Open" // "Open", "Investigating", "Synthesized"
)

data class AIAnalysisResult(
    val paperId: String,
    val executiveSummary: String,
    val keyFindings: List<String>,
    val methodologyOverview: String,
    val limitations: List<String>,
    val novelResearchQuestions: List<String>,
    val extractedConcepts: List<String>,
    val extractedCitations: List<String>
)

data class PaperComparison(
    val paperA: Paper,
    val paperB: Paper,
    val synthesisSummary: String,
    val commonGround: List<String>,
    val contrastingMethodologies: List<String>,
    val divergentConclusions: List<String>,
    val synergyOpportunities: List<String>
)

data class ResearchTopic(
    val id: String,
    val title: String,
    val description: String,
    val keywords: List<String> = emptyList(),
    val paperCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

data class AIResearchQueryResult(
    val query: String,
    val title: String,
    val synthesisOverview: String,
    val keyMethodologies: List<String>,
    val openQuestions: List<String>,
    val recommendedReadings: List<String>,
    val relevantPaperIds: List<String> = emptyList(),
    val generatedTopic: ResearchTopic? = null,
    val timestamp: Long = System.currentTimeMillis()
)

