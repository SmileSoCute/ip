package pathfinder.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import pathfinder.task.DeadlineTask;
import pathfinder.task.EventTask;
import pathfinder.task.Priority;
import pathfinder.task.Task;
import pathfinder.task.TodoTask;
import pathfinder.util.DateTimeParser;

/** Saves and loads Pathfinder tasks from a configured data file. */
public class Storage {
    private final Path dataFile;
    private int skippedLineCount;

    /**
     * Creates a storage manager that reads and writes the given file.
     *
     * @param dataFile path of the task data file.
     */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Returns the number of malformed lines skipped by the latest load.
     *
     * @return number of skipped malformed records.
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Loads valid tasks, skipping blank and malformed lines.
     *
     * @return tasks successfully read from the data file.
     * @throws IOException if the data file cannot be read.
     */
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
                Task task = parseTask(line);
                if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
                    skippedLineCount++;
                } else {
                    tasks.add(task);
                }
            } catch (IllegalArgumentException exception) {
                skippedLineCount++;
            }
        }
        return tasks;
    }

    /**
     * Replaces the data file with the current task list, using an atomic move where supported.
     *
     * @param tasks tasks to persist.
     * @throws IOException if the temporary or final data file cannot be written.
     */
    public void save(ArrayList<Task> tasks) throws IOException {
        Path parentDirectory = dataFile.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
        Path temporaryFile = dataFile.resolveSibling(dataFile.getFileName() + ".tmp");
        List<String> lines = tasks.stream().map(this::formatTask).toList();
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

    /**
     * Converts a task to an unambiguous, delimiter-safe storage record.
     *
     * @param task task to serialize.
     * @return serialized task record.
     * @throws IllegalArgumentException if the task type is unsupported.
     */
    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        String description = encode(task.getDescription());
        String priority = task.getPriority().name();
        if (task instanceof DeadlineTask deadline) {
            return "D | " + status + " | " + description + " | "
                    + encode(DateTimeParser.formatStored(deadline.getBy())) + " | " + priority;
        }
        if (task instanceof EventTask event) {
            return "E | " + status + " | " + description + " | "
                    + encode(DateTimeParser.formatStored(event.getFrom())) + " | "
                    + encode(DateTimeParser.formatStored(event.getTo())) + " | " + priority;
        }
        if (task instanceof TodoTask) {
            return "T | " + status + " | " + description + " | " + priority;
        }
        throw new IllegalArgumentException("Unsupported task class: " + task.getClass().getName());
    }

    /**
     * Parses either the current structured format or the legacy display format.
     *
     * @param line non-blank record from the data file.
     * @return parsed task.
     * @throws IllegalArgumentException if the record is malformed.
     */
    private Task parseTask(String line) {
        if (line.startsWith("[")) {
            return parseLegacyTask(line);
        }

        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3 || fields[0].length() != 1
                || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid task record");
        }

        int previousFieldCount = switch (fields[0]) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new IllegalArgumentException("Unknown task type");
        };
        requireFieldCount(fields, previousFieldCount, previousFieldCount + 1);

        String description = decodeRequired(fields[2]);
        Task task = switch (fields[0]) {
            case "T" -> new TodoTask(description);
            case "D" -> new DeadlineTask(description,
                    DateTimeParser.parseStored(decodeRequired(fields[3])));
            case "E" -> new EventTask(description,
                    DateTimeParser.parseStored(decodeRequired(fields[3])),
                    DateTimeParser.parseStored(decodeRequired(fields[4])));
            default -> throw new AssertionError("Task type should be validated");
        };

        Priority priority = fields.length == previousFieldCount
                ? Priority.NONE
                : Priority.valueOf(fields[previousFieldCount]);
        task.setPriority(priority);

        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses a record written in Pathfinder's previous display-based format.
     *
     * @param line legacy task record.
     * @return parsed task.
     * @throws IllegalArgumentException if the record is malformed.
     */
    private Task parseLegacyTask(String line) {
        if (line.length() < 8 || line.charAt(0) != '[' || line.charAt(2) != ']'
                || line.charAt(3) != '[' || line.charAt(5) != ']'
                || line.charAt(6) != ' ' || !(line.charAt(4) == 'X' || line.charAt(4) == ' ')) {
            throw new IllegalArgumentException("Invalid legacy task record");
        }

        char type = line.charAt(1);
        String details = line.substring(7);
        Task task = switch (type) {
            case 'T' -> new TodoTask(requireText(details));
            case 'D' -> parseLegacyDeadline(details);
            case 'E' -> parseLegacyEvent(details);
            default -> throw new IllegalArgumentException("Unknown legacy task type");
        };
        if (line.charAt(4) == 'X') {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses the description and due date from a legacy deadline record.
     *
     * @param details portion of the record following its task markers.
     * @return parsed deadline task.
     * @throws IllegalArgumentException if the legacy details are malformed.
     */
    private DeadlineTask parseLegacyDeadline(String details) {
        int byIndex = details.lastIndexOf(" (by: ");
        if (byIndex < 1 || !details.endsWith(")")) {
            throw new IllegalArgumentException("Invalid legacy deadline");
        }
        return new DeadlineTask(requireText(details.substring(0, byIndex)),
                DateTimeParser.parseLegacy(
                        requireText(details.substring(byIndex + 6, details.length() - 1))));
    }

    /**
     * Parses the description and time range from a legacy event record.
     *
     * @param details portion of the record following its task markers.
     * @return parsed event task.
     * @throws IllegalArgumentException if the legacy details are malformed.
     */
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

    /**
     * Encodes task text so separators in user input cannot corrupt the file format.
     *
     * @param text text to encode.
     * @return URL-safe Base64 text without padding.
     */
    private String encode(String text) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes one required, non-empty storage field.
     *
     * @param encoded URL-safe Base64 field.
     * @return decoded non-blank text.
     * @throws IllegalArgumentException if the field is not valid Base64 or is blank.
     */
    private String decodeRequired(String encoded) {
        try {
            return requireText(new String(Base64.getUrlDecoder().decode(encoded),
                    StandardCharsets.UTF_8));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid encoded field", exception);
        }
    }

    /**
     * Validates that stored text contains a non-whitespace value.
     *
     * @param text text to validate.
     * @return the original validated text.
     * @throws IllegalArgumentException if the text is blank.
     */
    private String requireText(String text) {
        if (text.isBlank()) {
            throw new IllegalArgumentException("Empty task field");
        }
        return text;
    }

    /**
     * Checks that a record contains exactly the fields required by its task type.
     *
     * @param fields fields parsed from the record.
     * @param expectedCounts permitted numbers of fields.
     * @throws IllegalArgumentException if the field count is not permitted.
     */
    private void requireFieldCount(String[] fields, int... expectedCounts) {
        for (int expectedCount : expectedCounts) {
            if (fields.length == expectedCount) {
                return;
            }
        }
        if (expectedCounts.length > 0) {
            throw new IllegalArgumentException("Incorrect field count");
        }
    }
}
