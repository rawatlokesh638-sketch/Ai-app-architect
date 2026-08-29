package com.example.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.json.BlueprintJsonAdapter
import com.example.data.model.ProjectBlueprint
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary
import com.example.util.PdfExporter

@Composable
fun ExportDialog(
    blueprint: ProjectBlueprint,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Export Project Blueprint",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Download or copy the implementation-ready specification in your preferred format:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Option 1: PDF Document
                ExportOptionCard(
                    title = "Export Formatted PDF Document",
                    subtitle = "Paginated executive document with tech stack, feature matrix, DB schema, and API specs.",
                    icon = Icons.Default.PictureAsPdf,
                    badge = "Formatted PDF",
                    onClick = {
                        PdfExporter.exportToPdf(context, blueprint)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Option 2: One-Shot Master Prompt
                ExportOptionCard(
                    title = "Copy One-Shot Master Prompt",
                    subtitle = "Optimized for AI Studio, Claude, Cursor, Replit with complete architecture context.",
                    icon = Icons.Default.AutoAwesome,
                    badge = "Recommended",
                    onClick = {
                        clipboardManager.setText(AnnotatedString(blueprint.masterCodingPrompt))
                        Toast.makeText(context, "Master Coding Prompt copied!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Option 3: Share / Download Markdown
                ExportOptionCard(
                    title = "Export Full Markdown Document",
                    subtitle = "Complete specification with feature matrices, schemas, and API docs.",
                    icon = Icons.Default.Description,
                    onClick = {
                        val md = generateMarkdownSpec(blueprint)
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, md)
                            putExtra(Intent.EXTRA_TITLE, "${blueprint.name} - Architecture Spec")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Export Architecture Markdown"))
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Option 4: Export Structured JSON
                ExportOptionCard(
                    title = "Export Structured JSON",
                    subtitle = "Machine-readable schema compatible with CI/CD tools and code generators.",
                    icon = Icons.Default.DataObject,
                    onClick = {
                        val json = BlueprintJsonAdapter.toJson(blueprint)
                        clipboardManager.setText(AnnotatedString(json))
                        Toast.makeText(context, "Full Blueprint JSON copied!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
fun ExportOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("export_${title.replace(" ", "_")}"),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(IndigoPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = IndigoLight, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    if (badge != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Badge(containerColor = CyanAccent.copy(alpha = 0.2f), contentColor = CyanAccent) {
                            Text(badge, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                }
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}

fun generateMarkdownSpec(b: ProjectBlueprint): String {
    val execSummarySection = if (b.executiveSummary.isNotBlank()) {
        "## 0. LLM Executive Summary\n${b.executiveSummary}\n\n"
    } else ""

    return """
# ${b.name} — Architecture Blueprint
**Tagline**: ${b.tagline}
**Category**: ${b.category} | **Version**: ${b.version} | **Health Score**: ${b.healthScore}/100

$execSummarySection## 1. Product Overview
- **Product Type**: ${b.ideaUnderstanding.productType}
- **Primary Problem**: ${b.ideaUnderstanding.primaryProblem}
- **Proposed Solution**: ${b.ideaUnderstanding.proposedSolution}
- **Business Model**: ${b.ideaUnderstanding.businessModel}

## 2. Technology Stack
- Frontend: ${b.techStack.frontend.name}
- Backend: ${b.techStack.backend.name}
- Database: ${b.techStack.database.name}
- Authentication: ${b.techStack.authentication.name}
- Storage: ${b.techStack.storage.name}
- AI Models: ${b.techStack.aiModels.name}

## 3. Features
${b.features.joinToString("\n") { "- [${it.priority}] ${it.name}: ${it.purpose}" }}

## 4. Screens & Routes
${b.uxArchitecture.screens.joinToString("\n") { "- ${it.name} (`${it.route}`): ${it.description}" }}

## 5. Security Plan
- ${b.securityPlan.authenticationStrategy}
- ${b.securityPlan.apiKeyProtection}

---
*Generated by AI App Architect*
""".trimIndent()
}
