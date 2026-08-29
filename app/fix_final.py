
import sys

file_path = 'app/src/main/java/com/example/ui/screens/ProjectWorkspaceScreen.kt'
with open(file_path, 'r') as f:
    lines = f.readlines()

new_lines = []
skip_next = False
for i, line in enumerate(lines):
    # Remove double @Composable
    if i < len(lines) - 1 and line.strip() == "@Composable" and lines[i+1].strip() == "@Composable":
        continue
    
    # Fix masterPrompt -> masterCodingPrompt
    if "p.masterPrompt" in line:
        line = line.replace("p.masterPrompt", "p.masterCodingPrompt")
    
    # Fix ScreensTabContent it issues
    if "Text(it, style = MaterialTheme.typography.bodyMedium)" in line:
        line = line.replace("Text(it, style = MaterialTheme.typography.bodyMedium)", "Text(it.name, style = MaterialTheme.typography.bodyMedium)")
        
    new_lines.append(line)

with open(file_path, 'w') as f:
    f.writelines(new_lines)
print("Successfully fixed remaining errors")
