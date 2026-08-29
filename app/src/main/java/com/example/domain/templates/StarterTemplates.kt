package com.example.domain.templates

data class StarterTemplate(
    val id: String,
    val title: String,
    val category: String,
    val iconName: String,
    val description: String,
    val suggestedPrompt: String,
    val keyFeatures: List<String>,
    val defaultStack: String
)

object StarterTemplates {
    val list = listOf(
        StarterTemplate(
            id = "ecommerce_meesho",
            title = "Social Commerce (Meesho-like)",
            category = "Ecommerce",
            iconName = "ShoppingBag",
            description = "Hyperlocal social commerce platform with seller catalogs, WhatsApp sharing, margin setting, and order tracking.",
            suggestedPrompt = "Build a Meesho-like social ecommerce app for local fashion sellers where resellers can share product catalogs on WhatsApp, set profit margins, collect online payments, and track orders with automated shipping integration.",
            keyFeatures = listOf("Reseller Margin Tool", "WhatsApp Catalog Sharing", "Escrow COD & UPI", "Inventory Sync"),
            defaultStack = "Kotlin / Compose + Node.js + PostgreSQL + Stripe/Razorpay"
        ),
        StarterTemplate(
            id = "ai_chat_agent",
            title = "Enterprise AI Copilot",
            category = "AI Chat App",
            iconName = "SmartToy",
            description = "Intelligent conversational assistant with RAG document retrieval, multi-modal vision, and structured tool calling.",
            suggestedPrompt = "Build an AI-powered conversational copilot app that allows users to chat with enterprise documents, run SQL queries via natural language, invoke custom tools, and export structured PDF summaries.",
            keyFeatures = listOf("Vector RAG Search", "Tool Calling Sandbox", "Streaming Markdown", "Role-based Memory"),
            defaultStack = "Next.js / Jetpack Compose + Python FastAPI + pgvector + Gemini 3.5 Flash"
        ),
        StarterTemplate(
            id = "saas_analytics",
            title = "B2B SaaS Analytics Hub",
            category = "SaaS",
            iconName = "Analytics",
            description = "Multi-tenant business metrics platform with real-time dashboards, team permissions, and usage-based billing.",
            suggestedPrompt = "Build a multi-tenant B2B SaaS analytics platform with custom telemetry ingestion, real-time funnel charts, team role management, and Stripe usage-based metered billing.",
            keyFeatures = listOf("Multi-Tenant RBAC", "Timescale Ingestion", "Metered Invoicing", "Custom Webhooks"),
            defaultStack = "React / Kotlin + Go / Gin + PostgreSQL + Redis + Stripe"
        ),
        StarterTemplate(
            id = "marketplace_services",
            title = "On-Demand Service Marketplace",
            category = "Marketplace",
            iconName = "Storefront",
            description = "Two-sided marketplace connecting verified service providers with clients via instant booking and escrow payments.",
            suggestedPrompt = "Build a two-sided on-demand marketplace connecting local home service professionals with homeowners, featuring verified identity checks, real-time quotes, calendar booking, and escrow milestone payments.",
            keyFeatures = listOf("Two-Sided Matching", "Milestone Escrow", "Calendar Scheduling", "Review Verification"),
            defaultStack = "React Native / Compose + Node.js + PostgreSQL + Supabase"
        ),
        StarterTemplate(
            id = "finance_wealth",
            title = "AI Wealth & Budget Tracker",
            category = "Finance",
            iconName = "AccountBalance",
            description = "Personal finance management with automated bank sync, smart receipt OCR, and predictive budget forecasting.",
            suggestedPrompt = "Build a personal wealth and expense tracker app with automated transaction categorization using AI, budget forecasting, split expenses with roommates, and encrypted bank connections.",
            keyFeatures = listOf("AI Receipt OCR", "Predictive Cashflow", "Multi-Currency Split", "End-to-End Encryption"),
            defaultStack = "Jetpack Compose + Kotlin Multiplatform + PostgreSQL + Plaid"
        ),
        StarterTemplate(
            id = "fitness_coach",
            title = "Adaptive AI Fitness Coach",
            category = "Fitness",
            iconName = "FitnessCenter",
            description = "Personalized workout and nutrition tracker with camera-based rep counting and dynamic progression curves.",
            suggestedPrompt = "Build an adaptive AI fitness companion app featuring personalized progressive workout plans, video form check analysis, macro meal logger with photo recognition, and smartwatch sensor sync.",
            keyFeatures = listOf("Computer Vision Form Check", "Progressive Overload AI", "Photo Macro Estimator", "Health Connect"),
            defaultStack = "Kotlin Compose + Python backend + Room + SQLite + HealthKit"
        ),
        StarterTemplate(
            id = "education_micro",
            title = "Interactive Micro-Learning",
            category = "Education",
            iconName = "School",
            description = "Gamified education platform with bite-sized lessons, interactive coding sandboxes, and spaced repetition.",
            suggestedPrompt = "Build a Duolingo-style micro-learning app for learning system design and software architecture, with interactive quizzes, spaced repetition flashcards, streak gamification, and peer leaderboards.",
            keyFeatures = listOf("Spaced Repetition Algorithm", "Interactive Canvas Quizzes", "Streak Multipliers", "Offline Caching"),
            defaultStack = "Jetpack Compose + Node.js + SQLite/Room + Redis"
        ),
        StarterTemplate(
            id = "delivery_hyperlocal",
            title = "Hyperlocal Food & Grocery",
            category = "Delivery",
            iconName = "LocalShipping",
            description = "Real-time dispatch system with live GPS courier tracking, dynamic surge pricing, and multi-vendor orders.",
            suggestedPrompt = "Build a 10-minute grocery delivery app with real-time dark-store inventory, live GPS rider tracking, route optimization, split basket payments, and push dispatch alerts.",
            keyFeatures = listOf("Sub-Second GPS Websockets", "Dynamic Route Optimizer", "Dark Store Inventory Lock", "Push Dispatch"),
            defaultStack = "Kotlin Android + Go Websocket Gateway + PostgreSQL + PostGIS + Redis"
        ),
        StarterTemplate(
            id = "social_niche",
            title = "Niche Community Platform",
            category = "Social Network",
            iconName = "Forum",
            description = "Community spaces with nested discussion threads, voice rooms, customizable karma badges, and moderation AI.",
            suggestedPrompt = "Build a niche developer community platform with nested threaded discussions, audio stage rooms, reputation badges, markdown code execution, and automated spam filtering.",
            keyFeatures = listOf("Nested Recursive Threads", "WebRTC Audio Rooms", "Token Reputation", "AI Content Guardrails"),
            defaultStack = "Jetpack Compose / Web + Elixir / Phoenix + PostgreSQL"
        ),
        StarterTemplate(
            id = "productivity_kanban",
            title = "Offline-First Project Studio",
            category = "Productivity",
            iconName = "Checklist",
            description = "High-speed project manager with multi-view boards, CRDT-based collaborative editing, and keyboard shortcuts.",
            suggestedPrompt = "Build a Linear-style offline-first issue tracker and project management app with instantaneous keyboard shortcuts, bidirectional sync using CRDTs, Gantt roadmaps, and Git commit integrations.",
            keyFeatures = listOf("CRDT Offline-First Sync", "Keyboard-Driven Navigation", "Dynamic Gantt Roadmap", "Webhook Integrations"),
            defaultStack = "Kotlin Compose + Rust / SQLite + WebSockets + Go"
        ),
        StarterTemplate(
            id = "booking_events",
            title = "Appointment & Event Booking",
            category = "Booking",
            iconName = "EventAvailable",
            description = "Smart scheduling platform with timezone detection, calendar sync, deposit collection, and SMS reminders.",
            suggestedPrompt = "Build a Calendly-style booking platform for service providers with customizable availability slots, instant Google/Outlook calendar sync, Stripe deposit collection, and automated SMS reminders.",
            keyFeatures = listOf("Timezone Slot Finder", "Calendar Sync Engine", "Deposit Hold", "Automated SMS/Email Reminders"),
            defaultStack = "React / Kotlin + Node.js + PostgreSQL + Stripe + Twilio"
        ),
        StarterTemplate(
            id = "community_learning",
            title = "Creator Community & Courses",
            category = "Community",
            iconName = "Group",
            description = "Paid creator community portal with video courses, live Q&A webinars, member leaderboards, and tiered access.",
            suggestedPrompt = "Build a Skool-style creator community app featuring video course modules, gamified member leaderboards, live stream event calendars, and recurring tier subscriptions.",
            keyFeatures = listOf("Gamified Level Progression", "HLS Video Streaming", "Member Directory", "Stripe Subscriptions"),
            defaultStack = "Next.js / Compose + Python FastAPI + PostgreSQL + AWS CloudFront"
        ),
        StarterTemplate(
            id = "content_cms",
            title = "Headless Content Studio",
            category = "Content Platform",
            iconName = "Article",
            description = "Modular CMS with AI copywriting assistance, automated image optimization, and multi-channel publishing API.",
            suggestedPrompt = "Build a modern headless CMS with block-based rich text editing, Gemini AI content generator, media asset optimization pipeline, and GraphQL publishing API.",
            keyFeatures = listOf("Block Editor Engine", "Gemini Content Generator", "GraphQL API Server", "CDN Asset Optimization"),
            defaultStack = "React / Kotlin + Go + PostgreSQL + Cloudflare R2"
        )
    )
}
