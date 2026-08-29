package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ProjectVersionEntity
import com.example.data.model.AssumptionItem
import com.example.data.model.ProjectBlueprint
import com.example.data.repository.AppRepository
import com.example.domain.engine.ArchitectureEngine
import com.example.domain.engine.GeminiSummaryGenerator
import com.example.domain.templates.StarterTemplate
import com.example.domain.templates.StarterTemplates
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    data class Generating(val step: String = "Analyzing product idea...", val progress: Float = 0f) : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "app_architect_db"
    ).fallbackToDestructiveMigration().build()

    private val repository = AppRepository(db)
    private val generationPipeline = com.example.domain.engine.GenerationPipeline(com.example.domain.engine.GeminiClient)

    val allProjects: StateFlow<List<ProjectBlueprint>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentProject = MutableStateFlow<ProjectBlueprint?>(null)
    val currentProject: StateFlow<ProjectBlueprint?> = _currentProject.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    private val _selectedTagFilter = MutableStateFlow<String?>(null)
    val selectedTagFilter: StateFlow<String?> = _selectedTagFilter.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessageEntity>> = _chatMessages.asStateFlow()

    private val _versions = MutableStateFlow<List<ProjectVersionEntity>>(emptyList())
    val versions: StateFlow<List<ProjectVersionEntity>> = _versions.asStateFlow()

    private val _selectedGenerationMode = MutableStateFlow("DEEP")
    val selectedGenerationMode: StateFlow<String> = _selectedGenerationMode.asStateFlow()

    private val _currentUserEmail = MutableStateFlow<String?>("rawatlokesh638@gmail.com")
    val currentUserEmail: StateFlow<String?> = _currentUserEmail.asStateFlow()

    private val _currentTheme = MutableStateFlow(com.example.ui.theme.AppThemePreset.CYBER_INDIGO)
    val currentTheme: StateFlow<com.example.ui.theme.AppThemePreset> = _currentTheme.asStateFlow()

    fun setGenerationMode(mode: String) {
        _selectedGenerationMode.value = mode
    }

    fun setTheme(theme: com.example.ui.theme.AppThemePreset) {
        _currentTheme.value = theme
    }

    fun loginUser(email: String) {
        _currentUserEmail.value = email
    }

    fun logoutUser() {
        _currentUserEmail.value = null
    }

    fun toggleFavorite(projectId: String) {
        viewModelScope.launch {
            val updated = repository.toggleFavorite(projectId)
            if (_currentProject.value?.id == projectId && updated != null) {
                _currentProject.value = updated
            }
        }
    }

    fun importExistingCodebase(input: String, onCompleted: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState.Generating("Analyzing existing repository structure & technical debt...", 0.1f)
            kotlinx.coroutines.delay(500)
            try {
                val (report, blueprint) = com.example.domain.engine.ExistingProjectAnalyzer.analyzeCodebase(input)
                repository.saveProject(blueprint, "Imported from existing codebase")
                _currentProject.value = blueprint
                _uiState.value = UiState.Success("Existing codebase analyzed & architecture report generated!")
                observeProjectDetails(blueprint.id)
                onCompleted(blueprint.id)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to import codebase.")
            }
        }
    }

    init {
        // Load settings and restore state
        viewModelScope.launch {
            repository.getSettings().collect { settings ->
                if (settings != null) {
                    _selectedGenerationMode.value = settings.generationMode
                    _currentTheme.value = com.example.ui.theme.AppThemePreset.values().find { it.name.lowercase() == settings.currentThemeId } ?: com.example.ui.theme.AppThemePreset.CYBER_INDIGO
                    
                    if (_currentProject.value == null && settings.lastActiveProjectId != null) {
                        selectProjectById(settings.lastActiveProjectId)
                    }
                }
            }
        }

        // Backfill executive summary for existing projects if blank
        viewModelScope.launch {
            allProjects.take(1).collect { list ->
                if (list.isNotEmpty()) {
                    list.filter { it.executiveSummary.isBlank() }.forEach { project ->
                        launch {
                            val newSummary = GeminiSummaryGenerator.generateExecutiveSummary(project)
                            val updated = project.copy(executiveSummary = newSummary)
                            repository.saveProject(updated, "Auto-generated LLM Executive Summary")
                        }
                    }
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategoryFilter.value = category
    }

    fun setTagFilter(tag: String?) {
        _selectedTagFilter.value = tag
    }

    fun selectProject(project: ProjectBlueprint) {
        _currentProject.value = project
        observeProjectDetails(project.id)
        saveCurrentSession()
    }

    fun selectProjectById(id: String) {
        viewModelScope.launch {
            val p = repository.getProject(id)
            if (p != null) {
                _currentProject.value = p
                observeProjectDetails(id)
                saveCurrentSession()
            }
        }
    }

    private fun saveCurrentSession() {
        val projectId = _currentProject.value?.id ?: return
        viewModelScope.launch {
            val currentSettings = repository.getSettings().firstOrNull() ?: com.example.data.local.AppSettings()
            repository.saveSettings(currentSettings.copy(
                lastActiveProjectId = projectId,
                currentThemeId = _currentTheme.value.name.lowercase(),
                generationMode = _selectedGenerationMode.value
            ))
        }
    }

    private fun observeProjectDetails(projectId: String) {
        viewModelScope.launch {
            repository.observeProject(projectId).collect { updated ->
                if (updated != null) {
                    _currentProject.value = updated
                }
            }
        }
        viewModelScope.launch {
            repository.getChatMessages(projectId).collect { messages ->
                _chatMessages.value = messages
            }
        }
        viewModelScope.launch {
            repository.getVersions(projectId).collect { verList ->
                _versions.value = verList
            }
        }
    }

    fun generateNewProject(
        rawIdea: String,
        category: String? = null,
        userDefinedTags: List<String> = emptyList(),
        onCompleted: (String) -> Unit = {}
    ) {
        if (rawIdea.isBlank()) {
            _uiState.value = UiState.Error("Please describe your app idea.")
            return
        }

        if (_uiState.value is UiState.Generating) return // Prevent duplicate requests

        viewModelScope.launch {
            val mode = _selectedGenerationMode.value
            val totalStages = com.example.domain.engine.GenerationStage.values().size
            
            generationPipeline.generate(rawIdea, mode).collect { progress ->
                if (progress.error != null) {
                    _uiState.value = UiState.Error(progress.error)
                } else {
                    val progressValue = (progress.stage.ordinal + 1).toFloat() / totalStages
                    _uiState.value = UiState.Generating(progress.stage.label, progressValue)
                    
                    if (progress.project != null) {
                        _currentProject.value = progress.project
                        // We save intermediate states for resume capability
                        repository.saveProject(progress.project, "Generating stage: ${progress.stage.label}")
                    }
                    
                    if (progress.stage == com.example.domain.engine.GenerationStage.FINALIZING && progress.project != null) {
                        _uiState.value = UiState.Success("Architecture blueprint created successfully!")
                        observeProjectDetails(progress.project.id)
                        onCompleted(progress.project.id)
                    }
                }
            }
        }
    }

    fun updateProjectTags(tags: List<String>) {
        val current = _currentProject.value ?: return
        val updated = current.copy(
            tags = tags.map { it.trim() }.filter { it.isNotBlank() }.distinct(),
            lastModified = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.saveProject(updated, "Updated project tags: ${tags.joinToString(", ")}")
            _currentProject.value = updated
        }
    }

    fun regenerateExecutiveSummary(project: ProjectBlueprint) {
        viewModelScope.launch {
            val newSummary = GeminiSummaryGenerator.generateExecutiveSummary(project)
            val updated = project.copy(
                executiveSummary = newSummary,
                lastModified = System.currentTimeMillis()
            )
            repository.saveProject(updated, "Regenerated LLM Executive Summary")
            if (_currentProject.value?.id == project.id) {
                _currentProject.value = updated
            }
        }
    }

    fun generateFromTemplate(template: StarterTemplate, onCompleted: (String) -> Unit) {
        generateNewProject(
            rawIdea = template.suggestedPrompt,
            category = template.category,
            userDefinedTags = listOf(template.category),
            onCompleted = onCompleted
        )
    }

    fun sendChatMessage(userPrompt: String) {
        val project = _currentProject.value ?: return
        if (userPrompt.isBlank()) return

        viewModelScope.launch {
            _uiState.value = UiState.Generating("Autonomous Architect analyzing project state...")
            val result = repository.sendChatMessage(project.id, userPrompt)
            if (result != null) {
                _currentProject.value = result.first
                _uiState.value = UiState.Success("Architecture update applied successfully!")
            } else {
                _uiState.value = UiState.Error("Autonomous modification failed. Please check your prompt.")
            }
        }
    }

    fun runHealthAudit() {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Generating("Performing deep architectural health audit...")
            val updated = repository.runHealthAudit(project.id)
            if (updated != null) {
                _currentProject.value = updated
                _uiState.value = UiState.Success("Health report generated!")
            } else {
                _uiState.value = UiState.Error("Health audit failed.")
            }
        }
    }

    fun generateRoadmap() {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Generating("AI Product Strategist computing future roadmap...")
            val updated = repository.generateRoadmap(project.id)
            if (updated != null) {
                _currentProject.value = updated
                _uiState.value = UiState.Success("Strategic roadmap updated!")
            } else {
                _uiState.value = UiState.Error("Roadmap generation failed.")
            }
        }
    }

    fun runBugHunter() {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Generating("AI QA Hunter scanning for bugs & vulnerabilities...")
            val updated = repository.runBugHunter(project.id)
            if (updated != null) {
                _currentProject.value = updated
                _uiState.value = UiState.Success("QA Audit complete! Check the health dashboard.")
            } else {
                _uiState.value = UiState.Error("Bug hunting session failed.")
            }
        }
    }

    fun autoFixInconsistencies() {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Generating("Auto-resolving consistency checks & security policies...")
            val fixed = repository.autoFixIssues(project.id)
            if (fixed != null) {
                _currentProject.value = fixed
                _uiState.value = UiState.Success("Consistency fixes applied!")
            }
        }
    }

    fun updateAssumption(item: AssumptionItem, newChoice: String) {
        val project = _currentProject.value ?: return
        val updatedAssumptions = project.assumptions.map {
            if (it.id == item.id) it.copy(currentChoice = newChoice) else it
        }

        viewModelScope.launch {
            val updatedBlueprint = project.copy(
                assumptions = updatedAssumptions,
                lastModified = System.currentTimeMillis()
            )
            repository.saveProject(updatedBlueprint, "Updated assumption: ${item.category} -> $newChoice")
            _currentProject.value = updatedBlueprint
        }
    }

    fun restoreVersion(versionId: Long) {
        viewModelScope.launch {
            val restored = repository.restoreVersion(versionId)
            if (restored != null) {
                _currentProject.value = restored
                _uiState.value = UiState.Success("Restored to previous version!")
            }
        }
    }

    fun duplicateCurrentProject() {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            val dup = repository.duplicateProject(project.id)
            if (dup != null) {
                _currentProject.value = dup
                observeProjectDetails(dup.id)
                _uiState.value = UiState.Success("Project duplicated!")
            }
        }
    }

    fun generateFileCode(filePath: String) {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Generating("AI Engineer writing code for $filePath...")
            val code = repository.generateFileCode(project.id, filePath)
            val updated = repository.updateFileContent(project.id, filePath, code)
            if (updated != null) {
                _currentProject.value = updated
                _uiState.value = UiState.Success("Code generated for $filePath")
            } else {
                _uiState.value = UiState.Error("Failed to update file content.")
            }
        }
    }

    fun runBuildSimulation() {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Generating("Simulating build, lint, and test cycles...")
            val updated = repository.runBuildSimulation(project.id)
            if (updated != null) {
                _currentProject.value = updated
                _uiState.value = UiState.Success("Build cycle complete!")
            } else {
                _uiState.value = UiState.Error("Build simulation failed.")
            }
        }
    }

    fun createSnapshot(reason: String) {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            val updated = repository.createSnapshot(project.id, reason)
            if (updated != null) {
                _currentProject.value = updated
            }
        }
    }

    fun restoreSnapshot(snapshotId: String) {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Generating("Restoring project snapshot...")
            val restored = repository.restoreSnapshot(project.id, snapshotId)
            if (restored != null) {
                _currentProject.value = restored
                _uiState.value = UiState.Success("Project restored!")
            } else {
                _uiState.value = UiState.Error("Snapshot restoration failed.")
            }
        }
    }

    fun updateFileContent(filePath: String, content: String) {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            val updated = repository.updateFileContent(project.id, filePath, content)
            if (updated != null) {
                _currentProject.value = updated
                _uiState.value = UiState.Success("File saved.")
            }
        }
    }

    fun improveApp() {
        sendChatMessage("Deeply analyze this project and improve the overall architecture, UX consistency, and production readiness. Add missing features that would make this product successful.")
    }

    fun deleteProject(projectId: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            if (_currentProject.value?.id == projectId) {
                _currentProject.value = null
            }
            onDeleted()
        }
    }

    fun clearUiState() {
        _uiState.value = UiState.Idle
    }
}
