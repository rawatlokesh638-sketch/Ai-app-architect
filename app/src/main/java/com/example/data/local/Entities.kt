package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val rawIdea: String,
    val tagline: String,
    val category: String,
    val version: String,
    val lastModified: Long,
    val healthScore: Int,
    val techStackSummary: String,
    val blueprintJson: String,
    val tags: String = "",
    val executiveSummary: String = "",
    val isFavorite: Boolean = false
)

@Entity(tableName = "project_versions")
data class ProjectVersionEntity(
    @PrimaryKey(autoGenerate = true) val versionId: Long = 0,
    val projectId: String,
    val versionNumber: String,
    val changeSummary: String,
    val timestamp: Long,
    val blueprintJson: String
)

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 0,
    val lastActiveProjectId: String? = null,
    val currentThemeId: String = "cyber_indigo",
    val activeTab: Int = 0,
    val generationMode: String = "DEEP"
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: String,
    val sender: String, // "USER", "AI"
    val message: String,
    val diffJson: String = "",
    val timestamp: Long
)
