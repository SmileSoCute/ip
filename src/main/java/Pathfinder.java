import java.nio.file.Path;
import java.util.Scanner;

public class Pathfinder {
    private static final String SEPARATOR = "____________________________________________________________";

    public static void main(String[] args) {
        greetMessage();
        List<Task> task = new List<>();
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                if (input.equals("bye")) {
                    break;
                }
                try {
                    handleCommand(input,task);

                } catch (PathfinderException e) {
                    echoMessage(e.getMessage());
                }
            }
        }
        System.out.println("Bye bye! Hope to see you around soon!");
        System.out.println(SEPARATOR);

    }

    private static void handleCommand(String input, List<Task> tasks) throws PathfinderException {
        if (input.equals("list")) {
            tasks.printList();
            return;
        }
        if  (input.equals("mark ") || input.startsWith("mark")) {
            Task task = tasks.get(readTaskNumber(input,"mark"));
            task.doTask();
            echoMessage("Awesome sauce! I have marked this task up dude: \n" + task);
            return;
        }
        if  (input.equals("unmark ") || input.startsWith("unmark")) {
            Task task = tasks.get(readTaskNumber(input,"unmark"));
            task.undoTask();
            echoMessage("Alright man, I have unmarked this task for you: \n" + task);
            return;
        }

        switch (readTaskType(input)) {
            case TODO -> tasks.add(new ToDoTask(readDescription(input,"todo")));
            case EVENT -> addEvent(input,tasks);
            case DEADLINE -> addDeadline(input,tasks);
        }
    }



    private static Inputs readTaskType(String input) throws PathfinderException {
        if (input.isEmpty()) {
            throw new PathfinderException("Oh no friend! You didn't enter anything!");
        }

        String keyword = input.split("\\s+", 2)[0];
        try {
            return Inputs.valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new PathfinderException("Oopsies! I don't understand what you are saying!");
        }
    }

    private static String readDescription(String input, String command) throws PathfinderException {
        String description = input.substring(command.length()).trim();
        if (description.isEmpty()) {
            throw new PathfinderException("Oopsies! A " + command + "needs a description friend!");
        }
        return description;
    }

    private static int readTaskNumber(String input, String command) throws PathfinderException {
        String numberText = readDescription(input, command);
        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new PathfinderException("Oopsies! Please provide a valid task number friend!");
        }
    }

    private static void addDeadline(String input, List<Task> tasks) throws PathfinderException {
        String details = readDescription(input, "deadline");
        int byIndex = details.indexOf(" /by ");
        if (byIndex < 0) {
            throw new PathfinderException("Oopsies friend! A deadline needs '/by' followed by a date of time!");
        }
        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + 5).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new PathfinderException("Oopsies! A deadline needs a description and a '/by' value!");
        }
        tasks.add(new DeadlineTask(description, by));
    }

    private static void addEvent(String input, List<Task> tasks) throws PathfinderException {
        String details = readDescription(input, "event");
        int fromIndex = details.indexOf(" /from ");
        int toIndex = details.indexOf(" /to ");
        if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
            throw new PathfinderException("Oopsies! An event needs both '/from' and '/to' values.");
        }

        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + 7).trim();
        String to = details.substring(toIndex + 5).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new PathfinderException("Oops! An event needs a description, '/from', and '/to' value!");

        }
        tasks.add(new EventTask(description, from, to));
    }





    public static void echoMessage(String message) {
        System.out.println(SEPARATOR);
        System.out.println(message);
        System.out.println(SEPARATOR);
    }

    public static void greetMessage() {
        String banner = "/================\\\n"
                + "|   Pathfinder   |\n"
                + "\\================/\n";
        System.out.println(SEPARATOR);
        System.out.print(banner);
        System.out.println("Hello friend! My name is Pathfinder.");
        System.out.println("What tasks can I do for you today?");
        System.out.println(SEPARATOR);
    }
}


