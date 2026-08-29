import re

with open("app/src/main/java/com/example/ui/screens/ProjectWorkspaceScreen.kt", "r") as f:
    content = f.read()

# Replace exactly
old_code = """fun CodeLabTabContent(
    p: ProjectBlueprint,
    onGenerateCode: (String) -> Unit,
    onSaveCode: (String, String) -> Unit
) {
    var selectedFile by remember { mutableStateOf<FileSpecification?>(null) }"""

new_code = """fun CodeLabTabContent(
    p: ProjectBlueprint,
    initialSelectedFile: FileSpecification?,
    onFileSelected: (FileSpecification?) -> Unit,
    onGenerateCode: (String) -> Unit,
    onSaveCode: (String, String) -> Unit
) {
    var selectedFile by remember(initialSelectedFile) { mutableStateOf(initialSelectedFile) }"""

if old_code in content:
    content = content.replace(old_code, new_code)
else:
    print("FAILED TO MATCH")
    # try regex
    regex = r"fun CodeLabTabContent\(\s*p: ProjectBlueprint,\s*onGenerateCode: \(String\) -> Unit,\s*onSaveCode: \(String, String\) -> Unit\s*\)\s*\{\s*var selectedFile by remember \{ mutableStateOf<FileSpecification\?>\(null\) \}"
    content = re.sub(regex, new_code, content)

with open("app/src/main/java/com/example/ui/screens/ProjectWorkspaceScreen.kt", "w") as f:
    f.write(content)
