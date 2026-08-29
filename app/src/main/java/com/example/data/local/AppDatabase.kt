package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY lastModified DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE tags LIKE '%' || :tag || '%' ORDER BY lastModified DESC")
    fun getProjectsByTag(tag: String): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    fun observeProjectById(id: String): Flow<ProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: String)

    // Version history
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: ProjectVersionEntity)

    @Query("SELECT * FROM project_versions WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getVersionsForProject(projectId: String): Flow<List<ProjectVersionEntity>>

    @Query("SELECT * FROM project_versions WHERE versionId = :versionId LIMIT 1")
    suspend fun getVersionById(versionId: Long): ProjectVersionEntity?

    // Chat messages
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE projectId = :projectId ORDER BY timestamp ASC")
    fun getChatMessages(projectId: String): Flow<List<ChatMessageEntity>>

    @Query("DELETE FROM chat_messages WHERE projectId = :projectId")
    suspend fun clearChatMessages(projectId: String)

    @Query("SELECT * FROM app_settings WHERE id = 0")
    fun getSettings(): Flow<AppSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettings)
}

@Database(
    entities = [
        ProjectEntity::class,
        ProjectVersionEntity::class,
        ChatMessageEntity::class,
        AppSettings::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
}
