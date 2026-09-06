package pathfinder.gui;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/** Represents one chat message with a speaker image and message text. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box using the reusable FXML layout.
     *
     * @param text text to display in the dialog.
     * @param image image representing the speaker.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a dialog box.", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /** Flips the dialog box to the left for Pathfinder messages. */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> nodes = FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(nodes);
        getChildren().setAll(nodes);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Adds a visual category to the dialog box when its command changes tasks.
     *
     * @param commandStyle category derived from the user's command.
     */
    private void changeDialogStyle(String commandStyle) {
        switch (commandStyle) {
            case "add" -> dialog.getStyleClass().add("add-label");
            case "mark" -> dialog.getStyleClass().add("marked-label");
            case "delete" -> dialog.getStyleClass().add("delete-label");
            default -> {
            }
        }
    }

    /**
     * Creates a right-aligned dialog box for a user's message.
     *
     * @param text text to display in the dialog.
     * @param image image representing the user.
     * @return configured user dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a left-aligned dialog box for a Pathfinder response.
     *
     * @param text text to display in the dialog.
     * @param image image representing Pathfinder.
     * @param commandStyle category used to color the response bubble.
     * @return configured Pathfinder dialog box.
     */
    public static DialogBox getPathfinderDialog(String text, Image image, String commandStyle) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.changeDialogStyle(commandStyle);
        return dialogBox;
    }
}
