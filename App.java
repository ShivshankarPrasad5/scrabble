package indy;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * It's time for Indy! This is the main class to get things started.
 *
 * Class comments here...
 *
 */

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Create top-level object, set up the scene, and show the stage here.
        PaneOrganizer organizer = new PaneOrganizer();
        Scene scene = new Scene(organizer.getRoot(), Constants.SCENE_WIDTH,
                Constants.SCENE_HEIGHT + 2*Constants.SQUARE_DIM);
        stage.setTitle("Scrabble");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // launch is a method inherited from Application
    }
}
