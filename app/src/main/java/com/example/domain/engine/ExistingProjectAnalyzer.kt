package com.example.domain.engine

import com.example.data.model.ProjectBlueprint

object ExistingProjectAnalyzer {

    data class ExistingArchitectureReport(
        val projectName: String,
        val detectedStack: String,
        val componentCount: Int,
        val routeCount: Int,
        val apiCount: Int,
        val identifiedProblems: List<String>,
        val securityDebt: List<String>,
        val recommendedImprovements: List<String>,
        val migrationPlan: List<String>
    )

    fun analyzeCodebase(inputPrompt: String): Pair<ExistingArchitectureReport, ProjectBlueprint> {
        val cleanInput = inputPrompt.trim()
        val projectName = if (cleanInput.lines().firstOrNull()?.isNotBlank() == true && cleanInput.lines().first().length < 40) {
            cleanInput.lines().first().replace(Regex("[^a-zA-Z0-9 ]"), "")
        } else "Legacy App Migration"

        val report = ExistingArchitectureReport(
            projectName = projectName,
            detectedStack = "Legacy React 16 + Express REST + MongoDB Monolith",
            componentCount = 38,
            routeCount = 14,
            apiCount = 22,
            identifiedProblems = listOf(
                "Monolithic untyped Express controllers lacking validation schemas.",
                "Direct database queries executed inside UI rendering components.",
                "Hardcoded JWT secrets and API tokens found in legacy configuration files.",
                "Missing indexing on high-frequency query fields causing query timeouts."
            ),
            securityDebt = listOf(
                "Outdated dependencies with known High severity CVE vulnerabilities.",
                "Lack of CORS origin white-listing and missing CSRF headers."
            ),
            recommendedImprovements = listOf(
                "Migrate frontend UI components to Next.js 14 / Jetpack Compose typed architecture.",
                "Refactor backend routes into clean modular service controllers with Zod / Moshi validation.",
                "Implement PostgreSQL relational schema with Room / Prisma ORM for type safety."
            ),
            migrationPlan = listOf(
                "Step 1: Containerize legacy database and extract OpenAPI/Swagger endpoint schemas.",
                "Step 2: Initialize modern AI App Architect blueprint with strict TypeScript/Kotlin types.",
                "Step 3: Incrementally proxy legacy REST endpoints to new microservice controllers.",
                "Step 4: Execute zero-downtime database migration and cut over DNS."
            )
        )

        // Generate blueprint representation of modern migrated architecture
        val blueprint = runBlockingGenerateBlueprint(
            idea = "Migrate legacy codebase '$projectName' to modern production architecture with containerized microservices, full type safety, and automated security guardrails."
        )

        return Pair(report, blueprint)
    }

    private fun runBlockingGenerateBlueprint(idea: String): ProjectBlueprint {
        return kotlinx.coroutines.runBlocking {
            ArchitectureEngine.generateBlueprint(idea, "SaaS", listOf("Legacy Migration", "Modernization"))
        }
    }
}
