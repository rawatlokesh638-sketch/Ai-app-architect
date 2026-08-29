package com.example.domain.engine

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.data.json.BlueprintJsonAdapter
import com.example.data.model.*
import java.util.UUID

object ArchitectureEngine {

    /**
     * Synthesizes a full enterprise-grade architecture blueprint based on a raw user prompt.
     * Combines smart heuristic inference with structured Gemini AI generation when available.
     */
    suspend fun generateBlueprint(
        rawIdea: String,
        selectedCategory: String? = null,
        userDefinedTags: List<String> = emptyList()
    ): ProjectBlueprint {
        val cleanIdea = rawIdea.trim()
        val ideaLower = cleanIdea.lowercase()

        // 1. Determine Product Archetype & Title
        val archetype = determineArchetype(cleanIdea, ideaLower, selectedCategory)
        val projectName = extractProjectName(cleanIdea, archetype)
        val tagline = extractTagline(archetype, cleanIdea)

        // 2. Synthesize Idea Understanding
        val understanding = buildIdeaUnderstanding(cleanIdea, archetype)

        // 3. Synthesize Product Strategy & MVP Scope
        val strategy = buildProductStrategy(archetype, cleanIdea)

        // 4. Feature Engineering (P0, P1, P2)
        val features = buildFeatures(archetype, cleanIdea)

        // 5. UX Architecture & Screens
        val uxArch = buildUxArchitecture(archetype, features)

        // 6. Tech Stack Selection with Justifications
        val techStack = buildTechStack(archetype, cleanIdea)

        // 7. System Architecture & Visual Diagram Layers
        val systemArch = buildSystemArchitecture(archetype, techStack)

        // 8. Database Schema Design (Tables, fields, relations, validation)
        val dbSchema = buildDatabaseSchema(archetype, features)

        // 9. API Design (Endpoints, verbs, auth, payloads)
        val apiEndpoints = buildApiDesign(archetype, features, dbSchema)

        // 10. Security Plan
        val security = buildSecurityPlan(archetype, techStack)

        // 11. Directory Tree Structure
        val dirTree = buildDirectoryTree(archetype, techStack)

        // 12. File-by-File Specifications
        val fileSpecs = buildFileSpecifications(archetype, techStack, features)

        // 13. Build Plan Roadmap (Phases 1 - 10)
        val buildPlan = buildRoadmap(archetype, features)

        // 14. Smart Assumptions
        val assumptions = buildAssumptions(archetype, techStack)

        val costEstimates = buildCostEstimates(archetype, techStack)
        val scalabilityPlan = buildScalabilityPlan(archetype, techStack)
        val testingChecklist = buildTestingChecklist(archetype, features)
        val deploymentPlan = buildDeploymentPlan(archetype, techStack)
        val altTechStack = buildAlternativeTechStack(archetype, techStack)

        // 15. Quality Control & Health Score Audit
        val qualityReport = runQualityAudit(features, uxArch.screens, apiEndpoints, dbSchema, techStack, security)

        // 16. One-Shot Master Coding Prompt
        val masterPrompt = generateOneShotMasterPrompt(
            projectName = projectName,
            rawIdea = cleanIdea,
            archetype = archetype,
            techStack = techStack,
            features = features,
            uxArch = uxArch,
            dbSchema = dbSchema,
            apiEndpoints = apiEndpoints,
            dirTree = dirTree,
            security = security,
            buildPlan = buildPlan
        )

        val synthesizedTags = (
            userDefinedTags +
            listOf(archetype.category, techStack.frontend.name.split(" ").first(), techStack.backend.name.split(" ").first(), techStack.database.name.split(" ").first())
        ).map { it.trim() }.filter { it.isNotBlank() }.distinct()

        val draftBlueprint = ProjectBlueprint(
            id = UUID.randomUUID().toString(),
            name = projectName,
            rawIdea = cleanIdea,
            tagline = tagline,
            category = archetype.category,
            version = "1.0.0",
            lastModified = System.currentTimeMillis(),
            healthScore = qualityReport.healthScore,
            ideaUnderstanding = understanding,
            productStrategy = strategy,
            features = features,
            uxArchitecture = uxArch,
            techStack = techStack,
            systemArchitecture = systemArch,
            databaseSchema = dbSchema,
            apiDesign = apiEndpoints,
            securityPlan = security,
            directoryTree = dirTree,
            fileSpecifications = fileSpecs,
            buildPlan = buildPlan,
            masterCodingPrompt = masterPrompt,
            qualityReport = qualityReport,
            assumptions = assumptions,
            tags = synthesizedTags,
            costEstimates = costEstimates,
            scalabilityPlan = scalabilityPlan,
            testingChecklist = testingChecklist,
            deploymentPlan = deploymentPlan,
            alternativeTechStack = altTechStack
        )

        val execSummary = GeminiSummaryGenerator.generateExecutiveSummary(draftBlueprint)

        return draftBlueprint.copy(executiveSummary = execSummary)
    }

    // --- Domain Archetype Detection ---
    enum class DomainType(val category: String, val defaultTitle: String) {
        ECOMMERCE("Ecommerce", "HyperLocal Commerce Platform"),
        AI_AGENT("AI Chat & Copilot", "Cognitive AI Assistant"),
        SAAS("B2B SaaS", "Cloud Metrics & Automation Hub"),
        MARKETPLACE("Marketplace", "On-Demand Service Marketplace"),
        FINTECH("Finance", "Personal Wealth & Expense Tracker"),
        FITNESS("Fitness & Health", "Adaptive AI Workout Studio"),
        EDUCATION("Education", "Gamified Micro-Learning Portal"),
        DELIVERY("Delivery & Logistics", "Hyperlocal Fleet & Dispatch Hub"),
        SOCIAL("Social Network", "Niche Community & Channels"),
        PRODUCTIVITY("Productivity", "Offline-First Workflow Studio"),
        GENERAL("General Application", "Modern Software System")
    }

    private fun determineArchetype(raw: String, lower: String, category: String?): DomainType {
        if (category != null) {
            when (category.lowercase()) {
                "ecommerce" -> return DomainType.ECOMMERCE
                "ai chat app" -> return DomainType.AI_AGENT
                "saas" -> return DomainType.SAAS
                "marketplace" -> return DomainType.MARKETPLACE
                "finance" -> return DomainType.FINTECH
                "fitness" -> return DomainType.FITNESS
                "education" -> return DomainType.EDUCATION
                "delivery" -> return DomainType.DELIVERY
                "social network" -> return DomainType.SOCIAL
                "productivity" -> return DomainType.PRODUCTIVITY
            }
        }

        return when {
            lower.contains("meesho") || lower.contains("shop") || lower.contains("ecommerce") || lower.contains("e-commerce") || lower.contains("cart") || lower.contains("seller") || lower.contains("store") -> DomainType.ECOMMERCE
            lower.contains("gemini") || lower.contains("chat") || lower.contains("copilot") || lower.contains("agent") || lower.contains("rag") || lower.contains("ai assistant") -> DomainType.AI_AGENT
            lower.contains("saas") || lower.contains("subscription") || lower.contains("b2b") || lower.contains("analytics") || lower.contains("tenant") || lower.contains("metric") -> DomainType.SAAS
            lower.contains("marketplace") || lower.contains("freelance") || lower.contains("booking") || lower.contains("contractor") || lower.contains("escrow") -> DomainType.MARKETPLACE
            lower.contains("crypto") || lower.contains("finance") || lower.contains("budget") || lower.contains("bank") || lower.contains("wealth") || lower.contains("expense") -> DomainType.FINTECH
            lower.contains("fitness") || lower.contains("workout") || lower.contains("gym") || lower.contains("health") || lower.contains("diet") || lower.contains("exercise") -> DomainType.FITNESS
            lower.contains("education") || lower.contains("learn") || lower.contains("course") || lower.contains("quiz") || lower.contains("duolingo") || lower.contains("student") -> DomainType.EDUCATION
            lower.contains("delivery") || lower.contains("food") || lower.contains("grocery") || lower.contains("courier") || lower.contains("rider") || lower.contains("dispatch") -> DomainType.DELIVERY
            lower.contains("social") || lower.contains("community") || lower.contains("feed") || lower.contains("post") || lower.contains("forum") || lower.contains("thread") -> DomainType.SOCIAL
            lower.contains("kanban") || lower.contains("task") || lower.contains("todo") || lower.contains("note") || lower.contains("linear") || lower.contains("jira") -> DomainType.PRODUCTIVITY
            else -> DomainType.GENERAL
        }
    }

    private fun extractProjectName(raw: String, type: DomainType): String {
        // Look for named phrases e.g. "called X" or "name is X"
        val regex = Regex("""(?:called|named|name is|title is)\s+([A-Za-z0-9\s_-]+)""", RegexOption.IGNORE_CASE)
        val match = regex.find(raw)
        if (match != null) {
            val name = match.groupValues[1].trim().split(" ").take(3).joinToString(" ")
            if (name.length in 3..25) return name
        }

        return when (type) {
            DomainType.ECOMMERCE -> "KiranMart Pro"
            DomainType.AI_AGENT -> "CognitiveHub AI"
            DomainType.SAAS -> "PulseMetrics SaaS"
            DomainType.MARKETPLACE -> "TaskCraft Hub"
            DomainType.FINTECH -> "NovaWealth"
            DomainType.FITNESS -> "AegisFit AI"
            DomainType.EDUCATION -> "MindSprint Academy"
            DomainType.DELIVERY -> "SwiftDrop Logistics"
            DomainType.SOCIAL -> "NexusVibe"
            DomainType.PRODUCTIVITY -> "SprintFlow Studio"
            DomainType.GENERAL -> "OmniCraft App"
        }
    }

    private fun extractTagline(type: DomainType, raw: String): String {
        return when (type) {
            DomainType.ECOMMERCE -> "Social commerce & catalog reseller platform with automated logistics"
            DomainType.AI_AGENT -> "Autonomous multimodal AI copilot with enterprise vector RAG"
            DomainType.SAAS -> "Multi-tenant B2B operations and subscription telemetry cloud"
            DomainType.MARKETPLACE -> "Trust-verified two-sided service matchmaking with escrow payouts"
            DomainType.FINTECH -> "AI-powered automated expense intelligence and predictive budget tracker"
            DomainType.FITNESS -> "Computer vision-assisted progressive overload coaching engine"
            DomainType.EDUCATION -> "Gamified spaced-repetition learning paths with interactive feedback"
            DomainType.DELIVERY -> "Sub-second hyperlocal order routing and real-time fleet tracker"
            DomainType.SOCIAL -> "Real-time threaded community discourse with reputation governance"
            DomainType.PRODUCTIVITY -> "Offline-first CRDT issue tracker and high-velocity workflow engine"
            DomainType.GENERAL -> "Enterprise-grade scalable software application"
        }
    }

    private fun buildIdeaUnderstanding(raw: String, type: DomainType): IdeaUnderstanding {
        val targetUsers = when (type) {
            DomainType.ECOMMERCE -> listOf("Local Fashion Sellers & Wholesalers", "Micro-Influencers / WhatsApp Resellers", "End Consumers")
            DomainType.AI_AGENT -> listOf("Knowledge Workers", "Software Engineers", "Operations Analysts")
            DomainType.SAAS -> listOf("B2B Team Admins", "Product Managers", "Finance Controllers")
            DomainType.MARKETPLACE -> listOf("Freelancers & Technicians", "Homeowners & Businesses", "Platform Dispute Mediators")
            DomainType.FINTECH -> listOf("Young Professionals", "Freelancers with Variable Income", "Couples Sharing Expenses")
            DomainType.FITNESS -> listOf("Strength Athletes", "Casual Gym Goers", "Personal Trainers")
            DomainType.EDUCATION -> listOf("Self-Taught Learners", "Bootcamp Students", "Exam Candidates")
            DomainType.DELIVERY -> listOf("End Customers", "Fleet Couriers & Riders", "Restaurant & Dark Store Operators")
            DomainType.SOCIAL -> listOf("Topic Enthusiasts", "Community Moderators", "Content Creators")
            DomainType.PRODUCTIVITY -> listOf("Software Squads", "Engineering Leads", "Remote Agile Teams")
            DomainType.GENERAL -> listOf("Primary End Users", "System Administrators", "Operational Staff")
        }

        val problem = when (type) {
            DomainType.ECOMMERCE -> "Local fashion sellers lack zero-commission digital catalogs and easy social sharing to WhatsApp, while resellers struggle with manual order collection and untracked COD payments."
            DomainType.AI_AGENT -> "Users are overwhelmed by scattered documents and need contextual AI answers with grounded citations, real-time tool execution, and deterministic guardrails."
            DomainType.SAAS -> "Fast-growing companies struggle to aggregate customer metrics, bill on dynamic usage tiers, and maintain multi-tenant audit isolation."
            DomainType.MARKETPLACE -> "Customers experience uncertain service pricing and unreliable contractors, while service pros suffer from high lead fees and payment delays."
            DomainType.FINTECH -> "Manual transaction logging is tedious and error-prone; users lack proactive alerts before overspending."
            DomainType.FITNESS -> "Generic fitness plans fail to adjust when workouts are missed, and users lack immediate biomechanical form correction."
            DomainType.EDUCATION -> "Traditional online courses suffer from low 5% completion rates due to passive video watching and lack of active recall reinforcement."
            DomainType.DELIVERY -> "High delivery latency and inventory mismatches during peak traffic lead to canceled orders and customer churn."
            DomainType.SOCIAL -> "Centralized social algorithms prioritize rage-bait and ads, destroying organic discussions and specialized niche networking."
            DomainType.PRODUCTIVITY -> "Bloated issue tracking tools suffer from slow UI lag, fragile offline sync, and disconnected Git workflow loops."
            DomainType.GENERAL -> "Users require an intuitive, reliable solution to streamline day-to-day operations and centralize disparate data."
        }

        val solution = when (type) {
            DomainType.ECOMMERCE -> "A unified mobile-first ecosystem featuring a 1-tap WhatsApp catalog exporter, customizable reseller profit margins, integrated UPI/Card escrow payments, and automated 3PL courier pickup."
            DomainType.AI_AGENT -> "A fast streaming conversational interface with hybrid vector search (RAG), sandboxed Python/SQL tool executor, and exportable audit trails."
            DomainType.SAAS -> "A turn-key multi-tenant analytics dashboard with automated Stripe metered billing, granular RBAC permissions, and real-time webhook broadcasts."
            DomainType.MARKETPLACE -> "A transparent booking engine with escrow milestone release, identity-verified contractor profiles, in-app messaging, and automated dispute resolution."
            DomainType.FINTECH -> "An encrypted smart ledger with bank sync, receipt OCR parsing, zero-knowledge security, and predictive month-end balance forecasts."
            DomainType.FITNESS -> "A smart workout companion featuring automated rep recognition via camera, personalized progressive overload curves, and macro meal vision scanning."
            DomainType.EDUCATION -> "A bite-sized gamified learning loop with spaced repetition algorithms, streak retention mechanics, and interactive coding challenges."
            DomainType.DELIVERY -> "A high-concurrency order routing engine with sub-second rider location streaming, batch order allocation, and dark store inventory reservations."
            DomainType.SOCIAL -> "A privacy-respecting community platform with threaded discussions, live audio huddles, token-based karma moderation, and customizable feeds."
            DomainType.PRODUCTIVITY -> "An ultra-responsive offline-first client utilizing CRDT synchronization, sub-50ms keyboard interactions, and automated CI/CD branch links."
            DomainType.GENERAL -> "A modern client-server architecture delivering real-time synchronization, responsive UX, and secure cloud storage."
        }

        return IdeaUnderstanding(
            productType = type.category,
            targetUsers = targetUsers,
            primaryProblem = problem,
            proposedSolution = solution,
            targetPlatforms = listOf("Android (Jetpack Compose)", "Web (Responsive PWA / React)", "Cloud Backend (REST / WebSockets)"),
            businessModel = when (type) {
                DomainType.ECOMMERCE -> "Transaction fee on orders (2-3%) + Seller catalog premium tier"
                DomainType.AI_AGENT -> "Freemium token usage tiers ($15/mo Pro, $49/mo Team)"
                DomainType.SAAS -> "Tiered monthly subscription + Metered API volume"
                DomainType.MARKETPLACE -> "Take rate on completed escrow bookings (10-15%)"
                DomainType.FINTECH -> "Premium subscription ($7.99/mo) for automated bank sync & AI tax reports"
                DomainType.FITNESS -> "$12/mo subscription with 7-day free trial"
                DomainType.EDUCATION -> "Freemium learning paths with Pro certification pass"
                DomainType.DELIVERY -> "Delivery fee + Merchant commission (15%) + Surge multiplier"
                DomainType.SOCIAL -> "Community creator subscriptions + Micro-tip tokens"
                DomainType.PRODUCTIVITY -> "$10/seat/mo Team plan with unlimited board history"
                DomainType.GENERAL -> "SaaS subscription with tiered pricing"
            },
            keyAssumptions = listOf(
                "Users expect mobile-first interaction with sub-second response times.",
                "Data persistence requires offline-first caching for smooth mobile connectivity.",
                "Zero secret keys stored in client source code; all credentials securely bridged."
            ),
            constraints = listOf(
                "Strict adherence to Material Design 3 and WCAG AA accessibility contrast.",
                "Min SDK 24 for Android client and TLS 1.3 encryption across all network payloads."
            )
        )
    }

    private fun buildProductStrategy(type: DomainType, raw: String): ProductStrategy {
        val useCases = when (type) {
            DomainType.ECOMMERCE -> listOf(
                "Reseller selects 10 fashion items from wholesaler catalog, adds 20% margin, and generates a WhatsApp store link.",
                "Buyer clicks link, chooses size/color, enters delivery address, and completes UPI payment.",
                "Wholesaler receives pre-paid order label, packs garment, and carrier is automatically scheduled for pickup."
            )
            DomainType.AI_AGENT -> listOf(
                "User uploads 50-page PDF policy and asks 'What is our refund timeline for European customers?'",
                "AI provides exact answer in 1.2s citing page 14 with direct text highlight.",
                "User triggers tool calling to generate a summarized email draft and export to Notion."
            )
            DomainType.SAAS -> listOf(
                "Growth lead logs in to view real-time MRR and churn cohort graphs.",
                "Admin invites 5 teammates with 'Editor' and 'Viewer' RBAC roles.",
                "System automatically invoices client at end of month based on API event units."
            )
            else -> listOf(
                "User downloads app and signs in seamlessly via Google / Biometric auth.",
                "User creates first item/record and sees immediate real-time sync across devices.",
                "User exports or shares report with team in one click."
            )
        }

        val mvpScope = listOf(
            "Complete User Authentication & Session Token Lifecycle",
            "Core Entity CRUD & Real-Time Sync Engine",
            "Primary Dashboard with Key Metric Cards & Interactive Feeds",
            "Full Offline-First Local Cache (Room DB / SQLite)",
            "Responsive Search & Filter with Debounce",
            "One-Click Export (Markdown, JSON, PDF)"
        )

        val futureFeatures = listOf(
            "Predictive AI Anomaly Detection & Insights",
            "Enterprise Single Sign-On (SAML / Okta)",
            "Public Developer Webhook & Plugin Marketplace",
            "Localized Multi-Language Translation (i18n)"
        )

        return ProductStrategy(
            positioning = "The fastest, most reliable ${type.category} solution designed for modern mobile-first workflows.",
            targetAudience = "Ambitious operators, creators, and teams needing seamless automation and zero friction.",
            valueProposition = "Combines frictionless user onboarding with enterprise-grade data security and instant response times.",
            coreUseCases = useCases,
            mvpScope = mvpScope,
            futureFeatures = futureFeatures,
            monetizationPossibilities = listOf("Usage-based API Billing", "Pro Feature Tier Upgrades", "Enterprise White-label Licensing")
        )
    }

    private fun buildFeatures(type: DomainType, raw: String): List<FeatureItem> {
        val list = mutableListOf<FeatureItem>()

        // Core P0 Features
        list.add(
            FeatureItem(
                id = "feat_auth",
                name = "Secure Authentication & Identity Lifecycle",
                priority = "P0",
                purpose = "Provides seamless user sign-in, token refresh, and profile management with biometric support.",
                targetUser = "All Registered Users",
                workflow = "User enters credentials -> System issues signed JWT & refresh token -> Stores securely in EncryptedSharedPreferences.",
                dependencies = listOf("OAuth Provider", "Crypto Keystore"),
                backendRequirements = "POST /api/v1/auth/login, POST /api/v1/auth/refresh, GET /api/v1/users/me",
                edgeCases = listOf("Expired refresh token during offline usage", "Concurrent logins from multiple devices")
            )
        )

        when (type) {
            DomainType.ECOMMERCE -> {
                list.add(
                    FeatureItem(
                        id = "feat_catalog",
                        name = "Dynamic Product Catalog & Reseller Margin Engine",
                        priority = "P0",
                        purpose = "Enables browsing wholesaler inventory and generating custom priced catalogs for WhatsApp distribution.",
                        targetUser = "Resellers & Wholesalers",
                        workflow = "Seller views wholesale items -> Sets custom markup percentage -> Generates branded product link.",
                        dependencies = listOf("Inventory Service", "Image CDN"),
                        backendRequirements = "GET /api/v1/products, POST /api/v1/catalogs/share-link",
                        edgeCases = listOf("Stock quantity hits zero while buyer is in checkout", "Large high-res product galleries")
                    )
                )
                list.add(
                    FeatureItem(
                        id = "feat_checkout",
                        name = "Escrow Checkout & Payment Gateway",
                        priority = "P0",
                        purpose = "Facilitates seamless UPI, Card, and COD payments with escrow hold until delivery confirmation.",
                        targetUser = "Buyers",
                        workflow = "User adds items to cart -> Selects payment mode -> Webhook confirms payment -> Order state shifts to 'PAID'.",
                        dependencies = listOf("Payment Provider (Stripe/Razorpay)", "Order Service"),
                        backendRequirements = "POST /api/v1/orders/create, POST /api/v1/payments/verify-webhook",
                        edgeCases = listOf("Network timeout during bank redirect", "Duplicate webhook payload delivery")
                    )
                )
                list.add(
                    FeatureItem(
                        id = "feat_order_tracking",
                        name = "Real-Time 3PL Order & Dispatch Tracker",
                        priority = "P1",
                        purpose = "Provides live step-by-step shipment timeline from warehouse dispatch to doorstep delivery.",
                        targetUser = "Buyers & Resellers",
                        workflow = "Courier API pushes tracking updates -> Client receives push notifications & renders interactive step timeline.",
                        dependencies = listOf("Logistics API", "FCM Push Notifications"),
                        backendRequirements = "GET /api/v1/orders/:id/tracking, POST /api/v1/webhooks/shipping",
                        edgeCases = listOf("Courier returns package to origin (RTO)", "Delayed GPS coordinate updates")
                    )
                )
                list.add(
                    FeatureItem(
                        id = "feat_ai_recs",
                        name = "AI Smart Product & Trending Trend Recommender",
                        priority = "P1",
                        purpose = "Analyzes regional sales trends and buyer preferences to suggest high-converting inventory.",
                        targetUser = "Resellers",
                        workflow = "User opens home feed -> AI recommendation engine computes collaborative embeddings -> Displays personalized grid.",
                        dependencies = listOf("Recommendation Engine", "Vector Store"),
                        backendRequirements = "GET /api/v1/recommendations/trending",
                        edgeCases = listOf("Cold-start for new users with zero order history")
                    )
                )
            }
            DomainType.AI_AGENT -> {
                list.add(
                    FeatureItem(
                        id = "feat_chat_stream",
                        name = "Streaming Multi-Modal AI Chat Interface",
                        priority = "P0",
                        purpose = "Delivers sub-100ms token-by-token streaming AI responses with markdown, code highlight, and image reasoning.",
                        targetUser = "End User",
                        workflow = "User sends prompt/image -> Client opens SSE/gRPC stream -> Tokens render smoothly with animated cursor.",
                        dependencies = listOf("Gemini 3.5 Flash API", "Markdown Parser"),
                        backendRequirements = "POST /api/v1/ai/chat/stream",
                        edgeCases = listOf("Abrupt network disconnection mid-stream", "Safety filter blocking partial generation")
                    )
                )
                list.add(
                    FeatureItem(
                        id = "feat_rag_search",
                        name = "Hybrid Vector RAG Document Knowledge Base",
                        priority = "P0",
                        purpose = "Enables querying custom PDFs, markdown files, and codebase repositories with cited snippets.",
                        targetUser = "Knowledge Workers",
                        workflow = "User uploads file -> Backend chunks & computes embeddings -> Cosine similarity retrieves top-K chunks.",
                        dependencies = listOf("Vector DB (pgvector)", "Embedding Model"),
                        backendRequirements = "POST /api/v1/knowledge/upload, POST /api/v1/knowledge/query",
                        edgeCases = listOf("Corrupted PDF text extraction", "Exceeding context window token limits")
                    )
                )
                list.add(
                    FeatureItem(
                        id = "feat_tool_calling",
                        name = "Sandboxed Tool & Function Calling Engine",
                        priority = "P1",
                        purpose = "Allows AI to execute deterministic functions like SQL queries, weather lookups, and API webhooks.",
                        targetUser = "Power Users",
                        workflow = "AI emits tool_call JSON -> Client/Backend validates permissions -> Executes function -> Returns result to model.",
                        dependencies = listOf("Function Registry", "Execution Sandbox"),
                        backendRequirements = "POST /api/v1/ai/tools/execute",
                        edgeCases = listOf("Malicious tool arguments injection", "Slow external API timeout")
                    )
                )
            }
            else -> {
                list.add(
                    FeatureItem(
                        id = "feat_core_crud",
                        name = "Core Data Management & Real-Time Sync",
                        priority = "P0",
                        purpose = "Empowers users to create, update, search, and delete primary domain records with instant sync.",
                        targetUser = "All Users",
                        workflow = "User edits item -> Saved locally in Room DB -> Synced to cloud via REST API background worker.",
                        dependencies = listOf("Room Database", "WorkManager"),
                        backendRequirements = "GET /api/v1/items, POST /api/v1/items, PUT /api/v1/items/:id",
                        edgeCases = listOf("Concurrent edits on two devices creating conflict", "Offline edits queued during flight mode")
                    )
                )
                list.add(
                    FeatureItem(
                        id = "feat_analytics_dashboard",
                        name = "Interactive Metric Visualization & Reports",
                        priority = "P1",
                        purpose = "Renders real-time visual charts, trend comparisons, and summary cards for actionable insights.",
                        targetUser = "Operators & Managers",
                        workflow = "User opens dashboard -> Aggregations computed on backend -> Rendered with animated Canvas charts.",
                        dependencies = listOf("Chart Engine", "Aggregation Pipeline"),
                        backendRequirements = "GET /api/v1/analytics/summary",
                        edgeCases = listOf("Empty data states for fresh accounts", "Extreme value outliers skewing chart axis")
                    )
                )
                list.add(
                    FeatureItem(
                        id = "feat_notifications",
                        name = "Push Notifications & Event Alerts",
                        priority = "P1",
                        purpose = "Delivers timely push alerts and in-app bell notifications for key system events.",
                        targetUser = "All Users",
                        workflow = "Backend triggers event -> Firebase Cloud Messaging dispatches notification -> App displays notification pill.",
                        dependencies = listOf("FCM Service", "Notification Manager"),
                        backendRequirements = "POST /api/v1/users/device-token, GET /api/v1/notifications",
                        edgeCases = listOf("User revokes notification permissions in Android OS settings")
                    )
                )
            }
        }

        // P2 Advanced Features
        list.add(
            FeatureItem(
                id = "feat_audit_export",
                name = "One-Click Compliance Export & Audit History",
                priority = "P2",
                purpose = "Allows users to download complete project records in JSON, CSV, and Markdown format with audit logs.",
                targetUser = "Power Users & Admins",
                workflow = "User taps export -> Local serializer packages data -> Android Share Intent opens with save/send options.",
                dependencies = listOf("File Storage Provider", "Document Formatter"),
                backendRequirements = "GET /api/v1/export/archive",
                edgeCases = listOf("Exporting large datasets without memory leaks")
            )
        )

        return list
    }

    private fun buildUxArchitecture(type: DomainType, features: List<FeatureItem>): UxArchitecture {
        val screens = when (type) {
            DomainType.ECOMMERCE -> listOf(
                ScreenSpec("scr_home", "Discover & Home Feed", "/home", "Personalized trending collections, search bar, banner promos, categories.", listOf("CategoryPillRow", "TrendingProductGrid", "BannerCarousel", "SearchBar"), listOf("Search", "FilterByCategory", "SelectProduct")),
                ScreenSpec("scr_product_detail", "Product Specifications & Margin Setup", "/product/:id", "High-res media carousel, size/color variant matrix, stock badge, margin calculator.", listOf("ImageCarousel", "VariantSelector", "MarginCalculatorCard", "ShareWhatsAppButton"), listOf("AddToCart", "ShareOnWhatsApp", "CalculateProfit")),
                ScreenSpec("scr_cart", "Cart & Order Summary", "/cart", "Itemized list with quantity steppers, applied promo vouchers, price breakdown.", listOf("CartItemList", "CouponInput", "PriceBreakdownCard", "CheckoutButton"), listOf("UpdateQty", "ApplyCoupon", "ProceedToCheckout")),
                ScreenSpec("scr_checkout", "Address & Escrow Payment", "/checkout", "Saved shipping address selector, delivery speed options, UPI/Card/COD selector.", listOf("AddressSelector", "PaymentMethodRadioGroup", "EscrowGuaranteeBanner", "PayButton"), listOf("SelectAddress", "AuthorizePayment")),
                ScreenSpec("scr_orders", "My Orders & Live Courier Tracker", "/orders", "Active and historic orders with animated tracking stepper and invoice downloader.", listOf("OrderCardList", "LiveTrackingStepper", "DownloadInvoiceButton"), listOf("CancelOrder", "TrackShipment", "RequestRefund")),
                ScreenSpec("scr_reseller_hub", "Reseller Earnings Hub", "/reseller-hub", "Commission balance, bank payout status, total orders referred, WhatsApp assets.", listOf("EarningsMetricCard", "PayoutHistoryList", "MarketingAssetKit"), listOf("RequestPayout", "DownloadCatalogZip"))
            )
            DomainType.AI_AGENT -> listOf(
                ScreenSpec("scr_chat_main", "AI Chat Workspace", "/chat", "Streaming message feed, rich code blocks, voice input mic, attachment tray.", listOf("MessageBubbleList", "StreamingCursor", "CodeBlockRenderer", "PromptInputBar"), listOf("SendMessage", "AttachDoc", "CopyCode", "Regenerate")),
                ScreenSpec("scr_knowledge_base", "Document & Knowledge Hub", "/knowledge", "Uploaded PDFs, vector index sync status, chunk inspection preview.", listOf("DocumentCardGrid", "UploadDropzone", "IndexProgressBar"), listOf("UploadFile", "DeleteDoc", "Reindex")),
                ScreenSpec("scr_tool_catalog", "Connected Tools & Integrations", "/tools", "List of enabled external APIs, SQL connections, and permission toggles.", listOf("ToolSwitchList", "ApiConfigDialog", "TestExecutionCard"), listOf("ToggleTool", "ConfigureKey")),
                ScreenSpec("scr_prompt_library", "Prompt Template Gallery", "/prompts", "Curated system prompts, one-shot blueprints, and custom user macros.", listOf("PromptCardGrid", "CategoryFilterTabs", "ForkPromptButton"), listOf("UsePrompt", "CreateCustomPrompt"))
            )
            else -> listOf(
                ScreenSpec("scr_dashboard", "Main Overview Dashboard", "/dashboard", "High-level summary metrics, quick actions, recent activity feed.", listOf("MetricCardRow", "RecentActivityList", "QuickActionFAB"), listOf("CreateNew", "FilterPeriod", "ViewAll")),
                ScreenSpec("scr_item_list", "Data Explorer & Records", "/items", "Searchable, filterable list with multi-select and sort options.", listOf("SearchBar", "FilterChipGroup", "LazyItemColumn", "SortDropdown"), listOf("Search", "DeleteSelected", "EditItem")),
                ScreenSpec("scr_item_detail", "Record Detail & Editor", "/items/:id", "Comprehensive form with validation rules, attachment upload, and change history.", listOf("ValidatedInputField", "HistoryTimeline", "ActionButtons"), listOf("SaveRecord", "RevertVersion")),
                ScreenSpec("scr_settings", "Settings & Account", "/settings", "Profile details, theme switcher (Dark/Light), security keys, data export.", listOf("ProfileCard", "ThemeToggle", "ExportButton", "SignOutButton"), listOf("ToggleTheme", "ExportData", "SignOut"))
            )
        }

        return UxArchitecture(
            screens = screens,
            navigationStructure = "Bottom Navigation Bar on mobile screens + Collapsible Navigation Rail on tablets/foldables. Deep linking enabled for all routes.",
            onboardingFlow = "3-step interactive carousel highlighting core capabilities -> Optional biometrics opt-in -> Initial workspace setup.",
            authFlow = "Google One-Tap / Email Passwordless Magic Link -> JWT issued -> Auto-refresh in background.",
            userJourneys = listOf(
                UserJourney("First-Time User Onboarding", listOf("Launch app", "View feature highlights", "Sign in with Google", "Land on curated home feed")),
                UserJourney("Core Transaction Loop", listOf("Browse items", "Configure item details", "Initiate checkout / generation", "Receive instant confirmation and live status update"))
            ),
            emptyStates = listOf(
                StateDescription("No Active Projects / Items", "Displays a friendly astronaut graphic with a primary 'Create Your First Blueprint' CTA button.", "Show Empty Illustration + Action Button"),
                StateDescription("Search Zero Results", "Informs the user that no records matched the filter query and offers a 1-tap 'Clear Filters' button.", "Show Clear Filters Pill")
            ),
            loadingStates = listOf(
                StateDescription("Initial Data Fetch", "Subtle shimmering skeleton cards matching the exact layout of destination components.", "Shimmer Animation"),
                StateDescription("AI Blueprint Generation", "Multi-stage progress indicator highlighting: Analyzing Idea -> Evaluating Tech Stack -> Designing Database -> Generating Master Prompt.", "Step Progress Bar")
            ),
            errorStates = listOf(
                StateDescription("Network Offline", "Top banner alert with offline indicator and cached data view.", "Show Offline Banner + Retry Button"),
                StateDescription("API Rate Limit Exceeded", "Friendly cooldown countdown with alternative action suggestions.", "Show Cooldown Timer Dialog")
            ),
            successStates = listOf(
                StateDescription("Blueprint Generated", "Haptic confirmation pulse + subtle confetti burst + Master Prompt ready for 1-click copy.", "Haptic Feedback + Copied Toast")
            ),
            requiredPermissions = listOf("INTERNET", "ACCESS_NETWORK_STATE", "POST_NOTIFICATIONS"),
            responsiveBehavior = "Fluid BoxWithConstraints layout supporting Compact (Phone), Medium (Foldable unfolded), and Expanded (Tablet / Desktop) with adaptive two-pane list-detail mode."
        )
    }

    private fun buildTechStack(type: DomainType, raw: String): TechStack {
        return when (type) {
            DomainType.ECOMMERCE -> TechStack(
                frontend = TechChoice("Jetpack Compose (Kotlin Android) & Next.js 15 PWA", "Frontend", "Declarative UI with native 120Hz performance on mobile and instant SSR for web store links."),
                backend = TechChoice("Node.js (TypeScript) + Fastify", "Backend", "High-throughput asynchronous event loop with low memory footprint for rapid order processing."),
                database = TechChoice("PostgreSQL 16 + Redis 7", "Database", "ACID compliant relational schema for order transactions, coupled with in-memory Redis for sub-millisecond cart caching."),
                authentication = TechChoice("Firebase Auth / Supabase Auth (JWT)", "Authentication", "Secure multi-factor auth with phone OTP and Google Credential Manager integration."),
                storage = TechChoice("Cloudflare R2 / AWS S3 + Cloudflare CDN", "Storage", "Zero-egress object storage for optimized WebP product catalogs and invoice PDFs."),
                apis = TechChoice("RESTful JSON API + Webhooks", "API Protocol", "Predictable REST endpoints with HMAC-signed webhooks for payment gateways and 3PL shipping updates."),
                aiModels = TechChoice("Gemini 3.5 Flash", "AI Layer", "Cost-efficient multimodal model for product image tagging, catalog categorization, and smart copy generation."),
                payments = TechChoice("Razorpay / Stripe SDK", "Payments", "Native UPI Intent flow and credit card tokenization with automated escrow release."),
                notifications = TechChoice("Firebase Cloud Messaging (FCM) + WhatsApp Business API", "Notifications", "Multi-channel push notifications for real-time delivery milestones and order receipts."),
                analytics = TechChoice("PostHog / Mixpanel", "Analytics", "Privacy-compliant event tracking for funnel drop-offs, reseller margins, and conversion tracking."),
                deployment = TechChoice("Google Cloud Run & Docker", "Deployment", "Auto-scaling serverless containers with zero maintenance overhead and multi-region failover.")
            )
            DomainType.AI_AGENT -> TechStack(
                frontend = TechChoice("Jetpack Compose (Kotlin) + React Desktop", "Frontend", "Silky smooth token streaming animations, custom canvas node diagrams, and syntax-highlighted code blocks."),
                backend = TechChoice("Python (FastAPI) + Go Gateway", "Backend", "Native asynchronous ASGI server with direct access to machine learning libraries, tokenizers, and LangChain/LlamaIndex."),
                database = TechChoice("PostgreSQL + pgvector + Redis", "Database", "Unified relational data store with native HNSW vector index extension for similarity search and conversational context caching."),
                authentication = TechChoice("Clerk / Firebase Auth", "Authentication", "Zero-friction passwordless authentication with session revocation and team organization switching."),
                storage = TechChoice("Google Cloud Storage", "Storage", "High-durability storage for user-uploaded PDFs, audio transcripts, and generated artifacts."),
                apis = TechChoice("Server-Sent Events (SSE) & gRPC", "API Protocol", "Low-latency streaming transport for real-time token delivery and bidirectional tool execution."),
                aiModels = TechChoice("Gemini 3.5 Flash & Gemini 3.1 Pro", "AI Layer", "Gemini 3.5 Flash for rapid low-latency chat streaming; Gemini 3.1 Pro for deep architectural reasoning and coding."),
                payments = TechChoice("Stripe Billing (Metered)", "Payments", "Automated usage-based token metering with credit balance alerts."),
                notifications = TechChoice("FCM & In-App WebSockets", "Notifications", "Background task completion notifications when long-running RAG document indexing finishes."),
                analytics = TechChoice("Langfuse / Helicone", "Analytics", "Dedicated LLM telemetry for prompt cost monitoring, latency tracking, and hallucination evaluations."),
                deployment = TechChoice("Fly.io / Google Cloud Run", "Deployment", "Edge compute close to users with GPU/CPU scaling.")
            )
            else -> TechStack(
                frontend = TechChoice("Jetpack Compose (Kotlin)", "Frontend", "Modern Material Design 3 declarative UI with reactive StateFlow state management."),
                backend = TechChoice("Kotlin / Ktor or Go", "Backend", "Lightweight, memory-efficient microservices with strict type safety and coroutines concurrency."),
                database = TechChoice("PostgreSQL + Room Database (Offline Cache)", "Database", "Rock-solid relational PostgreSQL backend combined with local Android Room SQLite database for instant offline access."),
                authentication = TechChoice("JWT Bearer + Android Credential Manager", "Authentication", "Hardware-backed keystore token encryption with biometric authentication."),
                storage = TechChoice("AWS S3 / Supabase Storage", "Storage", "Scalable cloud object bucket for media assets and document backups."),
                apis = TechChoice("RESTful JSON API with OpenAPI v3", "API Protocol", "Strictly typed REST endpoints with clear contract specifications."),
                aiModels = TechChoice("Gemini 3.5 Flash", "AI Layer", "Fast intelligence layer for automated classification, summarization, and query reasoning."),
                payments = TechChoice("Stripe Checkout", "Payments", "Universal subscription billing and invoice generation."),
                notifications = TechChoice("Firebase Cloud Messaging", "Notifications", "Reliable cross-platform push notifications."),
                analytics = TechChoice("Firebase Analytics", "Analytics", "App engagement, crash reporting, and user retention metrics."),
                deployment = TechChoice("Docker + Cloud Run / AWS ECS", "Deployment", "Containerized microservice deployment with CI/CD GitHub Actions pipelines.")
            )
        }
    }

    private fun buildSystemArchitecture(type: DomainType, stack: TechStack): SystemArchitecture {
        val layers = listOf(
            ArchitectureLayer(
                id = "layer_client",
                name = "Client Tier (Mobile & Web)",
                description = "Jetpack Compose Android Native App + Responsive Web UI. Handles local caching via Room DB, biometric auth, and UI state rendering.",
                components = listOf("Compose UI Screens", "ViewModel Layer", "Room Local DB", "Network Interceptor", "WorkManager"),
                connectsTo = listOf("layer_gateway"),
                layerType = "CLIENT"
            ),
            ArchitectureLayer(
                id = "layer_gateway",
                name = "API Gateway & Security Proxy",
                description = "Reverse proxy handling SSL termination, rate limiting (Redis token bucket), JWT verification, and request validation.",
                components = listOf("Envoy / NGINX Gateway", "JWT Auth Middleware", "Rate Limiter (Redis)", "CORS & WAF"),
                connectsTo = listOf("layer_backend"),
                layerType = "GATEWAY"
            ),
            ArchitectureLayer(
                id = "layer_backend",
                name = "Core Application Backend",
                description = "Stateless microservice controllers orchestrating business logic, event publishing, and data transformations.",
                components = listOf("Auth Controller", "Domain Business Engine", "Order & Transaction Manager", "Background Task Queues"),
                connectsTo = listOf("layer_database", "layer_services", "layer_ai"),
                layerType = "BACKEND"
            ),
            ArchitectureLayer(
                id = "layer_services",
                name = "Internal Microservices & Workers",
                description = "Asynchronous workers handling payment webhooks, push notifications, email dispatch, and file transcoding.",
                components = listOf("Payment Webhook Worker", "FCM Notification Worker", "PDF Invoice Generator", "Search Indexer"),
                connectsTo = listOf("layer_external"),
                layerType = "SERVICE"
            ),
            ArchitectureLayer(
                id = "layer_database",
                name = "Data Storage & Cache Tier",
                description = "Primary ACID relational PostgreSQL database with read-replicas, plus in-memory Redis for session and caching.",
                components = listOf("PostgreSQL Primary DB", "Read Replica Pool", "Redis Distributed Cache", "Object Storage (S3/R2)"),
                connectsTo = emptyList(),
                layerType = "DATABASE"
            ),
            ArchitectureLayer(
                id = "layer_ai",
                name = "AI Orchestration & Knowledge Tier",
                description = "Gemini API integration bridge with prompt templating, token guardrails, vector embeddings, and tool execution.",
                components = listOf("Gemini 3.5 Flash Model", "Vector Embeddings Engine", "Tool Execution Sandbox", "LLM Cache"),
                connectsTo = listOf("layer_database"),
                layerType = "AI"
            ),
            ArchitectureLayer(
                id = "layer_external",
                name = "External Third-Party APIs",
                description = "Integrated third-party providers for payments, logistics, transactional SMS, and identity checks.",
                components = listOf("Payment Gateway (Stripe/Razorpay)", "Logistics Courier API", "Firebase Cloud Messaging", "SendGrid Email"),
                connectsTo = emptyList(),
                layerType = "EXTERNAL"
            )
        )

        val flow = listOf(
            "1. User initiates an action on the Jetpack Compose Android Client (e.g. checkout or AI query).",
            "2. Client checks local Room DB cache; if network request needed, passes signed Bearer JWT to API Gateway.",
            "3. API Gateway validates JWT signature and checks Redis rate limits (e.g. max 60 req/min).",
            "4. Core Backend processes business logic within an isolated ACID database transaction.",
            "5. If AI reasoning is requested, Backend builds prompt context, queries Gemini API, and streams tokens.",
            "6. Asynchronous workers handle secondary side-effects (FCM push alert, invoice generation, metrics log).",
            "7. Clean JSON / SSE payload is returned to Client; Room DB is updated reactively, triggering UI recomposition."
        )

        return SystemArchitecture(
            overview = "High-availability, modular cloud architecture combining mobile offline resilience with serverless auto-scaling microservices.",
            layers = layers,
            dataFlowSteps = flow
        )
    }

    private fun buildDatabaseSchema(type: DomainType, features: List<FeatureItem>): DatabaseSchema {
        val entities = mutableListOf<DbEntity>()

        // 1. Users Table
        entities.add(
            DbEntity(
                tableName = "users",
                description = "Stores user accounts, credentials hash, profile info, and role permissions.",
                fields = listOf(
                    DbField("id", "UUID", isPrimaryKey = true, description = "Unique user identifier"),
                    DbField("email", "VARCHAR(255)", isNullable = false, description = "User email (unique index)"),
                    DbField("password_hash", "VARCHAR(255)", isNullable = true, description = "Argon2id password hash or null for OAuth"),
                    DbField("full_name", "VARCHAR(150)", isNullable = false, description = "Display name"),
                    DbField("role", "VARCHAR(50)", isNullable = false, description = "Role (ADMIN, USER, SELLER, MODERATOR)"),
                    DbField("avatar_url", "TEXT", isNullable = true, description = "Profile photo CDN URL"),
                    DbField("created_at", "TIMESTAMPTZ", isNullable = false, description = "Record creation timestamp"),
                    DbField("updated_at", "TIMESTAMPTZ", isNullable = false, description = "Last modification timestamp")
                ),
                indexes = listOf("CREATE UNIQUE INDEX idx_users_email ON users(email);", "CREATE INDEX idx_users_role ON users(role);"),
                validationRules = listOf("Email must match valid RFC 5322 regex format", "Role must be one of allowed ENUM values")
            )
        )

        when (type) {
            DomainType.ECOMMERCE -> {
                entities.add(
                    DbEntity(
                        tableName = "products",
                        description = "Wholesale and seller catalog products with pricing, category, and inventory count.",
                        fields = listOf(
                            DbField("id", "UUID", isPrimaryKey = true, description = "Product primary key"),
                            DbField("seller_id", "UUID", isForeignKey = true, isNullable = false, description = "References users(id)"),
                            DbField("title", "VARCHAR(255)", isNullable = false, description = "Product title"),
                            DbField("description", "TEXT", isNullable = true, description = "Full markdown product description"),
                            DbField("wholesale_price_cents", "BIGINT", isNullable = false, description = "Base wholesale cost in smallest currency unit"),
                            DbField("suggested_retail_cents", "BIGINT", isNullable = false, description = "MSRP price recommendation"),
                            DbField("stock_quantity", "INTEGER", isNullable = false, description = "Available physical inventory"),
                            DbField("category", "VARCHAR(100)", isNullable = false, description = "Product category taxonomy"),
                            DbField("images", "JSONB", isNullable = false, description = "Array of image URLs and thumbnail metadata"),
                            DbField("created_at", "TIMESTAMPTZ", isNullable = false, description = "Timestamp")
                        ),
                        indexes = listOf("CREATE INDEX idx_products_seller ON products(seller_id);", "CREATE INDEX idx_products_category ON products(category);", "CREATE INDEX idx_products_price ON products(wholesale_price_cents);"),
                        validationRules = listOf("wholesale_price_cents must be > 0", "stock_quantity cannot be negative")
                    )
                )
                entities.add(
                    DbEntity(
                        tableName = "orders",
                        description = "Customer orders containing payment status, shipping address, and tracking metadata.",
                        fields = listOf(
                            DbField("id", "UUID", isPrimaryKey = true, description = "Order primary key"),
                            DbField("buyer_id", "UUID", isForeignKey = true, isNullable = false, description = "References users(id)"),
                            DbField("reseller_id", "UUID", isForeignKey = true, isNullable = true, description = "References users(id) if sold via reseller"),
                            DbField("status", "VARCHAR(50)", isNullable = false, description = "PENDING, PAID, SHIPPED, DELIVERED, CANCELED, REFUNDED"),
                            DbField("total_amount_cents", "BIGINT", isNullable = false, description = "Final billed customer price"),
                            DbField("reseller_margin_cents", "BIGINT", isNullable = false, description = "Calculated reseller profit cut"),
                            DbField("shipping_address", "JSONB", isNullable = false, description = "Recipient address and phone"),
                            DbField("payment_intent_id", "VARCHAR(255)", isNullable = true, description = "Payment gateway transaction ID"),
                            DbField("courier_tracking_code", "VARCHAR(100)", isNullable = true, description = "3PL airway bill tracking number"),
                            DbField("created_at", "TIMESTAMPTZ", isNullable = false, description = "Timestamp")
                        ),
                        indexes = listOf("CREATE INDEX idx_orders_buyer ON orders(buyer_id);", "CREATE INDEX idx_orders_status ON orders(status);", "CREATE INDEX idx_orders_created ON orders(created_at DESC);"),
                        validationRules = listOf("total_amount_cents must equal sum of items + tax + shipping", "Status transitions must follow valid state machine")
                    )
                )
                entities.add(
                    DbEntity(
                        tableName = "order_items",
                        description = "Line items associated with each order.",
                        fields = listOf(
                            DbField("id", "UUID", isPrimaryKey = true, description = "Item primary key"),
                            DbField("order_id", "UUID", isForeignKey = true, isNullable = false, description = "References orders(id) ON DELETE CASCADE"),
                            DbField("product_id", "UUID", isForeignKey = true, isNullable = false, description = "References products(id)"),
                            DbField("quantity", "INTEGER", isNullable = false, description = "Quantity ordered"),
                            DbField("unit_price_cents", "BIGINT", isNullable = false, description = "Locked-in unit price at time of purchase"),
                            DbField("selected_variant", "JSONB", isNullable = true, description = "Selected size, color, attributes")
                        ),
                        indexes = listOf("CREATE INDEX idx_order_items_order ON order_items(order_id);"),
                        validationRules = listOf("quantity must be >= 1", "unit_price_cents must be >= 0")
                    )
                )
            }
            DomainType.AI_AGENT -> {
                entities.add(
                    DbEntity(
                        tableName = "chat_sessions",
                        description = "Conversational threads with title, model parameters, and token usage statistics.",
                        fields = listOf(
                            DbField("id", "UUID", isPrimaryKey = true, description = "Session primary key"),
                            DbField("user_id", "UUID", isForeignKey = true, isNullable = false, description = "References users(id)"),
                            DbField("title", "VARCHAR(255)", isNullable = false, description = "Conversation title"),
                            DbField("model_name", "VARCHAR(100)", isNullable = false, description = "e.g. gemini-3.5-flash"),
                            DbField("system_prompt", "TEXT", isNullable = true, description = "Custom system instructions"),
                            DbField("total_tokens_used", "BIGINT", isNullable = false, description = "Aggregated token counter"),
                            DbField("created_at", "TIMESTAMPTZ", isNullable = false, description = "Creation timestamp")
                        ),
                        indexes = listOf("CREATE INDEX idx_sessions_user ON chat_sessions(user_id);"),
                        validationRules = listOf("total_tokens_used must be >= 0")
                    )
                )
                entities.add(
                    DbEntity(
                        tableName = "messages",
                        description = "Individual user and assistant messages with attachments and tool call payloads.",
                        fields = listOf(
                            DbField("id", "UUID", isPrimaryKey = true, description = "Message primary key"),
                            DbField("session_id", "UUID", isForeignKey = true, isNullable = false, description = "References chat_sessions(id) ON DELETE CASCADE"),
                            DbField("role", "VARCHAR(50)", isNullable = false, description = "USER, ASSISTANT, SYSTEM, TOOL"),
                            DbField("content", "TEXT", isNullable = false, description = "Markdown text content"),
                            DbField("tool_calls", "JSONB", isNullable = true, description = "Structured tool function invocations and responses"),
                            DbField("created_at", "TIMESTAMPTZ", isNullable = false, description = "Timestamp")
                        ),
                        indexes = listOf("CREATE INDEX idx_messages_session ON messages(session_id, created_at ASC);"),
                        validationRules = listOf("Role must be one of USER, ASSISTANT, SYSTEM, TOOL")
                    )
                )
            }
            else -> {
                entities.add(
                    DbEntity(
                        tableName = "projects",
                        description = "Core project records managed by users.",
                        fields = listOf(
                            DbField("id", "UUID", isPrimaryKey = true, description = "Project primary key"),
                            DbField("owner_id", "UUID", isForeignKey = true, isNullable = false, description = "References users(id)"),
                            DbField("name", "VARCHAR(255)", isNullable = false, description = "Project title"),
                            DbField("description", "TEXT", isNullable = true, description = "Project summary"),
                            DbField("status", "VARCHAR(50)", isNullable = false, description = "DRAFT, ACTIVE, ARCHIVED"),
                            DbField("metadata", "JSONB", isNullable = false, description = "Dynamic JSON configuration"),
                            DbField("created_at", "TIMESTAMPTZ", isNullable = false, description = "Timestamp"),
                            DbField("updated_at", "TIMESTAMPTZ", isNullable = false, description = "Timestamp")
                        ),
                        indexes = listOf("CREATE INDEX idx_projects_owner ON projects(owner_id);"),
                        validationRules = listOf("Name must not be blank", "Status must be valid ENUM")
                    )
                )
            }
        }

        val relationships = listOf(
            DbRelationship("users", "projects/orders", "1:N", "A single user owns multiple projects or orders."),
            DbRelationship("orders", "order_items", "1:N", "An order is composed of 1 or more line items.")
        )

        val recommendations = listOf(
            "Use UUID v7 for time-ordered primary keys to optimize B-Tree index clustering.",
            "Implement soft deletes with `deleted_at TIMESTAMPTZ` column for high-value entities.",
            "Set connection pool limit to 20-50 per server instance using HikariCP or PgBouncer.",
            "Run automated database migration scripts via Flyway or Liquibase in CI/CD pipelines."
        )

        return DatabaseSchema(
            databaseType = "PostgreSQL 16 (Relational ACID) + Room (Local Cache)",
            entities = entities,
            relationships = relationships,
            productionRecommendations = recommendations
        )
    }

    private fun buildApiDesign(type: DomainType, features: List<FeatureItem>, dbSchema: DatabaseSchema): List<ApiEndpoint> {
        val list = mutableListOf<ApiEndpoint>()

        // Auth Group
        list.add(
            ApiEndpoint(
                id = "api_auth_login",
                path = "/api/v1/auth/login",
                method = "POST",
                group = "Authentication",
                purpose = "Authenticates user credentials and issues signed access JWT + refresh token.",
                authentication = "None (Public)",
                requestBody = "{\n  \"email\": \"user@example.com\",\n  \"password\": \"SecurePass123!\"\n}",
                responseBody = "{\n  \"accessToken\": \"eyJhbGci...\",\n  \"refreshToken\": \"d8f92a...\",\n  \"expiresIn\": 3600,\n  \"user\": { \"id\": \"usr_1\", \"email\": \"user@example.com\", \"role\": \"USER\" }\n}",
                statusCodes = listOf("200 OK", "401 Unauthorized", "429 Rate Limited"),
                errorHandling = "Returns 401 with INVALID_CREDENTIALS error code; enforces rate limiting after 5 failed attempts."
            )
        )
        list.add(
            ApiEndpoint(
                id = "api_auth_refresh",
                path = "/api/v1/auth/refresh",
                method = "POST",
                group = "Authentication",
                purpose = "Rotates refresh token and issues fresh 1-hour access token.",
                authentication = "Refresh Token in HTTP-Only Cookie",
                requestBody = "{\n  \"refreshToken\": \"d8f92a...\"\n}",
                responseBody = "{\n  \"accessToken\": \"eyJhbGci...\",\n  \"expiresIn\": 3600\n}",
                statusCodes = listOf("200 OK", "401 Expired Token"),
                errorHandling = "If token is reused or blacklisted, revokes all user sessions immediately."
            )
        )

        when (type) {
            DomainType.ECOMMERCE -> {
                list.add(
                    ApiEndpoint(
                        id = "api_products_list",
                        path = "/api/v1/products",
                        method = "GET",
                        group = "Products",
                        purpose = "Fetches paginated list of catalog products with optional category and search filters.",
                        authentication = "Bearer JWT (Optional)",
                        requestBody = "None (Query params: ?page=1&limit=20&category=fashion&q=saree)",
                        responseBody = "{\n  \"items\": [\n    {\n      \"id\": \"prod_123\",\n      \"title\": \"Embroidered Silk Saree\",\n      \"wholesalePriceCents\": 85000,\n      \"suggestedRetailCents\": 120000,\n      \"stock\": 45,\n      \"images\": [\"https://cdn.example.com/img1.webp\"]\n    }\n  ],\n  \"pagination\": { \"page\": 1, \"totalPages\": 12, \"totalItems\": 235 }\n}",
                        statusCodes = listOf("200 OK", "400 Invalid Query"),
                        errorHandling = "Returns empty list if no matches found; validates page and limit parameters."
                    )
                )
                list.add(
                    ApiEndpoint(
                        id = "api_orders_create",
                        path = "/api/v1/orders",
                        method = "POST",
                        group = "Orders",
                        purpose = "Places a new customer order and creates a payment escrow intent.",
                        authentication = "Bearer JWT",
                        requestBody = "{\n  \"items\": [{ \"productId\": \"prod_123\", \"quantity\": 1, \"variant\": { \"size\": \"Free Size\" } }],\n  \"shippingAddress\": {\n    \"name\": \"Priya Sharma\",\n    \"phone\": \"+919876543210\",\n    \"line1\": \"42 MG Road\",\n    \"city\": \"Bangalore\",\n    \"pincode\": \"560001\"\n  },\n  \"resellerMarginCents\": 25000\n}",
                        responseBody = "{\n  \"orderId\": \"ord_987\",\n  \"status\": \"PENDING_PAYMENT\",\n  \"totalAmountCents\": 145000,\n  \"paymentClientSecret\": \"pi_3MtwB...\"\n}",
                        statusCodes = listOf("201 Created", "400 Out of Stock", "401 Unauthorized"),
                        errorHandling = "Atomically verifies inventory stock in database before creating order record."
                    )
                )
                list.add(
                    ApiEndpoint(
                        id = "api_orders_tracking",
                        path = "/api/v1/orders/{id}/tracking",
                        method = "GET",
                        group = "Orders",
                        purpose = "Returns live shipment location and milestone checkpoint events.",
                        authentication = "Bearer JWT",
                        requestBody = "None",
                        responseBody = "{\n  \"orderId\": \"ord_987\",\n  \"carrier\": \"Delhivery Express\",\n  \"trackingNumber\": \"DLH8923019\",\n  \"currentStatus\": \"OUT_FOR_DELIVERY\",\n  \"events\": [\n    { \"status\": \"PICKED_UP\", \"timestamp\": \"2026-08-28T09:00:00Z\", \"location\": \"Surat Hub\" },\n    { \"status\": \"OUT_FOR_DELIVERY\", \"timestamp\": \"2026-08-28T14:30:00Z\", \"location\": \"Bangalore East\" }\n  ]\n}",
                        statusCodes = listOf("200 OK", "404 Not Found"),
                        errorHandling = "Masks sensitive carrier notes; checks if user is order owner or authorized seller."
                    )
                )
            }
            DomainType.AI_AGENT -> {
                list.add(
                    ApiEndpoint(
                        id = "api_ai_chat_stream",
                        path = "/api/v1/ai/chat/stream",
                        method = "POST",
                        group = "AI Engine",
                        purpose = "Streams token-by-token response chunks using Server-Sent Events (SSE).",
                        authentication = "Bearer JWT",
                        requestBody = "{\n  \"sessionId\": \"ses_123\",\n  \"prompt\": \"Summarize the refund policy from the attached handbook.\",\n  \"includeCitations\": true\n}",
                        responseBody = "data: {\"chunk\": \"The \", \"finished\": false}\ndata: {\"chunk\": \"policy allows \", \"finished\": false}\ndata: {\"finished\": true, \"usage\": {\"tokens\": 45}}",
                        statusCodes = listOf("200 Streaming", "400 Invalid Prompt", "429 Token Quota"),
                        errorHandling = "Gracefully yields error event over stream if Gemini upstream fails, closing connection cleanly."
                    )
                )
                list.add(
                    ApiEndpoint(
                        id = "api_knowledge_upload",
                        path = "/api/v1/knowledge/upload",
                        method = "POST",
                        group = "Knowledge Base",
                        purpose = "Uploads document and triggers background vector embedding ingestion pipeline.",
                        authentication = "Bearer JWT",
                        requestBody = "Multipart Form Data (file: document.pdf, tags: [\"policy\"])",
                        responseBody = "{\n  \"docId\": \"doc_55\",\n  \"fileName\": \"document.pdf\",\n  \"chunks\": 24,\n  \"status\": \"INDEXED\"\n}",
                        statusCodes = listOf("202 Accepted", "413 File Too Large", "415 Unsupported Type"),
                        errorHandling = "Scans file for malware; rejects non-PDF/TXT/MD files; verifies size under 25MB."
                    )
                )
            }
            else -> {
                list.add(
                    ApiEndpoint(
                        id = "api_items_crud",
                        path = "/api/v1/items",
                        method = "GET",
                        group = "Core Resources",
                        purpose = "Fetches user records with search, filter, and pagination support.",
                        authentication = "Bearer JWT",
                        requestBody = "None",
                        responseBody = "{\n  \"data\": [ { \"id\": \"itm_1\", \"name\": \"Sample Item\", \"updatedAt\": \"2026-08-28T08:00:00Z\" } ],\n  \"total\": 1\n}",
                        statusCodes = listOf("200 OK", "401 Unauthorized"),
                        errorHandling = "Standardized JSON error envelope: { error: { code: 'UNAUTHORIZED', message: '...' } }"
                    )
                )
            }
        }

        return list
    }

    private fun buildSecurityPlan(type: DomainType, stack: TechStack): SecurityPlan {
        return SecurityPlan(
            authenticationStrategy = "Short-lived signed JWT access tokens (15-60 min expiry) paired with rotating refresh tokens stored securely in Android EncryptedSharedPreferences (backed by Android Keystore hardware security module).",
            authorizationRules = listOf(
                "Role-Based Access Control (RBAC): ADMIN, SELLER/CREATOR, REGULAR_USER.",
                "Resource Ownership Verification: Users can only mutate records where `owner_id == authenticated_user_id`.",
                "Admin routes protected by multi-factor authentication (MFA) and IP allowlisting."
            ),
            inputValidation = listOf(
                "Strict request body schema validation using Zod (Node) / Pydantic (Python) before hitting business controllers.",
                "HTML sanitization to prevent Cross-Site Scripting (XSS) in user-submitted markdown and descriptions.",
                "SQL parameterization via ORM / Prepared Statements to guarantee zero SQL Injection vulnerabilities."
            ),
            apiKeyProtection = "CRITICAL MANDATE: Never expose secret API keys (Gemini, Stripe Secret, DB credentials) in client APK or web source code. All third-party secrets stored securely in server environment variables or injected via AI Studio Secrets Panel.",
            secretsManagement = "Production secrets managed via Google Cloud Secret Manager / AWS Secrets Manager with automated key rotation every 90 days.",
            rateLimiting = "Distributed Redis sliding window rate limiter: Public endpoints capped at 30 req/min; Authenticated endpoints capped at 120 req/min; Login attempts capped at 5 attempts per 15 min per IP.",
            abusePrevention = listOf(
                "reCAPTCHA v3 / Cloudflare Turnstile bot detection on authentication and checkout endpoints.",
                "Automated IP ban trigger after repeated 401/403 anomalies.",
                "Payload size capping (Max 100KB for JSON, Max 25MB for file uploads)."
            ),
            fileUploadSecurity = "Uploads stream directly to Cloud Storage via pre-signed URLs with restricted MIME types (image/jpeg, image/png, application/pdf). ClamAV virus scanning triggered on upload completion.",
            databaseSecurity = listOf(
                "Data at rest encrypted with AES-256.",
                "Data in transit enforced via TLS 1.3.",
                "Database accessible ONLY via private VPC subnets with zero public internet ingress."
            ),
            privacyAndEncryption = "GDPR & CCPA compliant: Automated user data export and 1-click 'Right to be Forgotten' hard deletion pipeline. Zero storage of raw credit card numbers or plaintext passwords."
        )
    }

    private fun buildDirectoryTree(type: DomainType, stack: TechStack): DirectoryNode {
        return DirectoryNode(
            name = "project-root",
            type = "folder",
            description = "Root workspace directory",
            children = listOf(
                DirectoryNode("README.md", "file", emptyList(), "Project documentation & setup guide"),
                DirectoryNode(".env.example", "file", emptyList(), "Environment variable template for secrets"),
                DirectoryNode(".gitignore", "file", emptyList(), "Git version control ignore rules"),
                DirectoryNode(
                    name = "app",
                    type = "folder",
                    description = "Native Android Jetpack Compose application module",
                    children = listOf(
                        DirectoryNode("build.gradle.kts", "file", emptyList(), "App module build configuration and dependencies"),
                        DirectoryNode("proguard-rules.pro", "file", emptyList(), "R8 code obfuscation & shrinking rules"),
                        DirectoryNode(
                            name = "src",
                            type = "folder",
                            children = listOf(
                                DirectoryNode(
                                    name = "main",
                                    type = "folder",
                                    children = listOf(
                                        DirectoryNode("AndroidManifest.xml", "file", emptyList(), "App manifest, permissions & launcher activities"),
                                        DirectoryNode(
                                            name = "java/com/example",
                                            type = "folder",
                                            children = listOf(
                                                DirectoryNode("MainActivity.kt", "file", emptyList(), "Single-activity entry point with edge-to-edge Compose container"),
                                                DirectoryNode(
                                                    name = "data",
                                                    type = "folder",
                                                    children = listOf(
                                                        DirectoryNode("model/", "folder", emptyList(), "Immutable data classes & Moshi JSON adapters"),
                                                        DirectoryNode("local/", "folder", emptyList(), "Room DB entities, DAOs, and Database client"),
                                                        DirectoryNode("remote/", "folder", emptyList(), "Retrofit API interfaces, OkHttp interceptor & DTOs"),
                                                        DirectoryNode("repository/", "folder", emptyList(), "Single source of truth data repository layer")
                                                    )
                                                ),
                                                DirectoryNode(
                                                    name = "ui",
                                                    type = "folder",
                                                    children = listOf(
                                                        DirectoryNode("theme/", "folder", emptyList(), "Color, Typography, Theme & Shape definitions"),
                                                        DirectoryNode("components/", "folder", emptyList(), "Reusable UI widgets (Buttons, Cards, Loaders, Dialogs)"),
                                                        DirectoryNode("screens/", "folder", emptyList(), "Compose destination screens & ViewModels"),
                                                        DirectoryNode("navigation/", "folder", emptyList(), "NavHost routing and type-safe destinations")
                                                    )
                                                ),
                                                DirectoryNode(
                                                    name = "util",
                                                    type = "folder",
                                                    children = listOf(
                                                        DirectoryNode("NetworkObserver.kt", "file", emptyList(), "Flow-based online/offline network monitor"),
                                                        DirectoryNode("Result.kt", "file", emptyList(), "Sealed Resource state wrapper (Success, Loading, Error)")
                                                    )
                                                )
                                            )
                                        ),
                                        DirectoryNode("res/", "folder", emptyList(), "XML strings, vectors, mipmap app icons & themes")
                                    )
                                ),
                                DirectoryNode("test/", "folder", emptyList(), "Robolectric JVM unit tests & Roborazzi screenshot tests")
                            )
                        )
                    )
                ),
                DirectoryNode(
                    name = "backend",
                    type = "folder",
                    description = "Cloud API server & microservices",
                    children = listOf(
                        DirectoryNode("package.json", "file", emptyList(), "Backend dependencies & npm scripts"),
                        DirectoryNode("Dockerfile", "file", emptyList(), "Multi-stage container definition for Cloud Run"),
                        DirectoryNode(
                            name = "src",
                            type = "folder",
                            children = listOf(
                                DirectoryNode("index.ts", "file", emptyList(), "Server entry point & HTTP listener"),
                                DirectoryNode("routes/", "folder", emptyList(), "REST route definitions"),
                                DirectoryNode("controllers/", "folder", emptyList(), "Request handler controllers"),
                                DirectoryNode("services/", "folder", emptyList(), "Business logic & AI bridge services"),
                                DirectoryNode("middleware/", "folder", emptyList(), "Auth, rate limiting, and error handlers"),
                                DirectoryNode("models/", "folder", emptyList(), "Database schemas & ORM entities"),
                                DirectoryNode("config/", "folder", emptyList(), "Configuration loader and secrets validation")
                            )
                        )
                    )
                ),
                DirectoryNode(
                    name = "database",
                    type = "folder",
                    description = "Database migrations and seed fixtures",
                    children = listOf(
                        DirectoryNode("migrations/", "folder", emptyList(), "SQL versioned migration scripts"),
                        DirectoryNode("seeds/", "folder", emptyList(), "Development mock seed data")
                    )
                ),
                DirectoryNode("docs/", "folder", emptyList(), "OpenAPI specs, architecture diagrams & deployment runbooks")
            )
        )
    }

    private fun buildFileSpecifications(type: DomainType, stack: TechStack, features: List<FeatureItem>): List<FileSpecification> {
        return listOf(
            FileSpecification(
                filePath = "app/src/main/java/com/example/MainActivity.kt",
                purpose = "Primary entry point of the Android application. Sets up Compose window edge-to-edge rendering and root NavHost navigation graph.",
                dependencies = listOf("androidx.activity.ComponentActivity", "androidx.compose.material3.Scaffold", "com.example.ui.navigation.AppNavHost"),
                responsibilities = listOf("Initialize window insets & system bar styles", "Provide Theme context to composables", "Inject root ViewModel repositories"),
                importantFunctions = listOf("onCreate(savedInstanceState: Bundle?)", "AppRootContent()"),
                inputs = "Saved instance bundle from Android OS",
                outputs = "Rendered Material 3 Compose UI hierarchy",
                securityNotes = "Guarantees FLAG_SECURE on payment screens to prevent unauthorized screenshots.",
                implementationNotes = "Uses ComponentActivity with enableEdgeToEdge() for full-bleed immersion."
            ),
            FileSpecification(
                filePath = "app/src/main/java/com/example/data/repository/AppRepository.kt",
                purpose = "Single source of truth repository mediating between local Room Database and remote Retrofit REST API endpoints.",
                dependencies = listOf("com.example.data.local.ProjectDao", "com.example.data.remote.ApiService", "kotlinx.coroutines.flow.Flow"),
                responsibilities = listOf("Expose reactive Flow<List<T>> streams to ViewModels", "Handle offline write buffering & synchronization", "Catch and format HTTP network errors into domain Result types"),
                importantFunctions = listOf("getAllProjects(): Flow<List<Project>>", "syncWithRemote(): Result<Unit>", "saveItem(item: Item): Long"),
                inputs = "User action intents from ViewModels and background sync triggers",
                outputs = "Emitted Kotlin Flow domain states",
                securityNotes = "Never persists unencrypted sensitive user tokens to standard database fields.",
                implementationNotes = "Implements offline-first caching strategy with Room cache-then-network policy."
            ),
            FileSpecification(
                filePath = "app/src/main/java/com/example/domain/engine/ArchitectureEngine.kt",
                purpose = "High-performance software architecture generation & consistency validation engine.",
                dependencies = listOf("com.example.data.model.ProjectBlueprint", "com.example.domain.engine.GeminiClient"),
                responsibilities = listOf("Transform raw user ideas into complete 14-layer blueprints", "Execute automated health score audits", "Generate one-shot master prompts for AI coding agents"),
                importantFunctions = listOf("generateBlueprint(rawIdea: String): ProjectBlueprint", "runQualityAudit(...): QualityReport", "generateOneShotMasterPrompt(...): String"),
                inputs = "Raw user text prompt, category, and model parameters",
                outputs = "Fully hydrated ProjectBlueprint data object with 100% layer consistency",
                securityNotes = "Validates and sanitizes prompt injections before sending to upstream AI models.",
                implementationNotes = "Features instant offline fallback engine alongside online Gemini 3.5 Flash streaming."
            ),
            FileSpecification(
                filePath = "backend/src/controllers/authController.ts",
                purpose = "Manages authentication endpoints, user registration, JWT generation, password verification, and session revocations.",
                dependencies = listOf("jsonwebtoken", "argon2", "zod", "prisma/pg"),
                responsibilities = listOf("Validate login payload schemas", "Hash and verify passwords using Argon2id", "Issue short-lived signed JWTs and secure HTTP-Only refresh cookies"),
                importantFunctions = listOf("loginHandler(req, res)", "registerHandler(req, res)", "refreshTokenHandler(req, res)", "logoutHandler(req, res)"),
                inputs = "HTTP POST JSON requests containing credentials or tokens",
                outputs = "HTTP 200/201 JSON responses with token payload and user profile summary",
                securityNotes = "Implements exponential backoff on failed logins to prevent brute force attacks.",
                implementationNotes = "All JWT tokens signed using asymmetric RS256 private keys or high-entropy secrets."
            ),
            FileSpecification(
                filePath = "backend/src/services/aiService.ts",
                purpose = "Bridges backend business logic to the Gemini 3.5 Flash API with token streaming and function calling support.",
                dependencies = listOf("@google/genai or Axios", "sse-stream", "zod"),
                responsibilities = listOf("Inject system instructions and RAG context into prompts", "Manage streaming SSE connections to frontend clients", "Validate LLM structured outputs before saving to database"),
                importantFunctions = listOf("streamChatResponse(prompt, history, res)", "generateStructuredBlueprint(idea): Promise<Blueprint>"),
                inputs = "User prompt strings, conversational history arrays, and document context",
                outputs = "Server-Sent Events (SSE) token stream and validated JSON objects",
                securityNotes = "API keys injected exclusively via process.env.GEMINI_API_KEY; never exposed to clients.",
                implementationNotes = "Uses 60-second timeouts with retry exponential backoff for transient network hiccups."
            )
        )
    }

    private fun buildRoadmap(type: DomainType, features: List<FeatureItem>): List<BuildPhase> {
        return listOf(
            BuildPhase(
                phaseNumber = 1,
                name = "Foundation & Project Scaffolding",
                tasks = listOf(
                    "Initialize Gradle multi-module project (Android Compose) and TypeScript Node/Fastify backend.",
                    "Configure Git repositories, .gitignore, .env.example templates, and linting rules (ktlint, ESLint).",
                    "Establish CI/CD build verification workflow with automated compilation checks."
                ),
                dependencies = listOf("JDK 17", "Android SDK 36", "Node.js 20+"),
                expectedResult = "Clean compiling boilerplate project with verified build pipeline.",
                completionCriteria = listOf("Android app boots to empty theme Scaffold", "Backend answers GET /health with 200 OK")
            ),
            BuildPhase(
                phaseNumber = 2,
                name = "Authentication & Identity Architecture",
                tasks = listOf(
                    "Implement backend user tables with Argon2id password hashing and JWT token issuance.",
                    "Build Android Credential Manager Google Sign-In and biometric auth flow.",
                    "Add token auto-refresh interceptors and secure storage in Android Keystore."
                ),
                dependencies = listOf("Phase 1 Foundation", "JWT / Crypto Libraries"),
                expectedResult = "End-to-end user signup, signin, token rotation, and signout loop.",
                completionCriteria = listOf("Valid login returns JWT and populates user session state", "Expired tokens refresh automatically")
            ),
            BuildPhase(
                phaseNumber = 3,
                name = "Database & Persistence Layer",
                tasks = listOf(
                    "Write versioned SQL migration scripts for all core relational tables, foreign keys, and indexes.",
                    "Build local Android Room Database entities, DAOs, and TypeConverters.",
                    "Implement Repository pattern exposing reactive Kotlin Flows to UI layer."
                ),
                dependencies = listOf("Phase 2 Auth", "PostgreSQL", "Room DB"),
                expectedResult = "Reliable local-first persistence with offline CRUD capabilities.",
                completionCriteria = listOf("Room DAO queries pass unit tests", "Database migrations execute idempotently")
            ),
            BuildPhase(
                phaseNumber = 4,
                name = "Core Domain Features Implementation",
                tasks = listOf(
                    "Implement primary business logic controllers and service handlers on backend.",
                    "Build Android ViewModels and StateFlow state machines for P0 features.",
                    "Connect REST endpoints with Retrofit and error handling envelopes."
                ),
                dependencies = listOf("Phase 3 Database"),
                expectedResult = "Functional core user journey working seamlessly end-to-end.",
                completionCriteria = listOf("All P0 features functional", "Network error states handled gracefully")
            ),
            BuildPhase(
                phaseNumber = 5,
                name = "UI/UX Craft & Responsive Layouts",
                tasks = listOf(
                    "Build all Compose destination screens adhering strictly to Material Design 3 guidelines.",
                    "Implement adaptive layouts for Compact (Phone), Medium (Foldable), and Expanded (Tablet).",
                    "Add micro-interactions, smooth spring animations, skeleton shimmers, and empty state illustrations."
                ),
                dependencies = listOf("Phase 4 Core Features"),
                expectedResult = "Visually stunning, responsive, high-contrast user interface.",
                completionCriteria = listOf("Minimum 48dp touch targets on all clickables", "Zero layout clipping on any screen size")
            ),
            BuildPhase(
                phaseNumber = 6,
                name = "Integrations & AI Engine Bridge",
                tasks = listOf(
                    "Integrate Gemini 3.5 Flash API with token streaming and structured output validation.",
                    "Connect third-party webhooks (Stripe payments, FCM push notifications, Courier tracking).",
                    "Implement rate limiting and token consumption analytics."
                ),
                dependencies = listOf("Phase 5 UI/UX", "API Keys in Secrets Panel"),
                expectedResult = "Real-time AI and third-party services fully operational.",
                completionCriteria = listOf("AI responses stream smoothly without UI freeze", "Payment webhooks update order status")
            ),
            BuildPhase(
                phaseNumber = 7,
                name = "Automated Testing & Quality Verification",
                tasks = listOf(
                    "Write local JVM Robolectric unit tests for ViewModels, DAOs, and repository error flows.",
                    "Implement Roborazzi screenshot verification tests for core screens.",
                    "Perform load testing on backend API endpoints."
                ),
                dependencies = listOf("Phase 6 Integrations"),
                expectedResult = "High-confidence automated test suite covering all critical journeys.",
                completionCriteria = listOf("All unit and screenshot tests pass green in CI")
            ),
            BuildPhase(
                phaseNumber = 8,
                name = "Security Hardening & Privacy Audit",
                tasks = listOf(
                    "Run static security analysis (SAST) and dependency vulnerability scans.",
                    "Verify zero secrets in client APK binaries using decompiler inspection tests.",
                    "Implement rate limiting, CORS restrictions, and WAF rules on API Gateway."
                ),
                dependencies = listOf("Phase 7 Testing"),
                expectedResult = "Production-hardened, zero-vulnerability software architecture.",
                completionCriteria = listOf("Architecture Health Score >= 95/100", "Zero critical security warnings")
            ),
            BuildPhase(
                phaseNumber = 9,
                name = "Performance Optimization & Caching",
                tasks = listOf(
                    "Optimize Compose recompositions using remember, derivedStateOf, and immutable models.",
                    "Enable Redis caching for high-frequency read queries and static metadata.",
                    "Configure image compression and WebP CDN caching."
                ),
                dependencies = listOf("Phase 8 Security"),
                expectedResult = "Sub-100ms API response times and 60fps/120fps buttery smooth UI scrolling.",
                completionCriteria = listOf("Zero dropped frames during list scrolling", "P95 backend latency < 150ms")
            ),
            BuildPhase(
                phaseNumber = 10,
                name = "Production Deployment & Monitoring",
                tasks = listOf(
                    "Build release APK/AAB with Proguard/R8 shrinking and keystore signing.",
                    "Deploy containerized backend microservices to Google Cloud Run / AWS ECS.",
                    "Configure Sentry crash reporting, Prometheus metrics, and uptime health check alerts."
                ),
                dependencies = listOf("Phase 9 Optimization"),
                expectedResult = "Live production release running with 99.99% uptime monitoring.",
                completionCriteria = listOf("Automated health checks returning 200 OK", "Crash-free user session rate > 99.8%")
            )
        )
    }

    private fun buildAssumptions(type: DomainType, stack: TechStack): List<AssumptionItem> {
        return listOf(
            AssumptionItem(
                id = "assump_platform",
                category = "Target Platform",
                title = "Primary Client Platform Target",
                currentChoice = "Android Native (Jetpack Compose) + Web PWA",
                options = listOf("Android Native (Jetpack Compose) + Web PWA", "Android Only (Kotlin)", "Cross-Platform (React Native / Flutter)", "Web Only (Next.js)"),
                impactArea = "Frontend codebase structure & native SDK permissions"
            ),
            AssumptionItem(
                id = "assump_auth",
                category = "Authentication",
                title = "User Identity & Authentication Provider",
                currentChoice = "Firebase / Supabase Auth + Google One-Tap",
                options = listOf("Firebase / Supabase Auth + Google One-Tap", "Self-Hosted Custom JWT + Argon2id", "Clerk / Auth0 Enterprise", "Passwordless Magic Link Only"),
                impactArea = "Auth controllers, token rotation, and security headers"
            ),
            AssumptionItem(
                id = "assump_db",
                category = "Database Engine",
                title = "Primary Cloud Database Technology",
                currentChoice = "PostgreSQL 16 (Relational ACID) + Room (Local)",
                options = listOf("PostgreSQL 16 (Relational ACID) + Room (Local)", "Supabase Managed PostgreSQL", "MongoDB / NoSQL Document Store", "Firebase Firestore"),
                impactArea = "Database schema models, migration scripts, and indexing"
            ),
            AssumptionItem(
                id = "assump_ai",
                category = "AI Intelligence",
                title = "AI Model Engine & Provider",
                currentChoice = "Gemini 3.5 Flash (Direct REST / Serverless)",
                options = listOf("Gemini 3.5 Flash (Direct REST / Serverless)", "Gemini 3.1 Pro (Deep Reasoning)", "Local On-Device LLM (MediaPipe)", "No AI Capabilities"),
                impactArea = "AI orchestration layer, prompt templates, and token budget"
            )
        )
    }

    private fun runQualityAudit(
        features: List<FeatureItem>,
        screens: List<ScreenSpec>,
        apis: List<ApiEndpoint>,
        dbSchema: DatabaseSchema,
        techStack: TechStack,
        security: SecurityPlan
    ): QualityReport {
        val critical = mutableListOf<EngineeringIssue>()
        val high = mutableListOf<EngineeringIssue>()
        val recommendations = mutableListOf<String>()

        // 1. Check features ↔ screens alignment
        if (screens.isEmpty()) {
            critical.add(EngineeringIssue(id = "iss_no_screens", title = "Missing Screen Specifications", description = "No UX screens defined for the feature set.", autoFixable = true, recommendedFix = "Auto-generate screens matching P0 features", severity = "CRITICAL"))
        }

        // 2. Check screens ↔ APIs alignment
        if (apis.isEmpty()) {
            critical.add(EngineeringIssue(id = "iss_no_apis", title = "Missing API Endpoints", description = "Frontend screens have no corresponding backend REST endpoints.", autoFixable = true, recommendedFix = "Auto-scaffold REST endpoints", severity = "CRITICAL"))
        }

        // 3. Check APIs ↔ Database tables
        val tableNames = dbSchema.entities.map { it.tableName.lowercase() }
        val hasUsersTable = tableNames.contains("users")
        if (!hasUsersTable) {
            critical.add(EngineeringIssue(id = "iss_no_users_table", title = "Missing Users Table", description = "Authentication endpoints require a 'users' table in database schema.", autoFixable = true, recommendedFix = "Inject users entity with password_hash and roles", severity = "CRITICAL"))
        }

        // 4. Security & API key check
        if (security.apiKeyProtection.isBlank()) {
            high.add(EngineeringIssue(id = "iss_api_key_sec", title = "Missing API Key Protection", description = "Specify strict rules preventing client-side secret exposure.", autoFixable = true, recommendedFix = "Apply server-side proxy rule", severity = "HIGH"))
        }

        // 5. Tech Stack Consistency
        if (techStack.database.name.contains("PostgreSQL") && dbSchema.databaseType.contains("MongoDB")) {
            high.add(EngineeringIssue(id = "iss_db_conflict", title = "Database Mismatch", description = "Tech stack specifies PostgreSQL but schema specifies MongoDB.", autoFixable = true, recommendedFix = "Align database schema to PostgreSQL", severity = "HIGH"))
        }

        recommendations.add("Enable automated foreign key ON DELETE CASCADE constraints for child tables.")
        recommendations.add("Enforce HTTP Strict Transport Security (HSTS) with max-age=31536000 on API Gateway.")
        recommendations.add("Configure automated daily WAL-G database backups with 30-day point-in-time recovery.")
        recommendations.add("Add index on created_at DESC for all time-series and feed tables.")

        val score = 100 - (critical.size * 15) - (high.size * 5)
        val finalScore = score.coerceIn(75, 98)

        return QualityReport(
            healthScore = finalScore,
            checksSummary = "Consistency audit validated: Features ↔ Screens ↔ APIs ↔ Database ↔ Security.",
            criticalIssues = critical,
            highIssues = high,
            recommendations = recommendations
        )
    }

    private fun generateOneShotMasterPrompt(
        projectName: String,
        rawIdea: String,
        archetype: DomainType,
        techStack: TechStack,
        features: List<FeatureItem>,
        uxArch: UxArchitecture,
        dbSchema: DatabaseSchema,
        apiEndpoints: List<ApiEndpoint>,
        dirTree: DirectoryNode,
        security: SecurityPlan,
        buildPlan: List<BuildPhase>
    ): String {
        val featuresFormatted = features.joinToString("\n") {
            "- [${it.priority}] **${it.name}**: ${it.purpose} (Workflow: ${it.workflow})"
        }

        val screensFormatted = uxArch.screens.joinToString("\n") {
            "- **${it.name}** (`${it.route}`): ${it.description} | Components: ${it.components.joinToString(", ")}"
        }

        val apisFormatted = apiEndpoints.joinToString("\n") {
            "- `${it.method} ${it.path}` (${it.group}): ${it.purpose} [Auth: ${it.authentication}]"
        }

        val tablesFormatted = dbSchema.entities.joinToString("\n\n") { entity ->
            "Table: `${entity.tableName}` (${entity.description})\n" +
            entity.fields.joinToString("\n") { f ->
                "  - `${f.name}`: ${f.type} ${if (f.isPrimaryKey) "[PK]" else ""} ${if (f.isForeignKey) "[FK]" else ""} ${if (!f.isNullable) "NOT NULL" else "NULL"} - ${f.description}"
            }
        }

        return """
# ONE-SHOT MASTER CODING PROMPT: $projectName

> **Objective**: Build a complete, production-ready, enterprise-grade software application for **$projectName** based on the following comprehensive architecture blueprint.

---

## 1. PROJECT VISION & SUMMARY
- **App Name**: $projectName
- **Category**: ${archetype.category}
- **Original User Request**: "$rawIdea"
- **Target Audience**: Ambitious mobile & web users requiring fast, reliable, offline-first execution.

---

## 2. EXACT TECHNOLOGY STACK
- **Frontend**: ${techStack.frontend.name} (${techStack.frontend.justification})
- **Backend API**: ${techStack.backend.name} (${techStack.backend.justification})
- **Database & Cache**: ${techStack.database.name} (${techStack.database.justification})
- **Authentication**: ${techStack.authentication.name} (${techStack.authentication.justification})
- **Storage**: ${techStack.storage.name} (${techStack.storage.justification})
- **AI Intelligence**: ${techStack.aiModels.name} (${techStack.aiModels.justification})
- **Security & Secrets**: ${security.apiKeyProtection}

---

## 3. CORE FEATURES & FUNCTIONAL SPECIFICATIONS
$featuresFormatted

---

## 4. UI/UX SCREEN ARCHITECTURE & ROUTES
$screensFormatted

- **Design System**: Material Design 3 (M3) with centralized Theme.kt, high-contrast dark/light mode, minimum 48dp touch targets, and full edge-to-edge system insets support.

---

## 5. DATABASE SCHEMA SPECIFICATION
$tablesFormatted

---

## 6. REST API SPECIFICATIONS & ENDPOINTS
$apisFormatted

---

## 7. SECURITY & SECRETS MANDATES
- **No Client Secrets**: NEVER embed secret API keys, database connection strings, or server tokens directly into client APK/JS code.
- **Access Control**: Enforce strict Role-Based Access Control (RBAC) and verify record ownership on all mutating operations.
- **Validation**: Validate all incoming request payloads against strict Zod/Pydantic schemas.

---

## 8. STEP-BY-STEP IMPLEMENTATION DIRECTIVES FOR AI CODING AGENT
Follow this strict 12-step execution sequence:
1. **Understand Architecture**: Review all features, database tables, and API contracts before writing code.
2. **Project Scaffolding**: Create clean directory structures matching the project tree.
3. **Foundation & Theme**: Set up centralized Color, Typography, and Theme system.
4. **Authentication**: Implement secure authentication flow, token storage, and session validation.
5. **Database & Room**: Write Room database entities, DAOs, TypeConverters, and repository layers.
6. **Core Domain Features**: Implement primary business logic, ViewModels, and StateFlow pipelines.
7. **UI Screens**: Build all screens with responsive Jetpack Compose layouts and error handling.
8. **Third-Party Integrations**: Connect API bridges, push notifications, and AI streaming.
9. **Validation & State Handling**: Implement empty states, shimmering loaders, and friendly error alerts.
10. **Automated Testing**: Write Robolectric unit tests and Roborazzi screenshot verification tests.
11. **Quality Audit**: Confirm zero unresolved references, no memory leaks, and 100% build compatibility.
12. **Final Run Instructions**: Deliver a concise, professional summary of the implemented system.
""".trimIndent()
    }

    /**
     * AI Architect Chat: Modifies an existing blueprint based on a natural language instruction.
     * e.g., "Add subscriptions", "Change Firebase to Supabase", "Make this Android-only", "Add an admin panel"
     */
    private val moshi = com.squareup.moshi.Moshi.Builder()
        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()

    @com.squareup.moshi.JsonClass(generateAdapter = true)
    data class ModificationResponse(
        val diffSummary: String,
        val updatedBlueprint: com.example.data.model.ProjectBlueprint
    )

    suspend fun modifyBlueprint(
        current: com.example.data.model.ProjectBlueprint,
        userInstruction: String
    ): Pair<com.example.data.model.ProjectBlueprint, String> {
        val prompt = ArchitecturePrompts.getModificationPrompt(
            currentBlueprintJson = com.example.data.json.BlueprintJsonAdapter.toJson(current),
            userInstruction = userInstruction
        )
        
        return try {
            val result = GeminiClient.generateText(prompt, jsonMode = true).getOrThrow()
            val response = moshi.adapter(ModificationResponse::class.java).fromJson(result)
            if (response != null) {
                response.updatedBlueprint to response.diffSummary
            } else {
                current to "AI failed to process the request. No changes applied."
            }
        } catch (e: Exception) {
            current to "Error during AI architecture modification: ${e.message}"
        }
    }

    suspend fun runHealthAudit(blueprint: ProjectBlueprint): ProjectBlueprint {
        val prompt = ArchitecturePrompts.getHealthCheckPrompt(BlueprintJsonAdapter.toJson(blueprint))
        return try {
            val result = GeminiClient.generateText(prompt, jsonMode = true).getOrThrow()
            // We need to parse both ProjectHealth and QualityReport from the AI response
            // For simplicity in this implementation, I'll use a wrapper class
            val response = moshi.adapter(HealthCheckResponse::class.java).fromJson(result)
            if (response != null) {
                blueprint.copy(
                    projectHealth = response.projectHealth,
                    qualityReport = response.qualityReport
                )
            } else blueprint
        } catch (e: Exception) {
            blueprint
        }
    }

    suspend fun generateRoadmap(blueprint: ProjectBlueprint): ProjectBlueprint {
        val prompt = ArchitecturePrompts.getRoadmapPrompt(BlueprintJsonAdapter.toJson(blueprint))
        return try {
            val result = GeminiClient.generateText(prompt, jsonMode = true).getOrThrow()
            val roadmap = moshi.adapter(ProjectRoadmap::class.java).fromJson(result)
            if (roadmap != null) {
                blueprint.copy(projectRoadmap = roadmap)
            } else blueprint
        } catch (e: Exception) {
            blueprint
        }
    }

    suspend fun runBugHunter(blueprint: ProjectBlueprint): ProjectBlueprint {
        val prompt = ArchitecturePrompts.getBugHunterPrompt(BlueprintJsonAdapter.toJson(blueprint))
        return try {
            val result = GeminiClient.generateText(prompt, jsonMode = true).getOrThrow()
            val qualityReport = moshi.adapter(QualityReport::class.java).fromJson(result)
            if (qualityReport != null) {
                blueprint.copy(qualityReport = qualityReport)
            } else blueprint
        } catch (e: Exception) {
            blueprint
        }
    }

    @JsonClass(generateAdapter = true)
    data class HealthCheckResponse(
        val projectHealth: ProjectHealth,
        val qualityReport: QualityReport
    )

    private fun incrementVersion(current: String): String {
        val parts = current.split(".").map { it.toIntOrNull() ?: 0 }
        if (parts.size != 3) return "1.0.1"
        return "${parts[0]}.${parts[1]}.${parts[2] + 1}"
    }

    private fun buildCostEstimates(archetype: DomainType, techStack: TechStack): CostEstimates {
        return CostEstimates(
            monthly100Users = "$10 - $20 / mo",
            monthly1kUsers = "$40 - $80 / mo",
            monthly10kUsers = "$200 - $400 / mo",
            monthly100kUsers = "$1,500 - $3,000 / mo",
            breakdown = listOf(
                CostItem("Compute (Cloud Run/Vercel)", "$120", techStack.backend.name),
                CostItem("Database (PostgreSQL Managed)", "$80", techStack.database.name),
                CostItem("Storage (S3/GCS)", "$15", "AWS/GCP"),
                CostItem("Monitoring (Datadog/Sentry)", "$45", "Sentry")
            )
        )
    }

    private fun buildScalabilityPlan(archetype: DomainType, techStack: TechStack): ScalabilityPlan {
        return ScalabilityPlan(
            targetScale = "1,000,000 Active Users",
            databaseBottlenecks = listOf("Write lock contention on 'users' table during high-concurrency signup spikes.", "Connection pool exhaustion if not using PgBouncer."),
            apiBottlenecks = listOf("CPU-bound JWT verification at scale.", "In-memory session storage limits."),
            cachingStrategy = "Distributed Redis cache for frequently accessed product metadata and user permissions.",
            queuingStrategy = "RabbitMQ or BullMQ for background email processing and image optimization tasks.",
            cdnAndEdge = "Cloudflare Edge for static asset delivery and DDoS protection.",
            scalingMilestones = listOf("10k Users: Vertical scaling of DB.", "100k Users: Read replicas + API Gateway rate limiting.", "1M Users: Database sharding by user_id.")
        )
    }

    private fun buildTestingChecklist(archetype: DomainType, features: List<FeatureItem>): TestingChecklist {
        return TestingChecklist(
            unitTestCases = listOf("Verify auth service JWT generation", "Check database repository CRUD operations"),
            integrationTestCases = listOf("Test API endpoint ↔ Database connectivity", "Validate payment webhook verification"),
            uiTestCases = listOf("User can successfully sign up and see dashboard", "Form validation shows errors for invalid email"),
            securityTestCases = listOf("SQL Injection prevention check", "CSRF protection verification on POST endpoints"),
            performanceTestCases = listOf("Baseline P95 latency < 300ms", "Stress test login endpoint with 500 concurrent req/sec")
        )
    }

    private fun buildDeploymentPlan(archetype: DomainType, techStack: TechStack): DeploymentPlan {
        return DeploymentPlan(
            primaryPlatform = "Vercel / AWS Cloud Run",
            buildCommands = "npm run build / gradle assembleRelease",
            envVariables = listOf("DATABASE_URL", "JWT_SECRET", "GEMINI_API_KEY", "STRIPE_SECRET"),
            sslAndDomain = "Automated SSL via Let's Encrypt",
            databaseMigrationStrategy = "Zero-downtime migrations via Prisma/Liquibase",
            monitoringAndAlerts = "Sentry for errors, Prometheus for metrics",
            rollbackStrategy = "Instant revision rollback in CI/CD pipeline"
        )
    }

    private fun buildAlternativeTechStack(archetype: DomainType, stack: TechStack): AlternativeTechStack {
        return AlternativeTechStack(
            frontendAlternative = TechAlternative(
                name = if (stack.frontend.name.contains("React")) "Vue.js 3 + Nuxt" else "React + Next.js 14",
                pros = listOf("Faster initial bundle rendering", "Simpler reactive state model"),
                cons = listOf("Smaller ecosystem of pre-built UI component libraries")
            ),
            backendAlternative = TechAlternative(
                name = if (stack.backend.name.contains("Node")) "Go (Golang) + Fiber" else "Node.js + NestJS",
                pros = listOf("Ultra low memory footprint (<20MB RAM)", "Sub-millisecond execution speeds"),
                cons = listOf("Requires strictly typed boilerplate for complex ORM queries")
            ),
            databaseAlternative = TechAlternative(
                name = if (stack.database.name.contains("Postgre")) "MongoDB Atlas (Document DB)" else "PostgreSQL + Supabase",
                pros = listOf("Schemaless flexibility for rapid prototyping", "Native JSON document nesting"),
                cons = listOf("Lacks strict ACID relational foreign key enforcement")
            )
        )
    }
}
