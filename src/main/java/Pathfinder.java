import java.util.Scanner;

public class Pathfinder {
    private static final String separator = "____________________________________________________________";

    public static void main(String[] args) {
        String banner = "/================\\\n"
                + "|   Pathfinder   |\n"
                + "\\================/\n";
        System.out.println(separator);
        System.out.print(banner);
        System.out.println("Hello friend! My name is Pathfinder.");
        System.out.println("What tasks can I do for you today?");
        System.out.println(separator);
        Scanner scanner = new Scanner(System.in);
        String msg = scanner.nextLine();
        while (!msg.equals("bye")) {
            echoMessage(msg);
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
}
