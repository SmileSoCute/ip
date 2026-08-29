import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Parses, stores, and displays Pathfinder dates and times consistently. */
public final class DateTimeParser {
    private static final DateTimeFormatter INPUT_DATE_TIME = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter
            .ofPattern("MMM d uuuu h:mm a", Locale.ENGLISH);

    private DateTimeParser() {
    }

    /** Parses user input as yyyy-MM-dd HHmm, or as yyyy-MM-dd at midnight. */
    public static LocalDateTime parseInput(String text) throws PathfinderException {
        try {
            return LocalDateTime.parse(text, INPUT_DATE_TIME);
        } catch (DateTimeParseException dateTimeException) {
            try {
                return LocalDate.parse(text, INPUT_DATE).atStartOfDay();
            } catch (DateTimeParseException dateException) {
                throw new PathfinderException(
                        "Oopsies! Use yyyy-MM-dd HHmm (or yyyy-MM-dd) for dates and times.");
            }
        }
    }

    /** Parses the ISO value used in Pathfinder's current storage format. */
    public static LocalDateTime parseStored(String text) {
        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid stored date and time", exception);
        }
    }

    /** Parses a date from a legacy display-based data file. */
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

    /** Formats a date and time for chatbot output. */
    public static String formatDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_DATE_TIME);
    }

    /** Formats a date and time for unambiguous storage. */
    public static String formatStored(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
