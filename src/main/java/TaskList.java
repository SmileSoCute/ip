public class TaskList {
    private String[] taskList;
    private int taskCount = 0;
    private final String separator = "____________________________________________________________";


    public TaskList() {
        this.taskList = new String[100];
    }

    public void addTask(String task) {
        taskList[taskCount] = task;
        taskCount++;
        System.out.println(separator);
        System.out.println("added : " + task);
        System.out.println(separator);
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < taskCount; i++) {
            result += (i + 1) + ". " + taskList[i] + "\n";
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
