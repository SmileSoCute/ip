package pathfinder.task;

/** Represents the importance assigned to a task. */
public enum Priority {
    HIGH,
    MEDIUM,
    LOW,
    NONE;

    /**
     * Returns the priority marker used in task displays.
     *
     * @return uppercase marker, or an empty string when no priority is assigned.
     */
    public String getDisplayMarker() {
        return this == NONE ? "" : "[" + name() + "]";
    }
}
