package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProjectBlueprint(
    val id: String = "",
    val name: String = "",
    val rawIdea: String = "",
    val tagline: String = "",
    val category: String = "General",
    val version: String = "1.0.0",
    val lastModified: Long = System.currentTimeMillis(),
    val healthScore: Int = 95,
    val ideaUnderstanding: IdeaUnderstanding = IdeaUnderstanding(),
    val productStrategy: ProductStrategy = ProductStrategy(),
    val requirements: ProjectRequirements = ProjectRequirements(),
    val features: List<FeatureItem> = emptyList(),
    val userRoles: List<UserRole> = emptyList(),
    val uxArchitecture: UxArchitecture = UxArchitecture(),
    val designSystem: DesignSystem = DesignSystem(),
    val techStack: TechStack = TechStack(),
    val systemArchitecture: SystemArchitecture = SystemArchitecture(),
    val databaseSchema: DatabaseSchema = DatabaseSchema(),
    val apiDesign: List<ApiEndpoint> = emptyList(),
    val securityPlan: SecurityPlan = SecurityPlan(),
    val integrations: List<IntegrationItem> = emptyList(),
    val adminArchitecture: AdminArchitecture = AdminArchitecture(),
    val errorHandling: ErrorHandlingPlan = ErrorHandlingPlan(),
    val performancePlan: PerformancePlan = PerformancePlan(),
    val directoryTree: DirectoryNode = DirectoryNode("root", "folder", emptyList()),
    val fileSpecifications: List<FileSpecification> = emptyList(),
    val buildPlan: List<BuildPhase> = emptyList(),
    val masterCodingPrompt: String = "",
    val qualityReport: QualityReport = QualityReport(),
    val projectHealth: ProjectHealth = ProjectHealth(),
    val projectRoadmap: ProjectRoadmap = ProjectRoadmap(),
    val engineeringLog: List<EngineeringChangeLog> = emptyList(),
    val productionChecklist: List<ChecklistItem> = emptyList(),
    val assumptions: List<AssumptionItem> = emptyList(),
    val tags: List<String> = emptyList(),
    val executiveSummary: String = "",
    val isFavorite: Boolean = false,
    val environmentVariables: List<EnvVarItem> = emptyList(),
    val costComplexity: CostComplexityReport = CostComplexityReport(),
    val testingStrategy: TestingStrategyPlan = TestingStrategyPlan(),
    val costEstimates: CostEstimates = CostEstimates(),
    val scalabilityPlan: ScalabilityPlan = ScalabilityPlan(),
    val testingChecklist: TestingChecklist = TestingChecklist(),
    val deploymentPlan: DeploymentPlan = DeploymentPlan(),
    val alternativeTechStack: AlternativeTechStack = AlternativeTechStack(),
    val architectureDiff: ArchitectureDiff? = null,
    val generationMode: String = "DEEP" // QUICK, STANDARD, DEEP
)

@JsonClass(generateAdapter = true)
data class ProjectRequirements(
    val functional: List<String> = emptyList(),
    val nonFunctional: List<String> = emptyList(),
    val productGoals: List<String> = emptyList(),
    val constraints: List<String> = emptyList(),
    val mvpScope: List<String> = emptyList(),
    val futureScope: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class UserRole(
    val name: String = "",
    val description: String = "",
    val permissions: List<String> = emptyList(),
    val primaryGoals: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class DesignSystem(
    val themeOverview: String = "",
    val primaryColor: String = "",
    val secondaryColor: String = "",
    val accentColor: String = "",
    val typography: List<String> = emptyList(),
    val componentStyles: String = "",
    val spacingGuidelines: String = "",
    val iconography: String = ""
)

@JsonClass(generateAdapter = true)
data class IntegrationItem(
    val name: String = "",
    val type: String = "", // PAYMENT, ANALYTICS, NOTIFICATION, AI, STORAGE, MAPS, etc.
    val provider: String = "",
    val purpose: String = "",
    val setupSteps: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AdminArchitecture(
    val overview: String = "",
    val panels: List<AdminPanel> = emptyList(),
    val dashboardMetrics: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AdminPanel(
    val name: String = "",
    val description: String = "",
    val actions: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ErrorHandlingPlan(
    val globalStrategy: String = "",
    val clientSideErrors: List<String> = emptyList(),
    val apiErrors: List<String> = emptyList(),
    val edgeCases: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PerformancePlan(
    val frontendOptimization: List<String> = emptyList(),
    val backendOptimization: List<String> = emptyList(),
    val databaseOptimization: List<String> = emptyList(),
    val targetLatency: String = ""
)

@JsonClass(generateAdapter = true)
data class ChecklistItem(
    val category: String = "",
    val task: String = "",
    val importance: String = "HIGH"
)

@JsonClass(generateAdapter = true)
data class TestingStrategyPlan(
    val overview: String = "",
    val unitTests: List<String> = emptyList(),
    val integrationTests: List<String> = emptyList(),
    val e2eTests: List<String> = emptyList(),
    val securityAudits: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class EnvVarItem(
    val key: String = "",
    val description: String = "",
    val example: String = "",
    val isSecret: Boolean = true
)

@JsonClass(generateAdapter = true)
data class CostComplexityReport(
    val complexityLevel: String = "MEDIUM", // LOW, MEDIUM, HIGH, VERY_HIGH
    val developmentEstimation: String = "",
    val infrastructureConsiderations: List<String> = emptyList(),
    val thirdPartyCosts: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class IdeaUnderstanding(
    val productType: String = "",
    val targetUsers: List<String> = emptyList(),
    val primaryProblem: String = "",
    val proposedSolution: String = "",
    val targetPlatforms: List<String> = emptyList(),
    val businessModel: String = "",
    val keyAssumptions: List<String> = emptyList(),
    val constraints: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ProductStrategy(
    val positioning: String = "",
    val targetAudience: String = "",
    val valueProposition: String = "",
    val coreUseCases: List<String> = emptyList(),
    val mvpScope: List<String> = emptyList(),
    val futureFeatures: List<String> = emptyList(),
    val monetizationPossibilities: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class FeatureItem(
    val id: String = "",
    val name: String = "",
    val priority: String = "P0", // P0, P1, P2
    val purpose: String = "",
    val targetUser: String = "",
    val workflow: String = "",
    val dependencies: List<String> = emptyList(),
    val backendRequirements: String = "",
    val edgeCases: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class UxArchitecture(
    val screens: List<ScreenSpec> = emptyList(),
    val navigationStructure: String = "",
    val onboardingFlow: String = "",
    val authFlow: String = "",
    val userJourneys: List<UserJourney> = emptyList(),
    val emptyStates: List<StateDescription> = emptyList(),
    val loadingStates: List<StateDescription> = emptyList(),
    val errorStates: List<StateDescription> = emptyList(),
    val successStates: List<StateDescription> = emptyList(),
    val requiredPermissions: List<String> = emptyList(),
    val responsiveBehavior: String = ""
)

@JsonClass(generateAdapter = true)
data class ScreenSpec(
    val id: String = "",
    val name: String = "",
    val route: String = "",
    val description: String = "",
    val components: List<ScreenComponent> = emptyList(),
    val actions: List<String> = emptyList(),
    val layoutHierarchy: String = "",
    val stateSpecifications: String = "",
    val interactions: List<String> = emptyList(),
    val animations: List<String> = emptyList(),
    val responsiveRules: String = "",
    val apiDependencies: List<String> = emptyList(),
    val permissionsRequired: List<String> = emptyList()
) {
    // Secondary constructor for backward compatibility with old mock data (String components)
    constructor(id: String, name: String, route: String, description: String, components: List<String>, actions: List<String>) : this(
        id = id,
        name = name,
        route = route,
        description = description,
        components = components.map { ScreenComponent(name = it, type = "Unknown", purpose = "Mock component") },
        actions = actions
    )
}

@JsonClass(generateAdapter = true)
data class ScreenComponent(
    val name: String = "",
    val type: String = "",
    val purpose: String = "",
    val props: Map<String, String> = emptyMap(),
    val states: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class UserJourney(
    val title: String = "",
    val steps: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class StateDescription(
    val context: String = "",
    val description: String = "",
    val visualAction: String = ""
)

@JsonClass(generateAdapter = true)
data class TechStack(
    val frontend: TechChoice = TechChoice(),
    val backend: TechChoice = TechChoice(),
    val database: TechChoice = TechChoice(),
    val authentication: TechChoice = TechChoice(),
    val storage: TechChoice = TechChoice(),
    val apis: TechChoice = TechChoice(),
    val aiModels: TechChoice = TechChoice(),
    val payments: TechChoice = TechChoice(),
    val notifications: TechChoice = TechChoice(),
    val analytics: TechChoice = TechChoice(),
    val deployment: TechChoice = TechChoice()
)

@JsonClass(generateAdapter = true)
data class TechChoice(
    val name: String = "",
    val category: String = "",
    val justification: String = ""
)

@JsonClass(generateAdapter = true)
data class SystemArchitecture(
    val overview: String = "",
    val layers: List<ArchitectureLayer> = emptyList(),
    val dataFlowSteps: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ArchitectureLayer(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val components: List<String> = emptyList(),
    val connectsTo: List<String> = emptyList(),
    val layerType: String = "SERVICE" // CLIENT, GATEWAY, BACKEND, SERVICE, DATABASE, EXTERNAL, AI
)

@JsonClass(generateAdapter = true)
data class DatabaseSchema(
    val databaseType: String = "PostgreSQL",
    val entities: List<DbEntity> = emptyList(),
    val relationships: List<DbRelationship> = emptyList(),
    val productionRecommendations: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class DbEntity(
    val tableName: String = "",
    val description: String = "",
    val fields: List<DbField> = emptyList(),
    val indexes: List<String> = emptyList(),
    val validationRules: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class DbField(
    val name: String = "",
    val type: String = "",
    val isPrimaryKey: Boolean = false,
    val isForeignKey: Boolean = false,
    val isNullable: Boolean = false,
    val description: String = ""
)

@JsonClass(generateAdapter = true)
data class DbRelationship(
    val fromTable: String = "",
    val toTable: String = "",
    val relationType: String = "", // 1:1, 1:N, N:M
    val description: String = ""
)

@JsonClass(generateAdapter = true)
data class ApiEndpoint(
    val id: String = "",
    val path: String = "",
    val method: String = "GET", // GET, POST, PUT, DELETE, PATCH
    val group: String = "General",
    val purpose: String = "",
    val authentication: String = "Bearer JWT",
    val requestBody: String = "",
    val responseBody: String = "",
    val statusCodes: List<String> = emptyList(),
    val errorHandling: String = ""
)

@JsonClass(generateAdapter = true)
data class SecurityPlan(
    val authenticationStrategy: String = "",
    val authorizationRules: List<String> = emptyList(),
    val inputValidation: List<String> = emptyList(),
    val apiKeyProtection: String = "",
    val secretsManagement: String = "",
    val rateLimiting: String = "",
    val abusePrevention: List<String> = emptyList(),
    val fileUploadSecurity: String = "",
    val databaseSecurity: List<String> = emptyList(),
    val privacyAndEncryption: String = ""
)

@JsonClass(generateAdapter = true)
data class DirectoryNode(
    val name: String = "",
    val type: String = "folder", // folder, file
    val children: List<DirectoryNode> = emptyList(),
    val description: String = ""
)

@JsonClass(generateAdapter = true)
data class FileSpecification(
    val filePath: String = "",
    val purpose: String = "",
    val dependencies: List<String> = emptyList(),
    val responsibilities: List<String> = emptyList(),
    val importantFunctions: List<String> = emptyList(),
    val inputs: String = "",
    val outputs: String = "",
    val securityNotes: String = "",
    val implementationNotes: String = ""
)

@JsonClass(generateAdapter = true)
data class BuildPhase(
    val phaseNumber: Int = 1,
    val name: String = "",
    val tasks: List<String> = emptyList(),
    val dependencies: List<String> = emptyList(),
    val expectedResult: String = "",
    val completionCriteria: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ProjectHealth(
    val architectureScore: Int = 0,
    val uiUxScore: Int = 0,
    val codeQualityScore: Int = 0,
    val securityScore: Int = 0,
    val performanceScore: Int = 0,
    val accessibilityScore: Int = 0,
    val testingScore: Int = 0,
    val scalabilityScore: Int = 0,
    val productionReadinessScore: Int = 0,
    val overallScore: Int = 0,
    val analysisDate: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class ProjectRoadmap(
    val now: List<RoadmapItem> = emptyList(),
    val next: List<RoadmapItem> = emptyList(),
    val later: List<RoadmapItem> = emptyList(),
    val experimental: List<RoadmapItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class RoadmapItem(
    val title: String = "",
    val description: String = "",
    val priority: String = "MEDIUM",
    val impact: String = "MEDIUM",
    val complexity: String = "MEDIUM",
    val dependencies: List<String> = emptyList(),
    val reason: String = ""
)

@JsonClass(generateAdapter = true)
data class EngineeringChangeLog(
    val id: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val request: String = "",
    val summary: String = "",
    val affectedFiles: List<String> = emptyList(),
    val status: String = "SUCCESS", // SUCCESS, FAILED, UNDONE
    val versionBefore: String = "",
    val versionAfter: String = ""
)

@JsonClass(generateAdapter = true)
data class QualityReport(
    val healthScore: Int = 95,
    val checksSummary: String = "All core architecture validations passed.",
    val criticalIssues: List<EngineeringIssue> = emptyList(),
    val highIssues: List<EngineeringIssue> = emptyList(),
    val mediumIssues: List<EngineeringIssue> = emptyList(),
    val lowIssues: List<EngineeringIssue> = emptyList(),
    val recommendations: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class EngineeringIssue(
    val id: String = "",
    val title: String = "",
    val category: String = "ARCHITECTURE", // ARCHITECTURE, UI, SECURITY, PERFORMANCE, ACCESSIBILITY
    val description: String = "",
    val location: String = "",
    val impact: String = "",
    val rootCause: String = "",
    val recommendedFix: String = "",
    val autoFixable: Boolean = true,
    val severity: String = "MEDIUM" // CRITICAL, HIGH, MEDIUM, LOW
)

@JsonClass(generateAdapter = true)
data class AssumptionItem(
    val id: String = "",
    val category: String = "",
    val title: String = "",
    val currentChoice: String = "",
    val options: List<String> = emptyList(),
    val impactArea: String = ""
)

@JsonClass(generateAdapter = true)
data class CostEstimates(
    val monthly100Users: String = "$15 - $30 / mo",
    val monthly1kUsers: String = "$60 - $120 / mo",
    val monthly10kUsers: String = "$350 - $700 / mo",
    val monthly100kUsers: String = "$2,500 - $5,000 / mo",
    val breakdown: List<CostItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CostItem(
    val service: String = "",
    val estimate10k: String = "",
    val provider: String = ""
)

@JsonClass(generateAdapter = true)
data class ScalabilityPlan(
    val targetScale: String = "1,000,000 Active Users",
    val databaseBottlenecks: List<String> = emptyList(),
    val apiBottlenecks: List<String> = emptyList(),
    val cachingStrategy: String = "",
    val queuingStrategy: String = "",
    val cdnAndEdge: String = "",
    val scalingMilestones: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class TestingChecklist(
    val unitTestCases: List<String> = emptyList(),
    val integrationTestCases: List<String> = emptyList(),
    val uiTestCases: List<String> = emptyList(),
    val securityTestCases: List<String> = emptyList(),
    val performanceTestCases: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class DeploymentPlan(
    val primaryPlatform: String = "Vercel / Cloud Run",
    val buildCommands: String = "",
    val envVariables: List<String> = emptyList(),
    val sslAndDomain: String = "",
    val databaseMigrationStrategy: String = "",
    val monitoringAndAlerts: String = "",
    val rollbackStrategy: String = ""
)

@JsonClass(generateAdapter = true)
data class AlternativeTechStack(
    val frontendAlternative: TechAlternative = TechAlternative(),
    val backendAlternative: TechAlternative = TechAlternative(),
    val databaseAlternative: TechAlternative = TechAlternative()
)

@JsonClass(generateAdapter = true)
data class TechAlternative(
    val name: String = "",
    val pros: List<String> = emptyList(),
    val cons: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ArchitectureDiff(
    val summary: String = "",
    val addedFeatures: List<String> = emptyList(),
    val modifiedComponents: List<String> = emptyList(),
    val newFiles: List<String> = emptyList(),
    val modifiedFiles: List<String> = emptyList(),
    val databaseChanges: List<String> = emptyList(),
    val apiChanges: List<String> = emptyList(),
    val securityImpact: String = ""
)

