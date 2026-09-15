package pathfinder.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests display markers for every supported priority. */
class PriorityTest {
    @Test
    void getDisplayMarker_allPriorities_returnsExpectedMarker() {
        assertEquals("[HIGH]", Priority.HIGH.getDisplayMarker());
        assertEquals("[MEDIUM]", Priority.MEDIUM.getDisplayMarker());
        assertEquals("[LOW]", Priority.LOW.getDisplayMarker());
        assertEquals("", Priority.NONE.getDisplayMarker());
    }
}
