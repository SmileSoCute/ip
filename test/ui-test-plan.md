# UI Test Plan

## Configuration

- Main class: `pathfinder.Pathfinder`
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

## Load legacy saved task types and statuses

**Aim:** Verify that Pathfinder remains compatible with saved files from the previous version.

**Initial data:**
```text
[T][X] read book
[D][ ] return book (by: Dec 2 2019 6:00 PM)
[E][ ] project meeting (from: Dec 3 2019 2:00 PM to: Dec 3 2019 4:00 PM)
```

**Input:**
```text
list
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
____________________________________________________________
Here are your tasks:
1. [T][X] read book
2. [D][ ] return book (by: Dec 2 2019 6:00 PM)
3. [E][ ] project meeting (from: Dec 3 2019 2:00 PM to: Dec 3 2019 4:00 PM)
____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```

**Expected data:**
```text
[T][X] read book
[D][ ] return book (by: Dec 2 2019 6:00 PM)
[E][ ] project meeting (from: Dec 3 2019 2:00 PM to: Dec 3 2019 4:00 PM)
```

## Save a changed task list

**Aim:** Verify that adding and marking a task saves its type, status, and description.

**Input:**
```text
todo read book
mark 1
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
____________________________________________________________
Okay! I've got it friend! I've added this task:
 [T][ ] read book
Alrighty currently u have 1 task(s) in the list yay!
____________________________________________________________
____________________________________________________________
Awesome sauce! I have marked this task up dude:
[T][X] read book
____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```

**Expected data:**
```text
T | 1 | cmVhZCBib29r | NONE
```

## Invalid commands and arguments

**Aim:** Verify that malformed commands produce specific errors and do not stop the chatbot.

**Input:**
```text

todo
deadline
deadline homework /by
event meeting /to 4pm /from 2pm
mark
mark abc
mark 1
list extra
marker 1
bye now
list
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
____________________________________________________________
Oh no friend! You didn't enter anything!
____________________________________________________________
____________________________________________________________
Oopsies! A todo needs a description, friend!
____________________________________________________________
____________________________________________________________
Oopsies! A deadline needs a description, friend!
____________________________________________________________
____________________________________________________________
Oopsies! A deadline needs '/by' followed by a date or time.
____________________________________________________________
____________________________________________________________
Oopsies! An event needs '/from' before '/to'.
____________________________________________________________
____________________________________________________________
Oopsies! A mark needs a description, friend!
____________________________________________________________
____________________________________________________________
Oopsies! Please provide one positive whole task number.
____________________________________________________________
____________________________________________________________
Oopsies! That task number doesn't exist, friend!
____________________________________________________________
____________________________________________________________
Oopsies! The list command does not take extra words.
____________________________________________________________
____________________________________________________________
Oopsies! I don't understand that command.
____________________________________________________________
____________________________________________________________
Oopsies! The bye command does not take extra words.
____________________________________________________________
____________________________________________________________
Your task list is empty, friend!
____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```

## Repeated status changes and invalid deletion

**Aim:** Verify repeated mark/unmark commands and out-of-range deletion are handled safely.

**Input:**
```text
todo read book
unmark 1
mark 1
mark 1
unmark 1
delete 0
delete 2
delete 1
list
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
____________________________________________________________
Okay! I've got it friend! I've added this task:
 [T][ ] read book
Alrighty currently u have 1 task(s) in the list yay!
____________________________________________________________
____________________________________________________________
Oopsies! That task is already marked as not done.
____________________________________________________________
____________________________________________________________
Awesome sauce! I have marked this task up dude:
[T][X] read book
____________________________________________________________
____________________________________________________________
Oopsies! That task is already marked as done.
____________________________________________________________
____________________________________________________________
Alright man, I have unmarked this task for you:
[T][ ] read book
____________________________________________________________
____________________________________________________________
Oopsies! That task number doesn't exist, friend!
____________________________________________________________
____________________________________________________________
Oopsies! That task number doesn't exist, friend!
____________________________________________________________
____________________________________________________________
Got it my friend! I've removed this task:
 [T][ ] read book
 Alrighty currently you have 0 task(s) in the list yay!
____________________________________________________________
____________________________________________________________
Your task list is empty, friend!
____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```

**Expected data:**
```text

```

## Malformed saved records

**Aim:** Verify malformed saved lines are skipped while valid tasks still load.

**Initial data:**
```text
not a task
T | 2 | cmVhZCBib29r
T | 0 | cmVhZCBib29r
D | 0 | !!! | U3VuZGF5
D | 0 | cmVhZCBib29r | bm90LWEtZGF0ZQ
```

**Input:**
```text
list
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
____________________________________________________________
Heads up! I skipped 4 invalid saved task(s).
____________________________________________________________
____________________________________________________________
Here are your tasks:
1. [T][ ] read book
____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```

**Expected data:**
```text
not a task
T | 2 | cmVhZCBib29r
T | 0 | cmVhZCBib29r
D | 0 | !!! | U3VuZGF5
D | 0 | cmVhZCBib29r | bm90LWEtZGF0ZQ
```

## Parse and display dates and times

**Aim:** Verify ISO and day/month/year inputs become LocalDateTime values and display in a friendly format.

**Input:**
```text
deadline return book /by 2/12/2019 1800
deadline date only /by 2019-12-02
event meeting /from 2019-12-03 1400 /to 2019-12-03 1600
list
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
____________________________________________________________
Okay! I've got it friend! I've added this task:
 [D][ ] return book (by: Dec 2 2019 6:00 PM)
Alrighty currently u have 1 task(s) in the list yay!
____________________________________________________________
____________________________________________________________
Okay! I've got it friend! I've added this task:
 [D][ ] date only (by: Dec 2 2019 12:00 AM)
Alrighty currently u have 2 task(s) in the list yay!
____________________________________________________________
____________________________________________________________
Okay! I've got it friend! I've added this task:
 [E][ ] meeting (from: Dec 3 2019 2:00 PM to: Dec 3 2019 4:00 PM)
Alrighty currently u have 3 task(s) in the list yay!
____________________________________________________________
____________________________________________________________
Here are your tasks:
1. [D][ ] return book (by: Dec 2 2019 6:00 PM)
2. [D][ ] date only (by: Dec 2 2019 12:00 AM)
3. [E][ ] meeting (from: Dec 3 2019 2:00 PM to: Dec 3 2019 4:00 PM)
____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```

**Expected data:**
```text
D | 0 | cmV0dXJuIGJvb2s | MjAxOS0xMi0wMlQxODowMDowMA | NONE
D | 0 | ZGF0ZSBvbmx5 | MjAxOS0xMi0wMlQwMDowMDowMA | NONE
E | 0 | bWVldGluZw | MjAxOS0xMi0wM1QxNDowMDowMA | MjAxOS0xMi0wM1QxNjowMDowMA | NONE
```

## Invalid dates and event ranges

**Aim:** Verify impossible dates, unsupported formats, and reversed event ranges are rejected.

**Input:**
```text
deadline impossible /by 30/2/2019 1800
deadline wrong format /by 12-02-2019 1800
event backwards /from 2019-12-03 1600 /to 2019-12-03 1400
event invalid /from 2019-12-03 /to tomorrow
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
____________________________________________________________
Oopsies! Use yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm.
____________________________________________________________
____________________________________________________________
Oopsies! Use yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm.
____________________________________________________________
____________________________________________________________
Oopsies! An event's '/to' time must be after its '/from' time.
____________________________________________________________
____________________________________________________________
Oopsies! Use yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm.
____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```

## Save descriptions containing storage delimiters

**Aim:** Verify pipes in descriptions and date/time values cannot corrupt the saved file format.

**Input:**
```text
todo read | book
deadline return | book /by 2019-12-02 1800
event project | meeting /from 2019-12-03 1400 /to 2019-12-03 1600
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
____________________________________________________________
Okay! I've got it friend! I've added this task:
 [T][ ] read | book
Alrighty currently u have 1 task(s) in the list yay!
____________________________________________________________
____________________________________________________________
Okay! I've got it friend! I've added this task:
 [D][ ] return | book (by: Dec 2 2019 6:00 PM)
Alrighty currently u have 2 task(s) in the list yay!
____________________________________________________________
____________________________________________________________
Okay! I've got it friend! I've added this task:
 [E][ ] project | meeting (from: Dec 3 2019 2:00 PM to: Dec 3 2019 4:00 PM)
Alrighty currently u have 3 task(s) in the list yay!
____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```

**Expected data:**
```text
T | 0 | cmVhZCB8IGJvb2s | NONE
D | 0 | cmV0dXJuIHwgYm9vaw | MjAxOS0xMi0wMlQxODowMDowMA | NONE
E | 0 | cHJvamVjdCB8IG1lZXRpbmc | MjAxOS0xMi0wM1QxNDowMDowMA | MjAxOS0xMi0wM1QxNjowMDowMA | NONE
```

## Find tasks by description

**Aim:** Verify that find searches descriptions case-insensitively, numbers only matching tasks, handles no matches and missing keywords, and does not change saved data.

**Initial data:**
```text
[T][X] read book
[D][ ] Return BOOK (by: Dec 2 2019 6:00 PM)
[E][ ] team meeting (from: Dec 3 2019 2:00 PM to: Dec 3 2019 4:00 PM)
```

**Input:**
```text
find BOOK
find meeting
find missing
find
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
____________________________________________________________
Alrighty friend! Here are the matching tasks I found:
1. [T][X] read book
2. [D][ ] Return BOOK (by: Dec 2 2019 6:00 PM)
____________________________________________________________
____________________________________________________________
Alrighty friend! Here are the matching tasks I found:
1. [E][ ] team meeting (from: Dec 3 2019 2:00 PM to: Dec 3 2019 4:00 PM)
____________________________________________________________
____________________________________________________________
Oopsies! I couldn't find any tasks containing "missing".
____________________________________________________________
____________________________________________________________
Oopsies! The find command needs a keyword, friend!
____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```

**Expected data:**
```text
[T][X] read book
[D][ ] Return BOOK (by: Dec 2 2019 6:00 PM)
[E][ ] team meeting (from: Dec 3 2019 2:00 PM to: Dec 3 2019 4:00 PM)
```

## Set, change, clear, and validate task priorities

**Aim:** Verify priority validation, idempotent updates, status retention, display, and persistence.

**Input:**
```text
priority
todo read book
priority 1 urgent
priority 1 high
priority 1 HIGH
mark 1
priority 1 medium
unmark 1
priority 1 none
todo write code
priority 2 HIGH
list
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
____________________________________________________________
Oopsies! Use priority TASK_NUMBER LEVEL.
____________________________________________________________
____________________________________________________________
Okay! I've got it friend! I've added this task:
 [T][ ] read book
Alrighty currently u have 1 task(s) in the list yay!
____________________________________________________________
____________________________________________________________
Oopsies! Priority must be high, medium, low, or none.
____________________________________________________________
____________________________________________________________
Alrighty friend! This task now has HIGH priority:
[T][ ][HIGH] read book
____________________________________________________________
____________________________________________________________
Alrighty friend! This task now has HIGH priority:
[T][ ][HIGH] read book
____________________________________________________________
____________________________________________________________
Awesome sauce! I have marked this task up dude:
[T][X][HIGH] read book
____________________________________________________________
____________________________________________________________
Alrighty friend! This task now has MEDIUM priority:
[T][X][MEDIUM] read book
____________________________________________________________
____________________________________________________________
Alright man, I have unmarked this task for you:
[T][ ][MEDIUM] read book
____________________________________________________________
____________________________________________________________
Alrighty friend! This task now has no priority:
[T][ ] read book
____________________________________________________________
____________________________________________________________
Okay! I've got it friend! I've added this task:
 [T][ ] write code
Alrighty currently u have 2 task(s) in the list yay!
____________________________________________________________
____________________________________________________________
Alrighty friend! This task now has HIGH priority:
[T][ ][HIGH] write code
____________________________________________________________
____________________________________________________________
Here are your tasks:
1. [T][ ] read book
2. [T][ ][HIGH] write code
____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```

**Expected data:**
```text
T | 0 | cmVhZCBib29r | NONE
T | 0 | d3JpdGUgY29kZQ | HIGH
```
