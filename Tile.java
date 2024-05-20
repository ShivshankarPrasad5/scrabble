package indy;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.util.ArrayList;

/**
 * Tile Class inherits from GameSquare
 * represents the tiles used in the game
 */
public class Tile extends GameSquare{
    private ArrayList<Character> letters;
    private Pane pane;
    public char letter;
    private Text letterText;
    private Rectangle clickable;
    private Rectangle background;
    private Text scoreText;
    private int score;
    private GameSquare[][] gameSquareBoard;
    private boolean temporary;
    private int initialX;
    private int initialY;
    private int rowIndex;
    private int colIndex;
    private Tile[][] tileBoard;
    private Label blankTileLabel;

    /**
     * constructor for Tile initializes all instance variables
     * calls setup methods
     * @param x x location
     * @param y y location
     * @param myPane parent pane
     * @param myGameSquareBoard gameSquareBoard
     * @param myTileBoard tileBoard
     * @param myLetters pool of letters (set to actual Scrabble probability distribution)
     */
    public Tile(int x, int y, Pane myPane, GameSquare [][] myGameSquareBoard,
                Tile [][] myTileBoard, ArrayList<Character> myLetters){
        super(x, y, myPane);
        this.pane = myPane;
        this.temporary = true;
        this.gameSquareBoard = myGameSquareBoard;
        this.tileBoard = myTileBoard;
        this.initialX = x;
        this.initialY = y;
        this.letters = myLetters;
        this.setBackground(x, y);
        this.blankTileLabel = new Label();
        this.setupBlankLabel();
        this.letterText = new Text();
        this.scoreText = new Text();
        this.score = 0;
        this.clickable = new Rectangle(x, y, Constants.SQUARE_DIM, Constants.SQUARE_DIM);
        this.setLetter(x, y);
        this.setupScoreLabel(x, y);
        this.setupClickable();
    }

    /**
     * overrides background to be in tile color
     * @param x x location
     * @param y y location
     */
    @Override
    protected void setBackground(int x, int y){
        this.background = new Rectangle(x, y, Constants.SQUARE_DIM, Constants.SQUARE_DIM);
        this.background.setFill(Constants.TILE_COLOR);
    }

    /**
     * moves tile along with mouse if it is clicked and dragged
     * @param event mouseDragged
     */
    public void move(MouseEvent event){
        //you cannot move tiles that have already been scored
        if(!this.temporary){
            return;
        }
        //moves all layers of tile
        this.background.setX(event.getX());
        this.background.setY(event.getY());
        this.letterText.setX(event.getX() + Constants.LETTER_OFFSET);
        this.letterText.setY(event.getY() + Constants.SQUARE_DIM - Constants.LETTER_OFFSET);
        this.scoreText.setY(event.getY() + Constants.SQUARE_DIM - Constants.SCORE_YOFFSET);
        this.scoreText.setX(event.getX() + Constants.SCORE_XOFFSET);
        this.clickable.setX(event.getX());
        this.clickable.setY(event.getY());
    }

    /**
     * "locks" tile into nearest gameSquare location
     * @param event mouseReleased
     */
    private void setLocation(MouseEvent event){
        //cannot move a tile that has already been scored
        if(!this.temporary){
            return;
        }

        //can only move a blank tile once you have picked a letter for it
        if(this.letter == ' '){
            this.handleBlankTile();
        }

        int boardX = Constants.SQUARE_DIM * (int) (event.getX() / Constants.SQUARE_DIM);
        int boardY = Constants.SQUARE_DIM * (int) (event.getY() / Constants.SQUARE_DIM);
        //check if the spot you want to move to is valid
        //otherwise return to original position
        if(!this.isValidSpot(boardY / Constants.SQUARE_DIM, boardX /Constants.SQUARE_DIM)){
            this.resetPos();
            return;
        }

        //if intended spot is empty, move all layers of tile to that spot
        //add tile to tileBoard in appropriate spot
        if(this.tileBoard[boardY / Constants.SQUARE_DIM][boardX /Constants.SQUARE_DIM] == null){
            this.background.setX(boardX);
            this.background.setY(boardY);
            this.letterText.setX(boardX + Constants.LETTER_OFFSET);
            this.letterText.setY(boardY + Constants.SQUARE_DIM - Constants.LETTER_OFFSET);
            this.scoreText.setY(boardY + Constants.SQUARE_DIM - Constants.SCORE_YOFFSET);
            this.scoreText.setX(boardX + Constants.SCORE_XOFFSET);
            this.clickable.setX(boardX);
            this.clickable.setY(boardY);
            this.rowIndex = boardY / Constants.SQUARE_DIM;
            this.colIndex = boardX /Constants.SQUARE_DIM;
            this.tileBoard[this.rowIndex][this.colIndex] = this;
        } else{
            this.resetPos();
        }
    }

    /**
     * picks a letter for tile from pool of remaining letters
     * @param x x location
     * @param y y location
     */
    private void setLetter(int x, int y){
        int num = (int) (this.letters.size() * Math.random());
        this.letter = this.letters.get(num);
        this.letters.remove(num);
        this.setupLetterText(x, y);
    }

    /**
     * graphically sets up letter via javafx Text
     * @param x x location
     * @param y y location
     */
    private void setupLetterText(int x, int y){
        this.letterText.setText(String.valueOf(this.letter));
        this.letterText.setTextAlignment(TextAlignment.CENTER);
        this.letterText.resize(Constants.SQUARE_DIM, Constants.SQUARE_DIM);
        this.letterText.setFont(new Font(Constants.TILE_TEXT_SIZE));
        this.letterText.setY(y + Constants.SQUARE_DIM - Constants.LETTER_OFFSET);
        this.letterText.setX(x + Constants.LETTER_OFFSET);
    }

    /**
     * graphically sets up score Text
     * @param x x location
     * @param y y location
     */
    private void setupScoreLabel(int x, int y){
        //switch statement using official Scrabble point values
        switch (this.letter){
            case 'D': case 'G':
                this.score = 2;
                break;
            case 'B': case 'C': case 'M': case 'P':
                this.score = 3;
                break;
            case 'F': case 'H': case 'V': case 'W': case 'Y':
                this.score = 4;
                break;
            case 'K':
                this.score = 5;
                break;
            case 'J': case 'X':
                this.score = 8;
                break;
            case 'Q': case 'Z':
                this.score = 10;
                break;
            case ' ':
                this.score = 0;
                break;
            default:
                this.score = 1;
        }
        //graphically shows score
        this.scoreText.setText(String.valueOf(this.score));
        this.scoreText.setTextAlignment(TextAlignment.CENTER);
        this.scoreText.resize(Constants.SCORE_SIZE, Constants.SCORE_SIZE);
        this.scoreText.setFont(new Font(Constants.SCORE_TEXT_SIZE));
        this.scoreText.setY(y + Constants.SQUARE_DIM - Constants.SCORE_YOFFSET);
        this.scoreText.setX(x + Constants.SCORE_XOFFSET);
    }

    /**
     * creates a transparent rectangle called clickable
     * convenient way to make tile and all layers respond
     * to mouse events
     */
    private void setupClickable(){
        this.clickable.setFill(Color.TRANSPARENT);
        this.clickable.setStrokeWidth(2);
        this.clickable.setStroke(Color.BLACK);
        this.clickable.setOnMouseDragged((MouseEvent e) -> this.move(e));
        this.clickable.setOnMouseReleased((MouseEvent e) -> this.setLocation(e));
    }

    /**
     * accessor for score
     * @return score
     */
    public int getScore(){
        return this.score;
    }

    /**
     * resets tile to its original position on tile rack
     */
    public void resetPos(){
        this.background.setX(this.initialX);
        this.background.setY(this.initialY);
        this.letterText.setX(this.initialX + Constants.LETTER_OFFSET);
        this.letterText.setY(this.initialY + Constants.SQUARE_DIM - Constants.LETTER_OFFSET);
        this.scoreText.setY(this.initialY + Constants.SQUARE_DIM - Constants.SCORE_YOFFSET);
        this.scoreText.setX(this.initialX + Constants.SCORE_XOFFSET);
        this.clickable.setX(this.initialX);
        this.clickable.setY(this.initialY);

        //if tile was originally blank, it loses the letter it
        //was assigned so you can pick a new letter when you actually use it
        if(this.score == 0){
            this.letter = ' ';
            this.setupLetterText(this.initialX, this.initialY);
        }
    }

    /**
     * checks if a spot is valid to move a tile into
     * @param row row number
     * @param col column number
     * @return true/false if the spot is valid
     */
    private boolean isValidSpot(int row, int col){
        //edge case of dragging over tile rack or offscreen
        if(row > this.tileBoard[0].length-1 || row < 0 ||
                col > this.tileBoard.length-1 || col < 0){
            return false;
        }

        //check if board is empty
        boolean boardEmpty = true;
        for (Tile[] tiles : this.tileBoard) {
            for (int j = 0; j < this.tileBoard[0].length; j++) {
                if (tiles[j] != null) {
                    boardEmpty = false;
                    break;
                }
            }
        }
        if(boardEmpty){
            return true;
        }
        //check if at least one neighboring square is full
        return this.checkNeighboringSquaresNotEmpty(row, col);
    }

    /**
     * accessor for row number
     * @return row number
     */
    public int getRow(){
        return this.rowIndex;
    }

    /**
     * accessor for column number
     * @return column number
     */
    public int getCol(){
        return this.colIndex;
    }

    /**
     * called when a tile has been scored
     * makes it so tile can no longer be moved
     */
    public void setFinal(){
        this.temporary = false;
    }

    /**
     * accessor for this.temporary
     * @return this.temporary
     */
    public boolean isTemporary(){
        return this.temporary;
    }

    /**
     * adds tile (all layers) to pane
     */
    public void addToPane(){
        this.pane.getChildren().addAll(this.background, this.letterText,
                this.scoreText, this.clickable);
    }

    /**
     * removes tile (all layers) from pane
     */
    public void removeFromPane(){
        this.pane.getChildren().removeAll(this.background, this.letterText,
                this.scoreText, this.clickable);
    }

    /**
     * setter for x location of tile
     * @param x new x location
     */
    public void setX(int x){
        this.background.setX(x);
        this.letterText.setX(x + Constants.LETTER_OFFSET);
        this.scoreText.setX(x + Constants.SCORE_XOFFSET);
        this.clickable.setX(x);
        this.initialX = x;
    }

    /**
     * displays a message to pick a letter
     * pane now takes in key input of what letter user chooses
     */
    private void handleBlankTile(){
        this.pane.getChildren().add(this.blankTileLabel);
        this.pane.requestFocus();
        this.pane.setOnKeyPressed((KeyEvent e) -> this.getBlankLetter(e));
    }

    /**
     * sets blank tile letter to letter typed by user
     * @param e key pressed
     */
    private void getBlankLetter(KeyEvent e){
        String letter = e.getCode().toString();
        char[] letterArray = letter.toCharArray();
        //checks if the user has actually provided  a letter
        //if not, user is prompted again to type a letter
        if(letterArray.length > 1){
            this.pane.getChildren().remove(this.blankTileLabel);
            this.handleBlankTile();
            return;
        }

        //another check on making sure user actually provides a letter
        this.letter = letterArray[0];
        if (!this.letters.contains(this.letter)){
            this.handleBlankTile();
        }

        //adds typed letter to tile graphically
        this.setupLetterText((int) this.background.getX(), (int) this.background.getY());
        this.pane.getChildren().remove(this.blankTileLabel);
        this.pane.setOnKeyPressed(null);
    }

    /**
     * visually sets up display message when dealing with a blank tile
     */
    private void setupBlankLabel(){
        this.blankTileLabel.setStyle(Constants.BLANK_BACKGROUND);
        this.blankTileLabel.setText("Press the letter you want \n the blank tile to be! ");
        this.blankTileLabel.setAlignment(Pos.CENTER);
        this.blankTileLabel.setFont(new Font(Constants.DISPLAY_FONT));
        this.blankTileLabel.setPrefSize(Constants.SCENE_WIDTH,
                Constants.SCENE_HEIGHT + 2 * Constants.SQUARE_DIM);
    }

    /**
     * checks if at least one neighboring square is full
     * @param row row number
     * @param col column number
     * @return true/false if all neighboring squares are empty
     */
    private boolean checkNeighboringSquaresNotEmpty(int row, int col){
        boolean NeighboringSquaresNotEmpty = false;
        //check left
        if (col > 0){
            if(this.tileBoard[row][col - 1] != null){
                NeighboringSquaresNotEmpty = true;
            }
        }
        //check right
        if (col < this.gameSquareBoard[0].length-1){
            if(this.tileBoard[row][col + 1] != null){
                NeighboringSquaresNotEmpty = true;
            }
        }
        //check up
        if (row > 0){
            if(this.tileBoard[row - 1][col] != null){
                NeighboringSquaresNotEmpty = true;
            }
        }
        //check down
        if (row < this.gameSquareBoard.length - 1){
            if(this.tileBoard[row + 1][col] != null){
                NeighboringSquaresNotEmpty = true;
            }
        }
        return NeighboringSquaresNotEmpty;
    }
}
