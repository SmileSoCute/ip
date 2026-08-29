import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class Pathfinder {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final Path DATA_FILE = Path.of("data", "pathfinder.txt");

    public static void main(String[] args) throws IOException {
        greetMessage();
        ArrayList<Task> tasks = loadTasks();
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

    private static void handleCommand(String input, ArrayList<Task> tasks)
            throws PathfinderException, IOException {
        if (input.equals("list")) {
            printList(tasks);
            return;
        }
        if  (input.equals("mark ") || input.startsWith("mark")) {
            Task task = getTasks(tasks ,readTaskNumber(input,"mark"));
            task.doTask();
            saveTasks(tasks);
            echoMessage("Awesome sauce! I have marked this task up dude:\n" + task);
            return;
        }
        if  (input.equals("unmark ") || input.startsWith("unmark")) {
            Task task = getTasks(tasks, readTaskNumber(input,"unmark"));
            task.undoTask();
            saveTasks(tasks);
            echoMessage("Alright man, I have unmarked this task for you: \n" + task);
            return;
        }
        if (input.equals("delete") ||  input.startsWith("delete ")) {
            deleteTask(tasks, readTaskNumber(input,"delete"));
            return;
        }

        switch (readTaskType(input)) {
            case TODO -> addTask(tasks, new ToDoTask(readDescription(input,"todo")));
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

    private static void addDeadline(String input, ArrayList<Task> tasks)
            throws PathfinderException, IOException {
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

    private static void addEvent(String input, ArrayList<Task> tasks)
            throws PathfinderException, IOException {
        String details = readDescription(input, "event");
        int fromIndex = details.indexOf(" /from ");
        int toIndex = details.indexOf(" /to ");
        if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
            throw new PathfinderException("Oopsies! An event needs both '/from' and '/to' values.");
        }

        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + 7, toIndex).trim();
        String to = details.substring(toIndex + 5).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new PathfinderException("Oops! An event needs a description, '/from', and '/to' value!");

        }
        addTask(tasks, new EventTask(description, from, to));
    }

    private static void deleteTask(ArrayList<Task> tasks, int number)
            throws PathfinderException, IOException {
        Task removed = getTasks(tasks, number);
        tasks.remove(number - 1);
        saveTasks(tasks);
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

    private static void addTask(ArrayList<Task> tasks, Task task) throws IOException {
        tasks.add(task);
        saveTasks(tasks);
        System.out.println(SEPARATOR);
        System.out.println("Okay! I've got it friend! I've added this task:");
        System.out.println(" " + task);
        System.out.println("Alrighty currently u have " + tasks.size() + " task(s) in the list yay!");
        System.out.println(SEPARATOR);
    }

    /** Saves the current task list to the project's data file. */
    private static void saveTasks(ArrayList<Task> tasks) throws IOException {
        Files.createDirectories(DATA_FILE.getParent());
        java.util.List<String> taskLines = tasks.stream()
                .map(Task::toString)
                .toList();
        Files.write(DATA_FILE, taskLines);
    }

    /** Loads all previously saved tasks, or an empty list if no data file exists. */
    private static ArrayList<Task> loadTasks() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(DATA_FILE)) {
            return tasks;
        }

        for (String line : Files.readAllLines(DATA_FILE)) {
            if (!line.isBlank()) {
                tasks.add(parseSavedTask(line));
            }
        }
        return tasks;
    }

    /** Converts one saved line back into its corresponding task object. */
    private static Task parseSavedTask(String line) throws IOException {
        if (line.length() < 7 || line.charAt(1) == ' ' || line.charAt(6) != ' ') {
            throw new IOException("Invalid task data: " + line);
        }

        char taskType = line.charAt(1);
        boolean isDone = line.charAt(4) == 'X';
        String details = line.substring(7);
        Task task;

        switch (taskType) {
        case 'T' -> task = new ToDoTask(details);
        case 'D' -> task = parseSavedDeadline(details, line);
        case 'E' -> task = parseSavedEvent(details, line);
        default -> throw new IOException("Unknown task type in data: " + line);
        }

        if (isDone) {
            task.doTask();
        }
        return task;
    }

    /** Recreates a deadline from its saved description and deadline value. */
    private static DeadlineTask parseSavedDeadline(String details, String originalLine)
            throws IOException {
        int byIndex = details.lastIndexOf(" (by: ");
        if (byIndex < 0 || !details.endsWith(")")) {
            throw new IOException("Invalid deadline data: " + originalLine);
        }
        String description = details.substring(0, byIndex);
        String by = details.substring(byIndex + 6, details.length() - 1);
        return new DeadlineTask(description, by);
    }

    /** Recreates an event from its saved description, start, and end values. */
    private static EventTask parseSavedEvent(String details, String originalLine)
            throws IOException {
        int fromIndex = details.lastIndexOf(" (from: ");
        int toIndex = details.lastIndexOf(" to: ");
        if (fromIndex < 0 || toIndex <= fromIndex || !details.endsWith(")")) {
            throw new IOException("Invalid event data: " + originalLine);
        }
        String description = details.substring(0, fromIndex);
        String from = details.substring(fromIndex + 8, toIndex);
        String to = details.substring(toIndex + 5, details.length() - 1);
        return new EventTask(description, from, to);
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
