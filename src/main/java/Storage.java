import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;

/** Saves and loads Pathfinder tasks from a fixed data file. */
public class Storage {
    private final Path dataFile;
    private int skippedLineCount;

    /** Creates storage that reads and writes the given file. */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /** Returns the number of malformed lines skipped by the latest load. */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /** Loads valid tasks, skipping blank and malformed lines. */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        skippedLineCount = 0;
        if (!Files.exists(dataFile)) {
            return tasks;
        }

        for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (IllegalArgumentException exception) {
                skippedLineCount++;
            }
        }
        return tasks;
    }

    /** Atomically replaces the data file with the current task list where supported. */
    public void save(ArrayList<Task> tasks) throws IOException {
        Files.createDirectories(dataFile.getParent());
        Path temporaryFile = dataFile.resolveSibling(dataFile.getFileName() + ".tmp");
        java.util.List<String> lines = tasks.stream().map(this::formatTask).toList();
        Files.write(temporaryFile, lines, StandardCharsets.UTF_8);

        try {
            Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /** Converts a task to an unambiguous, delimiter-safe storage record. */
    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        String description = encode(task.getDescription());
        if (task instanceof DeadlineTask deadline) {
            return "D | " + status + " | " + description + " | "
                    + encode(DateTimeParser.formatStored(deadline.getBy()));
        }
        if (task instanceof EventTask event) {
            return "E | " + status + " | " + description + " | "
                    + encode(DateTimeParser.formatStored(event.getFrom())) + " | "
                    + encode(DateTimeParser.formatStored(event.getTo()));
        }
        if (task instanceof ToDoTask) {
            return "T | " + status + " | " + description;
        }
        throw new IllegalArgumentException("Unsupported task class: " + task.getClass().getName());
    }

    /** Parses either the current structured format or the legacy display format. */
    private Task parseTask(String line) {
        if (line.startsWith("[")) {
            return parseLegacyTask(line);
        }

        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3 || fields[0].length() != 1
                || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid task record");
        }

        String description = decodeRequired(fields[2]);
        Task task = switch (fields[0]) {
        case "T" -> {
            requireFieldCount(fields, 3);
            yield new ToDoTask(description);
        }
        case "D" -> {
            requireFieldCount(fields, 4);
            yield new DeadlineTask(description,
                    DateTimeParser.parseStored(decodeRequired(fields[3])));
        }
        case "E" -> {
            requireFieldCount(fields, 5);
            yield new EventTask(description,
                    DateTimeParser.parseStored(decodeRequired(fields[3])),
                    DateTimeParser.parseStored(decodeRequired(fields[4])));
        }
        default -> throw new IllegalArgumentException("Unknown task type");
        };

        if (fields[1].equals("1")) {
            task.doTask();
        }
        return task;
    }

    /** Parses files written by Pathfinder's previous display-based format. */
    private Task parseLegacyTask(String line) {
        if (line.length() < 8 || line.charAt(0) != '[' || line.charAt(2) != ']'
                || line.charAt(3) != '[' || line.charAt(5) != ']'
                || line.charAt(6) != ' ' || !(line.charAt(4) == 'X' || line.charAt(4) == ' ')) {
            throw new IllegalArgumentException("Invalid legacy task record");
        }

        char type = line.charAt(1);
        String details = line.substring(7);
        Task task = switch (type) {
        case 'T' -> new ToDoTask(requireText(details));
        case 'D' -> parseLegacyDeadline(details);
        case 'E' -> parseLegacyEvent(details);
        default -> throw new IllegalArgumentException("Unknown legacy task type");
        };
        if (line.charAt(4) == 'X') {
            task.doTask();
        }
        return task;
    }

    /** Parses a legacy deadline line. */
    private DeadlineTask parseLegacyDeadline(String details) {
        int byIndex = details.lastIndexOf(" (by: ");
        if (byIndex < 1 || !details.endsWith(")")) {
            throw new IllegalArgumentException("Invalid legacy deadline");
        }
        return new DeadlineTask(requireText(details.substring(0, byIndex)),
                DateTimeParser.parseLegacy(
                        requireText(details.substring(byIndex + 6, details.length() - 1))));
    }

    /** Parses a legacy event line. */
    private EventTask parseLegacyEvent(String details) {
        int fromIndex = details.lastIndexOf(" (from: ");
        int toIndex = details.lastIndexOf(" to: ");
        if (fromIndex < 1 || toIndex <= fromIndex || !details.endsWith(")")) {
            throw new IllegalArgumentException("Invalid legacy event");
        }
        LocalDateTime from = DateTimeParser.parseLegacy(
                requireText(details.substring(fromIndex + 8, toIndex)));
        LocalDateTime to = DateTimeParser.parseLegacy(
                requireText(details.substring(toIndex + 5, details.length() - 1)));
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("Legacy event end is not after its start");
        }
        return new EventTask(requireText(details.substring(0, fromIndex)), from, to);
    }

    /** Encodes task text so separators in user input cannot corrupt the file format. */
    private String encode(String text) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /** Decodes one required, non-empty storage field. */
    private String decodeRequired(String encoded) {
        try {
            return requireText(new String(Base64.getUrlDecoder().decode(encoded),
                    StandardCharsets.UTF_8));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid encoded field", exception);
        }
    }

    /** Rejects missing or whitespace-only stored values. */
    private String requireText(String text) {
        if (text.isBlank()) {
            throw new IllegalArgumentException("Empty task field");
        }
        return text;
    }

    /** Checks that a record contains exactly the fields required by its task type. */
    private void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("Incorrect field count");
        }
    }
}
