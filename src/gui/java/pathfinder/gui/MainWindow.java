package pathfinder.gui;

import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import pathfinder.Pathfinder;

/** Controls user input and messages in the main Pathfinder window. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    // The avatar images were generated with OpenAI's image generation tool.
    private final Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image pathfinderImage = new Image(getClass().getResourceAsStream("/images/DaDuke.png"));
    private Pathfinder pathfinder;

    /** Configures scrolling after the FXML controls have been injected. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Pathfinder command engine and displays its welcome message.
     *
     * @param pathfinder command engine that processes user input.
     */
    public void setPathfinder(Pathfinder pathfinder) {
        this.pathfinder = pathfinder;
        dialogContainer.getChildren().add(DialogBox.getPathfinderDialog(
                "Hello friend! My name is Pathfinder.\nWhat tasks can I do for you today?",
                pathfinderImage, ""));
        if (!pathfinder.getStartupMessage().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getPathfinderDialog(
                    pathfinder.getStartupMessage(), pathfinderImage, ""));
        }
    }

    /** Adds the user's command and Pathfinder's response to the chat. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = pathfinder.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getPathfinderDialog(response, pathfinderImage, getCommandStyle(input))
        );
        userInput.clear();
    }

    /**
     * Returns the style category associated with a task-changing command.
     *
     * @param input raw user command.
     * @return style category for the response bubble.
     */
    private String getCommandStyle(String input) {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return "";
        }

        String command = trimmedInput.split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
        return switch (command) {
            case "todo", "deadline", "event" -> "add";
            case "mark", "unmark" -> "mark";
            case "delete" -> "delete";
            default -> "";
        };
    }
}
