package com.example.researchgraph.data

import com.example.researchgraph.model.CitationFormat
import com.example.researchgraph.model.Paper

object CitationHelper {

    fun formatCitation(paper: Paper, format: CitationFormat): String {
        val firstAuthor = paper.authors.firstOrNull() ?: "Unknown"
        val authorsList = paper.authors.joinToString(", ")
        val year = paper.year
        val title = paper.title
        val venue = paper.publicationVenue
        val doi = paper.doi

        return when (format) {
            CitationFormat.BIBTEX -> {
                val citeKey = (firstAuthor.split(" ").lastOrNull()?.lowercase() ?: "author") + year + title.take(4).lowercase().filter { it.isLetter() }
                """
@article{$citeKey,
  title     = {$title},
  author    = {${paper.authors.joinToString(" and ")}},
  journal   = {$venue},
  year      = {$year},
  doi       = {$doi},
  url       = {${paper.url}}
}
                """.trimIndent()
            }
            CitationFormat.APA -> {
                val apaAuthors = paper.authors.joinToString(", ") { author ->
                    val parts = author.split(" ")
                    if (parts.size > 1) "${parts.last()}, ${parts.first().first()}." else author
                }
                "$apaAuthors ($year). $title. $venue. https://doi.org/$doi"
            }
            CitationFormat.MLA -> {
                val mlaAuthors = if (paper.authors.size > 2) {
                    val parts = firstAuthor.split(" ")
                    "${parts.last()}, ${parts.first()}, et al."
                } else {
                    paper.authors.joinToString(" and ")
                }
                "$mlaAuthors \"$title.\" $venue, $year. DOI: $doi."
            }
            CitationFormat.CHICAGO -> {
                val chicagoAuthors = paper.authors.joinToString(", ")
                "$chicagoAuthors. \"$title.\" $venue ($year). https://doi.org/$doi."
            }
            CitationFormat.IEEE -> {
                val ieeeAuthors = paper.authors.joinToString(", ") { author ->
                    val parts = author.split(" ")
                    if (parts.size > 1) "${parts.first().first()}. ${parts.last()}" else author
                }
                "$ieeeAuthors, \"$title,\" $venue, $year, doi: $doi."
            }
            CitationFormat.HARVARD -> {
                val harvardAuthors = paper.authors.joinToString(", ") { author ->
                    val parts = author.split(" ")
                    if (parts.size > 1) "${parts.last()}, ${parts.first().first()}." else author
                }
                "$harvardAuthors ($year) '$title', $venue. Available at: ${paper.url} [Accessed ${java.time.LocalDate.now()}]."
            }
        }
    }

    fun generateBibliography(papers: List<Paper>, format: CitationFormat): String {
        val separator = if (format == CitationFormat.BIBTEX) "\n\n" else "\n\n"
        return papers.joinToString(separator) { formatCitation(it, format) }
    }
}
