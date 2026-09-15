package pathfinder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pathfinder.exception.PathfinderException;
import pathfinder.parser.Parser;
import pathfinder.storage.Storage;
import pathfinder.task.Priority;
import pathfinder.task.Task;
import pathfinder.ui.Ui;

/** Coordinates command parsing, task management, and storage for Pathfinder. */
public class Pathfinder {
    private static final String GOODBYE_MESSAGE = "Bye bye! Hope to see you around soon!";

    private final Storage storage;
    private final ArrayList<Task> tasks;
    private final String startupMessage;

    /** Creates a Pathfinder application that stores tasks in the default data file. */
    public Pathfinder() {
        this(Path.of("data", "pathfinder.txt"));
    }

    /**
     * Creates a Pathfinder application that stores tasks at the specified path.
     *
     * @param storagePath path of the task data file.
     */
    Pathfinder(Path storagePath) {
        storage = new Storage(storagePath);
        ArrayList<Task> loadedTasks;
        String message = "";

        try {
            loadedTasks = storage.load();
            if (storage.getSkippedLineCount() > 0) {
                message = "Heads up! I skipped " + storage.getSkippedLineCount()
                        + " invalid saved task(s).";
            }
        } catch (IOException exception) {
            loadedTasks = new ArrayList<>();
            message = "Oopsies! I couldn't read your saved tasks, so I started with an empty list.";
        }

        tasks = loadedTasks;
        startupMessage = message;
    }

    /**
     * Starts the original console interface for Pathfinder.
     *
     * @param args command-line arguments; Pathfinder does not use them.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Pathfinder pathfinder = new Pathfinder();
        ui.showGreeting();
        if (!pathfinder.getStartupMessage().isEmpty()) {
            ui.showMessage(pathfinder.getStartupMessage());
        }

        while (ui.hasNextInput()) {
            String input = ui.readInput();
            if (input.equalsIgnoreCase("bye")) {
                break;
            }
            ui.showMessage(pathfinder.getResponse(input));
        }

        ui.showGoodbye();
    }

    /**
     * Processes one command and returns the message that should be shown to the user.
     *
     * @param input complete command entered by the user.
     * @return Pathfinder's response to the command.
     */
    public String getResponse(String input) {
        String trimmedInput = input.trim();
        if (trimmedInput.equalsIgnoreCase("bye")) {
            return GOODBYE_MESSAGE;
        }

        try {
            return handleCommand(trimmedInput);
        } catch (PathfinderException exception) {
            return exception.getMessage();
        } catch (IOException exception) {
            return "Oopsies! I couldn't save your tasks. Your latest change was undone.";
        }
    }

    /**
     * Returns the warning generated while reading persisted tasks at startup.
     *
     * @return the startup warning, or an empty string when no warning occurred.
     */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Parses and performs one user command.
     *
     * @param input complete user command.
     * @return message describing the command result.
     * @throws PathfinderException if the command or its arguments are invalid.
     * @throws IOException if a command changes the list but the change cannot be saved.
     */
    private String handleCommand(String input) throws PathfinderException, IOException {
        String command = Parser.parseCommandWord(input);

        return switch (command) {
            case "list" -> {
                Parser.requireNoArguments(input, "list");
                yield printList();
            }
            case "mark" -> markTask(Parser.parseTaskNumber(input, "mark"));
            case "unmark" -> unmarkTask(Parser.parseTaskNumber(input, "unmark"));
            case "delete" -> deleteTask(Parser.parseTaskNumber(input, "delete"));
            case "find" -> findTasks(Parser.parseFindKeyword(input));
            case "priority" -> {
                Parser.PriorityCommand priorityCommand = Parser.parsePriorityCommand(input);
                yield setTaskPriority(priorityCommand.taskNumber(), priorityCommand.priority());
            }
            case "todo" -> addTask(Parser.parseTodo(input));
            case "deadline" -> addTask(Parser.parseDeadline(input));
            case "event" -> addTask(Parser.parseEvent(input));
            case "bye" -> throw new PathfinderException(
                    "Oopsies! The bye command does not take extra words.");
            default -> throw new PathfinderException("Oopsies! I don't understand that command.");
        };
    }

    /**
     * Marks a task and restores its previous status if saving fails.
     *
     * @param number one-based number of the task to mark.
     * @return confirmation message for the marked task.
     * @throws PathfinderException if the task number is invalid or the task is already done.
     * @throws IOException if the updated list cannot be saved.
     */
    private String markTask(int number) throws PathfinderException, IOException {
        Task task = getTask(number);
        if (task.isDone()) {
            throw new PathfinderException("Oopsies! That task is already marked as done.");
        }

        task.markAsDone();
        assert task.isDone() : "Task should be done after marking";
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            task.markAsIncomplete();
            assert !task.isDone() : "Failed mark should restore the incomplete status";
            throw exception;
        }
        return "Awesome sauce! I've marked this task as done, dude:\n" + task;
    }

    /**
     * Unmarks a task and restores its previous status if saving fails.
     *
     * @param number one-based number of the task to unmark.
     * @return confirmation message for the unmarked task.
     * @throws PathfinderException if the task number is invalid or the task is already incomplete.
     * @throws IOException if the updated list cannot be saved.
     */
    private String unmarkTask(int number) throws PathfinderException, IOException {
        Task task = getTask(number);
        if (!task.isDone()) {
            throw new PathfinderException("Oopsies! That task is already marked as not done.");
        }

        task.markAsIncomplete();
        assert !task.isDone() : "Task should be incomplete after unmarking";
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            task.markAsDone();
            assert task.isDone() : "Failed unmark should restore the completed status";
            throw exception;
        }
        return "Alright, man! I've unmarked this task for you:\n" + task;
    }

    /**
     * Deletes a task and restores it at the same position if saving fails.
     *
     * @param number one-based number of the task to delete.
     * @return confirmation message for the removed task.
     * @throws PathfinderException if the task number is invalid.
     * @throws IOException if the updated list cannot be saved.
     */
    private String deleteTask(int number) throws PathfinderException, IOException {
        Task removed = getTask(number);
        tasks.remove(number - 1);
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            tasks.add(number - 1, removed);
            throw exception;
        }
        return "Got it, my friend! I've removed this task:\n " + removed
                + "\nAlrighty! You currently have " + tasks.size() + " task(s) in the list, yay!";
    }

    /**
     * Returns a task using its one-based number.
     *
     * @param number one-based task number.
     * @return the requested task.
     * @throws PathfinderException if the task number is outside the list.
     */
    private Task getTask(int number) throws PathfinderException {
        if (number < 1 || number > tasks.size()) {
            throw new PathfinderException("Oopsies! That task number doesn't exist, friend!");
        }
        assert number >= 1 && number <= tasks.size()
                : "Validated task number should be within the task list";
        return tasks.get(number - 1);
    }

    /**
     * Changes a task's priority and restores its previous priority if saving fails.
     *
     * @param number one-based number of the task to update.
     * @param priority priority to assign.
     * @return confirmation message for the updated task.
     * @throws PathfinderException if the task number is invalid.
     * @throws IOException if the updated list cannot be saved.
     */
    private String setTaskPriority(int number, Priority priority)
            throws PathfinderException, IOException {
        Task task = getTask(number);
        Priority previousPriority = task.getPriority();
        if (previousPriority == priority) {
            return getPriorityUpdateMessage(task, priority);
        }

        task.setPriority(priority);
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            task.setPriority(previousPriority);
            assert task.getPriority() == previousPriority
                    : "Failed priority update should restore the previous priority";
            throw exception;
        }
        return getPriorityUpdateMessage(task, priority);
    }

    /** Returns the confirmation shown after assigning or clearing a priority. */
    private String getPriorityUpdateMessage(Task task, Priority priority) {
        if (priority == Priority.NONE) {
            return "Alrighty, friend! This task now has no priority:\n" + task;
        }
        return "Alrighty, friend! This task now has " + priority + " priority:\n" + task;
    }

    /**
     * Returns all tasks, or a clear message when the task list is empty.
     *
     * @return formatted task-list message.
     */
    private String printList() {
        if (tasks.isEmpty()) {
            return "Your task list is empty, friend!";
        }

        StringBuilder result = new StringBuilder("Here are your tasks:\n");
        for (int index = 0; index < tasks.size(); index++) {
            result.append(index + 1).append(". ").append(tasks.get(index));
            if (index < tasks.size() - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param keyword text to search for in task descriptions.
     * @return formatted search-result message.
     */
    private String findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();

        if (matchingTasks.isEmpty()) {
            return "Oopsies! I couldn't find any tasks containing \"" + keyword + "\".";
        }

        return IntStream.range(0, matchingTasks.size())
                .mapToObj(index -> (index + 1) + ". " + matchingTasks.get(index))
                .collect(Collectors.joining("\n",
                        "Alrighty, friend! Here are the matching tasks I found:\n", ""));
    }

    /**
     * Adds a task and removes it again if saving fails.
     *
     * @param task task to add.
     * @return confirmation message for the new task.
     * @throws IOException if the updated list cannot be saved.
     */
    private String addTask(Task task) throws PathfinderException, IOException {
        if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
            throw new PathfinderException("Oopsies! That task is already in your list, friend!");
        }

        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }

        return "Okay! I've got it, friend! I've added this task:\n " + task
                + "\nAlrighty! You currently have " + tasks.size() + " task(s) in the list, yay!";
    }
}
