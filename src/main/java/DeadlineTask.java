public class DeadlineTask extends Task {
    private String by;

    public DeadlineTask(String description, String by) {
        super(description);
        this.by = by;
    }

    /** Returns the deadline value supplied by the user. */
    public String getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by + ")";
    }
}
