package indy;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Game Class is the top level logic class
 * Contains an instance of Board and WordTree
 */
public class Game {
    private Pane pane;
    private Label label;
    private Board board;
    private int score1 = 0;
    private int score2 = 0;
    private int turn = 2;
    private GameSquare[][] gameSquareBoard;

    private ArrayList<Tile> p1Tiles;
    private ArrayList<Tile> p2Tiles;

    private WordTree wordTree;
    private Tile[][] tileBoard;
    private Label display;
    private Button moveButton;

    private ArrayList<Character> letters = new ArrayList<>(
            Arrays.asList('A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A',
                    'B', 'B', 'C', 'C', 'D', 'D', 'D', 'D',
                    'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E',
                    'F', 'F', 'G', 'G', 'G', 'H', 'H',
                    'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I',
                    'J', 'K', 'L', 'L', 'L', 'L', 'M', 'M',
                    'N', 'N', 'N', 'N', 'N', 'N',
                    'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O',
                    'P', 'P', 'Q', 'R', 'R', 'R', 'R', 'R', 'R',
                    'S', 'S', 'S', 'S', 'T', 'T', 'T', 'T', 'T', 'T',
                    'U', 'U', 'U', 'U', 'V', 'V', 'W', 'W', 'X',
                    'Y', 'Y', 'Z', ' ', ' '));

    /**
     * constructor of game class
     * initializes instance variables
     * initializes graphics and buttons
     * @param myPane parent Pane
     * @param myLabel score display label
     * @param moveButton submit move button
     */
    public Game(Pane myPane, Label myLabel, Button moveButton){
        this.pane = myPane;
        this.label = myLabel;
        this.display = new Label();
        this.board = new Board(this.pane);
        this.gameSquareBoard = this.board.getBoard();
        this.tileBoard = this.board.getTileBoard();
        this.update();
        this.p1Tiles = new ArrayList<>();
        this.p2Tiles = new ArrayList<>();
        this.setTileRack(this.turn);
        this.setupReset();
        this.setupGameOver();
        this.wordTree = new WordTree();
        this.moveButton = moveButton;
        this.moveButton.setOnAction((ActionEvent e) -> this.checkMove(this.turn));
    }

    /**
     * switches turns when a valid move is submitted
     */
    private void update(){
        this.switchTurns();
        this.label.setText("Player 1 Score: " + this.score1 + "   Player 2 Score: " +
                this.score2 + "   It's Player " + this.turn + "'s turn!");

    }

    /**
     * checks an addition of tiles, called when submit is pressed
     * @param player turn
     */
    private void checkMove(int player){
        int initialScore = 0;
        ArrayList<Tile> addedTiles = new ArrayList<>();

        //adds all newly added tiles to an array, addedTiles
        for (Tile[] tiles : this.tileBoard) {
            for (int j = 0; j < this.tileBoard[0].length; j++) {
                if (tiles[j] != null && tiles[j].isTemporary()) {
                    addedTiles.add(tiles[j]);
                }
            }
        }
        ArrayList<String> words = new ArrayList<>();
        //check if there are words and add scores
        if(player == 1){
            initialScore = this.score1;
            this.score1 += this.board.checkHorizWords(words);
            this.score1 += this.board.checkVertWords(words);
        } else {
            initialScore = this.score2;
            this.score2 += this.board.checkHorizWords(words);
            this.score2 += this.board.checkVertWords(words);
        }

        //if no tiles, displays to user to add tiles
        // turn does not switch and method is stopped with return
        if(words.isEmpty()){
            this.makeDisplayVisible("Add some tiles!");
            this.reset(addedTiles, initialScore);
            return;
        }

        //checks all words found against words in bst
        //if any word is not valid, reset score and return
        for (String word: words) {
            if(this.wordTree.search(word) == null){
                this.makeDisplayVisible(word + " is not a valid word");
                this.reset(addedTiles, initialScore);
                return;
            }
        }

        //now that words are checked, make tiles permanent
        //remove used tiles from player racks so they can be replaced
        for (Tile tile: addedTiles){
            tile.setFinal();
            if(this.turn == 1){
                this.p1Tiles.remove(tile);
            } else {
                this.p2Tiles.remove(tile);
            }
        }

        this.update();
        this.setTileRack(this.turn);
    }

    /**
     * switches turn to other player based on current player
     */
    private void switchTurns(){
        if (this.turn == 1){
            this.turn = 2;
        } else {
            this.turn = 1;
        }
    }

    /**
     * moves all added tiles to their original position on rack
     * clears board array and sets scores back to initial scores
     * @param addedTiles arrayList of newly added tiles to be checked
     * @param initialScore stores initial score in case a detected word is invalid
     */
    private void reset(ArrayList<Tile> addedTiles, int initialScore){
        for (Tile tile: addedTiles){
            tile.resetPos();
            this.tileBoard[tile.getRow()][tile.getCol()] = null;

        }
        if(this.turn ==1){
            this.score1 = initialScore;
        } else{
            this.score2 = initialScore;
        }
    }

    /**
     * adds tiles to current players rack until they have 7
     * removes/adds tiles to pane as needed based on whose turn it is
     * @param turn whose turn it is
     */
    private void setTileRack(int turn){
        if (turn == 1){
            //moves all old tiles to the left
            for(int i = 0; i < this.p1Tiles.size(); i++){
                this.p1Tiles.get(i).setX((i + 1) * Constants.SQUARE_DIM);
            }

            //adds new tiles to the right of current tiles
            while(this.p1Tiles.size() < 7){
                this.p1Tiles.add(new Tile( (this.p1Tiles.size() + 1) * Constants.SQUARE_DIM,
                        Constants.SCENE_HEIGHT, this.pane, this.gameSquareBoard,
                        this.tileBoard, this.letters));
            }

            //adds current player's tiles to pane
            for(Tile tile: this.p1Tiles){
                tile.addToPane();
            }
            //removes previous player's tiles from pane
            for(Tile tile: this.p2Tiles){
                if(tile.isTemporary()){
                    tile.removeFromPane();
                }
            }
        } else{
            //see comments above, just switched for p1 and p2
            for(int i = 0; i < this.p2Tiles.size(); i++){
                this.p2Tiles.get(i).setX((i + 1) * Constants.SQUARE_DIM);
            }

            while(this.p2Tiles.size() < 7){
                this.p2Tiles.add(new Tile( (this.p2Tiles.size() + 1) * Constants.SQUARE_DIM,
                        Constants.SCENE_HEIGHT, this.pane, this.gameSquareBoard,
                        this.tileBoard, this.letters));
            }

            for(Tile tile: this.p2Tiles){
                if(tile.isTemporary()){
                    tile.addToPane();
                }
            }

            for(Tile tile: this.p1Tiles){
                if(tile.isTemporary()){
                    tile.removeFromPane();
                }
            }
        }
    }

    /**
     * sets display screen graphically and adds it to pane
     * display is set to be removed when clicked
     * @param message message to be displayed
     */
    private void makeDisplayVisible(String message){
        this.display.setText(message + "\n Click anywhere to continue");
        this.display.setLayoutX(0);
        this.display.setLayoutY(0);
        this.display.setPrefSize(Constants.SCENE_WIDTH,
                Constants.SCENE_HEIGHT + Constants.SQUARE_DIM);
        this.display.setFont(new Font(Constants.DISPLAY_FONT));
        this.display.setStyle(Constants.DISPLAY_BACKGROUND);
        this.display.setAlignment(Pos.CENTER);
        this.display.setOnMouseClicked((MouseEvent) -> this.removeDisplay());
        this.moveButton.setVisible(false);
        this.pane.getChildren().add(this.display);

    }

    /**
     * called when display is clicked
     * removes display from screen and re-adds
     * submit move button to screen
     */
    private void removeDisplay(){
        this.pane.getChildren().remove(this.display);
        this.moveButton.setVisible(true);
    }

    /**
     * decides who the winner is and displays in score label
     * adds a transparent rectangle over board to prevent any mouse
     * interactions from interfering with current state of board
     */
    private void GameOver(){
        if(this.score1 > this.score2){
            this.label.setText("Player 1 wins!");
        } else if (this.score1 < this.score2){
            this.label.setText("Player 2 wins!");
        } else {
            this.label.setText("It's a tie!");
        }
        this.moveButton.setVisible(false);
        Rectangle rect = new Rectangle(0,0, Constants.SCENE_WIDTH,
                Constants.SCENE_HEIGHT + 2 * Constants.SQUARE_DIM);
        rect.setFill(Color.TRANSPARENT);
        this.pane.getChildren().add(rect);
        this.pane.setOnMouseDragged(null);
        this.moveButton.setVisible(false);
    }

    /**
     * sets up clickable label that resets move
     */
    private void setupReset(){
        Label resetLabel = new Label();
        resetLabel.setText("UNDO \n MOVE");
        resetLabel.setAlignment(Pos.CENTER);
        resetLabel.setLayoutX(0);
        resetLabel.setLayoutY(Constants.SCENE_HEIGHT);
        resetLabel.setPrefSize(Constants.SQUARE_DIM, Constants.SQUARE_DIM);
        resetLabel.setOnMouseClicked((MouseEvent event) -> this.reset());
        this.pane.getChildren().add(resetLabel);
    }

    /**
     * sets up clickable label that ends game
     */
    private void setupGameOver(){
        Label endLabel = new Label();
        endLabel.setText("NO VALID \n MOVES?");
        endLabel.setAlignment(Pos.CENTER);
        endLabel.setLayoutX(Constants.SCENE_WIDTH - Constants.SQUARE_DIM);
        endLabel.setLayoutY(Constants.SCENE_HEIGHT);
        endLabel.setPrefSize(Constants.SQUARE_DIM, Constants.SQUARE_DIM);
        endLabel.setOnMouseClicked((MouseEvent event) -> this.GameOver());
        this.pane.getChildren().add(endLabel);

    }

    /**
     * called when Undo Move label is clicked
     * sets all added tiles back to their original position
     * clears tile board
     */
    private void reset(){
        for (int i = 0; i < this.tileBoard.length; i++){
            for (int j = 0; j < this.tileBoard[0].length; j++){
                if(this.tileBoard[i][j] != null && this.tileBoard[i][j].isTemporary()){
                    this.tileBoard[i][j].resetPos();
                    this.tileBoard[i][j] = null;
                }
            }
        }
    }

}
