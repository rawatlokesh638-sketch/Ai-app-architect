package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DirectoryNode
import com.example.data.model.FileSpecification

@Composable
fun FileTreeView(
    rootNode: DirectoryNode,
    fileSpecs: List<FileSpecification>,
    onFileClick: (FileSpecification?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        item {
            FolderNodeView(
                node = rootNode,
                level = 0,
                fileSpecs = fileSpecs,
                onFileClick = onFileClick
            )
        }
    }
}

@Composable
fun FolderNodeView(
    node: DirectoryNode,
    level: Int,
    fileSpecs: List<FileSpecification>,
    onFileClick: (FileSpecification?) -> Unit
) {
    var isExpanded by remember { mutableStateOf(level == 0) }
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 90f else 0f)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 4.dp, horizontal = (level * 16 + 8).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .rotate(rotation),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = com.example.ui.theme.IndigoLight
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = node.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                node.children.forEach { child ->
                    if (child.type == "folder") {
                        FolderNodeView(
                            node = child,
                            level = level + 1,
                            fileSpecs = fileSpecs,
                            onFileClick = onFileClick
                        )
                    } else {
                        FileNodeView(
                            node = child,
                            level = level + 1,
                            spec = fileSpecs.find { it.filePath.endsWith(child.name) },
                            onFileClick = onFileClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FileNodeView(
    node: DirectoryNode,
    level: Int,
    spec: FileSpecification?,
    onFileClick: (FileSpecification?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFileClick(spec) }
            .padding(vertical = 4.dp, horizontal = (level * 16 + 24).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = com.example.ui.theme.CyanAccent
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = node.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (node.description.isNotBlank()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "- ${node.description}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                maxLines = 1,
                fontSize = 10.sp
            )
        }
    }
}
