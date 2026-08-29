---
name: test-ui
description: Run the Pathfinder console UI against command-and-output test cases in test/ui-test-plan.md. Use when asked to test or verify console interactions.
---

# Test UI

Run the project console program for every case recorded in `test/ui-test-plan.md`, comparing its output exactly with the expected output.

## Test plan

Read `test/ui-test-plan.md` before testing. Each case must use this structure:

````markdown
## <unique test-case name>

**Aim:** <what the test verifies>

**Initial data:**
```text
<optional contents of data/pathfinder.txt before the test>
```

**Input:**
```text
<one command per line>
```

**Expected output:**
```text
<complete program output, excluding user-entered commands>
```

**Expected data:**
```text
<optional contents of data/pathfinder.txt after the test>
```
````

`Initial data` and `Expected data` are optional. Use them for persistence tests. Each test runs in an isolated temporary directory, so one test's saved tasks do not affect another test.

## Run tests

Run `scripts/run_ui_tests.py` from the repository root using Python 3. The runner compiles all `src/main/java` files with Java 25, executes cases in plan order, and writes `test/ui-test-session.md`.

Afterward, show the console-input and console-output transcript in `test/ui-test-session.md`. The runner terminates on the first failed case and records both expected and actual outputs. Do not change the test plan or program merely to make a failing test pass.
