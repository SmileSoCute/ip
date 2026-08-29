package pathfinder.task;

/** Represents a task without an associated date or time. */
public class ToDoTask extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description description of the task
     */
    public ToDoTask(String description) {
        super(description);
    }

    /**
     * Returns this task with its todo type marker.
     *
     * @return the user-facing todo representation
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
