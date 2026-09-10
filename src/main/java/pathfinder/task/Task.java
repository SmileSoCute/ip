package pathfinder.task;

/** Represents a task with a description and completion status. */
public class Task {
    private final String description;
    private boolean isDone;
    private Priority priority;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description should not be null";
        assert !description.isBlank() : "Task description should not be blank";
        this.description = description;
        this.isDone = false;
        this.priority = Priority.NONE;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsIncomplete() {
        this.isDone = false;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if the task is complete, otherwise {@code false}
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the priority assigned to this task.
     *
     * @return assigned priority.
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Changes the priority assigned to this task.
     *
     * @param priority new priority.
     */
    public void setPriority(Priority priority) {
        assert priority != null : "Task priority should not be null";
        this.priority = priority;
    }

    /**
     * Returns the completion marker followed by the task description.
     *
     * @return The user-facing task representation.
     */
    @Override
    public String toString() {
        return (isDone ? "[X]" : "[ ]") + priority.getDisplayMarker() + " " + description;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return this.description;
    }
}
