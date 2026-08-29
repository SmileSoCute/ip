package pathfinder.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import pathfinder.exception.PathfinderException;

/** Tests the user-facing date and time formats accepted by {@link DateTimeParser}. */
class DateTimeParserTest {
    private static final String FORMAT_ERROR =
            "Oopsies! Use yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm.";

    @Test
    void parseInput_isoDateTime_returnsDateTime() throws PathfinderException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parseInput("2019-12-02 1800"));
    }

    @Test
    void parseInput_dayMonthYearDateTime_returnsDateTime() throws PathfinderException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parseInput("2/12/2019 1800"));
    }

    @Test
    void parseInput_isoDateOnly_returnsStartOfDay() throws PathfinderException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0),
                DateTimeParser.parseInput("2019-12-02"));
    }

    @Test
    void parseInput_dayMonthYearDateOnly_returnsStartOfDay() throws PathfinderException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0),
                DateTimeParser.parseInput("2/12/2019"));
    }

    @Test
    void parseInput_validBoundaryValues_returnsDateTime() throws PathfinderException {
        assertEquals(LocalDateTime.of(2024, 2, 29, 23, 59),
                DateTimeParser.parseInput("2024-02-29 2359"));
    }

    @Test
    void parseInput_impossibleDate_throwsPathfinderException() {
        assertInvalidInput("2019-02-29 1800");
        assertInvalidInput("30/2/2019 1800");
    }

    @Test
    void parseInput_invalidTime_throwsPathfinderException() {
        assertInvalidInput("2019-12-02 2400");
        assertInvalidInput("2/12/2019 2360");
    }

    @Test
    void parseInput_missingOrUnsupportedValue_throwsPathfinderException() {
        assertInvalidInput("");
        assertInvalidInput("tomorrow");
        assertInvalidInput("12-02-2019 1800");
    }

    @Test
    void parseStored_isoDateTime_returnsDateTime() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parseStored("2019-12-02T18:00:00"));
    }

    @Test
    void parseStored_invalidDateTime_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> DateTimeParser.parseStored("2019-02-29T18:00:00"));
        assertEquals("Invalid stored date and time", exception.getMessage());
    }

    @Test
    void parseLegacy_displayDateTime_returnsDateTime() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parseLegacy("Dec 2 2019 6:00 PM"));
    }

    @Test
    void parseLegacy_compactDateTime_returnsDateTime() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parseLegacy("2019-12-02 1800"));
    }

    @Test
    void parseLegacy_invalidDateTime_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> DateTimeParser.parseLegacy("2/12/2019 1800"));
        assertEquals("Invalid legacy date and time", exception.getMessage());
    }

    @Test
    void formatDisplay_afternoonDateTime_returnsFriendlyText() {
        assertEquals("Dec 2 2019 6:00 PM",
                DateTimeParser.formatDisplay(LocalDateTime.of(2019, 12, 2, 18, 0)));
    }

    @Test
    void formatDisplay_midnight_returnsTwelveAm() {
        assertEquals("Dec 2 2019 12:00 AM",
                DateTimeParser.formatDisplay(LocalDateTime.of(2019, 12, 2, 0, 0)));
    }

    @Test
    void formatStored_dateTime_returnsIsoText() {
        assertEquals("2019-12-02T18:00:00",
                DateTimeParser.formatStored(LocalDateTime.of(2019, 12, 2, 18, 0)));
    }

    @Test
    void formatStored_fractionalSecond_preservesPrecision() {
        assertEquals("2019-12-02T18:00:00.123",
                DateTimeParser.formatStored(
                        LocalDateTime.of(2019, 12, 2, 18, 0, 0, 123_000_000)));
    }

    @Test
    void formatStored_thenParseStored_returnsOriginalDateTime() {
        LocalDateTime original = LocalDateTime.of(2024, 2, 29, 23, 59, 58, 123_000_000);

        assertEquals(original,
                DateTimeParser.parseStored(DateTimeParser.formatStored(original)));
    }

    /** Verifies that invalid input produces Pathfinder's standard format guidance. */
    private static void assertInvalidInput(String input) {
        PathfinderException exception = assertThrows(PathfinderException.class,
                () -> DateTimeParser.parseInput(input));
        assertEquals(FORMAT_ERROR, exception.getMessage());
    }
}
