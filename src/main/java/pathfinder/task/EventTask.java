package pathfinder.task;

import java.time.LocalDateTime;

import pathfinder.util.DateTimeParser;

/** Represents a task that occurs between a start and end date-time. */
public class EventTask extends Task {
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    /**
     * Creates an incomplete event task.
     *
     * @param description Description of the event.
     * @param from Date and time at which the event starts.
     * @param to Date and time at which the event ends.
     */
    public EventTask(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.startDateTime = from;
        this.endDateTime = to;
    }

    /**
     * Returns the event's starting date and time.
     *
     * @return the event start.
     */
    public LocalDateTime getFrom() {
        return startDateTime;
    }

    /**
     * Returns the event's ending date and time.
     *
     * @return the event end.
     */
    public LocalDateTime getTo() {
        return endDateTime;
    }

    /**
     * Returns whether another task has the same description and time range.
     *
     * @param other task to compare with this event.
     * @return whether both events have the same user-entered details.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && other instanceof EventTask event
                && startDateTime.equals(event.startDateTime)
                && endDateTime.equals(event.endDateTime);
    }

    /**
     * Returns this task with its event type marker and formatted time range.
     *
     * @return the user-facing event representation.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + DateTimeParser.formatDisplay(startDateTime) + " to: "
                + DateTimeParser.formatDisplay(endDateTime) + ")";
    }
}
