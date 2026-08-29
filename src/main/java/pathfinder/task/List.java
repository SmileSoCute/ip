package pathfinder.task;

/**
 * Stores up to 100 describable items and renders them as a numbered list.
 *
 * @param <T> type of item stored in the list
 */
public class List<T extends Describable> {
    private T[] list;
    private int count = 0;
    private final String SEPARATOR = "____________________________________________________________";

    /** Creates an empty fixed-capacity list. */
    public List() {
        @SuppressWarnings("unchecked")
        T[] temp = (T[]) new Describable[100];
        this.list = temp;
    }

    /**
     * Returns the item identified by its one-based index.
     *
     * @param index one-based position of the item
     * @return the item at the requested position
     */
    public T get(int index) {
        return this.list[index - 1];
    }

    /**
     * Adds an item and displays confirmation on the console.
     *
     * @param task item to add
     */
    public void add(T task) {
        list[count] = task;
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
            result += (i + 1) + ". " + this.list[i] + "\n";
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
