# SE-EDU Java coding rules

Source: [Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html)

Use these rules for Java production and test code. For a topic not covered here, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html), as directed by the SE-EDU standard.

## Naming

- Use lowercase package names. For a school project, start with the project or group name and add logical package names.
- Name classes and enums with English nouns in PascalCase.
- Name variables in camelCase and methods with English verbs in camelCase.
- Name constants in SCREAMING_SNAKE_CASE.
- In test names, underscores may separate `featureUnderTest_testScenario_expectedBehavior`; omit later parts when unnecessary.
- Treat acronyms as words inside names, such as `exportHtmlSource`, not `exportHTMLSource`.
- Give large-scope variables descriptive names. Short scratch names such as `i` are acceptable only in a very small scope; reserve `j` and later letters for nested loops.
- Name booleans so they read as booleans, preferably with prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections.
- Give associated constants a common prefix when it helps show their relationship.

## Layout

- Indent with four spaces and never tabs.
- Keep lines below the 120-character hard limit and preferably below 110 characters.
- Indent wrapped lines eight spaces beyond the parent line. Break after a comma and before an operator, dot, or operator-like symbol when practical.
- Keep a method or constructor name attached to its opening parenthesis. Prefer high-level expression breaks that preserve readability.
- Use K&R braces: place an opening brace at the end of the declaration or control-statement line and the closing brace on its own line.
- Format methods, conditionals, loops, switches, and try/catch/finally blocks consistently with K&R style.
- Mark intentional traditional-switch fallthrough with `// Fallthrough`. Arrow-style switch cases do not need this comment.
- Put spaces around operators, after Java keywords, after commas, and after semicolons in `for` clauses. Surround ternary colons with spaces.
- Separate logical units within a block with one blank line.

## Packages, imports, types, and variables

- Put every class in a package.
- List imports explicitly; never use wildcard imports. Keep ordering consistent and remove unused imports.
- Attach array brackets to the type, such as `int[] values`.
- Initialize variables where declared when a valid initial value is available, and declare them in the smallest practical scope.
- Keep class variables non-public unless they are constants or belong to a behavior-free data class.

## Loops and conditionals

- Put the body on separate lines and always surround it with braces, even when it contains one statement.
- Apply the same brace rule to every `if`, `else`, `for`, `while`, and `do-while` body.

## Comments and JavaDoc

- Write comments in English, use American spelling, and avoid local slang.
- Write descriptive JavaDoc header comments for all classes and public methods. The SE-EDU standard permits omission for straightforward getters/setters, overrides whose inherited documentation applies exactly, and test code; document them when project requirements ask for broader coverage.
- Start `/**` on its own line for normal JavaDoc blocks. A single-line JavaDoc is acceptable for a class member.
- Make the first sentence a short summary that begins with a third-person verb such as `Returns`, `Adds`, or `Sends`.
- Align each `*`, include one space after it, and leave a blank JavaDoc line before block tags.
- End every `@param`, `@return`, and `@throws` description with punctuation.
- Include `@param` for either all parameters or none. Omit them only when every parameter is self-explanatory or already fully explained in the main description.
- Use `{@inheritDoc}` for an override when inherited documentation applies but needs an addition.
- Do not insert a blank line between a JavaDoc block and the declaration it documents.
- Indent comments to match the code they describe.

## Review checklist

- Search all Java files in scope, including tests.
- Check for lines over 120 characters and tab characters.
- Review declarations for naming, visibility, collection plurals, and boolean phrasing.
- Review every control structure for braces and layout.
- Review imports for explicitness, grouping, ordering, and necessity.
- Review public APIs for required JavaDoc and validate JavaDoc syntax with the build when supported.
