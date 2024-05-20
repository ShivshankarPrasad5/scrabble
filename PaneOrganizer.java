package indy;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;


/**
 * This is the PaneOrganizer class, the top level graphical class. Graphical elements
 * such as the buttonPane and gamePane are instantiated and organized here. PaneOrganizer
 * additionally contains top-level logic class Game.
 */
public class PaneOrganizer {
    private BorderPane root;
    private Pane gamePane;
    private StackPane buttonPane;
    private Label label;
    private Button moveButton;

    /**
     * constructor initializes root pane, creates necessary panes, and
     * instantiates a new Game.
     */
    public PaneOrganizer() {
        this.root = new BorderPane();
        this.gamePane = this.createGamePane();
        this.createButtonPane();
        new Game(this.gamePane, this.label, this.moveButton);

    }

    /**
     * Accessor method for the root.
     */
    public BorderPane getRoot() {
        return (this.root);
    }

    /**
     * creates the GamePane (parent pane)
     * @return returns the pane so other classes can know about it
     */
    public Pane createGamePane() {
        Pane pane = new Pane();
        this.root.setCenter(pane);
        return pane;
    }

    /**
     * creates buttonPane and adds quit and submit buttons
     */
    private void createButtonPane(){
        //Sets up button visually
        this.buttonPane = new StackPane();
        this.buttonPane.setPrefSize(Constants.SCENE_WIDTH, Constants.SQUARE_DIM);
        this.buttonPane.setAlignment(Pos.CENTER_LEFT);
        this.root.setBottom(this.buttonPane);

        //Creates quit button
        Button quitButton = new Button("Quit");
        quitButton.setTranslateX(Constants.SCENE_WIDTH - Constants.SQUARE_DIM);

        //Quits the program once clicked
        quitButton.setOnAction((ActionEvent e) -> System.exit(0));
        this.buttonPane.setFocusTraversable(false);
        quitButton.setFocusTraversable(false);

        //adds a label that will contain the score and line count
        this.label = new Label();
        this.label.setTranslateX(Constants.SCORE_LABEL_X);

        //adds button to submit moves
        this.moveButton = new Button("Submit Move");
        this.moveButton.setTranslateX(Constants.SCENE_WIDTH - Constants.MOVE_BUTTON_OFFSET);

        this.buttonPane.getChildren().addAll(quitButton, this.label, this.moveButton);
    }
}