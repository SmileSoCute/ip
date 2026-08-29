---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU basic and intermediate Java coding standard in this project. Use whenever creating, editing, refactoring, reviewing, or testing Java source code in the repository.
---

# SE-EDU Java Coding Standard

Keep all Java production and test code consistent with the SE-EDU basic and intermediate coding standard.

Before changing or reviewing Java code, read [references/rules.md](references/rules.md). Treat the linked SE-EDU guide as authoritative. Use the Google Java Style Guide only for topics the SE-EDU guide does not cover.

## Workflow

1. Inspect the relevant Java files and nearby code before editing.
2. Preserve behavior unless the user also requested a functional change.
3. Apply the rules to new code and correct violations in touched code. For a coding-standard audit, inspect all Java files in scope.
4. Prefer clear manual fixes over broad formatter output that would obscure the meaningful diff.
5. Check imports, names, line lengths, indentation, braces, variable scope, and JavaDoc after editing.
6. Run the repository's required Java verification and investigate failures before completion.

When a rule requires judgment, optimize for readability and consistency with nearby compliant code. Report any deliberate exception and its reason.
