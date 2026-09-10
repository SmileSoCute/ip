package pathfinder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Pathfinder responses used by the JavaFX interface. */
class PathfinderTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_addAndListCommands_returnsTaskDetails() {
        Pathfinder pathfinder = createPathfinder();

        String addResponse = pathfinder.getResponse("todo read book");
        String listResponse = pathfinder.getResponse("list");

        assertEquals("Okay! I've got it friend! I've added this task:\n [T][ ] read book"
                + "\nAlrighty currently u have 1 task(s) in the list yay!", addResponse);
        assertEquals("Here are your tasks:\n1. [T][ ] read book", listResponse);
    }

    @Test
    void getResponse_markCommand_updatesStoredTask() {
        Pathfinder pathfinder = createPathfinder();
        pathfinder.getResponse("todo read book");

        String markResponse = pathfinder.getResponse("mark 1");
        Pathfinder reloadedPathfinder = createPathfinder();

        assertEquals("Awesome sauce! I have marked this task up dude:\n[T][X] read book", markResponse);
        assertEquals("Here are your tasks:\n1. [T][X] read book", reloadedPathfinder.getResponse("list"));
    }

    @Test
    void getResponse_unmarkCommand_updatesStoredTask() {
        Pathfinder pathfinder = createPathfinder();
        pathfinder.getResponse("todo read book");
        pathfinder.getResponse("mark 1");

        String unmarkResponse = pathfinder.getResponse("unmark 1");
        Pathfinder reloadedPathfinder = createPathfinder();

        assertEquals("Alright man, I have unmarked this task for you:\n[T][ ] read book", unmarkResponse);
        assertEquals("Here are your tasks:\n1. [T][ ] read book", reloadedPathfinder.getResponse("list"));
    }

    @Test
    void getResponse_invalidCommand_returnsExistingErrorMessage() {
        Pathfinder pathfinder = createPathfinder();

        assertEquals("Oopsies! I don't understand that command.", pathfinder.getResponse("unknown"));
    }

    @Test
    void getResponse_findCommand_filtersAndRenumbersMatches() {
        Pathfinder pathfinder = createPathfinder();
        pathfinder.getResponse("todo read book");
        pathfinder.getResponse("todo write code");
        pathfinder.getResponse("todo return BOOK");

        String response = pathfinder.getResponse("find book");

        assertEquals("Alrighty friend! Here are the matching tasks I found:\n"
                + "1. [T][ ] read book\n"
                + "2. [T][ ] return BOOK", response);
    }

    /** Creates Pathfinder with an isolated data file for one test. */
    private Pathfinder createPathfinder() {
        return new Pathfinder(temporaryDirectory.resolve("data/pathfinder.txt"));
    }
}
