package com.example.domain.engine

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ProjectBlueprint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiSummaryGenerator {

    private const val TAG = "GeminiSummaryGenerator"
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun generateExecutiveSummary(blueprint: ProjectBlueprint): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val prompt = """
            You are a senior enterprise software architect. Write a concise, 2-sentence executive summary for this application blueprint to be displayed on a dashboard card.
            
            Application Name: ${blueprint.name}
            Category: ${blueprint.category}
            Tagline: ${blueprint.tagline}
            Raw Idea: ${blueprint.rawIdea}
            Primary Problem: ${blueprint.ideaUnderstanding.primaryProblem}
            Proposed Solution: ${blueprint.ideaUnderstanding.proposedSolution}
            Frontend Framework: ${blueprint.techStack.frontend.name}
            Backend Stack: ${blueprint.techStack.backend.name}
            Database: ${blueprint.techStack.database.name}
            Core Features: ${blueprint.features.take(4).joinToString(", ") { it.name }}
            
            Rules:
            1. Write exactly 2 concise, professional sentences.
            2. Highlighting the primary user value and key architectural implementation.
            3. Keep under 220 characters total.
            4. Do NOT output headings, bullet points, or quote marks. Just plain summary text.
        """.trimIndent()

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val requestJson = JSONObject().apply {
                    val contentArr = JSONArray().apply {
                        val partsArr = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put(JSONObject().put("parts", partsArr))
                    }
                    put("contents", contentArr)

                    val genConfig = JSONObject().apply {
                        put("temperature", 0.4)
                        put("maxOutputTokens", 120)
                    }
                    put("generationConfig", genConfig)
                }

                val url = "$BASE_URL/$MODEL:generateContent?key=$apiKey"
                val body = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBodyStr = response.body?.string()
                        if (!responseBodyStr.isNullOrBlank()) {
                            val jsonResp = JSONObject(responseBodyStr)
                            val candidates = jsonResp.optJSONArray("candidates")
                            if (candidates != null && candidates.length() > 0) {
                                val firstCandidate = candidates.getJSONObject(0)
                                val content = firstCandidate.optJSONObject("content")
                                val parts = content?.optJSONArray("parts")
                                if (parts != null && parts.length() > 0) {
                                    val text = parts.getJSONObject(0).optString("text", "").trim()
                                    if (text.isNotBlank()) {
                                        return@withContext text.replace("\n", " ")
                                    }
                                }
                            }
                        }
                    } else {
                        Log.w(TAG, "Gemini API call failed with status code: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error generating executive summary via Gemini API", e)
            }
        }

        return@withContext fallbackExecutiveSummary(blueprint)
    }

    fun fallbackExecutiveSummary(blueprint: ProjectBlueprint): String {
        val feName = blueprint.techStack.frontend.name.split(" ").firstOrNull() ?: "Mobile/Web"
        val beName = blueprint.techStack.backend.name.split(" ").firstOrNull() ?: "Cloud"
        val topFeature = blueprint.features.firstOrNull()?.name ?: "core workflows"
        val problem = blueprint.ideaUnderstanding.primaryProblem.ifBlank { "core operational challenges" }

        return "${blueprint.name} delivers an enterprise-grade ${blueprint.category.lowercase()} platform built with $feName and $beName. " +
                "It addresses $problem by enabling $topFeature with end-to-end data security."
    }
}
