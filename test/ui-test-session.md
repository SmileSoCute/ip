# UI Test Session

## PASS: Greeting and immediate exit

Aim: Verify that Pathfinder greets the user and exits when the first command is `bye`.

### Console input

```text
bye
```

### Expected output

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

### Actual output

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

## PASS: Load saved task types and statuses

Aim: Verify that Pathfinder loads saved ToDo, deadline, and event tasks with their completion statuses when it starts.

### Console input

```text
list
bye
```

### Expected output

```text
____________________________________________________________
/================\
|   Pathfinder   |
\================/
Hello friend! My name is Pathfinder.
What tasks can I do for you today?
____________________________________________________________
____________________________________________________________
1. [T][X] read book
2. [D][ ] return book (by: Sunday)
3. [E][ ] project meeting (from: Mon 2pm to: 4pm)

____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```

### Actual output

```text
____________________________________________________________
/================\
|   Pathfinder   |
\================/
Hello friend! My name is Pathfinder.
What tasks can I do for you today?
____________________________________________________________
____________________________________________________________
1. [T][X] read book
2. [D][ ] return book (by: Sunday)
3. [E][ ] project meeting (from: Mon 2pm to: 4pm)

____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```

### Expected data

```text
[T][X] read book
[D][ ] return book (by: Sunday)
[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

### Actual data

```text
[T][X] read book
[D][ ] return book (by: Sunday)
[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

## PASS: Save a changed task list

Aim: Verify that adding and marking a task still produces the correct console responses while triggering automatic saves.

### Console input

```text
todo read book
mark 1
bye
```

### Expected output

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

### Actual output

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

### Expected data

```text
[T][X] read book
```

### Actual data

```text
[T][X] read book
```

