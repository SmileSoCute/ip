/** Represents a user-correctable Pathfinder command error. */
public class PathfinderException extends Exception {
    /** Creates an exception containing a user-facing explanation. */
    public PathfinderException(String message) {
        super(message);
    }
}
