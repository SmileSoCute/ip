package pathfinder.parser;

/** Identifies the supported commands that create tasks. */
public enum Inputs {
    /** Command that creates a task with a due date and time. */
    DEADLINE,

    /** Command that creates a task without a date or time. */
    TODO,

    /** Command that creates a task with a start and end date-time. */
    EVENT,
}
