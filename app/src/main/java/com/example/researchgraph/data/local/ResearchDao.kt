package com.example.researchgraph.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ResearchDao {

    // ==========================================
    // Research Topics Operations
    // ==========================================

    @Query("SELECT * FROM research_topics ORDER BY isPinned DESC, createdAt DESC")
    fun getAllTopics(): Flow<List<ResearchTopicEntity>>

    @Query("SELECT * FROM research_topics WHERE id = :id")
    suspend fun getTopicById(id: String): ResearchTopicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: ResearchTopicEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<ResearchTopicEntity>)

    @Delete
    suspend fun deleteTopic(topic: ResearchTopicEntity)

    @Query("DELETE FROM research_topics WHERE id = :id")
    suspend fun deleteTopicById(id: String)

    @Query("UPDATE research_topics SET isPinned = NOT isPinned WHERE id = :id")
    suspend fun toggleTopicPin(id: String)

    @Query("SELECT COUNT(*) FROM research_topics")
    suspend fun getTopicCount(): Int

    // ==========================================
    // Saved Notes Operations
    // ==========================================

    @Query("SELECT * FROM saved_notes ORDER BY isPinned DESC, createdAt DESC")
    fun getAllSavedNotes(): Flow<List<SavedNoteEntity>>

    @Query("SELECT * FROM saved_notes WHERE id = :id")
    suspend fun getNoteById(id: String): SavedNoteEntity?

    @Query("SELECT * FROM saved_notes WHERE paperId = :paperId ORDER BY createdAt DESC")
    fun getNotesForPaper(paperId: String): Flow<List<SavedNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: SavedNoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<SavedNoteEntity>)

    @Delete
    suspend fun deleteNote(note: SavedNoteEntity)

    @Query("DELETE FROM saved_notes WHERE id = :id")
    suspend fun deleteNoteById(id: String)

    @Query("UPDATE saved_notes SET isPinned = NOT isPinned WHERE id = :id")
    suspend fun toggleNotePin(id: String)

    @Query("SELECT COUNT(*) FROM saved_notes")
    suspend fun getNoteCount(): Int
}
