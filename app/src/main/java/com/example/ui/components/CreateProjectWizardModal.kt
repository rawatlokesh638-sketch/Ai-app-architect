package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectWizardModal(
    onDismiss: () -> Unit,
    onGenerate: (rawIdea: String, platform: String, expLevel: String, businessModel: String, mode: String) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }

    var projectName by remember { mutableStateOf("") }
    var ideaDescription by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf("Web & Mobile (Cross-platform)") }
    var selectedExpLevel by remember { mutableStateOf("Intermediate") }
    var selectedBusinessModel by remember { mutableStateOf("Subscription (SaaS)") }
    var selectedMode by remember { mutableStateOf("DEEP") }

    val platforms = listOf("Web App", "Android", "iOS", "Web & Mobile (Cross-platform)", "Desktop", "Backend / Microservices", "SaaS")
    val expLevels = listOf("Beginner", "Intermediate", "Advanced", "Enterprise Expert")
    val businessModels = listOf("Free", "Freemium", "Subscription (SaaS)", "One-time Payment", "Marketplace Fee", "Ad-supported", "Other")
    val modes = listOf(
        "QUICK" to "Basic summary + core tech (10 secs)",
        "STANDARD" to "Moderate detail + diagrams (45 secs)",
        "DEEP" to "Full production spec (3 mins, default)"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header & Step Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "New Architecture Wizard",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Step $step of 7",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanAccent
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { step / 7f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = IndigoPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (step) {
                    1 -> {
                        Text("1. Name Your Application (Optional)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Provide a working title or leave blank for AI auto-naming.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = projectName,
                            onValueChange = { projectName = it },
                            placeholder = { Text("e.g. CloudPulse, FitnessTracker") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    2 -> {
                        Text("2. Describe Your Product Idea", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("What problem does this app solve? Who are the target users?", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = ideaDescription,
                            onValueChange = { ideaDescription = it },
                            placeholder = { Text("Build an on-demand delivery app connecting local bakeries with customers...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                    }

                    3 -> {
                        Text("3. Target Platform Architecture", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            platforms.forEach { platform ->
                                FilterChip(
                                    selected = selectedPlatform == platform,
                                    onClick = { selectedPlatform = platform },
                                    label = { Text(platform) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    4 -> {
                        Text("4. Target Technical Experience Level", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            expLevels.forEach { exp ->
                                FilterChip(
                                    selected = selectedExpLevel == exp,
                                    onClick = { selectedExpLevel = exp },
                                    label = { Text(exp) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    5 -> {
                        Text("5. Business & Monetization Model", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            businessModels.forEach { model ->
                                FilterChip(
                                    selected = selectedBusinessModel == model,
                                    onClick = { selectedBusinessModel = model },
                                    label = { Text(model) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    6 -> {
                        Text("6. Generation Mode (Depth)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            modes.forEach { (mode, desc) ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable { selectedMode = mode },
                                    color = if (selectedMode == mode) IndigoPrimary.copy(alpha = 0.1f) else Color.Transparent,
                                    border = BorderStroke(1.dp, if (selectedMode == mode) IndigoPrimary else MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(selected = selectedMode == mode, onClick = { selectedMode = mode })
                                            Text(mode, fontWeight = FontWeight.Bold)
                                        }
                                        Text(desc, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 32.dp))
                                    }
                                }
                            }
                        }
                    }

                    7 -> {
                        Text("7. AI Architectural Inference Summary", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = IndigoPrimary.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("AI Inferred Blueprint Specifications:", style = MaterialTheme.typography.labelSmall, color = CyanAccent, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("• Platform: $selectedPlatform", style = MaterialTheme.typography.bodySmall)
                                Text("• Target Audience: $selectedExpLevel developers", style = MaterialTheme.typography.bodySmall)
                                Text("• Monetization: $selectedBusinessModel", style = MaterialTheme.typography.bodySmall)
                                Text("• Generation Mode: $selectedMode", style = MaterialTheme.typography.bodySmall)
                                Text("• Stack Assumptions: PostgreSQL + Node.js/Kotlin + REST APIs + JWT Auth", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Navigation Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (step > 1) {
                        OutlinedButton(onClick = { step-- }) {
                            Text("Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (step < 7) {
                        Button(
                            onClick = { step++ },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                        ) {
                            Text("Next Step")
                        }
                    } else {
                        Button(
                            onClick = {
                                val fullIdea = if (projectName.isNotBlank()) {
                                    "Project Name: $projectName. $ideaDescription"
                                } else ideaDescription
                                onGenerate(fullIdea, selectedPlatform, selectedExpLevel, selectedBusinessModel, selectedMode)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate Architecture", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
