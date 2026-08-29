package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectBlueprint
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary

@Composable
fun DocsAndExportTabContent(
    blueprint: ProjectBlueprint,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var activeDocTab by remember { mutableIntStateOf(0) } // 0: README.md, 1: Setup Guide, 2: API Spec, 3: DB Schema

    val docText = remember(activeDocTab, blueprint) {
        when (activeDocTab) {
            0 -> generateReadme(blueprint)
            1 -> generateSetupGuide(blueprint)
            2 -> generateApiDoc(blueprint)
            else -> generateDbDoc(blueprint)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = CyanAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Documentation & Specification Generator",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(docText))
                                Toast.makeText(context, "Documentation copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Doc", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        FilterChip(
                            selected = activeDocTab == 0,
                            onClick = { activeDocTab = 0 },
                            label = { Text("README.md") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(
                            selected = activeDocTab == 1,
                            onClick = { activeDocTab = 1 },
                            label = { Text("Setup Guide") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(
                            selected = activeDocTab == 2,
                            onClick = { activeDocTab = 2 },
                            label = { Text("API Spec") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CodeBlockView(code = docText)
                }
            }
        }
    }
}

private fun generateReadme(b: ProjectBlueprint): String {
    return """
# ${b.name}

> ${b.tagline}

## 🚀 Product Overview
- **Category**: ${b.category}
- **Primary Problem**: ${b.ideaUnderstanding.primaryProblem}
- **Proposed Solution**: ${b.ideaUnderstanding.proposedSolution}

## 🛠️ Stack & Architecture
- **Frontend**: ${b.techStack.frontend.name}
- **Backend**: ${b.techStack.backend.name}
- **Database**: ${b.techStack.database.name}
- **Authentication**: ${b.techStack.authentication.name}
""".trimIndent()
}

private fun generateSetupGuide(b: ProjectBlueprint): String {
    return """
# Developer Setup Guide for ${b.name}

## 1. Prerequisites
- Node.js >= 18.x / JDK 17
- PostgreSQL 15+ / Docker Desktop
- Git CLI

## 2. Local Environment Setup
```bash
git clone https://github.com/organization/${b.name.lowercase().replace(" ", "-")}.git
cd ${b.name.lowercase().replace(" ", "-")}
cp .env.example .env
npm install
npm run dev
```
""".trimIndent()
}

private fun generateApiDoc(b: ProjectBlueprint): String {
    return b.apiDesign.joinToString("\n\n") { ep ->
        "### ${ep.method} ${ep.path}\n- **Purpose**: ${ep.purpose}\n- **Auth**: ${ep.authentication}\n- **Request**: `${ep.requestBody}`\n- **Response**: `${ep.responseBody}`"
    }
}

private fun generateDbDoc(b: ProjectBlueprint): String {
    return b.databaseSchema.entities.joinToString("\n\n") { entity ->
        "### Table: ${entity.tableName}\n${entity.description}\nFields:\n" + entity.fields.joinToString("\n") { f -> "  - `${f.name}` (${f.type}) -> PK: ${f.isPrimaryKey}" }
    }
}
