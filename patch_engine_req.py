import re

with open("app/src/main/java/com/example/domain/engine/ArchitectureEngine.kt", "r") as f:
    content = f.read()

new_funcs = """

    private fun buildProjectRequirements(type: DomainType, rawIdea: String): ProjectRequirements {
        return ProjectRequirements(
            functional = listOf("User Registration and Authentication", "Role-based access control", "Core CRUD operations for main entities"),
            nonFunctional = listOf("99.9% Uptime SLA", "Sub-200ms API response times", "GDPR compliance"),
            productGoals = listOf("Achieve seamless user onboarding", "Minimize cognitive load through intuitive UX"),
            constraints = listOf("Must operate within strict mobile data limits", "Requires backward compatibility for legacy APIs"),
            mvpScope = listOf("Core user flow", "Basic reporting", "Email notifications"),
            futureScope = listOf("AI-driven analytics", "Third-party integrations (Zapier)", "Advanced multi-tenant support")
        )
    }

    private fun buildCostComplexityReport(type: DomainType): CostComplexityReport {
        return CostComplexityReport(
            complexityLevel = "HIGH",
            developmentEstimation = "Requires 4-6 weeks for MVP (2 backend, 2 frontend engineers)",
            infrastructureConsiderations = listOf("Autoscaling Kubernetes clusters needed for traffic spikes", "Multi-region database replication"),
            thirdPartyCosts = listOf("$20/mo per active seat for Auth provider", "Estimated $150/mo for Managed PostgreSQL", "S3 Storage costs (variable)")
        )
    }
"""

content = content[:content.rfind("}")] + new_funcs + "}\n"

# Inject into generateProjectBlueprint
call_site = """        val systemArch = buildSystemArchitecture(archetype, techStack)"""
inject_calls = """        val systemArch = buildSystemArchitecture(archetype, techStack)
        val requirements = buildProjectRequirements(archetype, cleanIdea)
        val costComplexity = buildCostComplexityReport(archetype)"""
content = content.replace(call_site, inject_calls)

constructor_site = """            systemArchitecture = systemArch,"""
constructor_inject = """            systemArchitecture = systemArch,
            requirements = requirements,
            costComplexity = costComplexity,"""
content = content.replace(constructor_site, constructor_inject)

with open("app/src/main/java/com/example/domain/engine/ArchitectureEngine.kt", "w") as f:
    f.write(content)
