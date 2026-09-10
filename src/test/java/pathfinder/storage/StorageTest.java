package pathfinder.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pathfinder.task.DeadlineTask;
import pathfinder.task.EventTask;
import pathfinder.task.Task;
import pathfinder.task.TodoTask;

/** Tests Pathfinder's task persistence, compatibility, and corruption handling. */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyList() throws IOException {
        Storage storage = createStorage();

        ArrayList<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    void saveThenLoad_allTaskTypes_preservesFieldsAndStatus() throws IOException {
        Storage storage = createStorage();
        ArrayList<Task> tasks = new ArrayList<>();
        TodoTask todo = new TodoTask("read | book");
        DeadlineTask deadline = new DeadlineTask("return book",
                LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.markAsDone();
        EventTask event = new EventTask("project meeting",
                LocalDateTime.of(2019, 12, 3, 14, 0),
                LocalDateTime.of(2019, 12, 3, 16, 0));
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.save(tasks);
        ArrayList<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        TodoTask loadedTodo = assertInstanceOf(TodoTask.class, loaded.get(0));
        DeadlineTask loadedDeadline = assertInstanceOf(DeadlineTask.class, loaded.get(1));
        EventTask loadedEvent = assertInstanceOf(EventTask.class, loaded.get(2));
        assertEquals("read | book", loadedTodo.getDescription());
        assertFalse(loadedTodo.isDone());
        assertEquals("return book", loadedDeadline.getDescription());
        assertTrue(loadedDeadline.isDone());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), loadedDeadline.getBy());
        assertEquals("project meeting", loadedEvent.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 3, 14, 0), loadedEvent.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 3, 16, 0), loadedEvent.getTo());
    }

    @Test
    void save_descriptionContainingDelimiter_writesDelimiterSafeRecord() throws IOException {
        Path dataFile = dataFile();
        Storage storage = new Storage(dataFile);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new TodoTask("read | book"));

        storage.save(tasks);

        assertEquals("T | 0 | cmVhZCB8IGJvb2s" + System.lineSeparator(),
                Files.readString(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void save_secondTaskList_replacesPreviousFileContents() throws IOException {
        Storage storage = createStorage();
        ArrayList<Task> originalTasks = new ArrayList<>();
        originalTasks.add(new TodoTask("first"));
        originalTasks.add(new TodoTask("second"));
        storage.save(originalTasks);
        ArrayList<Task> replacementTasks = new ArrayList<>();
        replacementTasks.add(new TodoTask("replacement"));

        storage.save(replacementTasks);
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("replacement", loaded.get(0).getDescription());
    }

    @Test
    void save_nestedDataPath_createsParentDirectories() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nested/data/pathfinder.txt");
        Storage storage = new Storage(dataFile);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new TodoTask("read book"));

        storage.save(tasks);

        assertTrue(Files.isRegularFile(dataFile));
    }

    @Test
    void save_unsupportedBaseTask_throwsIllegalArgumentException() {
        Storage storage = createStorage();
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task("generic task"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> storage.save(tasks));

        assertTrue(exception.getMessage().startsWith("Unsupported task class:"));
    }

    @Test
    void load_legacyRecords_loadsEveryTaskTypeAndStatus() throws IOException {
        Path dataFile = dataFile();
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, String.join(System.lineSeparator(),
                "[T][X] read book",
                "[D][ ] return book (by: Dec 2 2019 6:00 PM)",
                "[E][ ] meeting (from: Dec 3 2019 2:00 PM to: Dec 3 2019 4:00 PM)"),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        ArrayList<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertInstanceOf(TodoTask.class, loaded.get(0));
        assertTrue(loaded.get(0).isDone());
        DeadlineTask deadline = assertInstanceOf(DeadlineTask.class, loaded.get(1));
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        EventTask event = assertInstanceOf(EventTask.class, loaded.get(2));
        assertEquals(LocalDateTime.of(2019, 12, 3, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 3, 16, 0), event.getTo());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    void load_malformedRecords_skipsInvalidLinesAndLoadsValidLine() throws IOException {
        Path dataFile = dataFile();
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, String.join(System.lineSeparator(),
                "not a task",
                "T | 2 | cmVhZCBib29r",
                "D | 0 | cmVhZCBib29r | bm90LWEtZGF0ZQ",
                "",
                "T | 0 | cmVhZCBib29r"), StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
        assertEquals(3, storage.getSkippedLineCount());
    }

    @Test
    void load_afterPreviousMalformedLoad_resetsSkippedLineCount() throws IOException {
        Path dataFile = dataFile();
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "not a task", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);
        storage.load();
        Files.writeString(dataFile, "T | 0 | cmVhZCBib29r", StandardCharsets.UTF_8);

        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(0, storage.getSkippedLineCount());
    }

    /** Creates storage at the standard test data path. */
    private Storage createStorage() {
        return new Storage(dataFile());
    }

    /** Returns an isolated data-file path for one test. */
    private Path dataFile() {
        return temporaryDirectory.resolve("data/pathfinder.txt");
    }
}
