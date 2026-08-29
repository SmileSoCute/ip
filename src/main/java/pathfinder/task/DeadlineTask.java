package pathfinder.task;

import java.time.LocalDateTime;

import pathfinder.util.DateTimeParser;

/** Represents a task that must be completed by a specific date and time. */
public class DeadlineTask extends Task {
    private final LocalDateTime dueDateTime;

    /** Creates an incomplete deadline task with the given description and due date-time. */
    public DeadlineTask(String description, LocalDateTime by) {
        super(description);
        this.dueDateTime = by;
    }

    /** Returns the deadline date and time. */
    public LocalDateTime getBy() {
        return dueDateTime;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + DateTimeParser.formatDisplay(dueDateTime) + ")";
    }
}
