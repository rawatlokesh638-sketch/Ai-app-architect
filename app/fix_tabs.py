
import sys

def replace_function(content, function_name, new_content):
    lines = content.split('\n')
    start_line = -1
    for i, line in enumerate(lines):
        if f"fun {function_name}" in line:
            start_line = i
            break
    
    if start_line == -1:
        return content

    brace_count = 0
    end_line = -1
    for i in range(start_line, len(lines)):
        brace_count += lines[i].count('{')
        brace_count -= lines[i].count('}')
        if brace_count == 0 and '{' in ''.join(lines[start_line:i+1]):
            end_line = i
            break
            
    if end_line == -1:
        return content
        
    new_content_lines = lines[:start_line] + [new_content] + lines[end_line + 1:]
    return '\n'.join(new_content_lines)

file_path = 'app/src/main/java/com/example/ui/screens/ProjectWorkspaceScreen.kt'
with open(file_path, 'r') as f:
    content = f.read()

new_features = """@Composable
fun FeaturesTabContent(features: List<FeatureItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        items(features) { feature ->
            SectionCard(title = "LOGIC MODULE: ${feature.name}", icon = Icons.Default.Extension) {
                Text(feature.purpose, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = CyanAccent)
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("STRATEGIC WORKFLOW", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = IndigoLight)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(feature.workflow, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = IndigoPrimary.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.2f))
                ) {
                    Text(
                        "PRIORITY: ${feature.priority.uppercase()}", 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = IndigoLight
                    )
                }
            }
        }
    }
}"""

new_architecture = """@Composable
fun ArchitectureTabContent(arch: SystemArchitecture) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            SectionCard(title = "CORE SYSTEM ARCHITECTURE", icon = Icons.Default.AccountTree) {
                Text(arch.overview, style = MaterialTheme.typography.bodyLarge, lineHeight = 26.sp, fontWeight = FontWeight.Medium)
            }
        }
        items(arch.layers) { layer ->
            SectionCard(title = "LAYER: ${layer.name.uppercase()}", icon = Icons.Default.Layers) {
                Text(layer.description, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = CyanAccent)
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                    layer.components.forEach { 
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                        ) {
                            Text(it, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                        }
                    }
                }
            }
        }
    }
}"""

new_screens = """@Composable
fun ScreensTabContent(ux: UxArchitecture) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        items(ux.screens) { screen ->
            SectionCard(title = "SCREEN: ${screen.name.uppercase()}", icon = Icons.Default.Dashboard) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("ROUTE ID:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(screen.route, style = MaterialTheme.typography.labelSmall, color = CyanAccent, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(screen.description, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(20.dp))
                
                Text("UI COMPONENTS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = IndigoLight)
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    screen.components.forEach { 
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Adjust, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(it, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}"""

content = replace_function(content, 'FeaturesTabContent', new_features)
content = replace_function(content, 'ArchitectureTabContent', new_architecture)
content = replace_function(content, 'ScreensTabContent', new_screens)

with open(file_path, 'w') as f:
    f.write(content)
print("Successfully upgraded all tabs")
