import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

/** Runs the Pathfinder command-line chatbot. */
public class Pathfinder {
    private static final Storage STORAGE = new Storage(Path.of("data", "pathfinder.txt"));
    private static final Ui UI = new Ui();

    /** Loads saved tasks and processes commands until input ends or the user enters bye. */
    public static void main(String[] args) {
        UI.showGreeting();
        ArrayList<Task> tasks = loadTasksSafely();

        while (UI.hasNextInput()) {
            String input = UI.readInput();
            if (input.equalsIgnoreCase("bye")) {
                break;
            }

            try {
                handleCommand(input, tasks);
            } catch (PathfinderException exception) {
                UI.showMessage(exception.getMessage());
            } catch (IOException exception) {
                UI.showMessage("Oopsies! I couldn't save your tasks. Your latest change was undone.");
            }
        }

        UI.showGoodbye();
    }

    /** Loads valid saved tasks without allowing storage problems to stop startup. */
    private static ArrayList<Task> loadTasksSafely() {
        try {
            ArrayList<Task> tasks = STORAGE.load();
            if (STORAGE.getSkippedLineCount() > 0) {
                UI.showMessage("Heads up! I skipped " + STORAGE.getSkippedLineCount()
                        + " invalid saved task(s).");
            }
            return tasks;
        } catch (IOException exception) {
            UI.showMessage("Oopsies! I couldn't read your saved tasks, so I started with an empty list.");
            return new ArrayList<>();
        }
    }

    /** Performs one command or throws a user-friendly error for invalid input. */
    private static void handleCommand(String input, ArrayList<Task> tasks)
            throws PathfinderException, IOException {
        String command = readCommand(input);

        switch (command) {
        case "list" -> {
            requireNoArguments(input, "list");
            printList(tasks);
        }
        case "mark" -> markTask(tasks, readTaskNumber(input, "mark"));
        case "unmark" -> unmarkTask(tasks, readTaskNumber(input, "unmark"));
        case "delete" -> deleteTask(tasks, readTaskNumber(input, "delete"));
        case "todo" -> addTask(tasks, new ToDoTask(readDescription(input, "todo")));
        case "deadline" -> addDeadline(input, tasks);
        case "event" -> addEvent(input, tasks);
        case "bye" -> throw new PathfinderException("Oopsies! The bye command does not take extra words.");
        default -> throw new PathfinderException("Oopsies! I don't understand that command.");
        }
    }

    /** Returns the lower-case first word of a command. */
    private static String readCommand(String input) throws PathfinderException {
        if (input.isEmpty()) {
            throw new PathfinderException("Oh no friend! You didn't enter anything!");
        }
        return input.split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
    }

    /** Rejects unexpected text after a command that has no arguments. */
    private static void requireNoArguments(String input, String command)
            throws PathfinderException {
        if (!input.equalsIgnoreCase(command)) {
            throw new PathfinderException("Oopsies! The " + command + " command does not take extra words.");
        }
    }

    /** Returns the non-empty text after a command keyword. */
    private static String readDescription(String input, String command)
            throws PathfinderException {
        String description = input.substring(command.length()).trim();
        if (description.isEmpty()) {
            throw new PathfinderException("Oopsies! A " + command + " needs a description, friend!");
        }
        return description;
    }

    /** Parses a positive task number after mark, unmark, or delete. */
    private static int readTaskNumber(String input, String command)
            throws PathfinderException {
        String numberText = readDescription(input, command);
        if (!numberText.matches("[0-9]+")) {
            throw new PathfinderException("Oopsies! Please provide one positive whole task number.");
        }
        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new PathfinderException("Oopsies! That task number is too large.");
        }
    }

    /** Parses and adds a deadline containing both a description and /by value. */
    private static void addDeadline(String input, ArrayList<Task> tasks)
            throws PathfinderException, IOException {
        String details = readDescription(input, "deadline");
        int byIndex = details.indexOf(" /by ");
        if (byIndex < 0) {
            throw new PathfinderException("Oopsies! A deadline needs '/by' followed by a date or time.");
        }

        String description = details.substring(0, byIndex).trim();
        String byText = details.substring(byIndex + 5).trim();
        if (description.isEmpty() || byText.isEmpty()) {
            throw new PathfinderException("Oopsies! A deadline needs both a description and a '/by' value.");
        }
        LocalDateTime by = DateTimeParser.parseInput(byText);
        addTask(tasks, new DeadlineTask(description, by));
    }

    /** Parses and adds an event containing a description, /from value, and /to value. */
    private static void addEvent(String input, ArrayList<Task> tasks)
            throws PathfinderException, IOException {
        String details = readDescription(input, "event");
        int fromIndex = details.indexOf(" /from ");
        int toIndex = details.indexOf(" /to ");
        if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
            throw new PathfinderException("Oopsies! An event needs '/from' before '/to'.");
        }

        String description = details.substring(0, fromIndex).trim();
        String fromText = details.substring(fromIndex + 7, toIndex).trim();
        String toText = details.substring(toIndex + 5).trim();
        if (description.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
            throw new PathfinderException(
                    "Oopsies! An event needs a description, a '/from' value, and a '/to' value.");
        }
        LocalDateTime from = DateTimeParser.parseInput(fromText);
        LocalDateTime to = DateTimeParser.parseInput(toText);
        if (!to.isAfter(from)) {
            throw new PathfinderException(
                    "Oopsies! An event's '/to' time must be after its '/from' time.");
        }
        addTask(tasks, new EventTask(description, from, to));
    }

    /** Marks a task and restores its old status if saving fails. */
    private static void markTask(ArrayList<Task> tasks, int number)
            throws PathfinderException, IOException {
        Task task = getTask(tasks, number);
        if (task.isDone()) {
            throw new PathfinderException("Oopsies! That task is already marked as done.");
        }

        task.doTask();
        try {
            STORAGE.save(tasks);
        } catch (IOException exception) {
            task.undoTask();
            throw exception;
        }
        UI.showMessage("Awesome sauce! I have marked this task up dude:\n" + task);
    }

    /** Unmarks a task and restores its old status if saving fails. */
    private static void unmarkTask(ArrayList<Task> tasks, int number)
            throws PathfinderException, IOException {
        Task task = getTask(tasks, number);
        if (!task.isDone()) {
            throw new PathfinderException("Oopsies! That task is already marked as not done.");
        }

        task.undoTask();
        try {
            STORAGE.save(tasks);
        } catch (IOException exception) {
            task.doTask();
            throw exception;
        }
        UI.showMessage("Alright man, I have unmarked this task for you:\n" + task);
    }

    /** Deletes a task and puts it back if saving fails. */
    private static void deleteTask(ArrayList<Task> tasks, int number)
            throws PathfinderException, IOException {
        Task removed = getTask(tasks, number);
        tasks.remove(number - 1);
        try {
            STORAGE.save(tasks);
        } catch (IOException exception) {
            tasks.add(number - 1, removed);
            throw exception;
        }
        UI.showMessage("Got it my friend! I've removed this task:\n " + removed
                + "\n Alrighty currently you have " + tasks.size() + " task(s) in the list yay!");
    }

    /** Returns a task using its one-based number. */
    private static Task getTask(ArrayList<Task> tasks, int number) throws PathfinderException {
        if (number < 1 || number > tasks.size()) {
            throw new PathfinderException("Oopsies! That task number doesn't exist, friend!");
        }
        return tasks.get(number - 1);
    }

    /** Displays all tasks, or a clear message when the list is empty. */
    private static void printList(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            UI.showMessage("Your task list is empty, friend!");
            return;
        }

        StringBuilder result = new StringBuilder("Here are your tasks:\n");
        for (int i = 0; i < tasks.size(); i++) {
            result.append(i + 1).append(". ").append(tasks.get(i));
            if (i < tasks.size() - 1) {
                result.append("\n");
            }
        }
        UI.showMessage(result.toString());
    }

    /** Adds a task and removes it again if saving fails. */
    private static void addTask(ArrayList<Task> tasks, Task task) throws IOException {
        tasks.add(task);
        try {
            STORAGE.save(tasks);
        } catch (IOException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }

        UI.showTaskAdded(task, tasks.size());
    }
}
