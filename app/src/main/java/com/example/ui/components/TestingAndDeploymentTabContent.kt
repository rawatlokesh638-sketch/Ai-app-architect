package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectBlueprint
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary

@Composable
fun TestingAndDeploymentTabContent(
    blueprint: ProjectBlueprint,
    modifier: Modifier = Modifier
) {
    val testing = blueprint.testingChecklist
    val deploy = blueprint.deploymentPlan

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. TESTING ARCHITECT
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = CyanAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Testing Architecture & QA Checklist",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Unit Tests:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    testing.unitTestCases.forEach { tc ->
                        Text("• $tc", style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Integration Tests:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    testing.integrationTestCases.forEach { tc ->
                        Text("• $tc", style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Security & Pen Test Fuzzing:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    testing.securityTestCases.forEach { tc ->
                        Text("• $tc", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // 2. DEPLOYMENT ARCHITECT
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = IndigoPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Production Deployment Blueprint",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Target Infrastructure: ${deploy.primaryPlatform}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = CyanAccent)

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Build Command:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    CodeBlockView(code = deploy.buildCommands)

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Production Environment Variables:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    CodeBlockView(code = deploy.envVariables.joinToString("\n"))

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Rollback Strategy:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text(deploy.rollbackStrategy, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
