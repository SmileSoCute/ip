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
    void hasSameDetails_differentTypeOrEventTimeRange_returnsFalse() {
        TodoTask todo = new TodoTask("read book");
        DeadlineTask deadline = new DeadlineTask("read book",
                LocalDateTime.of(2019, 12, 2, 18, 0));
        EventTask firstEvent = new EventTask("meeting",
                LocalDateTime.of(2019, 12, 3, 14, 0),
                LocalDateTime.of(2019, 12, 3, 16, 0));
        EventTask differentEvent = new EventTask("meeting",
                LocalDateTime.of(2019, 12, 3, 14, 0),
                LocalDateTime.of(2019, 12, 3, 17, 0));

        assertFalse(todo.hasSameDetails(deadline));
        assertFalse(firstEvent.hasSameDetails(differentEvent));
        assertFalse(todo.hasSameDetails(null));
    }

    @Test
    void taskTypes_toString_formatsTypeDatesAndStatus() {
        TodoTask todo = new TodoTask("read book");
        DeadlineTask deadline = new DeadlineTask("return book",
                LocalDateTime.of(2019, 12, 2, 18, 0));
        EventTask event = new EventTask("meeting",
                LocalDateTime.of(2019, 12, 3, 14, 0),
                LocalDateTime.of(2019, 12, 3, 16, 0));
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
        assertEquals("[D][ ] return book (by: Dec 2 2019 6:00 PM)", deadline.toString());
        assertEquals("[E][ ] meeting (from: Dec 3 2019 2:00 PM to: Dec 3 2019 4:00 PM)",
                event.toString());
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
