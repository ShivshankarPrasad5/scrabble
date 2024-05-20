package indy;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * GameSquare class is the parent of Tile
 * represents the squares on the empty board
 */
public class GameSquare {
    private Rectangle background;
    private Color color;
    private Color initialColor;
    private Pane pane;

    /**
     * constructor for gameSquare, initializes instance variables
     * @param x x location
     * @param y y location
     * @param myPane parent pane
     */
    public GameSquare(int x, int y, Pane myPane){
        this.pane = myPane;
        this.color = Constants.SQUARE_COLOR;
        this.initialColor = Constants.SQUARE_COLOR;
        this.background = new Rectangle(x, y, Constants.SQUARE_DIM, Constants.SQUARE_DIM);
        this.setBackground(x, y);
        this.setSpecialSquares();
    }

    /**
     * creates and adds background graphically, overridden in Tile
     * @param x x location
     * @param y y location
     */
    protected void setBackground(int x, int y){
        this.background.setFill(Constants.SQUARE_COLOR);
        this.background.setStroke(Color.BLACK);
        this.background.setStrokeWidth(2);
        this.pane.getChildren().add(this.background);

    }

    /**
     * changes fill color, used for highlighting squares
     */
    public void changeGlow(){
        this.background.setFill(Constants.HIGHLIGHT_COLOR);
    }

    /**
     * resets color if a premium square was used
     * in an invalid word, to make sure it can be
     * used later for a valid word
     */
    public void resetColor(){
        this.background.setFill(this.initialColor);
        this.color = this.initialColor;
    }

    /**
     * resets color once a premium square has been "used up"
     * according to Scrabble words, a premium square only
     * boosts the score on the first turn it is played in
     */
    public void setToNormal(){
        this.initialColor = Constants.SQUARE_COLOR;
    }

    /**
     * graphically and logically creates premium squares
     */
    private void setSpecialSquares(){
        int row = (int) this.background.getY() / Constants.SQUARE_DIM;
        int col = (int) this.background.getX() / Constants.SQUARE_DIM;

        //double letter
        if((row == col) || (row == 8 - col)){
            this.color = Constants.DL_COLOR;
            this.initialColor = Constants.DL_COLOR;
            this.background.setFill(Constants.DL_COLOR);
        }

        //double word
        if (((row == 2 || row == 6) && col == 4) ||
                ((col == 2 || col == 6) && row == 4)){
            this.color = Constants.DW_COLOR;
            this.initialColor = Constants.DW_COLOR;
            this.background.setFill(Constants.DW_COLOR);
        }

        //triple letter
        if((row == 1 || row == 7) && (col == 1 || col == 7)){
            this.color = Constants.TL_COLOR;
            this.initialColor = Constants.TL_COLOR;
            this.background.setFill(Constants.TL_COLOR);
        }

        //triple word
        if ((row == 0 || row == 4 || row == 8) && (col == 0 || col == 4 || col == 8)){
            this.color = Constants.TW_COLOR;
            this.initialColor = Constants.TW_COLOR;
            this.background.setFill(Constants.TW_COLOR);
        }

        //set middle square
        if (row == 4 && col == 4){
            this.color = Constants.MID_COLOR;
            this.initialColor = Constants.MID_COLOR;
            this.background.setFill(Constants.MID_COLOR);
        }
    }

    /**
     * accessor for color of square
     * @return color
     */
    public Color getColor(){
        return this.color;
    }
}
