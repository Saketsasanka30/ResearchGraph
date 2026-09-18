package com.example.researchgraph.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "research_topics")
data class ResearchTopicEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val keywords: String = "",
    val paperCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)
