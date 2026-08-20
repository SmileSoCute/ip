import java.util.Scanner;

public class Pathfinder {
    private static final String separator = "____________________________________________________________";

    public static void main(String[] args) {
        greetMessage();

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        List<Task> list = new List<>();

        while (true) {
            if (input.equals("bye")) {
                break;
            }
            if (input.equals("list")) {
                list.printList();
                input = scanner.nextLine();
                continue;
            }
            if (input.startsWith("mark ")) {
                Task task = list.get(Integer.parseInt(input.substring(5)));
                task.doTask();
                echoMessage("Awesome sauce! I have marked this task up dude: \n" + task);
                input = scanner.nextLine();
                continue;
            }

            if (input.startsWith("unmark ")) {
                Task task = list.get(Integer.parseInt(input.substring(7)));
                task.undoTask();
                echoMessage("Alright man, I have unmarked this task for you: \n" + task);
                input = scanner.nextLine();
                continue;
            }

            if (input.startsWith("todo ")) {
                list.add(new ToDoTask(input.substring(5)));
            } else if  (input.startsWith("deadline ")) {
                int byIndex = input.indexOf(" /by ");
                String description = input.substring(9, byIndex);
                String by = input.substring(byIndex + 5);

                list.add(new DeadlineTask(description, by));
            } else if (input.startsWith("event ")) {
                int fromIndex = input.indexOf(" /from ");
                int toIndex = input.indexOf(" /to ");

                String description = input.substring(6, fromIndex);
                String from = input.substring(fromIndex + 7, toIndex);
                String to = input.substring(toIndex + 5);

                list.add(new EventTask(description, from, to));
            }
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
