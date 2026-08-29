package pathfinder.task;

import java.time.LocalDateTime;

import pathfinder.util.DateTimeParser;

/** Represents a task that must be completed by a specific date and time. */
public class DeadlineTask extends Task {
    private final LocalDateTime dueDateTime;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description Description of the task.
     * @param by Date and time by which the task should be completed.
     */
    public DeadlineTask(String description, LocalDateTime by) {
        super(description);
        this.dueDateTime = by;
    }

    /**
     * Returns the deadline date and time.
     *
     * @return the deadline date and time
     */
    public LocalDateTime getBy() {
        return dueDateTime;
    }

    /**
     * Returns this task with its deadline type marker and formatted due date.
     *
     * @return the user-facing deadline representation
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + DateTimeParser.formatDisplay(dueDateTime) + ")";
    }
}
