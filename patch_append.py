import re

with open("app/src/main/java/com/example/ui/screens/ProjectWorkspaceScreen.kt", "r") as f:
    content = f.read()

# Replace the tabs list
old_tabs = '        "Master Prompt" to Icons.Default.AutoAwesome\n    )'
new_tabs = '        "Master Prompt" to Icons.Default.AutoAwesome,\n        "User Roles" to Icons.Default.Group\n    )'
content = content.replace(old_tabs, new_tabs)

# Replace the when block
old_when = '                                    16 -> MasterPromptTabContent(currentProject)\n                                }'
new_when = '                                    16 -> MasterPromptTabContent(currentProject)\n                                    17 -> UserRolesTabContent(currentProject.userRoles)\n                                }'
content = content.replace(old_when, new_when)

with open("app/src/main/java/com/example/ui/screens/ProjectWorkspaceScreen.kt", "w") as f:
    f.write(content)
