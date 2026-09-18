package com.example.researchgraph.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_notes")
data class SavedNoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val tags: String = "",
    val paperId: String? = null,
    val paperTitle: String? = null,
    val sectionTitle: String? = null,
    val highlightColor: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)
