package pathfinder.task;

import java.time.LocalDateTime;

import pathfinder.util.DateTimeParser;

public class EventTask extends Task {
    private LocalDateTime from;
    private LocalDateTime to;

    public EventTask(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns the event's starting value. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event's ending value. */
    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + DateTimeParser.formatDisplay(from) + " to: "
                + DateTimeParser.formatDisplay(to) + ")";
    }
}
