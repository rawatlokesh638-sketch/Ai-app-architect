import re

with open("app/src/main/java/com/example/domain/engine/ArchitectureEngine.kt", "r") as f:
    content = f.read()

new_funcs = """

    private fun buildProjectHealth(type: DomainType): ProjectHealth {
        return ProjectHealth(
            architectureScore = 95,
            uiUxScore = 90,
            codeQualityScore = 100, // Not written yet, but structurally sound
            securityScore = 85,
            performanceScore = 92,
            accessibilityScore = 88,
            testingScore = 80,
            scalabilityScore = 90,
            productionReadinessScore = 75,
            overallScore = 89
        )
    }
"""

content = content[:content.rfind("}")] + new_funcs + "}\n"

# Inject into generateProjectBlueprint
call_site = """        val roadmap = buildDefaultRoadmap(archetype)"""
inject_calls = """        val roadmap = buildDefaultRoadmap(archetype)
        val health = buildProjectHealth(archetype)"""
content = content.replace(call_site, inject_calls)

constructor_site = """            projectRoadmap = roadmap,"""
constructor_inject = """            projectRoadmap = roadmap,
            projectHealth = health,"""
content = content.replace(constructor_site, constructor_inject)

with open("app/src/main/java/com/example/domain/engine/ArchitectureEngine.kt", "w") as f:
    f.write(content)
