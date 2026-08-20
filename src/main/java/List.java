public class List<T extends Describable> {
    private T[] list;
    private int count = 0;
    private final String separator = "____________________________________________________________";


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
        System.out.println(separator);
        System.out.println("added : " + task.getDescription());
        System.out.println(separator);
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
        System.out.println(separator);
        String result = this.toString();
        System.out.print(result);
        System.out.println(separator);

    }


}
