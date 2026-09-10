package pathfinder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
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
        pathfinder.getResponse("priority 3 high");

        String response = pathfinder.getResponse("find book");

        assertEquals("Alrighty friend! Here are the matching tasks I found:\n"
                + "1. [T][ ] read book\n"
                + "2. [T][ ][HIGH] return BOOK", response);
    }

    @Test
    void getResponse_priorityCommand_persistsAndSurvivesStatusChanges() {
        Pathfinder pathfinder = createPathfinder();
        pathfinder.getResponse("todo read book");

        String priorityResponse = pathfinder.getResponse("priority 1 high");
        pathfinder.getResponse("mark 1");
        Pathfinder reloadedPathfinder = createPathfinder();

        assertEquals("Alrighty friend! This task now has HIGH priority:\n"
                + "[T][ ][HIGH] read book", priorityResponse);
        assertEquals("Here are your tasks:\n1. [T][X][HIGH] read book",
                reloadedPathfinder.getResponse("list"));
    }

    @Test
    void getResponse_priorityCommand_replacesClearsAndRepeatsPriority() {
        Pathfinder pathfinder = createPathfinder();
        pathfinder.getResponse("todo read book");
        pathfinder.getResponse("priority 1 high");

        String repeatResponse = pathfinder.getResponse("priority 1 HIGH");
        String replaceResponse = pathfinder.getResponse("priority 1 medium");
        String clearResponse = pathfinder.getResponse("priority 1 none");

        assertEquals("Alrighty friend! This task now has HIGH priority:\n"
                + "[T][ ][HIGH] read book", repeatResponse);
        assertEquals("Alrighty friend! This task now has MEDIUM priority:\n"
                + "[T][ ][MEDIUM] read book", replaceResponse);
        assertEquals("Alrighty friend! This task now has no priority:\n"
                + "[T][ ] read book", clearResponse);
        assertEquals("Here are your tasks:\n1. [T][ ] read book", pathfinder.getResponse("list"));
    }

    @Test
    void getResponse_prioritySaveFails_restoresPreviousPriority() throws IOException {
        Path storagePath = temporaryDirectory.resolve("data/pathfinder.txt");
        Pathfinder pathfinder = new Pathfinder(storagePath);
        pathfinder.getResponse("todo read book");
        Files.delete(storagePath);
        Files.delete(storagePath.getParent());
        Files.writeString(storagePath.getParent(), "not a directory");

        String response = pathfinder.getResponse("priority 1 high");

        assertEquals("Oopsies! I couldn't save your tasks. Your latest change was undone.",
                response);
        assertEquals("Here are your tasks:\n1. [T][ ] read book", pathfinder.getResponse("list"));
    }

    @Test
    void getResponse_repeatedPriorityWhenStorageUnavailable_succeedsWithoutSaving()
            throws IOException {
        Path storagePath = temporaryDirectory.resolve("data/pathfinder.txt");
        Pathfinder pathfinder = new Pathfinder(storagePath);
        pathfinder.getResponse("todo read book");
        pathfinder.getResponse("priority 1 high");
        Files.delete(storagePath);
        Files.delete(storagePath.getParent());
        Files.writeString(storagePath.getParent(), "not a directory");

        String response = pathfinder.getResponse("priority 1 HIGH");

        assertEquals("Alrighty friend! This task now has HIGH priority:\n"
                + "[T][ ][HIGH] read book", response);
    }

    /** Creates Pathfinder with an isolated data file for one test. */
    private Pathfinder createPathfinder() {
        return new Pathfinder(temporaryDirectory.resolve("data/pathfinder.txt"));
    }
}
