package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DirectoryNode
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary

@Composable
fun DirectoryTreeView(
    rootNode: DirectoryNode,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    tint = IndigoLight,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Project Directory Structure",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = {
                    val fullTreeString = formatTreeToString(rootNode, 0)
                    clipboardManager.setText(AnnotatedString(fullTreeString))
                    Toast.makeText(context, "Directory tree copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("copy_tree_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Directory Tree",
                    tint = CyanAccent
                )
            }
        }

        Text(
            text = "Stack-adapted folder tree. Tap any folder to expand or collapse sub-directories.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Render Directory Tree Nodes
        DirectoryNodeItem(
            node = rootNode,
            depth = 0,
            initiallyExpanded = true
        )
    }
}

@Composable
fun DirectoryNodeItem(
    node: DirectoryNode,
    depth: Int,
    initiallyExpanded: Boolean = true
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    val isFolder = node.type == "folder" || node.children.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (depth * 14).dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .clickable(enabled = isFolder) { isExpanded = !isExpanded }
                .padding(vertical = 4.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isFolder) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.FolderOpen else Icons.Default.Folder,
                    contentDescription = null,
                    tint = if (isExpanded) CyanAccent else IndigoLight,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                val fileIcon = when {
                    node.name.endsWith(".kt") || node.name.endsWith(".ts") || node.name.endsWith(".js") -> Icons.Default.Code
                    node.name.endsWith(".json") || node.name.endsWith(".xml") -> Icons.Default.DataObject
                    node.name.endsWith(".md") || node.name.endsWith(".txt") -> Icons.Default.Description
                    else -> Icons.Default.InsertDriveFile
                }
                Icon(
                    imageVector = fileIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = node.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                fontWeight = if (isFolder) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isFolder) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (node.description.isNotBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "— ${node.description}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }
        }

        if (isFolder) {
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    node.children.forEach { child ->
                        DirectoryNodeItem(
                            node = child,
                            depth = depth + 1,
                            initiallyExpanded = depth < 2
                        )
                    }
                }
            }
        }
    }
}

private fun formatTreeToString(node: DirectoryNode, depth: Int): String {
    val indent = "  ".repeat(depth)
    val sb = StringBuilder()
    sb.append("$indent${node.name}${if (node.type == "folder") "/" else ""}\n")
    node.children.forEach { child ->
        sb.append(formatTreeToString(child, depth + 1))
    }
    return sb.toString()
}
