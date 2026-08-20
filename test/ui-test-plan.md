# UI Test Plan

## Configuration

- Main class: `Pathfinder`
- Java version: 25

## Greeting and immediate exit

**Aim:** Verify that Pathfinder greets the user and exits when the first command is `bye`.

**Input:**
```text
bye
```

**Expected output:**
```text
____________________________________________________________
/================\
|   Pathfinder   |
\================/
Hello friend! My name is Pathfinder.
What tasks can I do for you today?
____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```
