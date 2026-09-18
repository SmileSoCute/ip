package pathfinder.exception;

/** Represents a user-correctable Pathfinder command error. */
public class PathfinderException extends Exception {
    /**
     * Creates an exception containing a user-facing explanation.
     *
     * @param message explanation of the invalid command or input.
     */
    public PathfinderException(String message) {
        super(message);
    }
}
