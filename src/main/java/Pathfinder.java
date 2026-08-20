import java.util.ArrayList;
import java.util.Scanner;

public class Pathfinder {
    private static final String SEPARATOR = "____________________________________________________________";

    public static void main(String[] args) {
        greetMessage();
        ArrayList<Task> tasks = new ArrayList<>();
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                if (input.equals("bye")) {
                    break;
                }
                try {
                    handleCommand(input,tasks);

                } catch (PathfinderException e) {
                    echoMessage(e.getMessage());
                }
            }
        }
        System.out.println("Bye bye! Hope to see you around soon!");
        System.out.println(SEPARATOR);

    }

    private static void handleCommand(String input, ArrayList<Task> tasks) throws PathfinderException {
        if (input.equals("list")) {
            printList(tasks);
            return;
        }
        if  (input.equals("mark ") || input.startsWith("mark")) {
            Task task = getTasks(tasks ,readTaskNumber(input,"mark"));
            task.doTask();
            echoMessage("Awesome sauce! I have marked this task up dude: \n" + task);
            return;
        }
        if  (input.equals("unmark ") || input.startsWith("unmark")) {
            Task task = getTasks(tasks, readTaskNumber(input,"unmark"));
            task.undoTask();
            echoMessage("Alright man, I have unmarked this task for you: \n" + task);
            return;
        }
        if (input.equals("delete") ||  input.startsWith("delete ")) {
            deleteTask(tasks, readTaskNumber(input,"delete"));
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

    private static void addDeadline(String input, ArrayList<Task> tasks) throws PathfinderException {
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
        addTask(tasks, new DeadlineTask(description, by));
    }

    private static void addEvent(String input, ArrayList<Task> tasks) throws PathfinderException {
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
        addTask(tasks, new EventTask(description, from, to));
    }

    private static void deleteTask(ArrayList<Task> tasks, int number) throws PathfinderException {
        Task removed = getTasks(tasks, number);
        tasks.remove(number - 1);
        echoMessage("Got it my friend! I've removed this task:\n " + removed + "\n Alrighty currently you have " + tasks.size() + " task(s) in the list yay!");
    }

    private static Task getTasks(ArrayList<Task> tasks, int number) throws PathfinderException {
        if (number < 1 || number > tasks.size()) {
            throw new PathfinderException("Oopsies! That tasks number doesn't exist my friend!");
        }
        return tasks.get(number - 1);
    }

    private static void printList(ArrayList<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }
        echoMessage(sb.toString());
    }

    private static void addTask(ArrayList<Task> tasks, Task task) {
        tasks.add(task);
        System.out.println(SEPARATOR);
        System.out.println("Okay! I've got it friend! I've added this task:");
        System.out.println(" " + task);
        System.out.println("Alrighty currently u have " + tasks.size() + " task(s) in the list yay!");
        System.out.println(SEPARATOR);
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


