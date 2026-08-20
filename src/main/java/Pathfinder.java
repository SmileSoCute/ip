import java.util.Scanner;

public class Pathfinder {
    private static final String separator = "____________________________________________________________";

    public static void main(String[] args) {
        greetMessage();
        Scanner scanner = new Scanner(System.in);
        String msg = scanner.nextLine();
        TaskList task = new TaskList();
        while (true) {
            if (msg.equals("bye")) {
                break;
            }
            if (msg.equals("list")) {
                task.printList();
                msg = scanner.nextLine();
                continue;
            }
            task.addTask(msg);
            msg = scanner.nextLine();

        }
        scanner.close();
        System.out.println("Bye bye! Hope to see you around soon!");
        System.out.println(separator);


    }

    public static void echoMessage(String message) {
        System.out.println(separator);
        System.out.println(message);
        System.out.println(separator);
    }

    public static void greetMessage() {
        String banner = "/================\\\n"
                + "|   Pathfinder   |\n"
                + "\\================/\n";
        System.out.println(separator);
        System.out.print(banner);
        System.out.println("Hello friend! My name is Pathfinder.");
        System.out.println("What tasks can I do for you today?");
        System.out.println(separator);
    }
}
