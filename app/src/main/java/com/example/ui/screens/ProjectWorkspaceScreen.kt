package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectWorkspaceScreen(
    projectId: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(projectId) {
        viewModel.selectProjectById(projectId)
    }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val project by viewModel.currentProject.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val versions by viewModel.versions.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()

    var selectedTabIdx by remember { mutableIntStateOf(0) }
    var isExportDialogOpen by remember { mutableStateOf(false) }
    var isVersionDialogOpen by remember { mutableStateOf(false) }
    var isThemeModalOpen by remember { mutableStateOf(false) }
    var selectedFileSpec by remember { mutableStateOf<FileSpecification?>(null) }

    val tabs = listOf(
        "Product Overview" to Icons.Default.RocketLaunch,
        "Engineering Health" to Icons.Default.HealthAndSafety,
        "Strategic Roadmap" to Icons.Default.Map,
        "Requirements" to Icons.Default.Gavel,
        "UI/UX Design" to Icons.Default.DesignServices,
        "Technical Architecture" to Icons.Default.AccountTree,
        "Database Studio" to Icons.Default.Storage,
        "API Studio" to Icons.Default.Http,
        "Project Structure" to Icons.Default.FolderOpen,
        "Code Specification" to Icons.Default.Code,
        "Environment & Secrets" to Icons.Default.Key,
        "Quality & Security" to Icons.Default.VerifiedUser,
        "Scale & Cost" to Icons.Default.Assessment,
        "Deployment Center" to Icons.Default.CloudUpload,
        "Integrations" to Icons.Default.IntegrationInstructions,
        "Master Prompt" to Icons.Default.AutoAwesome
    )

    if (project == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = IndigoPrimary)
        }
        return
    }

    val currentProject = project!!

    var isRightPanelOpen by remember { mutableStateOf(false) }
    var rightPanelContent by remember { mutableStateOf("AI") } // AI, PREVIEW, INSPECTOR

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = IndigoPrimary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.Architecture, contentDescription = null, tint = CyanAccent, modifier = Modifier.padding(6.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(currentProject.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = EmeraldSuccess.copy(alpha = 0.1f),
                                    modifier = Modifier.padding(end = 6.dp)
                                ) {
                                    Text("v${currentProject.version}", style = MaterialTheme.typography.labelSmall, color = EmeraldSuccess, modifier = Modifier.padding(horizontal = 4.dp))
                                }
                                Text("FACTORY • ${currentProject.category.uppercase()}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Quick Action Group
                    Row(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp)).padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { isVersionDialogOpen = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.History, contentDescription = "History", modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = { isThemeModalOpen = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Palette, contentDescription = "Themes", modifier = Modifier.size(18.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    FilledTonalButton(
                        onClick = { isExportDialogOpen = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share")
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Button(
                        onClick = { 
                            isRightPanelOpen = !isRightPanelOpen
                            rightPanelContent = "AI"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRightPanelOpen && rightPanelContent == "AI") IndigoPrimary else MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = if (isRightPanelOpen && rightPanelContent == "AI") Color.White else MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp).padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Assistant")
                    }
                }
            )
        },
        bottomBar = {
            FactoryStatusBar(currentProject)
        }
    ) { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            // Sidebar Navigation (Left)
            Surface(
                modifier = Modifier.width(260.dp).fillMaxHeight().drawBehind {
                    drawLine(
                        color = CardBorderDark,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                },
                color = MaterialTheme.colorScheme.surface
            ) {
                Column {
                    Text(
                        "WORKSPACE", 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        letterSpacing = 1.sp
                    )
                    
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        itemsIndexed(tabs) { idx, (label, icon) ->
                            SidebarItem(
                                label = label,
                                icon = icon,
                                isSelected = selectedTabIdx == idx,
                                onClick = { selectedTabIdx = idx }
                            )
                        }
                    }
                }
            }

            // Main Content Area (Center)
            Box(modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.background)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Internal Workspace Header
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(tabs[selectedTabIdx].first, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                            Text("Factory Stage: Architectural Blueprinting", style = MaterialTheme.typography.labelSmall, color = CyanAccent)
                        }
                        
                        // Workspace Actions
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (selectedTabIdx == 10 || selectedTabIdx == 11) { // Database or API
                                OutlinedButton(onClick = {}, shape = RoundedCornerShape(8.dp)) {
                                    Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("SQL/Specs")
                                }
                            }
                        }
                    }
                    
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                        color = Color.Transparent
                    ) {
                        when (selectedTabIdx) {
                            0 -> OverviewTabContent(
                                p = currentProject,
                                onUpdateTags = { viewModel.updateProjectTags(it) },
                                onRegenerateSummary = { viewModel.regenerateExecutiveSummary(currentProject) }
                            )
                            1 -> HealthDashboardTabContent(
                                p = currentProject,
                                onRunAudit = { viewModel.runHealthAudit() },
                                onRunBugHunter = { viewModel.runBugHunter() }
                            )
                            2 -> RoadmapTabContent(
                                roadmap = currentProject.projectRoadmap,
                                onGenerate = { viewModel.generateRoadmap() }
                            )
                            3 -> RequirementsTabContent(currentProject.requirements)
                            4 -> ScreenBuilderTabContent(currentProject.uxArchitecture, currentProject.designSystem)
                            5 -> ArchitectureTabContent(currentProject.systemArchitecture, currentProject.techStack)
                            6 -> DatabaseTabContent(currentProject.databaseSchema)
                            7 -> ApiDesignTabContent(currentProject.apiDesign)
                            8 -> DirectoryTabContent(currentProject.directoryTree, currentProject.fileSpecifications) {
                                selectedFileSpec = it
                                selectedTabIdx = 9
                            }
                            9 -> FileSpecsTabContent(currentProject.fileSpecifications, initialSelectedFile = selectedFileSpec)
                            10 -> EnvironmentTabContent(currentProject.environmentVariables)
                            11 -> SecurityTabContent(currentProject.securityPlan, currentProject.qualityReport, onAutoFix = { viewModel.autoFixInconsistencies() })
                            12 -> ScaleAndCostTabContent(currentProject.scalabilityPlan, currentProject.costEstimates, currentProject.costComplexity)
                            13 -> DeploymentTabContent(currentProject.deploymentPlan, currentProject.buildPlan)
                            14 -> IntegrationsTabContent(currentProject.integrations)
                            15 -> MasterPromptTabContent(currentProject)
                        }
                    }
                }
            }
            
            // Right Panel (Context/Assistant)
            AnimatedVisibility(visible = isRightPanelOpen) {
                Surface(
                    modifier = Modifier.width(380.dp).fillMaxHeight().drawBehind {
                        drawLine(
                            color = CardBorderDark,
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    },
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("AI ARCHITECT LAB", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            IconButton(onClick = { isRightPanelOpen = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                            }
                        }
                        
                        AiArchitectChatTabContent(
                            messages = chatMessages,
                            onSend = { viewModel.sendChatMessage(it) }
                        )
                    }
                }
            }
        }
    }

    if (isExportDialogOpen) {
        ExportDialog(
            blueprint = currentProject,
            onDismiss = { isExportDialogOpen = false }
        )
    }

    if (isVersionDialogOpen) {
        VersionHistoryDialog(
            currentVersion = currentProject.version,
            versions = versions,
            onRestoreVersion = { id -> viewModel.restoreVersion(id) },
            onDuplicateProject = { viewModel.duplicateCurrentProject() },
            onDismiss = { isVersionDialogOpen = false }
        )
    }

    if (isThemeModalOpen) {
        ThemeSelectorModal(
            currentTheme = currentTheme,
            onDismiss = { isThemeModalOpen = false },
            onSelectTheme = { theme -> viewModel.setTheme(theme) }
        )
    }
}

// ================= TAB: OVERVIEW =================
@Composable
fun OverviewTabContent(
    p: ProjectBlueprint,
    onUpdateTags: (List<String>) -> Unit,
    onRegenerateSummary: () -> Unit
) {
    var isEditingTags by remember { mutableStateOf(false) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            SectionCard(title = "Executive Summary", icon = Icons.Default.AutoAwesome) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        p.executiveSummary.ifBlank { "Generating high-level architecture overview..." },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onRegenerateSummary) {
                        Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = CyanAccent)
                    }
                }
            }
        }
        item {
            SectionCard(title = "Primary Goal & Vision", icon = Icons.Default.Lightbulb) {
                Text(p.tagline, style = MaterialTheme.typography.titleMedium, color = IndigoLight)
                Spacer(modifier = Modifier.height(4.dp))
                Text(p.rawIdea, style = MaterialTheme.typography.bodySmall)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HealthScoreCard(
                    modifier = Modifier.weight(1f), 
                    qualityReport = p.qualityReport, 
                    health = p.projectHealth,
                    onAutoFixClick = {}
                )
                Surface(
                    modifier = Modifier.weight(1f).height(120.dp).clip(RoundedCornerShape(16.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.Center) {
                        Text("Category", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(p.category, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CyanAccent)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${p.features.size} Features • ${p.uxArchitecture.screens.size} Screens", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
        item {
            SectionCard(title = "Project Metadata & Tags", icon = Icons.Default.Tag) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp, modifier = Modifier.weight(1f)) {
                        p.tags.forEach { tag ->
                            AssistChip(onClick = {}, label = { Text("#$tag") })
                        }
                        if (p.tags.isEmpty()) Text("No tags assigned.", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { isEditingTags = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Tags")
                    }
                }
            }
        }
    }

    if (isEditingTags) {
        EditTagsDialog(
            currentTags = p.tags,
            onDismiss = { isEditingTags = false },
            onSave = {
                onUpdateTags(it)
                isEditingTags = false
            }
        )
    }
}

@Composable
fun StrategyTabContent(p: ProjectBlueprint) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            SectionCard(title = "Market Positioning", icon = Icons.Default.GpsFixed) {
                Text(p.productStrategy.positioning, style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            SectionCard(title = "Value Proposition", icon = Icons.Default.Star) {
                Text(p.productStrategy.valueProposition, style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            SectionCard(title = "Monetization Strategy", icon = Icons.Default.Payments) {
                p.productStrategy.monetizationPossibilities.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
            }
        }
    }
}

@Composable
fun RequirementsTabContent(req: ProjectRequirements) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            SectionCard(title = "Functional Requirements", icon = Icons.Default.Settings) {
                req.functional.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
            }
        }
        item {
            SectionCard(title = "Non-Functional Requirements", icon = Icons.Default.Security) {
                req.nonFunctional.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
            }
        }
    }
}

@Composable
fun FeaturesTabContent(features: List<FeatureItem>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(features) { feature ->
            Surface(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(feature.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Badge(containerColor = IndigoPrimary.copy(alpha = 0.1f), contentColor = IndigoLight) {
                            Text(feature.priority, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                    Text(feature.purpose, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Workflow: ${feature.workflow}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun UserRolesTabContent(roles: List<UserRole>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(roles) { role ->
            SectionCard(title = role.name, icon = Icons.Default.Person) {
                Text(role.description, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Permissions:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                role.permissions.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

@Composable
fun ScreensTabContent(ux: UxArchitecture) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(ux.screens) { screen ->
            Surface(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(screen.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Route: ${screen.route}", style = MaterialTheme.typography.labelSmall, color = CyanAccent)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(screen.description, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Components:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    screen.components.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
    }
}

@Composable
fun TechStackTabContent(stack: TechStack) {
    val choices = listOf(
        "Frontend" to stack.frontend,
        "Backend" to stack.backend,
        "Database" to stack.database,
        "Auth" to stack.authentication,
        "AI" to stack.aiModels,
        "Deployment" to stack.deployment
    )
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(choices) { (label, choice) ->
            SectionCard(title = label, icon = Icons.Default.Layers) {
                Text(choice.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CyanAccent)
                Text(choice.justification, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ArchitectureTabContent(arch: SystemArchitecture) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            SectionCard(title = "Architecture Overview", icon = Icons.Default.AccountTree) {
                Text(arch.overview, style = MaterialTheme.typography.bodyMedium)
            }
        }
        items(arch.layers) { layer ->
            SectionCard(title = layer.name, icon = Icons.Default.Layers) {
                Text(layer.description, style = MaterialTheme.typography.bodySmall)
                Text("Components: ${layer.components.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun DatabaseTabContent(db: DatabaseSchema) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            SectionCard(title = "Database Strategy", icon = Icons.Default.Storage) {
                Text("Type: ${db.databaseType}", style = MaterialTheme.typography.titleMedium, color = CyanAccent)
                db.productionRecommendations.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
            }
        }
        items(db.entities) { entity ->
            SectionCard(title = "Table: ${entity.tableName}", icon = Icons.Default.TableChart) {
                Text(entity.description, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                entity.fields.forEach { field ->
                    Text("${field.name}: ${field.type} ${if (field.isPrimaryKey) "(PK)" else ""}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun ApiDesignTabContent(endpoints: List<ApiEndpoint>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(endpoints) { endpoint ->
            Surface(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(endpoint.path, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Badge(containerColor = EmeraldSuccess.copy(alpha = 0.1f), contentColor = EmeraldSuccess) {
                            Text(endpoint.method, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                    Text(endpoint.purpose, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun SecurityTabContent(security: SecurityPlan) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            SectionCard(title = "Authentication & Authorization", icon = Icons.Default.VerifiedUser) {
                InfoRow("Strategy", security.authenticationStrategy)
                Text("Rules:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                security.authorizationRules.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
            }
        }
        item {
            SectionCard(title = "Data Protection", icon = Icons.Default.Lock) {
                InfoRow("Secrets Management", security.secretsManagement)
                InfoRow("Privacy & Encryption", security.privacyAndEncryption)
            }
        }
    }
}

@Composable
fun AdminTabContent(admin: AdminArchitecture) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            SectionCard(title = "Admin Overview", icon = Icons.Default.AdminPanelSettings) {
                Text(admin.overview, style = MaterialTheme.typography.bodyMedium)
            }
        }
        items(admin.panels) { panel ->
            SectionCard(title = panel.name, icon = Icons.Default.DashboardCustomize) {
                Text(panel.description, style = MaterialTheme.typography.bodySmall)
                Text("Actions: ${panel.actions.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun DirectoryTabContent(
    root: DirectoryNode,
    specs: List<FileSpecification>,
    onFileClick: (FileSpecification) -> Unit
) {
    var expandedFolders by remember { mutableStateOf(setOf("root")) }

    fun toggleFolder(name: String) {
        expandedFolders = if (expandedFolders.contains(name)) expandedFolders - name else expandedFolders + name
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        item {
            Text("Complete Project Structure", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }
        renderDirectory(root, 0, expandedFolders, onToggle = { toggleFolder(it) }) { name ->
            val spec = specs.find { it.filePath.endsWith(name) }
            if (spec != null) onFileClick(spec)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
fun LazyListScope.renderDirectory(
    node: DirectoryNode,
    indent: Int,
    expandedFolders: Set<String>,
    onToggle: (String) -> Unit,
    onFileClick: (String) -> Unit
) {
    item {
        Row(
            modifier = Modifier.fillMaxWidth().clickable {
                if (node.type == "folder") onToggle(node.name) else onFileClick(node.name)
            }
            .padding(start = (indent * 16).dp)
            .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (node.type == "folder") {
                    if (expandedFolders.contains(node.name)) Icons.Default.FolderOpen else Icons.Default.Folder
                } else Icons.Default.Description,
                contentDescription = null,
                tint = if (node.type == "folder") IndigoLight else CyanAccent,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(node.name, style = MaterialTheme.typography.bodyMedium)
        }
    }

    if (node.type == "folder" && expandedFolders.contains(node.name)) {
        node.children.forEach { child ->
            renderDirectory(child, indent + 1, expandedFolders, onToggle, onFileClick)
        }
    }
}

@Composable
fun FileSpecsTabContent(
    specs: List<FileSpecification>,
    initialSelectedFile: FileSpecification? = null
) {
    var selectedFile by remember { mutableStateOf(initialSelectedFile ?: specs.firstOrNull()) }

    Row(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.width(200.dp).fillMaxHeight().border(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
            items(specs) { spec ->
                val fileName = spec.filePath.split("/").last()
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { selectedFile = spec },
                    color = if (selectedFile == spec) IndigoPrimary.copy(alpha = 0.1f) else Color.Transparent
                ) {
                    Text(fileName, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        Column(modifier = Modifier.weight(1f).padding(16.dp).verticalScroll(rememberScrollState())) {
            if (selectedFile != null) {
                Text(selectedFile!!.filePath, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(12.dp))
                SectionCard(title = "Responsibilities", icon = Icons.Default.Assignment) {
                    selectedFile!!.responsibilities.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                }
                Spacer(modifier = Modifier.height(12.dp))
                SectionCard(title = "Implementation Notes", icon = Icons.Default.Note) {
                    Text(selectedFile!!.implementationNotes, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun BuildPlanTabContent(phases: List<BuildPhase>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(phases) { phase ->
            SectionCard(title = "Phase ${phase.phaseNumber}: ${phase.name}", icon = Icons.Default.Flag) {
                Text("Tasks:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                phase.tasks.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Result: ${phase.expectedResult}", style = MaterialTheme.typography.bodySmall, color = EmeraldSuccess)
            }
        }
    }
}

@Composable
fun MasterPromptTabContent(p: ProjectBlueprint) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Implementation Master Prompt", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Button(onClick = {
                clipboardManager.setText(AnnotatedString(p.masterCodingPrompt))
                Toast.makeText(context, "Prompt copied to clipboard!", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy All")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(12.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Text(
                p.masterCodingPrompt,
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}


// ================= TAB: HEALTH DASHBOARD =================
@Composable
fun HealthDashboardTabContent(
    p: ProjectBlueprint,
    onRunAudit: () -> Unit,
    onRunBugHunter: () -> Unit
) {
    val health = p.projectHealth
    val report = p.qualityReport

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Engineering Health Dashboard", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Overall Score: ${health.overallScore}%", style = MaterialTheme.typography.headlineMedium, color = if (health.overallScore > 80) EmeraldSuccess else IndigoPrimary, fontWeight = FontWeight.Black)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onRunAudit, shape = RoundedCornerShape(8.dp)) {
                        Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Deep Audit")
                    }
                    OutlinedButton(onClick = onRunBugHunter, shape = RoundedCornerShape(8.dp)) {
                        Icon(Icons.Default.BugReport, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bug Hunter")
                    }
                }
            }
        }

        item {
            FlowRow(
                mainAxisSpacing = 12.dp,
                crossAxisSpacing = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                HealthMetricMiniCard("Architecture", health.architectureScore)
                HealthMetricMiniCard("UI/UX", health.uiUxScore)
                HealthMetricMiniCard("Security", health.securityScore)
                HealthMetricMiniCard("Performance", health.performanceScore)
                HealthMetricMiniCard("Production", health.productionReadinessScore)
            }
        }

        if (report.criticalIssues.isNotEmpty()) {
            item {
                SectionCard(title = "CRITICAL VULNERABILITIES", icon = Icons.Default.Warning, containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)) {
                    report.criticalIssues.forEach { issue ->
                        IssueItemView(issue)
                    }
                }
            }
        }

        item {
            SectionCard(title = "Engineering Quality Report", icon = Icons.Default.Verified) {
                Text(report.checksSummary, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))
                
                if (report.highIssues.isNotEmpty()) {
                    Text("High Priority Issues", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    report.highIssues.forEach { IssueItemView(it) }
                }
                
                if (report.mediumIssues.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Standard Improvements", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    report.mediumIssues.forEach { IssueItemView(it) }
                }
            }
        }
    }
}

@Composable
fun HealthMetricMiniCard(label: String, score: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.Center) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$score", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                LinearProgressIndicator(
                    progress = score / 100f,
                    modifier = Modifier.weight(1f).height(4.dp).clip(CircleShape),
                    color = if (score > 80) EmeraldSuccess else if (score > 50) IndigoPrimary else MaterialTheme.colorScheme.error,
                    trackColor = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
fun IssueItemView(issue: EngineeringIssue) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        color = Color.Transparent
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.FiberManualRecord, 
                    contentDescription = null, 
                    modifier = Modifier.size(8.dp),
                    tint = when(issue.severity) {
                        "CRITICAL" -> MaterialTheme.colorScheme.error
                        "HIGH" -> MaterialTheme.colorScheme.error
                        "MEDIUM" -> IndigoPrimary
                        else -> EmeraldSuccess
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(issue.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(issue.category, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 4.dp))
                }
            }
            Text(issue.description, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp))
            if (issue.recommendedFix.isNotBlank()) {
                Text("Fix: ${issue.recommendedFix}", style = MaterialTheme.typography.labelSmall, color = CyanAccent, modifier = Modifier.padding(start = 16.dp, top = 2.dp))
            }
        }
    }
}

// ================= TAB: STRATEGIC ROADMAP =================
@Composable
fun RoadmapTabContent(
    roadmap: ProjectRoadmap,
    onGenerate: () -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("AI Product Strategy Roadmap", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Intelligent growth path based on project category.", style = MaterialTheme.typography.bodySmall)
                }
                Button(onClick = onGenerate, shape = RoundedCornerShape(8.dp)) {
                    Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compute Roadmap")
                }
            }
        }

        if (roadmap.now.isEmpty() && roadmap.next.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("No roadmap generated yet. Click 'Compute Roadmap' to start.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            item { RoadmapSection("NOW", "Immediate Engineering & Value", roadmap.now, IndigoPrimary) }
            item { RoadmapSection("NEXT", "Growth & Scalability Features", roadmap.next, CyanAccent) }
            item { RoadmapSection("LATER", "Future Vision & Innovation", roadmap.later, EmeraldSuccess) }
            item { RoadmapSection("EXPERIMENTAL", "Research & High-Risk Ideas", roadmap.experimental, MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}

@Composable
fun RoadmapSection(title: String, subtitle: String, items: List<RoadmapItem>, color: Color) {
    if (items.isEmpty()) return
    
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.width(8.dp))
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = color.copy(alpha = 0.3f))
        
        items.forEach { item ->
            Surface(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = color.copy(alpha = 0.1f)
                        ) {
                            Text(item.priority, style = MaterialTheme.typography.labelSmall, color = color, modifier = Modifier.padding(horizontal = 6.dp))
                        }
                    }
                    Text(item.description, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Strategic Reason: ${item.reason}", style = MaterialTheme.typography.labelSmall, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            }
        }
    }
}
@Composable
fun UserFlowsTabContent(ux: UxArchitecture) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxSize()) {
        item {
            SectionCard(title = "End-to-End User Journeys", icon = Icons.Default.Route) {
                ux.userJourneys.forEach { journey ->
                    Text(journey.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = IndigoLight)
                    journey.steps.forEachIndexed { i, step ->
                        Text("${i + 1}. $step", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp, top = 2.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
        item {
            SectionCard(title = "Navigation Graph & Deep Linking", icon = Icons.Default.CompareArrows) {
                Text(ux.navigationStructure, style = MaterialTheme.typography.bodyMedium)
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// ================= TAB: UI/UX =================
@Composable
fun UxTabContent(ux: UxArchitecture, design: DesignSystem?) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxSize()) {
        item {
            SectionCard(title = "Design System & Theming", icon = Icons.Default.Palette) {
                if (design != null) {
                    InfoRow("Primary Color", design.primaryColor)
                    InfoRow("Secondary Color", design.secondaryColor)
                    InfoRow("Typography Style", design.typography.joinToString(", "))
                    InfoRow("Component Styles", design.componentStyles)
                } else {
                    Text("Standard Material 3 design system with dynamic color support.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        item {
            SectionCard(title = "State Handling Policies", icon = Icons.Default.ReportProblem) {
                Text("Empty States:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                ux.emptyStates.forEach { Text("• ${it.context}: ${it.description}", style = MaterialTheme.typography.bodySmall) }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Error States:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                ux.errorStates.forEach { Text("• ${it.context}: ${it.description}", style = MaterialTheme.typography.bodySmall) }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Loading States:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                ux.loadingStates.forEach { Text("• ${it.context}: ${it.description}", style = MaterialTheme.typography.bodySmall) }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// ================= TAB: INTEGRATIONS =================
@Composable
fun IntegrationsTabContent(integrations: List<IntegrationItem>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
        items(integrations) { integration ->
            Surface(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(integration.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Badge(containerColor = CyanAccent.copy(alpha = 0.15f), contentColor = CyanAccent) {
                            Text(integration.type, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                    Text(integration.purpose, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Provider: ${integration.provider}", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// ================= TAB: NOTIFICATIONS =================
@Composable
fun NotificationsTabContent(notif: TechChoice) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxSize()) {
        item {
            SectionCard(title = "Notification Strategy", icon = Icons.Default.Notifications) {
                Text(notif.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(notif.justification, style = MaterialTheme.typography.bodySmall)
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// ================= TAB: PAYMENTS =================
@Composable
fun PaymentsTabContent(pay: TechChoice) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxSize()) {
        item {
            SectionCard(title = "Payment Integration", icon = Icons.Default.Payment) {
                Text(pay.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(pay.justification, style = MaterialTheme.typography.bodySmall)
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// ================= TAB: SCALABILITY =================
@Composable
fun ScalabilityTabContent(plan: ScalabilityPlan) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxSize()) {
        item {
            SectionCard(title = "Scalability Overview", icon = Icons.Default.Speed) {
                InfoRow("Target Scale", plan.targetScale)
                InfoRow("Caching Strategy", plan.cachingStrategy)
                InfoRow("Queuing Strategy", plan.queuingStrategy)
            }
        }
        item {
            SectionCard(title = "Architecture Bottlenecks", icon = Icons.Default.ReportProblem) {
                Text("Database Bottlenecks:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                plan.databaseBottlenecks.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                Spacer(modifier = Modifier.height(8.dp))
                Text("API Bottlenecks:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                plan.apiBottlenecks.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// ================= TAB: COST ESTIMATE =================
@Composable
fun CostTabContent(estimates: CostEstimates, report: CostComplexityReport?) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
        if (report != null) {
            item {
                SectionCard(title = "Project Complexity Report", icon = Icons.Default.AttachMoney) {
                    InfoRow("Complexity Level", report.complexityLevel)
                    InfoRow("Dev Estimation", report.developmentEstimation)
                }
            }
        }
        item {
            SectionCard(title = "Infrastructure Cost Projection", icon = Icons.Default.CloudQueue) {
                InfoRow("100 Users", estimates.monthly100Users)
                InfoRow("1k Users", estimates.monthly1kUsers)
                InfoRow("10k Users", estimates.monthly10kUsers)
                InfoRow("100k Users", estimates.monthly100kUsers)
            }
        }
        items(estimates.breakdown) { est ->
            Surface(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(est.service, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(est.estimate10k, style = MaterialTheme.typography.labelLarge, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                    }
                    Text(est.provider, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// ================= TAB: TESTING =================
@Composable
fun TestingTabContent(strategy: TestingStrategyPlan?, checklist: TestingChecklist) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxSize()) {
        if (strategy != null) {
            item {
                SectionCard(title = "Testing Strategy", icon = Icons.Default.BugReport) {
                    InfoRow("Overview", strategy.overview)
                    Text("Unit Tests:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    strategy.unitTests.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
        item {
            SectionCard(title = "Production Readiness Checklist", icon = Icons.Default.FactCheck) {
                Text("Unit Test Cases:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                checklist.unitTestCases.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Integration Test Cases:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                checklist.integrationTestCases.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// ================= TAB: DEPLOYMENT =================
@Composable
fun DeploymentTabContent(plan: DeploymentPlan) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxSize()) {
        item {
            SectionCard(title = "Infrastructure & Commands", icon = Icons.Default.CloudUpload) {
                InfoRow("Primary Platform", plan.primaryPlatform)
                InfoRow("Build Commands", plan.buildCommands)
                InfoRow("SSL & Domain", plan.sslAndDomain)
            }
        }
        item {
            SectionCard(title = "Operations Strategy", icon = Icons.Default.Timeline) {
                InfoRow("Database Migration", plan.databaseMigrationStrategy)
                InfoRow("Monitoring", plan.monitoringAndAlerts)
                InfoRow("Rollback Strategy", plan.rollbackStrategy)
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// ================= TAB: QUALITY REPORT =================
@Composable
fun QualityReportTabContent(p: ProjectBlueprint, onAutoFix: () -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxSize()) {
        item {
            HealthScoreCard(qualityReport = p.qualityReport, onAutoFixClick = onAutoFix)
        }
        item {
            SectionCard(title = "Critical Architecture Issues", icon = Icons.Default.Report) {
                if (p.qualityReport.criticalIssues.isEmpty()) {
                    Text("No critical issues found.", color = EmeraldSuccess)
                }
                p.qualityReport.criticalIssues.forEach { issue ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = RoseError, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(issue.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(issue.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// ================= TAB: AI ARCHITECT CHAT =================
@Composable
fun AiArchitectChatTabContent(
    messages: List<com.example.data.local.ChatMessageEntity>,
    onSend: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val quickCommands = listOf(
        "Make my app better" to Icons.Default.Bolt,
        "Is app ko production ready karo" to Icons.Default.Verified,
        "Fix everything safe" to Icons.Default.GppGood,
        "Add Google Login" to Icons.Default.Login,
        "Add Payment System" to Icons.Default.Payments
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    color = CyanAccent.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(
                        "Autonomous Engineer Lab Active. Request structural modifications or use quick commands to iterate on the architecture.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                        color = CyanAccent
                    )
                }
            }
            items(messages) { msg ->
                ChatBubble(msg)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Commands Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            items(quickCommands) { (label, icon) ->
                AssistChip(
                    onClick = { onSend(label) },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = CyanAccent,
                        leadingIconContentColor = CyanAccent
                    )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Request an architectural change...") },
                modifier = Modifier.weight(1f).testTag("chat_input"),
                shape = RoundedCornerShape(24.dp),
                maxLines = 4
            )
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSend(inputText)
                        inputText = ""
                    }
                },
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(48.dp).testTag("chat_send_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ChatBubble(msg: com.example.data.local.ChatMessageEntity) {
    val isUser = msg.sender == "USER"
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isUser) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 0.dp,
                bottomEnd = if (isUser) 0.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(msg.message, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp))
        }
    }
}

@Composable
fun SidebarItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = if (isSelected) IndigoPrimary.copy(alpha = 0.12f) else Color.Transparent,
        border = if (isSelected) BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f)) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FactoryStatusBar(p: ProjectBlueprint) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(28.dp).drawBehind {
            drawLine(
                color = CardBorderDark,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        },
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.OfflineBolt, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("MODE: ${p.generationMode}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.Circle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(8.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("ENGINE: GEMINI-3-FLASH-PREVIEW", style = MaterialTheme.typography.labelSmall)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("HEALTH: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${p.healthScore}/100", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (p.healthScore > 80) EmeraldSuccess else RoseError)
                Spacer(modifier = Modifier.width(16.dp))
                Text("STATUS: READY FOR IMPLEMENTATION", style = MaterialTheme.typography.labelSmall, color = CyanAccent)
            }
        }
    }
}

@Composable
fun ScreenBuilderTabContent(ux: UxArchitecture, design: DesignSystem?) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            SectionCard(title = "Unified Design System", icon = Icons.Default.Palette) {
                if (design != null) {
                    InfoRow("Primary Color", design.primaryColor)
                    InfoRow("Secondary Color", design.secondaryColor)
                    InfoRow("Typography", design.typography.joinToString(", "))
                    InfoRow("Component Philosophy", design.componentStyles)
                    InfoRow("Spacing & Grid", design.spacingGuidelines)
                }
            }
        }
        
        items(ux.screens) { screen ->
            Surface(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(screen.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(screen.route, style = MaterialTheme.typography.labelSmall, color = CyanAccent, fontFamily = FontFamily.Monospace)
                        }
                        IconButton(onClick = {}) { Icon(Icons.Default.Refresh, contentDescription = "Regenerate Screen", modifier = Modifier.size(16.dp)) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(screen.description, style = MaterialTheme.typography.bodySmall)
                    
                    if (screen.layoutHierarchy.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("LAYOUT HIERARCHY", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = IndigoLight)
                        Text(screen.layoutHierarchy, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("COMPONENTS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = IndigoLight)
                    FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp, modifier = Modifier.padding(top = 4.dp)) {
                        screen.components.forEach { comp ->
                            AssistChip(onClick = {}, label = { Text(comp.name, style = MaterialTheme.typography.labelSmall) })
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

@Composable
fun EnvironmentTabContent(vars: List<EnvVarItem>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = CyanAccent)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Secrets are masked by default. Never share your production keys in plain text. Use these variable names in your application build configuration.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        items(vars) { variable ->
            Surface(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                var isRevealed by remember { mutableStateOf(false) }
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(variable.key, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text(variable.description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(
                            if (isRevealed) variable.example else "••••••••••••••••",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    
                    IconButton(onClick = { isRevealed = !isRevealed }) {
                        Icon(if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ScaleAndCostTabContent(scale: ScalabilityPlan, cost: CostEstimates, report: CostComplexityReport?) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            SectionCard(title = "Scalability Roadmap", icon = Icons.Default.TrendingUp) {
                InfoRow("Target Scale", scale.targetScale)
                InfoRow("Caching Strategy", scale.cachingStrategy)
                InfoRow("Queuing & Jobs", scale.queuingStrategy)
                InfoRow("Edge & CDN", scale.cdnAndEdge)
            }
        }
        item {
            SectionCard(title = "User Load Bottlenecks", icon = Icons.Default.Speed) {
                Text("Database Scalability:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                scale.databaseBottlenecks.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                Spacer(modifier = Modifier.height(8.dp))
                Text("API / Computation Limits:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                scale.apiBottlenecks.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
            }
        }
        item {
            SectionCard(title = "Estimated Monthly Infrastructure", icon = Icons.Default.AttachMoney) {
                InfoRow("100 Users", cost.monthly100Users)
                InfoRow("10k Users", cost.monthly10kUsers)
                InfoRow("100k Users", cost.monthly100kUsers)
            }
        }
    }
}

@Composable
fun ArchitectureTabContent(arch: SystemArchitecture, stack: TechStack) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            SectionCard(title = "Core Technology Stack", icon = Icons.Default.Layers) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        InfoRow("Frontend", stack.frontend.name)
                        InfoRow("Backend", stack.backend.name)
                        InfoRow("Primary DB", stack.database.name)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        InfoRow("Auth", stack.authentication.name)
                        InfoRow("AI Layer", stack.aiModels.name)
                        InfoRow("Deploy", stack.deployment.name)
                    }
                }
            }
        }
        item {
            SectionCard(title = "System Layout Overview", icon = Icons.Default.AutoAwesomeMotion) {
                Text(arch.overview, style = MaterialTheme.typography.bodyMedium)
            }
        }
        items(arch.layers) { layer ->
            SectionCard(title = "Layer: ${layer.name}", icon = Icons.Default.Schema) {
                Text(layer.description, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Responsibilities:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = IndigoLight)
                layer.components.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

@Composable
fun SecurityTabContent(security: SecurityPlan, quality: QualityReport, onAutoFix: () -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            HealthScoreCard(qualityReport = quality, onAutoFixClick = onAutoFix)
        }
        item {
            SectionCard(title = "Security Infrastructure", icon = Icons.Default.Lock) {
                InfoRow("Authentication", security.authenticationStrategy)
                InfoRow("API Protection", security.apiKeyProtection)
                InfoRow("Secret Mgmt", security.secretsManagement)
                InfoRow("Rate Limiting", security.rateLimiting)
            }
        }
        item {
            SectionCard(title = "Data Privacy & Compliance", icon = Icons.Default.PrivacyTip) {
                Text(security.privacyAndEncryption, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun DeploymentTabContent(plan: DeploymentPlan, phases: List<BuildPhase>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            SectionCard(title = "Deployment Pipeline", icon = Icons.Default.Terminal) {
                InfoRow("Primary Platform", plan.primaryPlatform)
                InfoRow("Build Commands", plan.buildCommands)
                InfoRow("Migration Strategy", plan.databaseMigrationStrategy)
                InfoRow("Rollback Plan", plan.rollbackStrategy)
            }
        }
        items(phases) { phase ->
            SectionCard(title = "Build Phase ${phase.phaseNumber}: ${phase.name}", icon = Icons.Default.Build) {
                Text("TASKS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = IndigoLight)
                phase.tasks.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                Spacer(modifier = Modifier.height(8.dp))
                Text("COMPLETION CRITERIA", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                phase.completionCriteria.forEach { Text("✓ $it", style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

// Helpers
@Composable
fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 8.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    if (value.isBlank()) return
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = IndigoLight)
        Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun EditTagsDialog(
    currentTags: List<String>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    var tagsInput by remember { mutableStateOf(currentTags.joinToString(", ")) }
    val suggestedTags = listOf("Mobile", "Android", "AI", "Cloud", "Fintech", "OfflineFirst", "GraphQL", "Realtime", "B2B", "MVP")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tag, contentDescription = null, tint = CyanAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Project Tags", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    "Enter user-defined tags separated by commas for categorization and search filtering:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    placeholder = { Text("e.g. Mobile, WhatsApp, HighPriority, Regional") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_tags_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text("Quick Add Suggestions:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(suggestedTags) { tag ->
                        AssistChip(
                            onClick = {
                                val currentList = tagsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                if (!currentList.contains(tag)) {
                                    tagsInput = (currentList + tag).joinToString(", ")
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
                    val updatedList = tagsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    onSave(updatedList)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier.testTag("save_tags_button")
            ) {
                Text("Save Tags")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    mainAxisSpacing: androidx.compose.ui.unit.Dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(mainAxisSpacing),
        verticalArrangement = Arrangement.spacedBy(crossAxisSpacing),
        content = { content() }
    )
}
