package pathfinder.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

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
    void hasSameDetails_caseInsensitiveDescriptionAndMatchingDates_returnsTrue() {
        DeadlineTask firstTask = new DeadlineTask("return book",
                LocalDateTime.of(2019, 12, 2, 18, 0));
        DeadlineTask matchingTask = new DeadlineTask("RETURN BOOK",
                LocalDateTime.of(2019, 12, 2, 18, 0));
        DeadlineTask differentDateTask = new DeadlineTask("return book",
                LocalDateTime.of(2019, 12, 3, 18, 0));

        assertTrue(firstTask.hasSameDetails(matchingTask));
        assertFalse(firstTask.hasSameDetails(differentDateTask));
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
