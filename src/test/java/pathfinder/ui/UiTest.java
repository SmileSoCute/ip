package pathfinder.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/** Tests console input trimming and the formatted console output. */
class UiTest {
    private static final String SEPARATOR = "____________________________________________________________";

    @Test
    void readInput_leadingAndTrailingWhitespace_returnsTrimmedInput() {
        InputStream originalInput = System.in;
        try {
            System.setIn(new ByteArrayInputStream("  todo read book  \n".getBytes(StandardCharsets.UTF_8)));
            Ui ui = new Ui();

            assertTrue(ui.hasNextInput());
            assertEquals("todo read book", ui.readInput());
            assertFalse(ui.hasNextInput());
        } finally {
            System.setIn(originalInput);
        }
    }

    @Test
    void showMethods_printGreetingMessageAndGoodbyeWithSeparators() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            Ui ui = new Ui();
            ui.showGreeting();
            ui.showMessage("First line", "Second line");
            ui.showGoodbye();
        } finally {
            System.setOut(originalOutput);
        }

        String lineSeparator = System.lineSeparator();
        String expected = SEPARATOR + lineSeparator
                + "/================\\\n"
                + "|   Pathfinder   |\n"
                + "\\================/\n"
                + "Hello, friend! My name is Pathfinder." + lineSeparator
                + "What can I help you with today?" + lineSeparator
                + SEPARATOR + lineSeparator
                + SEPARATOR + lineSeparator
                + "First line" + lineSeparator
                + "Second line" + lineSeparator
                + SEPARATOR + lineSeparator
                + "Bye bye! Hope to see you around soon!" + lineSeparator
                + SEPARATOR + lineSeparator;

        assertEquals(expected, output.toString(StandardCharsets.UTF_8));
    }
}
