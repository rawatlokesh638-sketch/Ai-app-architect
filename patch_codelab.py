import re

with open("app/src/main/java/com/example/ui/screens/ProjectWorkspaceScreen.kt", "r") as f:
    content = f.read()

# Update invocation
old_invocation = """                                    3 -> CodeLabTabContent(
                                        p = currentProject,
                                        onGenerateCode = { viewModel.generateFileCode(it) },
                                        onSaveCode = { path, content -> viewModel.updateFileContent(path, content) }
                                    )"""
new_invocation = """                                    3 -> CodeLabTabContent(
                                        p = currentProject,
                                        initialSelectedFile = selectedFileSpec,
                                        onFileSelected = { selectedFileSpec = it },
                                        onGenerateCode = { viewModel.generateFileCode(it) },
                                        onSaveCode = { path, content -> viewModel.updateFileContent(path, content) }
                                    )"""
content = content.replace(old_invocation, new_invocation)

# Update definition
old_def = """fun CodeLabTabContent(
    p: ProjectBlueprint,
    onGenerateCode: (String) -> Unit,
    onSaveCode: (String, String) -> Unit
) {
    var selectedFile by remember { mutableStateOf<FileSpecification?>(null) }"""
new_def = """fun CodeLabTabContent(
    p: ProjectBlueprint,
    initialSelectedFile: FileSpecification?,
    onFileSelected: (FileSpecification?) -> Unit,
    onGenerateCode: (String) -> Unit,
    onSaveCode: (String, String) -> Unit
) {
    var selectedFile by remember(initialSelectedFile) { mutableStateOf(initialSelectedFile) }"""
content = content.replace(old_def, new_def)

# Find where selectedFile is set inside CodeLabTabContent and also call onFileSelected
# Specifically in the DirectoryTreeView inside CodeLabTabContent
# Wait, let's see how DirectoryTreeView is called inside CodeLabTabContent.
