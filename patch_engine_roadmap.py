import re

with open("app/src/main/java/com/example/domain/engine/ArchitectureEngine.kt", "r") as f:
    content = f.read()

new_funcs = """

    private fun buildDefaultRoadmap(type: DomainType): ProjectRoadmap {
        return ProjectRoadmap(
            now = listOf(RoadmapItem("MVP Release", "Core user flow and database schema", "P0", "1 month")),
            next = listOf(RoadmapItem("Payments Integration", "Stripe integration for subscriptions", "P1", "2 weeks")),
            later = listOf(RoadmapItem("Analytics Dashboard", "Admin metrics and user behavior tracking", "P2", "1 month")),
            experimental = listOf(RoadmapItem("AI Recommendations", "Personalized feed based on user history", "P3", "TBD"))
        )
    }
"""

content = content[:content.rfind("}")] + new_funcs + "}\n"

# Inject into generateProjectBlueprint
call_site = """        val systemArch = buildSystemArchitecture(archetype, techStack)"""
inject_calls = """        val systemArch = buildSystemArchitecture(archetype, techStack)
        val roadmap = buildDefaultRoadmap(archetype)"""
content = content.replace(call_site, inject_calls)

constructor_site = """            systemArchitecture = systemArch,"""
constructor_inject = """            systemArchitecture = systemArch,
            projectRoadmap = roadmap,"""
content = content.replace(constructor_site, constructor_inject)

with open("app/src/main/java/com/example/domain/engine/ArchitectureEngine.kt", "w") as f:
    f.write(content)
