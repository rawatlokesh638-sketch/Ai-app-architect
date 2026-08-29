package com.example.data.repository

import com.example.data.json.BlueprintJsonAdapter
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ProjectEntity
import com.example.data.local.ProjectVersionEntity
import com.example.data.model.ProjectBlueprint
import com.example.domain.engine.ArchitectureEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class AppRepository(private val database: AppDatabase) {

    private val projectDao = database.projectDao()

    val allProjects: Flow<List<ProjectBlueprint>> = projectDao.getAllProjects().map { entities ->
        entities.mapNotNull { entity ->
            BlueprintJsonAdapter.fromJson(entity.blueprintJson)
        }
    }

    fun observeProject(id: String): Flow<ProjectBlueprint?> {
        return projectDao.observeProjectById(id).map { entity ->
            entity?.let { BlueprintJsonAdapter.fromJson(it.blueprintJson) }
        }
    }

    suspend fun getProject(id: String): ProjectBlueprint? = withContext(Dispatchers.IO) {
        val entity = projectDao.getProjectById(id)
        entity?.let { BlueprintJsonAdapter.fromJson(it.blueprintJson) }
    }

    suspend fun saveProject(blueprint: ProjectBlueprint, changeSummary: String = "Updated blueprint") = withContext(Dispatchers.IO) {
        val json = BlueprintJsonAdapter.toJson(blueprint)
        val entity = ProjectEntity(
            id = blueprint.id,
            name = blueprint.name,
            rawIdea = blueprint.rawIdea,
            tagline = blueprint.tagline,
            category = blueprint.category,
            version = blueprint.version,
            lastModified = blueprint.lastModified,
            healthScore = blueprint.healthScore,
            techStackSummary = "${blueprint.techStack.frontend.name} + ${blueprint.techStack.backend.name} + ${blueprint.techStack.database.name}",
            blueprintJson = json,
            tags = blueprint.tags.joinToString(","),
            executiveSummary = blueprint.executiveSummary,
            isFavorite = blueprint.isFavorite
        )
        projectDao.insertProject(entity)

        // Record version entry
        val versionEntry = ProjectVersionEntity(
            projectId = blueprint.id,
            versionNumber = blueprint.version,
            changeSummary = changeSummary,
            timestamp = blueprint.lastModified,
            blueprintJson = json
        )
        projectDao.insertVersion(versionEntry)
    }

    suspend fun deleteProject(id: String) = withContext(Dispatchers.IO) {
        projectDao.deleteProject(id)
        projectDao.clearChatMessages(id)
    }

    suspend fun duplicateProject(id: String): ProjectBlueprint? = withContext(Dispatchers.IO) {
        val original = getProject(id) ?: return@withContext null
        val duplicated = original.copy(
            id = UUID.randomUUID().toString(),
            name = "${original.name} (Copy)",
            version = "1.0.0",
            lastModified = System.currentTimeMillis()
        )
        saveProject(duplicated, "Duplicated from ${original.name}")
        duplicated
    }

    fun getVersions(projectId: String): Flow<List<ProjectVersionEntity>> {
        return projectDao.getVersionsForProject(projectId)
    }

    suspend fun restoreVersion(versionId: Long): ProjectBlueprint? = withContext(Dispatchers.IO) {
        val versionEntity = projectDao.getVersionById(versionId) ?: return@withContext null
        val blueprint = BlueprintJsonAdapter.fromJson(versionEntity.blueprintJson) ?: return@withContext null
        val updated = blueprint.copy(
            lastModified = System.currentTimeMillis()
        )
        saveProject(updated, "Restored to Version ${versionEntity.versionNumber}")
        updated
    }

    fun getChatMessages(projectId: String): Flow<List<ChatMessageEntity>> {
        return projectDao.getChatMessages(projectId)
    }

    suspend fun sendChatMessage(
        projectId: String,
        userMessage: String
    ): Pair<ProjectBlueprint, String>? = withContext(Dispatchers.IO) {
        val project = getProject(projectId) ?: return@withContext null

        // Save User Chat Message
        projectDao.insertChatMessage(
            ChatMessageEntity(
                projectId = projectId,
                sender = "USER",
                message = userMessage,
                timestamp = System.currentTimeMillis()
            )
        )

        // Modify Blueprint
        val (modifiedBlueprint, diffSummary) = ArchitectureEngine.modifyBlueprint(project, userMessage)

        // Save AI Chat Message with diff
        projectDao.insertChatMessage(
            ChatMessageEntity(
                projectId = projectId,
                sender = "AI",
                message = "I have updated the architecture for \"$userMessage\".\n\n$diffSummary",
                diffJson = diffSummary,
                timestamp = System.currentTimeMillis()
            )
        )

        // Persist updated blueprint
        saveProject(modifiedBlueprint, "AI Chat update: $userMessage")

        Pair(modifiedBlueprint, diffSummary)
    }

    suspend fun toggleFavorite(projectId: String): ProjectBlueprint? = withContext(Dispatchers.IO) {
        val original = getProject(projectId) ?: return@withContext null
        val updated = original.copy(
            isFavorite = !original.isFavorite,
            lastModified = System.currentTimeMillis()
        )
        saveProject(updated, if (updated.isFavorite) "Bookmarked as favorite" else "Removed from favorites")
        updated
    }

    suspend fun autoFixIssues(projectId: String): ProjectBlueprint? = withContext(Dispatchers.IO) {
        val project = getProject(projectId) ?: return@withContext null
        val (fixedBlueprint, _) = ArchitectureEngine.modifyBlueprint(project, "Auto-fix architecture consistency issues")
        saveProject(fixedBlueprint, "Automated consistency fix applied")
        fixedBlueprint
    }

    suspend fun runHealthAudit(projectId: String): ProjectBlueprint? = withContext(Dispatchers.IO) {
        val project = getProject(projectId) ?: return@withContext null
        val audited = ArchitectureEngine.runHealthAudit(project)
        saveProject(audited, "Performed Engineering Health Audit")
        audited
    }

    suspend fun generateRoadmap(projectId: String): ProjectBlueprint? = withContext(Dispatchers.IO) {
        val project = getProject(projectId) ?: return@withContext null
        val updated = ArchitectureEngine.generateRoadmap(project)
        saveProject(updated, "Generated Product Roadmap")
        updated
    }

    suspend fun runBugHunter(projectId: String): ProjectBlueprint? = withContext(Dispatchers.IO) {
        val project = getProject(projectId) ?: return@withContext null
        val updated = ArchitectureEngine.runBugHunter(project)
        saveProject(updated, "Performed Deep QA Bug Hunting Audit")
        updated
    }

    suspend fun generateFileCode(projectId: String, filePath: String): String = withContext(Dispatchers.IO) {
        val project = getProject(projectId) ?: return@withContext "// Project not found"
        ArchitectureEngine.generateFileCode(project, filePath)
    }

    suspend fun updateFileContent(projectId: String, filePath: String, content: String): ProjectBlueprint? = withContext(Dispatchers.IO) {
        val project = getProject(projectId) ?: return@withContext null
        val updatedSpecs = project.fileSpecifications.map {
            if (it.filePath == filePath) it.copy(content = content) else it
        }
        val updated = project.copy(fileSpecifications = updatedSpecs, lastModified = System.currentTimeMillis())
        saveProject(updated, "Updated content for $filePath")
        updated
    }

    suspend fun runBuildSimulation(projectId: String): ProjectBlueprint? = withContext(Dispatchers.IO) {
        val project = getProject(projectId) ?: return@withContext null
        val updated = ArchitectureEngine.runBuildSimulation(project)
        saveProject(updated, "Ran Build Simulation")
        updated
    }

    suspend fun createSnapshot(projectId: String, reason: String): ProjectBlueprint? = withContext(Dispatchers.IO) {
        val project = getProject(projectId) ?: return@withContext null
        val updated = ArchitectureEngine.createSnapshot(project, reason)
        saveProject(updated, "Created Project Snapshot: $reason")
        updated
    }

    suspend fun restoreSnapshot(projectId: String, snapshotId: String): ProjectBlueprint? = withContext(Dispatchers.IO) {
        val project = getProject(projectId) ?: return@withContext null
        val snapshot = project.snapshots.find { it.id == snapshotId } ?: return@withContext null
        val restored = BlueprintJsonAdapter.fromJson(snapshot.blueprintJson) ?: return@withContext null
        saveProject(restored, "Restored Snapshot: ${snapshot.reason}")
        restored
    }

    fun getSettings(): Flow<com.example.data.local.AppSettings?> {
        return projectDao.getSettings()
    }

    suspend fun saveSettings(settings: com.example.data.local.AppSettings) = withContext(Dispatchers.IO) {
        projectDao.saveSettings(settings)
    }
}
