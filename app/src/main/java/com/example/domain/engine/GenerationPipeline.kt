package com.example.domain.engine

import com.example.data.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.JsonClass
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

enum class GenerationStage(val label: String) {
    ANALYZE_IDEA("Analyzing idea..."),
    REQUIREMENTS("Extracting requirements..."),
    FEATURES_AND_ROLES("Designing features & roles..."),
    UX_AND_SCREENS("Designing user experience & screens..."),
    TECH_STACK("Selecting technology stack..."),
    SYSTEM_ARCH("Designing system architecture..."),
    DATABASE_DESIGN("Designing database schema..."),
    API_DESIGN("Designing API endpoints..."),
    SECURITY_AND_OPS("Designing security & operations..."),
    COST_AND_ENV("Estimating costs & environment..."),
    DIRECTORY_AND_FILES("Creating project directory & file specs..."),
    IMPLEMENTATION_PLAN("Generating implementation plan..."),
    MASTER_PROMPT("Generating master coding prompt..."),
    CONSISTENCY_CHECK("Running consistency check..."),
    FINALIZING("Finalizing project blueprint...")
}

data class GenerationProgress(
    val stage: GenerationStage,
    val project: ProjectBlueprint? = null,
    val error: String? = null
)

class GenerationPipeline(private val geminiClient: GeminiClient) {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    fun generate(idea: String, mode: String = "STANDARD"): Flow<GenerationProgress> = flow {
        var currentBlueprint = ProjectBlueprint(
            id = UUID.randomUUID().toString(),
            rawIdea = idea,
            generationMode = mode,
            lastModified = System.currentTimeMillis()
        )

        try {
            // Stage 1: Analyze Idea
            emit(GenerationProgress(GenerationStage.ANALYZE_IDEA))
            val ideaInfo = analyzeIdea(idea)
            currentBlueprint = currentBlueprint.copy(
                name = ideaInfo.name,
                tagline = ideaInfo.tagline,
                category = ideaInfo.category,
                ideaUnderstanding = ideaInfo.understanding
            )

            // Stage 2: Requirements
            emit(GenerationProgress(GenerationStage.REQUIREMENTS, currentBlueprint))
            val requirements = generateRequirements(ProjectRequirementsResponse(
                name = currentBlueprint.name,
                rawIdea = currentBlueprint.rawIdea,
                mode = mode
            ))
            currentBlueprint = currentBlueprint.copy(requirements = requirements)

            // Stage 3: Features & Roles
            emit(GenerationProgress(GenerationStage.FEATURES_AND_ROLES, currentBlueprint))
            val featuresAndRoles = generateFeaturesAndRoles(currentBlueprint)
            currentBlueprint = currentBlueprint.copy(
                features = featuresAndRoles.features,
                userRoles = featuresAndRoles.roles
            )

            // Stage 4: UX & Screens
            emit(GenerationProgress(GenerationStage.UX_AND_SCREENS, currentBlueprint))
            val ux = generateUx(currentBlueprint)
            currentBlueprint = currentBlueprint.copy(
                uxArchitecture = ux.ux,
                designSystem = ux.designSystem
            )

            // Stage 5: Tech Stack
            emit(GenerationProgress(GenerationStage.TECH_STACK, currentBlueprint))
            val techStack = generateTechStack(currentBlueprint)
            currentBlueprint = currentBlueprint.copy(techStack = techStack)

            // Stage 6: System Arch
            emit(GenerationProgress(GenerationStage.SYSTEM_ARCH, currentBlueprint))
            val systemArch = generateSystemArch(currentBlueprint)
            currentBlueprint = currentBlueprint.copy(systemArchitecture = systemArch)

            // Stage 7: Database Design
            emit(GenerationProgress(GenerationStage.DATABASE_DESIGN, currentBlueprint))
            val db = generateDatabase(currentBlueprint)
            currentBlueprint = currentBlueprint.copy(databaseSchema = db)

            // Stage 8: API Design
            emit(GenerationProgress(GenerationStage.API_DESIGN, currentBlueprint))
            val apis = generateApis(currentBlueprint)
            currentBlueprint = currentBlueprint.copy(apiDesign = apis)

            // Stage 9: Security & Ops
            emit(GenerationProgress(GenerationStage.SECURITY_AND_OPS, currentBlueprint))
            val securityAndOps = generateSecurityAndOps(currentBlueprint)
            currentBlueprint = currentBlueprint.copy(
                securityPlan = securityAndOps.security,
                integrations = securityAndOps.integrations,
                adminArchitecture = securityAndOps.admin,
                errorHandling = securityAndOps.errorHandling,
                performancePlan = securityAndOps.performance,
                scalabilityPlan = securityAndOps.scalability
            )

            // Stage 10: Cost & Env
            emit(GenerationProgress(GenerationStage.COST_AND_ENV, currentBlueprint))
            val costAndEnv = generateCostAndEnv(currentBlueprint)
            currentBlueprint = currentBlueprint.copy(
                costComplexity = costAndEnv.costComplexity,
                environmentVariables = costAndEnv.envVars,
                costEstimates = costAndEnv.estimates
            )

            // Stage 11: Directory & Files
            emit(GenerationProgress(GenerationStage.DIRECTORY_AND_FILES, currentBlueprint))
            val dirAndFiles = generateDirectoryAndFiles(currentBlueprint)
            currentBlueprint = currentBlueprint.copy(
                directoryTree = dirAndFiles.tree,
                fileSpecifications = dirAndFiles.specs
            )

            // Stage 12: Implementation Plan
            emit(GenerationProgress(GenerationStage.IMPLEMENTATION_PLAN, currentBlueprint))
            val buildPlan = generateBuildPlan(currentBlueprint)
            currentBlueprint = currentBlueprint.copy(buildPlan = buildPlan)

            // Stage 13: Master Prompt
            emit(GenerationProgress(GenerationStage.MASTER_PROMPT, currentBlueprint))
            val masterPrompt = generateMasterPrompt(currentBlueprint)
            currentBlueprint = currentBlueprint.copy(masterCodingPrompt = masterPrompt)

            // Stage 14: Consistency Check
            emit(GenerationProgress(GenerationStage.CONSISTENCY_CHECK, currentBlueprint))
            val healthReport = runConsistencyCheck(currentBlueprint)
            currentBlueprint = currentBlueprint.copy(
                qualityReport = healthReport.report,
                healthScore = healthReport.report.healthScore,
                productionChecklist = healthReport.checklist
            )

            // Stage 15: Finalizing
            emit(GenerationProgress(GenerationStage.FINALIZING, currentBlueprint))
            // Generate summary and final touches
            val summary = generateSummary(currentBlueprint)
            currentBlueprint = currentBlueprint.copy(executiveSummary = summary)

            emit(GenerationProgress(GenerationStage.FINALIZING, currentBlueprint))
        } catch (e: Exception) {
            emit(GenerationProgress(currentBlueprint.id.let { GenerationStage.FINALIZING }, currentBlueprint, e.message))
        }
    }

    private suspend fun analyzeIdea(idea: String): IdeaAnalysisResponse {
        val prompt = """
            Analyze this app idea: "$idea"
            Return a JSON object with:
            - name: A professional name for the project (e.g. "ArchForge-AI").
            - tagline: A one-sentence value proposition.
            - category: The app category (Ecommerce, SaaS, AI, etc.).
            - understanding: {
                productType: string,
                targetUsers: string[],
                primaryProblem: string,
                proposedSolution: string,
                targetPlatforms: string[],
                businessModel: string,
                keyAssumptions: string[],
                constraints: string[]
              }
        """.trimIndent()
        
        val result = geminiClient.generateText(prompt, jsonMode = true).getOrThrow()
        return moshi.adapter(IdeaAnalysisResponse::class.java).fromJson(result)!!
    }

    private suspend fun generateRequirements(bp: ProjectRequirementsResponse): ProjectRequirements {
        val depth = if (bp.mode == "DEEP") "EXHAUSTIVE PRODUCTION" else "detailed"
        val prompt = """
            Based on the project "${bp.name}" (${bp.rawIdea}), generate $depth requirements.
            Return a JSON object:
            {
              "functional": string[], // At least 20 detailed functional requirements. Enumerate every action.
              "nonFunctional": string[], // At least 15 (security, performance, observability, etc.)
              "productGoals": string[], // Business outcomes
              "constraints": string[], // Technical or business limits
              "mvpScope": string[], // Explicit list for version 1.0
              "futureScope": string[] // Roadmap items
            }
            Do NOT summarize with "etc". Be explicit. If it's a social app, list signup, login, password reset, etc separately.
        """.trimIndent()
        val result = geminiClient.generateText(prompt, jsonMode = true).getOrThrow()
        return moshi.adapter(ProjectRequirements::class.java).fromJson(result)!!
    }

    private suspend fun generateFeaturesAndRoles(bp: ProjectBlueprint): FeaturesAndRolesResponse {
        val depth = if (bp.generationMode == "DEEP") "EXHAUSTIVE PRODUCTION (30+ features)" else "detailed (15-20 features)"
        val prompt = """
            Project: ${bp.name}
            Generate user roles and an $depth feature hierarchy (P0, P1, P2).
            Return JSON:
            {
              "roles": [{ "name": string, "description": string, "permissions": string[], "primaryGoals": string[] }],
              "features": [{
                "id": string,
                "name": string,
                "priority": "P0" | "P1" | "P2",
                "purpose": string,
                "targetUser": string,
                "workflow": string,
                "dependencies": string[],
                "backendRequirements": string,
                "edgeCases": string[]
              }]
            }
        """.trimIndent()
        val result = geminiClient.generateText(prompt, jsonMode = true).getOrThrow()
        return moshi.adapter(FeaturesAndRolesResponse::class.java).fromJson(result)!!
    }

    private suspend fun generateUx(bp: ProjectBlueprint): UxResponse {
        val prompt = ArchitecturePrompts.getUxPrompt(bp.name, bp.rawIdea)
        val result = geminiClient.generateText(prompt, jsonMode = true).getOrThrow()
        return moshi.adapter(UxResponse::class.java).fromJson(result)!!
    }

    private suspend fun generateTechStack(bp: ProjectBlueprint): TechStack {
        val prompt = """
            Select the best tech stack for: ${bp.name}
            Consider the requirements and idea: ${bp.rawIdea}
            Return JSON:
            {
              "frontend": { "name": string, "category": "Frontend", "justification": string },
              "backend": { "name": string, "category": "Backend", "justification": string },
              "database": { "name": string, "category": "Database", "justification": string },
              "authentication": { "name": string, "category": "Authentication", "justification": string },
              "storage": { "name": string, "category": "Storage", "justification": string },
              "apis": { "name": string, "category": "API Protocol", "justification": string },
              "aiModels": { "name": string, "category": "AI Layer", "justification": string },
              "payments": { "name": string, "category": "Payments", "justification": string },
              "notifications": { "name": string, "category": "Notifications", "justification": string },
              "analytics": { "name": string, "category": "Analytics", "justification": string },
              "deployment": { "name": string, "category": "Deployment", "justification": string }
            }
        """.trimIndent()
        val result = geminiClient.generateText(prompt, jsonMode = true).getOrThrow()
        return moshi.adapter(TechStack::class.java).fromJson(result)!!
    }

    private suspend fun generateSystemArch(bp: ProjectBlueprint): SystemArchitecture {
        val prompt = """
            Design the system architecture for: ${bp.name}
            Tech Stack: ${bp.techStack.frontend.name}, ${bp.techStack.backend.name}, ${bp.techStack.database.name}
            Return JSON:
            {
              "overview": string,
              "layers": [{ "id": string, "name": string, "description": string, "components": string[], "connectsTo": string[], "layerType": string }],
              "dataFlowSteps": string[]
            }
        """.trimIndent()
        val result = geminiClient.generateText(prompt, jsonMode = true).getOrThrow()
        return moshi.adapter(SystemArchitecture::class.java).fromJson(result)!!
    }

    private suspend fun generateDatabase(bp: ProjectBlueprint): DatabaseSchema {
        val prompt = """
            Design the database schema for: ${bp.name}
            Database: ${bp.techStack.database.name}
            Return JSON:
            {
              "databaseType": string,
              "entities": [{
                "tableName": string,
                "description": string,
                "fields": [{ "name": string, "type": string, "isPrimaryKey": boolean, "isForeignKey": boolean, "isNullable": boolean, "description": string }],
                "indexes": string[],
                "validationRules": string[]
              }],
              "relationships": [{ "fromTable": string, "toTable": string, "relationType": string, "description": string }],
              "productionRecommendations": string[]
            }
        """.trimIndent()
        val result = geminiClient.generateText(prompt, jsonMode = true).getOrThrow()
        return moshi.adapter(DatabaseSchema::class.java).fromJson(result)!!
    }

    private suspend fun generateApis(bp: ProjectBlueprint): List<ApiEndpoint> {
        val prompt = """
            Design the API endpoints for: ${bp.name}
            Entities: ${bp.databaseSchema.entities.joinToString { it.tableName }}
            Return JSON:
            [
              {
                "id": string,
                "path": string,
                "method": "GET" | "POST" | "PUT" | "DELETE",
                "group": string,
                "purpose": string,
                "authentication": string,
                "requestBody": string,
                "responseBody": string,
                "statusCodes": string[],
                "errorHandling": string
              }
            ]
            Include at least 20 endpoints.
        """.trimIndent()
        val result = geminiClient.generateText(prompt, jsonMode = true).getOrThrow()
        return moshi.adapter(ApiListResponse::class.java).fromJson("{\"apis\":$result}")!!.apis
    }

    private suspend fun generateSecurityAndOps(bp: ProjectBlueprint): SecurityOpsResponse {
        val prompt = """
            Project: ${bp.name}
            Generate exhaustive security plan, integrations, admin architecture, error handling, performance, and scalability plans.
            Return JSON:
            {
              "security": {
                "authenticationStrategy": string,
                "authorizationRules": string[],
                "inputValidation": string[],
                "apiKeyProtection": string,
                "secretsManagement": string,
                "rateLimiting": string,
                "abusePrevention": string[],
                "fileUploadSecurity": string,
                "databaseSecurity": string[],
                "privacyAndEncryption": string
              },
              "integrations": [{ "name": string, "type": string, "provider": string, "purpose": string, "setupSteps": string[] }],
              "admin": {
                "overview": string,
                "panels": [{ "name": string, "description": string, "actions": string[] }],
                "dashboardMetrics": string[]
              },
              "errorHandling": {
                "globalStrategy": string,
                "clientSideErrors": string[],
                "apiErrors": string[],
                "edgeCases": string[]
              },
              "performance": {
                "frontendOptimization": string[],
                "backendOptimization": string[],
                "databaseOptimization": string[],
                "targetLatency": string
              },
              "scalability": {
                "strategy": string,
                "horizontalScaling": string,
                "cachingLayers": string[],
                "loadBalancing": string
              }
            }
        """.trimIndent()
        val result = geminiClient.generateText(prompt, jsonMode = true).getOrThrow()
        return moshi.adapter(SecurityOpsResponse::class.java).fromJson(result)!!
    }

    private suspend fun generateCostAndEnv(bp: ProjectBlueprint): CostEnvResponse {
        val prompt = """
            Project: ${bp.name}
            Estimate costs and environment variables for production.
            Return JSON:
            {
              "costComplexity": {
                "complexityLevel": "LOW" | "MEDIUM" | "HIGH" | "VERY_HIGH",
                "developmentEstimation": string,
                "infrastructureConsiderations": string[],
                "thirdPartyCosts": string[]
              },
              "envVars": [{ "key": string, "description": string, "example": string, "isSecret": boolean }],
              "estimates": {
                "monthly100Users": string,
                "monthly1kUsers": string,
                "monthly10kUsers": string,
                "monthly100kUsers": string,
                "breakdown": [{ "service": string, "estimate10k": string, "provider": string }]
              }
            }
        """.trimIndent()
        val result = geminiClient.generateText(prompt, jsonMode = true).getOrThrow()
        return moshi.adapter(CostEnvResponse::class.java).fromJson(result)!!
    }

    private suspend fun generateDirectoryAndFiles(bp: ProjectBlueprint): DirectoryAndFilesResponse {
        val techStr = "${bp.techStack.frontend.name}, ${bp.techStack.backend.name}"
        val prompt = ArchitecturePrompts.getDirectoryPrompt(bp.name, techStr)
        val result = geminiClient.generateText(prompt, jsonMode = true).getOrThrow()
        return moshi.adapter(DirectoryAndFilesResponse::class.java).fromJson(result)!!
    }

    private suspend fun generateBuildPlan(bp: ProjectBlueprint): List<BuildPhase> {
        val prompt = """
            Project: ${bp.name}
            Generate a 10-phase implementation roadmap.
            Return JSON:
            [
              {
                "phaseNumber": number,
                "name": string,
                "tasks": string[],
                "dependencies": string[],
                "expectedResult": string,
                "completionCriteria": string[]
              }
            ]
        """.trimIndent()
        val result = geminiClient.generateText(prompt, jsonMode = true).getOrThrow()
        return moshi.adapter(BuildPlanResponse::class.java).fromJson("{\"phases\":$result}")!!.phases
    }

    private suspend fun generateMasterPrompt(bp: ProjectBlueprint): String {
        val prompt = """
            Project: ${bp.name}
            Generate a ONE-SHOT MASTER CODING PROMPT.
            This prompt must be an exhaustive implementation spec for another AI.
            Include: Objectives, Requirements, Tech Stack, Architecture, DB Schema, API Specs, Directory Tree, File Specs, Implementation Order.
            Return the prompt as a raw string.
        """.trimIndent()
        return geminiClient.generateText(prompt).getOrThrow()
    }

    private suspend fun runConsistencyCheck(bp: ProjectBlueprint): HealthReportResponse {
        val prompt = """
            Project: ${bp.name}
            Run a consistency audit on the generated architecture.
            Check: Features ↔ Screens, Screens ↔ APIs, APIs ↔ DB.
            Return JSON:
            {
              "report": {
                "healthScore": number, // 0-100
                "checksSummary": string,
                "criticalIssues": [{ "id": string, "title": string, "description": string, "autoFixable": boolean, "fixAction": string }],
                "warnings": [{ "id": string, "title": string, "description": string, "autoFixable": boolean, "fixAction": string }],
                "recommendations": string[]
              },
              "checklist": [{ "category": string, "task": string, "importance": "HIGH" | "MEDIUM" | "LOW" }]
            }
        """.trimIndent()
        val result = geminiClient.generateText(prompt, jsonMode = true).getOrThrow()
        return moshi.adapter(HealthReportResponse::class.java).fromJson(result)!!
    }

    private suspend fun generateSummary(bp: ProjectBlueprint): String {
        val prompt = """
            Project: ${bp.name}
            Generate a high-level executive summary for this architecture.
            Highlight the key strengths and value.
        """.trimIndent()
        return geminiClient.generateText(prompt).getOrThrow()
    }

    // Helper Response Classes
    @JsonClass(generateAdapter = true) data class IdeaAnalysisResponse(val name: String, val tagline: String, val category: String, val understanding: IdeaUnderstanding)
    @JsonClass(generateAdapter = true) data class FeaturesAndRolesResponse(val roles: List<UserRole>, val features: List<FeatureItem>)
    @JsonClass(generateAdapter = true) data class UxResponse(val ux: UxArchitecture, val designSystem: DesignSystem)
    @JsonClass(generateAdapter = true) data class ApiListResponse(val apis: List<ApiEndpoint>)
    @JsonClass(generateAdapter = true) data class SecurityOpsResponse(val security: SecurityPlan, val integrations: List<IntegrationItem>, val admin: AdminArchitecture, val errorHandling: ErrorHandlingPlan, val performance: PerformancePlan, val scalability: ScalabilityPlan)
    @JsonClass(generateAdapter = true) data class CostEnvResponse(val costComplexity: CostComplexityReport, val envVars: List<EnvVarItem>, val estimates: CostEstimates)
    @JsonClass(generateAdapter = true) data class DirectoryAndFilesResponse(val tree: DirectoryNode, val specs: List<FileSpecification>)
    @JsonClass(generateAdapter = true) data class BuildPlanResponse(val phases: List<BuildPhase>)
    @JsonClass(generateAdapter = true) data class HealthReportResponse(val report: QualityReport, val checklist: List<ChecklistItem>)
    @JsonClass(generateAdapter = true) data class ProjectRequirementsResponse(val name: String, val rawIdea: String, val mode: String)
}
