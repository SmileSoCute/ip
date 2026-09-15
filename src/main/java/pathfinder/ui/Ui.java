package pathfinder.ui;

import java.util.Scanner;

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
        System.out.println("Hello, friend! My name is Pathfinder.");
        System.out.println("What can I help you with today?");
        System.out.println(SEPARATOR);
    }

    /**
     * Displays one or more response lines between separator lines.
     *
     * @param messages response lines to display
     */
    public void showMessage(String... messages) {
        System.out.println(SEPARATOR);
        for (String message : messages) {
            System.out.println(message);
        }
        System.out.println(SEPARATOR);
    }

    /** Displays Pathfinder's farewell. */
    public void showGoodbye() {
        System.out.println("Bye bye! Hope to see you around soon!");
        System.out.println(SEPARATOR);
    }
}
