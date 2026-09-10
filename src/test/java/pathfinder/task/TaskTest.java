package pathfinder.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests the internal assumptions maintained by tasks. */
class TaskTest {
    @Test
    void constructor_nullDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Task(null));
    }

    @Test
    void constructor_blankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Task("   "));
    }
}
