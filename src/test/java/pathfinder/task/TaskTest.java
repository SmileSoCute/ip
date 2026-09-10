package pathfinder.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests the internal assumptions maintained by tasks. */
class TaskTest {
    @Test
    void constructor_validDescription_hasNoPriority() {
        Task task = new Task("read book");

        assertEquals(Priority.NONE, task.getPriority());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void setPriority_highThenNone_updatesDisplayMarker() {
        Task task = new Task("read book");

        task.setPriority(Priority.HIGH);
        assertEquals("[ ][HIGH] read book", task.toString());

        task.setPriority(Priority.NONE);
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void constructor_nullDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Task(null));
    }

    @Test
    void constructor_blankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Task("   "));
    }
}
