# Pathfinder User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Prioritizing tasks

Use `priority TASK_NUMBER LEVEL` to assign a priority to an existing task.
The supported levels are `high`, `medium`, `low`, and `none`. Level names
are case-insensitive.

For example, after adding a task, give it high priority:

```text
todo read book
priority 1 high
```

Pathfinder responds to the priority command with:

```text
Alrighty friend! This task now has HIGH priority:
[T][ ][HIGH] read book
```

Change the level by running the command again with `medium` or `low`.
Remove the priority using:

```text
priority 1 none
```

Tasks without a priority have no extra marker. Priorities remain attached
when tasks are marked or unmarked, and they do not change task order or
task numbers. The `find` command continues to search descriptions only.

If the command does not contain exactly a task number and a level, Pathfinder
responds with `Oopsies! Use priority TASK_NUMBER LEVEL.` If the level is not
one of the four supported names, it responds with
`Oopsies! Priority must be high, medium, low, or none.` Existing saved tasks
without a priority remain usable and are treated as having no priority.

## Feature ABC

// Feature details


## Feature XYZ

// Feature details
