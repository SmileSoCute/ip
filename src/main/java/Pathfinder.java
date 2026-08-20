import java.util.Scanner;

public class Pathfinder {
    private static final String separator = "____________________________________________________________";

    public static void main(String[] args) {
        greetMessage();
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        List<Task> list = new List<>();
        while (true) {
            if (input.startsWith("mark")) {
                Task task = list.get(Integer.parseInt(input.substring(5)));
                task.doTask();
                echoMessage("Awesome sauce! I have marked this task up dude: \n" + task);
                input = scanner.nextLine();
                continue;
            }

            if  (input.startsWith("unmark")) {
                Task task = list.get(Integer.parseInt(input.substring(7)));
                task.undoTask();
                echoMessage("Alright man, I have unmarked this task for you: \n" + task);
                input = scanner.nextLine();

                continue;
            }
            if (input.equals("bye")) {
                break;
            }
            if (input.equals("list")) {
                list.printList();
                input = scanner.nextLine();
                continue;
            }
            list.add(new Task(input));
            input = scanner.nextLine();

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
