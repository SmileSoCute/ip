package pathfinder.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import pathfinder.exception.PathfinderException;

/** Parses, stores, and displays Pathfinder dates and times consistently. */
public final class DateTimeParser {
    private static final DateTimeFormatter INPUT_DATE_TIME = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter INPUT_SLASH_DATE_TIME = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_SLASH_DATE = DateTimeFormatter
            .ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter
            .ofPattern("MMM d uuuu h:mm a", Locale.ENGLISH);

    /** Prevents instantiation of this utility class. */
    private DateTimeParser() {
    }

    /**
     * Parses supported ISO or day/month/year input, using midnight when time is omitted.
     *
     * @param text date or date-time entered by the user
     * @return parsed date and time
     * @throws PathfinderException if the value does not match a supported format
     */
    public static LocalDateTime parseInput(String text) throws PathfinderException {
        try {
            return LocalDateTime.parse(text, INPUT_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // Try the next supported format.
        }
        try {
            return LocalDateTime.parse(text, INPUT_SLASH_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // Try a date without a time next.
        }
        try {
            return LocalDate.parse(text, INPUT_DATE).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // Try the remaining supported format.
        }
        try {
            return LocalDate.parse(text, INPUT_SLASH_DATE).atStartOfDay();
        } catch (DateTimeParseException exception) {
            throw new PathfinderException(
                    "Oopsies! Use yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm.");
        }
    }

    /**
     * Parses the ISO value used in Pathfinder's current storage format.
     *
     * @param text stored ISO date-time
     * @return parsed date and time
     * @throws IllegalArgumentException if the stored value is invalid
     */
    public static LocalDateTime parseStored(String text) {
        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid stored date and time", exception);
        }
    }

    /**
     * Parses a date from a legacy display-based data file.
     *
     * @param text date-time from a legacy task record
     * @return parsed date and time
     * @throws IllegalArgumentException if the legacy value is invalid
     */
    public static LocalDateTime parseLegacy(String text) {
        try {
            return LocalDateTime.parse(text, DISPLAY_DATE_TIME);
        } catch (DateTimeParseException displayException) {
            try {
                return LocalDateTime.parse(text, INPUT_DATE_TIME);
            } catch (DateTimeParseException inputException) {
                throw new IllegalArgumentException("Invalid legacy date and time", inputException);
            }
        }
    }

    /**
     * Formats a date and time for chatbot output.
     *
     * @param dateTime date and time to format
     * @return user-friendly date-time text
     */
    public static String formatDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_DATE_TIME);
    }

    /**
     * Formats a date and time for unambiguous storage.
     *
     * @param dateTime date and time to format
     * @return ISO local date-time text
     */
    public static String formatStored(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
