package pathfinder.task;

/** Represents a task without an associated date or time. */
public class TodoTask extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description Description of the task.
     */
    public TodoTask(String description) {
        super(description);
    }

    /**
     * Returns this task with its todo type marker.
     *
     * @return The user-facing todo representation.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
