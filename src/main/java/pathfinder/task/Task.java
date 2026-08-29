package pathfinder.task;

/** Represents a task with a description and completion status. */
public class Task implements Describable {
    private String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void doTask() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void undoTask() {
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
     * Returns the completion marker followed by the task description.
     *
     * @return the user-facing task representation
     */
    @Override
    public String toString() {
        return (isDone ? "[X] " : "[ ] ") + description;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    @Override
    public String getDescription() {
        return this.description;
    }
}
