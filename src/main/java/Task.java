public class Task implements Describable {
    private String description;
    private boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void doTask() {
        this.isDone = true;
    }

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
