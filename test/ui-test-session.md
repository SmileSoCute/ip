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

## PASS: Load legacy saved task types and statuses

Aim: Verify that Pathfinder remains compatible with saved files from the previous version.

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
Here are your tasks:
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
Here are your tasks:
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

Aim: Verify that adding and marking a task saves its type, status, and description.

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
T | 1 | cmVhZCBib29r
```

### Actual data

```text
T | 1 | cmVhZCBib29r
```

## PASS: Invalid commands and arguments

Aim: Verify that malformed commands produce specific errors and do not stop the chatbot.

### Console input

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

## PASS: Repeated status changes and invalid deletion

Aim: Verify repeated mark/unmark commands and out-of-range deletion are handled safely.

### Console input

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

### Expected data

```text

```

### Actual data

```text
```

## PASS: Malformed saved records

Aim: Verify malformed saved lines are skipped while valid tasks still load.

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
Heads up! I skipped 3 invalid saved task(s).
____________________________________________________________
____________________________________________________________
Here are your tasks:
1. [T][ ] read book
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
Heads up! I skipped 3 invalid saved task(s).
____________________________________________________________
____________________________________________________________
Here are your tasks:
1. [T][ ] read book
____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```

### Expected data

```text
not a task
T | 2 | cmVhZCBib29r
T | 0 | cmVhZCBib29r
D | 0 | !!! | U3VuZGF5
```

### Actual data

```text
not a task
T | 2 | cmVhZCBib29r
T | 0 | cmVhZCBib29r
D | 0 | !!! | U3VuZGF5
```

## PASS: Save text containing storage delimiters

Aim: Verify pipes in descriptions and date/time values cannot corrupt the saved file format.

### Console input

```text
todo read | book
deadline return | book /by no idea | Sunday
event project | meeting /from Mon | 2pm /to 4pm | later
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
 [T][ ] read | book
Alrighty currently u have 1 task(s) in the list yay!
____________________________________________________________
____________________________________________________________
Okay! I've got it friend! I've added this task:
 [D][ ] return | book (by: no idea | Sunday)
Alrighty currently u have 2 task(s) in the list yay!
____________________________________________________________
____________________________________________________________
Okay! I've got it friend! I've added this task:
 [E][ ] project | meeting (from: Mon | 2pm to: 4pm | later)
Alrighty currently u have 3 task(s) in the list yay!
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
 [T][ ] read | book
Alrighty currently u have 1 task(s) in the list yay!
____________________________________________________________
____________________________________________________________
Okay! I've got it friend! I've added this task:
 [D][ ] return | book (by: no idea | Sunday)
Alrighty currently u have 2 task(s) in the list yay!
____________________________________________________________
____________________________________________________________
Okay! I've got it friend! I've added this task:
 [E][ ] project | meeting (from: Mon | 2pm to: 4pm | later)
Alrighty currently u have 3 task(s) in the list yay!
____________________________________________________________
Bye bye! Hope to see you around soon!
____________________________________________________________
```

### Expected data

```text
T | 0 | cmVhZCB8IGJvb2s
D | 0 | cmV0dXJuIHwgYm9vaw | bm8gaWRlYSB8IFN1bmRheQ
E | 0 | cHJvamVjdCB8IG1lZXRpbmc | TW9uIHwgMnBt | NHBtIHwgbGF0ZXI
```

### Actual data

```text
T | 0 | cmVhZCB8IGJvb2s
D | 0 | cmV0dXJuIHwgYm9vaw | bm8gaWRlYSB8IFN1bmRheQ
E | 0 | cHJvamVjdCB8IG1lZXRpbmc | TW9uIHwgMnBt | NHBtIHwgbGF0ZXI
```

