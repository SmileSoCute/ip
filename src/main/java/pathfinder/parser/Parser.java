package pathfinder.parser;

import java.time.LocalDateTime;
import java.util.Locale;

import pathfinder.exception.PathfinderException;
import pathfinder.task.DeadlineTask;
import pathfinder.task.EventTask;
import pathfinder.task.TodoTask;
import pathfinder.util.DateTimeParser;

/** Interprets raw user input and validates command arguments. */
public final class Parser {
    /** Prevents instantiation of this utility class. */
    private Parser() {
    }

    /**
     * Extracts and normalizes the command word from user input.
     *
     * @param input complete user input
     * @return lower-case first word of the command
     * @throws PathfinderException if the input is empty
     */
    public static String parseCommandWord(String input) throws PathfinderException {
        if (input.isEmpty()) {
            throw new PathfinderException("Oh no friend! You didn't enter anything!");
        }
        return input.split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
    }

    /**
     * Rejects unexpected text after a command that has no arguments.
     *
     * @param input complete user input
     * @param command expected command word
     * @throws PathfinderException if the input contains additional text
     */
    public static void requireNoArguments(String input, String command)
            throws PathfinderException {
        if (!input.equalsIgnoreCase(command)) {
            throw new PathfinderException(
                    "Oopsies! The " + command + " command does not take extra words.");
        }
    }

    /**
     * Creates a todo from the required text following its command word.
     *
     * @param input Complete todo command.
     * @return The parsed todo task.
     * @throws PathfinderException If the description is missing.
     */
    public static TodoTask parseTodo(String input) throws PathfinderException {
        return new TodoTask(parseDescription(input, "todo"));
    }

    /**
     * Parses a positive task number after commands such as mark, unmark, or delete.
     *
     * @param input complete command input
     * @param command command whose task number is being parsed
     * @return parsed task number
     * @throws PathfinderException if the number is missing, malformed, or too large
     */
    public static int parseTaskNumber(String input, String command)
            throws PathfinderException {
        String numberText = parseDescription(input, command);
        if (!numberText.matches("[0-9]+")) {
            throw new PathfinderException(
                    "Oopsies! Please provide one positive whole task number.");
        }
        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new PathfinderException("Oopsies! That task number is too large.");
        }
    }

    /**
     * Returns the required keyword following the find command.
     *
     * @param input Complete find command.
     * @return The keyword to search for.
     * @throws PathfinderException If the keyword is missing.
     */
    public static String parseFindKeyword(String input) throws PathfinderException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new PathfinderException(
                    "Oopsies! The find command needs a keyword, friend!");
        }
        return keyword;
    }

    /**
     * Creates a deadline from its description and {@code /by} value.
     *
     * @param input complete deadline command
     * @return the parsed deadline task
     * @throws PathfinderException if required fields or a valid date-time are missing
     */
    public static DeadlineTask parseDeadline(String input) throws PathfinderException {
        String details = parseDescription(input, "deadline");
        int byIndex = details.indexOf(" /by ");
        if (byIndex < 0) {
            throw new PathfinderException(
                    "Oopsies! A deadline needs '/by' followed by a date or time.");
        }

        String description = details.substring(0, byIndex).trim();
        String byText = details.substring(byIndex + 5).trim();
        if (description.isEmpty() || byText.isEmpty()) {
            throw new PathfinderException(
                    "Oopsies! A deadline needs both a description and a '/by' value.");
        }
        LocalDateTime by = DateTimeParser.parseInput(byText);
        return new DeadlineTask(description, by);
    }

    /**
     * Creates an event from its description, {@code /from} value, and {@code /to} value.
     *
     * @param input complete event command
     * @return the parsed event task
     * @throws PathfinderException if required fields are invalid or the end is not after the start
     */
    public static EventTask parseEvent(String input) throws PathfinderException {
        String details = parseDescription(input, "event");
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
        return new EventTask(description, from, to);
    }

    /**
     * Extracts the required non-empty text after a command word.
     *
     * @param input complete command input
     * @param command command word to remove
     * @return trimmed text following the command
     * @throws PathfinderException if no text follows the command
     */
    private static String parseDescription(String input, String command)
            throws PathfinderException {
        String description = input.substring(command.length()).trim();
        if (description.isEmpty()) {
            throw new PathfinderException(
                    "Oopsies! A " + command + " needs a description, friend!");
        }
        return description;
    }
}
