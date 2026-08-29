package pathfinder.ui;

import java.util.Scanner;

import pathfinder.task.Task;

/** Handles all console input and output for Pathfinder. */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";

    private final Scanner scanner;

    /** Creates a UI connected to standard console input and output. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another line of console input is available.
     *
     * @return {@code true} if another line can be read, otherwise {@code false}
     */
    public boolean hasNextInput() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next line of console input.
     *
     * @return trimmed input line
     */
    public String readInput() {
        return scanner.nextLine().trim();
    }

    /** Displays Pathfinder's banner and greeting. */
    public void showGreeting() {
        String banner = "/================\\\n"
                + "|   Pathfinder   |\n"
                + "\\================/\n";
        System.out.println(SEPARATOR);
        System.out.print(banner);
        System.out.println("Hello friend! My name is Pathfinder.");
        System.out.println("What tasks can I do for you today?");
        System.out.println(SEPARATOR);
    }

    /**
     * Displays one response between separator lines.
     *
     * @param message response to display
     */
    public void showMessage(String message) {
        System.out.println(SEPARATOR);
        System.out.println(message);
        System.out.println(SEPARATOR);
    }

    /**
     * Displays the task that was added and the new number of tasks.
     *
     * @param task newly added task
     * @param taskCount number of tasks after the addition
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(SEPARATOR);
        System.out.println("Okay! I've got it friend! I've added this task:");
        System.out.println(" " + task);
        System.out.println("Alrighty currently u have " + taskCount + " task(s) in the list yay!");
        System.out.println(SEPARATOR);
    }

    /** Displays Pathfinder's farewell. */
    public void showGoodbye() {
        System.out.println("Bye bye! Hope to see you around soon!");
        System.out.println(SEPARATOR);
    }
}
