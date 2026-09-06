package pathfinder;

import javafx.application.Application;
import pathfinder.gui.Main;

/** Launches the JavaFX application without directly starting an Application subclass. */
public final class Launcher {
    private Launcher() {
    }

    /** Launches the Pathfinder JavaFX user interface. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
