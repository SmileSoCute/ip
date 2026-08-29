package pathfinder.task;

import java.time.LocalDateTime;

import pathfinder.util.DateTimeParser;

public class DeadlineTask extends Task {
    private LocalDateTime by;

    public DeadlineTask(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /** Returns the deadline date and time. */
    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + DateTimeParser.formatDisplay(by) + ")";
    }
}
