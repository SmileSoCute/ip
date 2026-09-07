package pathfinder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;

import pathfinder.exception.PathfinderException;
import pathfinder.parser.Parser;
import pathfinder.storage.Storage;
import pathfinder.task.Task;
import pathfinder.ui.Ui;

/** Coordinates command parsing, task management, storage, and console interaction. */
public class Pathfinder {
    private static final Storage STORAGE = new Storage(Path.of("data", "pathfinder.txt"));
    private static final Ui UI = new Ui();

    /** Creates a Pathfinder application entry-point instance. */
    public Pathfinder() {
    }

    /**
     * Starts Pathfinder, loads saved tasks, and processes commands until the user exits.
     *
     * @param args command-line arguments; Pathfinder does not use them
     */
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

    /**
     * Loads valid saved tasks without allowing storage problems to stop startup.
     *
     * @return loaded tasks, or an empty list when the data file cannot be read
     */
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

    /**
     * Parses and performs one user command.
     *
     * @param input complete command entered by the user
     * @param tasks current mutable task list
     * @throws PathfinderException if the command or its arguments are invalid
     * @throws IOException if a command changes the list but the change cannot be saved
     */
    private static void handleCommand(String input, ArrayList<Task> tasks)
            throws PathfinderException, IOException {
        String command = Parser.parseCommandWord(input);

        switch (command) {
            case "list" -> {
                Parser.requireNoArguments(input, "list");
                printList(tasks);
            }
            case "mark" -> markTask(tasks, Parser.parseTaskNumber(input, "mark"));
            case "unmark" -> unmarkTask(tasks, Parser.parseTaskNumber(input, "unmark"));
            case "delete" -> deleteTask(tasks, Parser.parseTaskNumber(input, "delete"));
            case "find" -> findTasks(tasks, Parser.parseFindKeyword(input));
            case "todo" -> addTask(tasks, Parser.parseTodo(input));
            case "deadline" -> addTask(tasks, Parser.parseDeadline(input));
            case "event" -> addTask(tasks, Parser.parseEvent(input));
            case "bye" -> throw new PathfinderException(
                    "Oopsies! The bye command does not take extra words.");
            default -> throw new PathfinderException("Oopsies! I don't understand that command.");
        }
    }

    /**
     * Marks a task and restores its previous status if saving fails.
     *
     * @param tasks current mutable task list
     * @param number one-based number of the task to mark
     * @throws PathfinderException if the task number is invalid or the task is already done
     * @throws IOException if the updated list cannot be saved
     */
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
        UI.showMessage("Awesome sauce! I have marked this task up dude:", task.toString());
    }

    /**
     * Unmarks a task and restores its previous status if saving fails.
     *
     * @param tasks current mutable task list
     * @param number one-based number of the task to unmark
     * @throws PathfinderException if the task number is invalid or the task is already incomplete
     * @throws IOException if the updated list cannot be saved
     */
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
        UI.showMessage("Alright man, I have unmarked this task for you:", task.toString());
    }

    /**
     * Deletes a task and restores it at the same position if saving fails.
     *
     * @param tasks current mutable task list
     * @param number one-based number of the task to delete
     * @throws PathfinderException if the task number is invalid
     * @throws IOException if the updated list cannot be saved
     */
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
        UI.showMessage("Got it my friend! I've removed this task:",
                " " + removed,
                " Alrighty currently you have " + tasks.size() + " task(s) in the list yay!");
    }

    /**
     * Returns a task using its one-based number.
     *
     * @param tasks current task list
     * @param number one-based task number
     * @return the requested task
     * @throws PathfinderException if the task number is outside the list
     */
    private static Task getTask(ArrayList<Task> tasks, int number) throws PathfinderException {
        if (number < 1 || number > tasks.size()) {
            throw new PathfinderException("Oopsies! That task number doesn't exist, friend!");
        }
        return tasks.get(number - 1);
    }

    /**
     * Displays all tasks, or a clear message when the list is empty.
     *
     * @param tasks tasks to display
     */
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

    /**
     * Displays tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param tasks Tasks to search.
     * @param keyword Keyword to find in task descriptions.
     */
    private static void findTasks(ArrayList<Task> tasks, String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        StringBuilder result = new StringBuilder(
                "Alrighty friend! Here are the matching tasks I found:\n");
        int matchCount = 0;

        for (Task task : tasks) {
            String description = task.getDescription().toLowerCase(Locale.ROOT);
            if (description.contains(normalizedKeyword)) {
                matchCount++;
                result.append(matchCount).append(". ").append(task).append("\n");
            }
        }

        if (matchCount == 0) {
            UI.showMessage("Oopsies! I couldn't find any tasks containing \""
                    + keyword + "\".");
            return;
        }

        result.setLength(result.length() - 1);
        UI.showMessage(result.toString());
    }

    /**
     * Adds a task and removes it again if saving fails.
     *
     * @param tasks current mutable task list
     * @param task task to add
     * @throws IOException if the updated list cannot be saved
     */
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
