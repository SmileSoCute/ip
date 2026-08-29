package pathfinder.task;

/** Represents a task without an associated date or time. */
public class TodoTask extends Task {
    /** Creates an incomplete todo task with the given description. */
    public TodoTask(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
