import re

with open("app/src/main/java/com/example/domain/engine/ArchitectureEngine.kt", "r") as f:
    content = f.read()

# Add new functions before the last brace
new_funcs = """

    private fun buildDesignSystem(type: DomainType): DesignSystem {
        return DesignSystem(
            themeOverview = "Clean, highly legible interface focusing on content and speed.",
            primaryColor = if (type == DomainType.FINTECH) "#00C853" else "#2962FF",
            secondaryColor = "#FF3D00",
            accentColor = "#00E5FF",
            typography = listOf("Inter for UI", "Roboto Mono for Data"),
            componentStyles = "Rounded corners (8dp), subtle drop shadows, solid color fills",
            spacingGuidelines = "8dp baseline grid (8, 16, 24, 32, 64)"
        )
    }

    private fun buildEnvironmentVariables(type: DomainType, stack: TechStack): List<EnvVarItem> {
        val vars = mutableListOf(
            EnvVarItem("DATABASE_URL", "Primary database connection string", "postgres://user:pass@host:5432/db", true),
            EnvVarItem("JWT_SECRET", "HMAC key for JWT signing", "x8f9...d21", true),
            EnvVarItem("PORT", "Server port", "8080", false),
            EnvVarItem("API_KEY", "External API access", "ak_12345", true)
        )
        if (type == DomainType.AI_AGENT) {
            vars.add(EnvVarItem("OPENAI_API_KEY", "LLM Access", "sk-...", true))
        }
        return vars
    }

    private fun buildIntegrations(type: DomainType): List<IntegrationItem> {
        val ints = mutableListOf(
            IntegrationItem("SendGrid", "NOTIFICATION", "Twilio", "Transactional emails for signup and alerts", listOf("Create API Key", "Verify Sender Domain", "Add to .env")),
            IntegrationItem("Sentry", "ANALYTICS", "Sentry.io", "Error tracking and crash reporting", listOf("Create Project", "Add DSN to frontend/backend"))
        )
        if (type == DomainType.ECOMMERCE || type == DomainType.FINTECH) {
            ints.add(IntegrationItem("Stripe", "PAYMENT", "Stripe", "Process credit cards and subscriptions", listOf("Get Publishable Key", "Get Secret Key", "Setup Webhook Endpoint")))
        }
        return ints
    }

    private fun buildUserRoles(type: DomainType): List<UserRole> {
        return listOf(
            UserRole("Admin", "Full system access", listOf("manage_users", "view_financials", "system_config"), listOf("Maintain system health", "Monitor abuse")),
            UserRole("Standard User", "Regular authenticated user", listOf("read_own_data", "write_own_data"), listOf("Achieve core tasks", "Manage profile"))
        )
    }

    private fun buildAdminArchitecture(type: DomainType): AdminArchitecture {
        return AdminArchitecture(
            overview = "Secure internal portal for managing users and system metrics.",
            panels = listOf(
                AdminPanel("User Management", "View, ban, or elevate user accounts.", listOf("Search Users", "Reset Passwords", "Ban Accounts")),
                AdminPanel("System Health", "Live metrics on server load and DB latency.", listOf("CPU Usage Graph", "Active Connections", "Error Logs"))
            ),
            dashboardMetrics = listOf("Total Active Users (24h)", "Revenue / Transactions (Today)", "System Error Rate (%)")
        )
    }
"""

content = content[:content.rfind("}")] + new_funcs + "}\n"

# Now inject these calls into the blueprint generation
# Locate where buildSystemArchitecture is called
call_site = """        val systemArch = buildSystemArchitecture(archetype, techStack)"""
inject_calls = """        val systemArch = buildSystemArchitecture(archetype, techStack)
        val designSystem = buildDesignSystem(archetype)
        val envVars = buildEnvironmentVariables(archetype, techStack)
        val integrations = buildIntegrations(archetype)
        val userRoles = buildUserRoles(archetype)
        val adminArch = buildAdminArchitecture(archetype)"""
content = content.replace(call_site, inject_calls)

# Add them to the ProjectBlueprint constructor
constructor_site = """            systemArchitecture = systemArch,
            databaseSchema = dbSchema,"""
constructor_inject = """            systemArchitecture = systemArch,
            databaseSchema = dbSchema,
            designSystem = designSystem,
            environmentVariables = envVars,
            integrations = integrations,
            userRoles = userRoles,
            adminArchitecture = adminArch,"""
content = content.replace(constructor_site, constructor_inject)

with open("app/src/main/java/com/example/domain/engine/ArchitectureEngine.kt", "w") as f:
    f.write(content)
