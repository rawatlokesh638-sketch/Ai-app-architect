import re

with open("app/src/main/java/com/example/ui/screens/ProjectWorkspaceScreen.kt", "r") as f:
    content = f.read()

old_click = """                    onFileClick = { path ->
                        selectedFile = p.fileSpecifications.find { it.filePath == path }
                    }"""
new_click = """                    onFileClick = { path ->
                        val found = p.fileSpecifications.find { it.filePath == path }
                        selectedFile = found
                        onFileSelected(found)
                    }"""
content = content.replace(old_click, new_click)

with open("app/src/main/java/com/example/ui/screens/ProjectWorkspaceScreen.kt", "w") as f:
    f.write(content)
