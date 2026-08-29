package pathfinder.task;

/** Represents a task with a description and completion status. */
public class Task implements Describable {
    private final String description;
    private boolean isDone;

    /** Creates an incomplete task with the given description. */
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

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return isDone;
    }

    @Override
    public String toString() {
        return (isDone ? "[X] " : "[ ] ") + description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
