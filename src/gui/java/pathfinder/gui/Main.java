package pathfinder.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pathfinder.Pathfinder;

/** Starts the Pathfinder JavaFX user interface. */
public class Main extends Application {
    private final Pathfinder pathfinder = new Pathfinder();

    /** Creates and displays the main Pathfinder window. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            Scene scene = new Scene(mainWindow);
            stage.setScene(scene);
            stage.setTitle("Pathfinder");
            stage.setMinHeight(360);
            stage.setMinWidth(440);
            stage.setWidth(520);
            stage.setHeight(680);
            stage.setResizable(true);
            fxmlLoader.<MainWindow>getController().setPathfinder(pathfinder);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the main window.", exception);
        }
    }
}
