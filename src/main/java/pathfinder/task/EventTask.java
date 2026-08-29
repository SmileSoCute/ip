package pathfinder.task;

import java.time.LocalDateTime;

import pathfinder.util.DateTimeParser;

/** Represents a task that occurs between a start and end date-time. */
public class EventTask extends Task {
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    /** Creates an incomplete event task with the given description and time range. */
    public EventTask(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.startDateTime = from;
        this.endDateTime = to;
    }

    /** Returns the event's starting value. */
    public LocalDateTime getFrom() {
        return startDateTime;
    }

    /** Returns the event's ending value. */
    public LocalDateTime getTo() {
        return endDateTime;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + DateTimeParser.formatDisplay(startDateTime) + " to: "
                + DateTimeParser.formatDisplay(endDateTime) + ")";
    }
}
