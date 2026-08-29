
import sys

def replace_function(file_path, function_name, new_content):
    with open(file_path, 'r') as f:
        lines = f.readlines()
    
    start_line = -1
    for i, line in enumerate(lines):
        if f"fun {function_name}" in line:
            start_line = i
            break
    
    if start_line == -1:
        print(f"Function {function_name} not found")
        return

    # Find the end of the function (counting braces)
    brace_count = 0
    end_line = -1
    for i in range(start_line, len(lines)):
        brace_count += lines[i].count('{')
        brace_count -= lines[i].count('}')
        if brace_count == 0 and '{' in ''.join(lines[start_line:i+1]):
            end_line = i
            break
            
    if end_line == -1:
        print(f"End of function {function_name} not found")
        return
        
    new_lines = lines[:start_line] + [new_content + "\n"] + lines[end_line + 1:]
    
    with open(file_path, 'w') as f:
        f.writelines(new_lines)
    print(f"Successfully replaced {function_name}")

new_codelab = """@Composable
fun CodeLabTabContent(
    p: ProjectBlueprint,
    onGenerateCode: (String) -> Unit,
    onSaveCode: (String, String) -> Unit
) {
    var selectedFile by remember { mutableStateOf<FileSpecification?>(null) }
    var editedCode by remember { mutableStateOf("") }

    LaunchedEffect(selectedFile) {
        if (selectedFile != null && selectedFile!!.content.isBlank()) {
            editedCode = generateSmartBoilerplate(selectedFile!!.filePath, p.name)
        } else {
            editedCode = selectedFile?.content ?: ""
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar: File Explorer
        Surface(
            modifier = Modifier.width(260.dp).fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ARCHITECTURAL SOURCE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = CyanAccent, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(16.dp))
                DirectoryTreeView(
                    node = p.directoryTree,
                    selectedFile = selectedFile?.filePath,
                    onFileClick = { path ->
                        selectedFile = p.fileSpecifications.find { it.filePath == path }
                    }
                )
            }
        }

        // Center: Code Editor
        Column(modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp)) {
            if (selectedFile != null) {
                val file = selectedFile!!
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(file.filePath.substringAfterLast("/"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                        Text(file.filePath, style = MaterialTheme.typography.labelSmall, color = CyanAccent, fontFamily = FontFamily.Monospace)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onGenerateCode(file.filePath) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI BUILD")
                        }
                        Button(
                            onClick = { onSaveCode(file.filePath, editedCode) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                        ) {
                            Text("COMMIT")
                        }
                    }
                }
                
                Surface(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E1E1E),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    if (editedCode.isBlank()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Terminal, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Awaiting AI Build for this module", color = Color.Gray)
                            Button(onClick = { onGenerateCode(file.filePath) }, modifier = Modifier.padding(top = 12.dp)) {
                                Text("Generate Boilerplate")
                            }
                        }
                    } else {
                        CodeEditorView(
                            code = editedCode,
                            onCodeChange = { editedCode = it },
                            language = file.filePath.substringAfterLast(".", "kt")
                        )
                    }
                }
            } else {
                // Empty state: Show Master Prompt
                SectionCard(title = "Master Architectural Directive", icon = Icons.Default.PrecisionManufacturing) {
                    Text(
                        "No file selected. Reviewing high-level project DNA...",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanAccent
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E1E1E),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                            Text(
                                p.masterPrompt,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 18.sp,
                                    color = Color(0xFFD4D4D4)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}"""

replace_function('app/src/main/java/com/example/ui/screens/ProjectWorkspaceScreen.kt', 'CodeLabTabContent', new_codelab)
