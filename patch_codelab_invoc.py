import re

with open("app/src/main/java/com/example/ui/screens/ProjectWorkspaceScreen.kt", "r") as f:
    content = f.read()

regex = r"3 -> CodeLabTabContent\(\s*p = currentProject,\s*onGenerateCode = \{ viewModel.generateFileCode\(it\) \},\s*onSaveCode = \{ path, content -> viewModel.updateFileContent\(path, content\) \}\s*\)"

new_invoc = """3 -> CodeLabTabContent(
                                        p = currentProject,
                                        initialSelectedFile = selectedFileSpec,
                                        onFileSelected = { selectedFileSpec = it },
                                        onGenerateCode = { viewModel.generateFileCode(it) },
                                        onSaveCode = { path, content -> viewModel.updateFileContent(path, content) }
                                    )"""

content = re.sub(regex, new_invoc, content)

with open("app/src/main/java/com/example/ui/screens/ProjectWorkspaceScreen.kt", "w") as f:
    f.write(content)
