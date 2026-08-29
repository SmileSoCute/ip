package pathfinder.task;

/**
 * Stores up to 100 describable items and renders them as a numbered list.
 *
 * @param <T> type of item stored in the list.
 */
public class List<T extends Describable> {
    private static final String SEPARATOR = "____________________________________________________________";

    private final T[] items;
    private int count = 0;

    /** Creates an empty fixed-capacity list. */
    public List() {
        @SuppressWarnings("unchecked")
        T[] emptyItems = (T[]) new Describable[100];
        this.items = emptyItems;
    }

    /**
     * Returns the item identified by its one-based index.
     *
     * @param index One-based position of the item.
     * @return The item at the requested position.
     */
    public T get(int index) {
        return this.items[index - 1];
    }

    /**
     * Adds an item and displays confirmation on the console.
     *
     * @param task Item to add.
     */
    public void add(T task) {
        items[count] = task;
        count++;
        System.out.println(SEPARATOR);
        System.out.println("Okay! I've got it friend! I've added this task:");
        System.out.println(" " + task);
        System.out.println("Alrighty currently u have " + count + " task(s) in the list yay!");
        System.out.println(SEPARATOR);
    }

    /**
     * Returns all stored items as a numbered, newline-separated string.
     *
     * @return numbered representation of the stored items
     */
    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < count; i++) {
            result += (i + 1) + ". " + this.items[i] + "\n";
        }
        return result;
    }

    /** Displays the numbered list between separator lines. */
    public void printList() {
        System.out.println(SEPARATOR);
        String result = this.toString();
        System.out.print(result);
        System.out.println(SEPARATOR);
    }
}
