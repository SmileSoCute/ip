public class List<T extends Describable> {
    private T[] list;
    private int count = 0;
    private final String SEPARATOR = "____________________________________________________________";


    public List() {
        @SuppressWarnings("unchecked")
        T[] temp = (T[]) new Describable[100];
        this.list = temp;
    }

    public T get(int index) {
        return this.list[index - 1];
    }

    public void add(T task) {
        list[count] = task;
        count++;
        System.out.println(SEPARATOR);
        System.out.println("Okay! I've got it friend! I've added this task:");
        System.out.println(" " + task);
        System.out.println("Alrighty currently u have " + count + " task(s) in the list yay!");
        System.out.println(SEPARATOR);
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < count; i++) {
            result += (i + 1) + ". " + this.list[i] + "\n";
        }
        return result;
    }

    public void printList() {
        System.out.println(SEPARATOR);
        String result = this.toString();
        System.out.print(result);
        System.out.println(SEPARATOR);

    }


}
