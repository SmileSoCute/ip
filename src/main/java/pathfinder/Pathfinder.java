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
        case "bye" -> throw new PathfinderException("Oopsies! The bye command does not take extra words.");
        default -> throw new PathfinderException("Oopsies! I don't understand that command.");
        }
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

    /** Displays tasks whose descriptions contain the keyword, ignoring case. */
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
