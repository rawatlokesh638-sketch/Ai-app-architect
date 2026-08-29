package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectBlueprint
import com.example.domain.templates.StarterTemplate
import com.example.domain.templates.StarterTemplates
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onProjectSelected: (String) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.allProjects.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    val selectedTag by viewModel.selectedTagFilter.collectAsState()
    val generationMode by viewModel.selectedGenerationMode.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val currentUserEmail by viewModel.currentUserEmail.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()

    var rawIdeaInput by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: My Projects, 1: Favorites, 2: Templates
    var isNewProjectDialogOpen by remember { mutableStateOf(false) }
    var isWizardDialogOpen by remember { mutableStateOf(false) }
    var isImportDialogOpen by remember { mutableStateOf(false) }
    var isAuthDialogOpen by remember { mutableStateOf(false) }
    var isThemeModalOpen by remember { mutableStateOf(false) }

    val categories = listOf("All", "Ecommerce", "AI Chat App", "SaaS", "Marketplace", "Finance", "Fitness", "Education", "Delivery", "Social Network", "Productivity")

    val availableTags = remember(projects) {
        projects.flatMap { it.tags }.map { it.trim() }.filter { it.isNotBlank() }.distinct()
    }

    val filteredProjects = projects.filter { p ->
        val matchesQuery = searchQuery.isBlank() ||
                p.name.contains(searchQuery, ignoreCase = true) ||
                p.tagline.contains(searchQuery, ignoreCase = true) ||
                p.rawIdea.contains(searchQuery, ignoreCase = true) ||
                p.category.contains(searchQuery, ignoreCase = true) ||
                p.tags.any { it.contains(searchQuery, ignoreCase = true) }
        val matchesCategory = selectedCategory == null || selectedCategory == "All" || p.category.equals(selectedCategory, ignoreCase = true)
        val matchesTag = selectedTag == null || p.tags.any { it.equals(selectedTag, ignoreCase = true) }
        val matchesFavorite = if (selectedTab == 1) p.isFavorite else true
        matchesQuery && matchesCategory && matchesTag && matchesFavorite
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_app_logo),
                                contentDescription = "AI Software Factory Logo",
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("AI Software Factory", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp)
                                Text("Premium Software Architecture Engine", style = MaterialTheme.typography.labelSmall, color = CyanAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { isThemeModalOpen = true },
                            modifier = Modifier.testTag("theme_button")
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = "Select Theme", tint = MaterialTheme.colorScheme.primary)
                        }

                        IconButton(onClick = { isImportDialogOpen = true }) {
                            Icon(Icons.Default.UploadFile, contentDescription = "Import Codebase", tint = MaterialTheme.colorScheme.primary)
                        }

                        IconButton(onClick = { isAuthDialogOpen = true }) {
                            Icon(
                                imageVector = if (currentUserEmail != null) Icons.Default.AccountCircle else Icons.Default.PersonOutline,
                                contentDescription = "User Auth",
                                tint = if (currentUserEmail != null) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.testTag("settings_button")
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { isWizardDialogOpen = true },
                    containerColor = IndigoPrimary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("New Wizard") },
                    modifier = Modifier.testTag("fab_new_project")
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    // Hero Banner & Quick Idea Prompt Input
                    HeroIdeaSection(
                        rawIdea = rawIdeaInput,
                        onIdeaChanged = { rawIdeaInput = it },
                        generationMode = generationMode,
                        onModeSelected = { viewModel.setGenerationMode(it) },
                        onGenerate = {
                            if (rawIdeaInput.isNotBlank()) {
                                viewModel.generateNewProject(rawIdeaInput) { newId ->
                                    onProjectSelected(newId)
                                }
                            }
                        }
                    )
                }

                // Quick Stats Banner
                item {
                    StatsRow(
                        totalProjects = projects.size,
                        avgHealth = if (projects.isNotEmpty()) projects.map { it.projectHealth.overallScore }.average().toInt() else 95,
                        activeTemplates = StarterTemplates.list.size
                    )
                }

                // Tabs Selector: My Projects vs Favorites vs Starter Templates
                item {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = IndigoLight
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("My Projects (${projects.size})")
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Favorites (${projects.count { it.isFavorite }})")
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Widgets, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Templates (${StarterTemplates.list.size})")
                                }
                            }
                        )
                    }
                }

                if (selectedTab == 0 || selectedTab == 1) {
                    // Search and Category Filter Chips
                    item {
                        Column {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = { Text("Search blueprints, tech stacks, or features...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                trailingIcon = {
                                    if (searchQuery.isNotBlank()) {
                                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("search_projects_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(categories) { cat ->
                                    val isSelected = (selectedCategory == null && cat == "All") || selectedCategory == cat
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            viewModel.setCategoryFilter(if (cat == "All") null else cat)
                                        },
                                        label = { Text(cat) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = IndigoPrimary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            if (availableTags.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    item {
                                        Text("Tags:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    items(availableTags) { tag ->
                                        val isSelected = selectedTag.equals(tag, ignoreCase = true)
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                viewModel.setTagFilter(if (isSelected) null else tag)
                                            },
                                            label = { Text("#$tag") },
                                            leadingIcon = if (isSelected) {
                                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                            } else null,
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = CyanAccent,
                                                selectedLabelColor = Color.Black
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (filteredProjects.isEmpty()) {
                        item {
                            EmptyProjectsView(
                                onCreateNew = { isNewProjectDialogOpen = true }
                            )
                        }
                    } else {
                        items(filteredProjects, key = { it.id }) { project ->
                            ProjectCardItem(
                                project = project,
                                onClick = { onProjectSelected(project.id) },
                                onDelete = {
                                    viewModel.deleteProject(project.id) {}
                                },
                                onTagClick = { tag ->
                                    viewModel.setTagFilter(if (selectedTag == tag) null else tag)
                                },
                                onRegenerateSummary = {
                                    viewModel.regenerateExecutiveSummary(project)
                                },
                                onToggleFavorite = {
                                    viewModel.toggleFavorite(project.id)
                                }
                            )
                        }
                    }
                } else {
                    // Starter Templates Gallery
                    items(StarterTemplates.list, key = { it.id }) { template ->
                        TemplateCardItem(
                            template = template,
                            onUseTemplate = {
                                viewModel.generateFromTemplate(template) { newId ->
                                    onProjectSelected(newId)
                                }
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Auth Dialog
    if (isAuthDialogOpen) {
        com.example.ui.components.AuthDialog(
            currentEmail = currentUserEmail,
            onDismiss = { isAuthDialogOpen = false },
            onLoginSuccess = { email -> viewModel.loginUser(email) },
            onLogout = { viewModel.logoutUser() }
        )
    }

    // Import Codebase Modal
    if (isImportDialogOpen) {
        com.example.ui.components.ImportProjectModal(
            onDismiss = { isImportDialogOpen = false },
            onImportCodebase = { input ->
                viewModel.importExistingCodebase(input) { newId ->
                    onProjectSelected(newId)
                }
            }
        )
    }

    // 6-Step Architecture Wizard Modal
    if (isWizardDialogOpen) {
        com.example.ui.components.CreateProjectWizardModal(
            onDismiss = { isWizardDialogOpen = false },
            onGenerate = { idea, platform, expLevel, businessModel, mode ->
                val enrichedIdea = "$idea. Target Platform: $platform. Target Developer Experience: $expLevel. Monetization Strategy: $businessModel."
                viewModel.setGenerationMode(mode) // Set the selected mode globally for the next generation
                viewModel.generateNewProject(enrichedIdea, "SaaS", listOf(platform, expLevel, businessModel)) { newId ->
                    onProjectSelected(newId)
                }
            }
        )
    }

    // Theme Selector Modal
    if (isThemeModalOpen) {
        com.example.ui.components.ThemeSelectorModal(
            currentTheme = currentTheme,
            onDismiss = { isThemeModalOpen = false },
            onSelectTheme = { theme ->
                viewModel.setTheme(theme)
            }
        )
    }

    // Legacy Quick New Project Dialog
    if (isNewProjectDialogOpen) {
        NewProjectModal(
            onDismiss = { isNewProjectDialogOpen = false },
            onGenerate = { idea: String, category: String?, tags: List<String> ->
                isNewProjectDialogOpen = false
                viewModel.generateNewProject(idea, category, tags) { newId ->
                    onProjectSelected(newId)
                }
            }
        )
    }
}

@Composable
fun HeroIdeaSection(
    rawIdea: String,
    onIdeaChanged: (String) -> Unit,
    generationMode: String,
    onModeSelected: (String) -> Unit,
    onGenerate: () -> Unit
) {
    val samplePrompts = listOf(
        "Build a fitness app with AI workout plans, tracking and social sharing.",
        "Build an AI-powered conversational copilot with RAG vector search.",
        "Build a multi-tenant B2B SaaS analytics hub with metered billing."
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Engineering, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Describe your application idea.",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(
                text = "The engine will generate a complete production-grade blueprint: Requirements, UI/UX, System Architecture, Database Schema, API Specs, and a Master Implementation Prompt.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            OutlinedTextField(
                value = rawIdea,
                onValueChange = onIdeaChanged,
                placeholder = {
                    Text(
                        "e.g. \"Build a professional AI-powered fitness ecosystem with user accounts, wearable integrations, and customized nutrition planning.\"",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .testTag("hero_idea_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Mode:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            selected = generationMode == "STANDARD",
                            onClick = { onModeSelected("STANDARD") },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) {
                            Text("Standard", fontSize = 10.sp)
                        }
                        SegmentedButton(
                            selected = generationMode == "DEEP",
                            onClick = { onModeSelected("DEEP") },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Deep", fontSize = 10.sp)
                            }
                        }
                    }
                }

                Button(
                    onClick = onGenerate,
                    enabled = rawIdea.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("generate_blueprint_btn")
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate")
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(samplePrompts) { prompt ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onIdeaChanged(prompt) },
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = prompt.take(35) + "...",
                            style = MaterialTheme.typography.labelSmall,
                            color = IndigoLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsRow(totalProjects: Int, avgHealth: Int, activeTemplates: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            title = "Blueprints Built",
            value = "$totalProjects",
            icon = Icons.Default.Layers,
            tint = IndigoLight,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Avg Architecture Health",
            value = "$avgHealth%",
            icon = Icons.Default.VerifiedUser,
            tint = EmeraldSuccess,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "SaaS Templates",
            value = "$activeTemplates",
            icon = Icons.Default.AutoAwesome,
            tint = CyanAccent,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ProjectCardItem(
    project: ProjectBlueprint,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onTagClick: ((String) -> Unit)? = null,
    onRegenerateSummary: (() -> Unit)? = null,
    onToggleFavorite: (() -> Unit)? = null
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val healthColor = when {
        project.projectHealth.overallScore >= 90 -> EmeraldSuccess
        project.projectHealth.overallScore >= 75 -> AmberWarning
        else -> RoseError
    }

    val execSummaryText = if (project.executiveSummary.isNotBlank()) {
        project.executiveSummary
    } else {
        "Enterprise architecture for ${project.techStack.frontend.name} frontend and ${project.techStack.backend.name} backend, addressing ${project.ideaUnderstanding.primaryProblem.ifBlank { "core user workflows" }}."
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("project_card_${project.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge(containerColor = IndigoPrimary.copy(alpha = 0.15f), contentColor = IndigoLight) {
                        Text("v${project.version}", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 4.dp))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onToggleFavorite?.invoke() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (project.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (project.isFavorite) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Health Score Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = healthColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, healthColor.copy(alpha = 0.4f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = healthColor, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${project.projectHealth.overallScore}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = healthColor
                            )
                        }
                    }
                }
            }

            Text(
                text = project.tagline.ifBlank { project.rawIdea },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
            )

            // Prominent Executive Summary Section
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "LLM Executive Summary",
                                tint = CyanAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "EXECUTIVE SUMMARY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent,
                                letterSpacing = 0.6.sp
                            )
                        }

                        if (onRegenerateSummary != null) {
                            IconButton(
                                onClick = onRegenerateSummary,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Regenerate Summary",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = execSummaryText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            // Tech Stack Badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                StackBadge(project.techStack.frontend.name.split(" ").firstOrNull() ?: "Compose", CyanAccent)
                StackBadge(project.techStack.backend.name.split(" ").firstOrNull() ?: "Node", IndigoLight)
                StackBadge(project.techStack.database.name.split(" ").firstOrNull() ?: "PostgreSQL", EmeraldSuccess)
            }

            // User & System Tags
            if (project.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(project.tags) { tag ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.clickable { onTagClick?.invoke(tag) }
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Modified ${dateFormat.format(Date(project.lastModified))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Row {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = "Open", tint = IndigoLight, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun StackBadge(name: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun TemplateCardItem(
    template: StarterTemplate,
    onUseTemplate: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(IndigoPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = IndigoLight, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(template.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(template.category, style = MaterialTheme.typography.labelSmall, color = CyanAccent)
                    }
                }

                Button(
                    onClick = onUseTemplate,
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("use_template_${template.id}")
                ) {
                    Text("Use Template", style = MaterialTheme.typography.labelMedium)
                }
            }

            Text(
                text = template.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "Default Stack: ${template.defaultStack}",
                style = MaterialTheme.typography.labelSmall,
                color = IndigoLight
            )
        }
    }
}

@Composable
fun EmptyProjectsView(onCreateNew: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(IndigoPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Architecture, contentDescription = null, tint = IndigoLight, modifier = Modifier.size(36.dp))
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text("No Architecture Blueprints Found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            "Enter your app idea in the top prompt bar or select a starter template to generate your first blueprint.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onCreateNew,
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create New Blueprint")
        }
    }
}

@Composable
fun NewProjectModal(
    onDismiss: () -> Unit,
    onGenerate: (String, String?, List<String>) -> Unit
) {
    var idea by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Ecommerce") }
    var tagsInput by remember { mutableStateOf("") }

    val categories = listOf("Ecommerce", "AI Chat App", "SaaS", "Marketplace", "Finance", "Fitness", "Education", "Delivery", "Social Network", "Productivity")
    val suggestedTags = listOf("Mobile", "Android", "AI", "Cloud", "Fintech", "Realtime", "OfflineFirst", "GraphQL")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Architecture, contentDescription = null, tint = CyanAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create New Software Blueprint", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    "Describe the application you want to architect in plain English:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = idea,
                    onValueChange = { idea = it },
                    placeholder = { Text("e.g. \"I want to build a Meesho-like ecommerce app for local fashion sellers with WhatsApp catalogs and COD tracking.\"") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text("Select Category:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("User-Defined Tags (comma separated):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    placeholder = { Text("e.g. Mobile, WhatsApp, HighPriority, Regional") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(suggestedTags) { tag ->
                        AssistChip(
                            onClick = {
                                val current = tagsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                if (!current.contains(tag)) {
                                    tagsInput = (current + tag).joinToString(", ")
                                }
                            },
                            label = { Text("+$tag", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedTags = tagsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    onGenerate(idea, selectedCategory, parsedTags)
                },
                enabled = idea.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Text("Generate Complete Blueprint")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
