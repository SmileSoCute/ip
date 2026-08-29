package pathfinder.task;

import java.time.LocalDateTime;

import pathfinder.util.DateTimeParser;

/** Represents a task that occurs between a start and end date-time. */
public class EventTask extends Task {
    private LocalDateTime from;
    private LocalDateTime to;

    /**
     * Creates an incomplete event task.
     *
     * @param description description of the event
     * @param from date and time at which the event starts
     * @param to date and time at which the event ends
     */
    public EventTask(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's starting date and time.
     *
     * @return the event start
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event's ending date and time.
     *
     * @return the event end
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns this task with its event type marker and formatted time range.
     *
     * @return the user-facing event representation
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + DateTimeParser.formatDisplay(from) + " to: "
                + DateTimeParser.formatDisplay(to) + ")";
    }
}
