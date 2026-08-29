---
name: seedu-git-standard
description: Apply and review the SE-EDU Git conventions for commit messages and branch names in this project. Use whenever proposing, creating, or amending a commit, or when naming a Git branch.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) for every commit message and branch name in this repository.

This skill does not authorize a commit, amend, push, branch creation, or other Git mutation. Obtain the authorization required by the repository instructions before performing one.

## Commit subjects

- Write a meaningful subject for every commit.
- Prefer at most 50 characters and never exceed 72 characters.
- Use the imperative mood: `Add tests`, not `Added tests` or `Adding tests`.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- Add an optional `<scope>:` or `<category>:` prefix only when it clarifies the change, for example `Parser: Handle empty input`.

## Commit bodies

Add a body for every non-trivial commit.

- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters.
- Use blank lines between paragraphs and bullets where they improve readability.
- Explain what changed and why; leave implementation mechanics to the diff.
- Describe the pre-change situation in the present tense and describe the change in the imperative mood.
- Avoid redundant qualifiers such as `currently` and `originally`.
- Avoid repeating details already clear from code comments.
- Split the work into finer-grained commits if a clear body becomes excessively long.

A useful body order is: existing situation, reason for change, change being made, rationale for that approach, and any other relevant context. Omit sections that add no value.

## Branch names

- Use meaningful keywords in kebab case, such as `refactor-ui-tests`.
- For an issue-specific branch, prefer `issueNumber-keywords-from-title`, such as `1234-ui-freeze-error`.

## Before and after a commit

1. Inspect the status and diff so the commit contains one coherent change and preserves unrelated user work.
2. Draft the subject and, when needed, the body using the rules above.
3. Check subject and body line lengths before committing.
4. After committing, inspect the resulting commit message and report the commit hash and subject.
