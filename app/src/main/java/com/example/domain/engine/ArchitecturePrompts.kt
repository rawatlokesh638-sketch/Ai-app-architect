package com.example.domain.engine

object ArchitecturePrompts {

    const val SYSTEM_INSTRUCTIONS = """
        You are the AI Software Factory Generation Engine. 
        Your mission is to transform a single app idea into a high-fidelity, production-grade software blueprint.
        
        PRINCIPLES:
        1. NO TRUNCATION: Always provide complete details. If a list is requested, provide the full list.
        2. PRODUCTION FIRST: Every design decision must be scalable, secure, and professional.
        3. GRANULARITY: Provide deep technical details (schemas, types, status codes, component props).
        4. CONSISTENCY: Ensure that features mentioned in requirements exist in screens, APIs, and the database.
        5. ARCHITECTURAL RIGOR: Follow industry standards (Clean Architecture, MVVM, Microservices where appropriate).
    """

    fun getUxPrompt(projectName: String, idea: String) = """
        Project: $projectName ($idea)
        Task: Design the UX architecture and design system.
        
        Return a JSON object exactly matching this structure:
        {
          "ux": {
            "screens": [
              {
                "id": "string",
                "name": "string",
                "route": "string",
                "description": "string",
                "layoutHierarchy": "Descriptive string of the view tree",
                "components": [
                  {
                    "name": "string",
                    "type": "string",
                    "purpose": "string",
                    "props": {},
                    "states": ["string"]
                  }
                ],
                "stateSpecifications": "string",
                "interactions": ["string"],
                "animations": ["string"],
                "responsiveRules": "string",
                "apiDependencies": ["string"],
                "permissionsRequired": ["string"]
              }
            ],
            "navigationStructure": "string",
            "onboardingFlow": "string",
            "authFlow": "string",
            "userJourneys": [{ "title": "string", "steps": ["string"] }],
            "requiredPermissions": ["string"],
            "responsiveBehavior": "string"
          },
          "designSystem": {
            "themeOverview": "string",
            "primaryColor": "string",
            "secondaryColor": "string",
            "accentColor": "string",
            "typography": ["string"],
            "componentStyles": "string",
            "spacingGuidelines": "string",
            "iconography": "string"
          }
        }
        
        Be EXHAUSTIVE. Provide at least 15 core screens. 
        For each screen, provide at least 8-10 components.
    """.trimIndent()

    fun getDirectoryPrompt(projectName: String, techStack: String) = """
        Project: $projectName
        Tech Stack: $techStack
        Task: Create a COMPLETE production-grade directory tree and file specifications.
        
        EVERYTHING must be inside ONE root folder named "${projectName.replace(" ", "-")}".
        Provide a structure for a REAL-WORLD software project (Monorepo or polyrepo style as appropriate).
        
        Return a JSON object exactly matching this structure:
        {
          "tree": {
            "name": "string",
            "type": "folder",
            "children": [ ... ] 
          },
          "specs": [
            {
              "filePath": "string",
              "purpose": "string",
              "dependencies": ["string"],
              "responsibilities": ["string"],
              "importantFunctions": ["string"],
              "inputs": "string",
              "outputs": "string",
              "securityNotes": "string",
              "implementationNotes": "string"
            }
          ]
        }
        
        Include at least 100 files in the tree. 
        Provide specs for at least 40 critical files.
    """.trimIndent()

    fun getModificationPrompt(currentBlueprintJson: String, userInstruction: String): String {
        return """
            Current Architecture Blueprint (JSON):
            $currentBlueprintJson
            
            User Modification Request:
            "$userInstruction"
            
            ACT AS AN AUTONOMOUS SENIOR SOFTWARE ENGINEER. YOUR GOAL IS TO MODIFY THE CURRENT BLUEPRINT TO INCORPORATE THE USER'S REQUEST.
            
            INSTRUCTIONS:
            1. Analyze the current project state (requirements, features, screens, database, APIs, etc.).
            2. Create an internal change plan before modifying anything.
            3. In your 'diffSummary', use the following structure (Markdown):
               ## UNDERSTANDING
               Clear summary of what the user requested in the context of the existing project.
               
               ## PLAN
               Step-by-step breakdown of architectural changes.
               
               ## AFFECTED MODULES
               List of features, screens, or layers impacted.
               
               ## FILES TO CREATE / MODIFY
               List of file paths that would be added or changed in a real implementation.
               
               ## DATABASE & API IMPACT
               Describe schema or endpoint changes.
               
               ## SECURITY & PERFORMANCE NOTES
               Any implications on safety or speed.

            4. Provide the COMPLETE updated ProjectBlueprint object in the 'updatedBlueprint' field.
            5. Increment the version number (e.g., from 1.0.0 to 1.0.1).
            6. Add an entry to 'engineeringLog' describing this change.
            7. Ensure the design system remains consistent unless a redesign was requested.
            
            RETURN JSON:
            {
              "diffSummary": "Markdown summary using the mandatory structure above",
              "updatedBlueprint": { ... entire updated ProjectBlueprint object ... }
            }
        """.trimIndent()
    }

    fun getHealthCheckPrompt(blueprintJson: String) = """
        Analyze this Project Blueprint and provide a detailed Engineering Health Report.
        Blueprint: $blueprintJson
        
        Evaluate across these dimensions (0-100):
        - Architecture (Cleanliness, separation of concerns)
        - UI/UX (Completeness, design system, states)
        - Code Quality (File specs, responsibilities)
        - Security (Input validation, auth, secrets)
        - Performance (Optimization, latency)
        - Accessibility (Labels, contrast, structure)
        - Testing (Coverage, strategy)
        - Scalability (Database bottlenecks, caching)
        - Production Readiness (Env vars, monitoring, rollback)
        
        Identify specific Engineering Issues with levels: CRITICAL, HIGH, MEDIUM, LOW.
        For each issue: Title, Category, Description, Location, Impact, Root Cause, Recommended Fix, Auto-Fixable (true/false).
        
        Return JSON matching ProjectHealth and QualityReport structures.
    """.trimIndent()

    fun getRoadmapPrompt(blueprintJson: String) = """
        Analyze this Project Blueprint and act as a Product Strategist.
        Blueprint: $blueprintJson
        
        Suggest a prioritized roadmap for the future of this product.
        Categories: NOW (Immediate value/fixes), NEXT (High impact growth), LATER (Future features), EXPERIMENTAL (Innovation).
        
        For each item: Title, Description, Priority, Impact, Complexity, Dependencies, Reason (Why this is the next best move).
        Recommendations must be specific to this project's category and features.
        
        Return JSON matching ProjectRoadmap structure.
    """.trimIndent()

    fun getBugHunterPrompt(blueprintJson: String) = """
        Act as a QA Engineer and Security Auditor. 
        Perform a Deep Audit on this blueprint for potential bugs, logical flaws, and security risks.
        Blueprint: $blueprintJson
        
        Search for:
        - Broken navigation logic
        - Missing loading/error states in critical screens
        - Incorrect API handling or missing status codes
        - Invalid or inconsistent database relationships
        - Security risks (exposed data, missing validation)
        - Performance bottlenecks
        - Duplicated logic in file specs
        
        Categorize issues into CRITICAL, HIGH, MEDIUM, LOW.
        Return JSON matching the QualityReport structure with detailed EngineeringIssue entries.
    """.trimIndent()
}
