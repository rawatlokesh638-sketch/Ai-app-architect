
import sys

file_path = 'app/src/main/java/com/example/ui/screens/ProjectWorkspaceScreen.kt'
with open(file_path, 'r') as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    # Fix costComplexity
    if "Text(p.costComplexity," in line:
        line = line.replace("Text(p.costComplexity,", "Text(p.costComplexity.complexityLevel,")
    
    # Fix it.name in DatabaseTabContent (it's a List<String>)
    if "Text(it.name, style = MaterialTheme.typography.bodyMedium)" in line and i > 800 and i < 850:
        line = line.replace("Text(it.name, style = MaterialTheme.typography.bodyMedium)", "Text(it, style = MaterialTheme.typography.bodyMedium)")
    
    # Fix API Endpoint parameters/responses if they are still there
    if "endpoint.parameters" in line:
        line = line.replace("endpoint.parameters", "endpoint.requestBody")
    if "endpoint.responses" in line:
        line = line.replace("endpoint.responses", "endpoint.responseBody")
        
    new_lines.append(line)

with open(file_path, 'w') as f:
    f.writelines(new_lines)
print("Successfully aligned workspace with blueprint model")
