package indy;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import java.util.ArrayList;

/**
 * Board Class handles all logic that is internal to the game board
 */
public class Board {
    private GameSquare [][] gameSquareBoard;
    private Tile [][]tileBoard;
    private Pane pane;

    /**
     * constructor of board class
     * initializes instance variables and generates board
     * @param myPane parent pane
     */
    public Board(Pane myPane){
        this.pane = myPane;
        this.pane.setOnMouseDragged((MouseEvent e) -> this.highlightSquares(e));
        this.gameSquareBoard = new GameSquare[Constants.SCENE_HEIGHT/ Constants.SQUARE_DIM]
                [Constants.SCENE_WIDTH/ Constants.SQUARE_DIM];
        this.tileBoard = new Tile[Constants.SCENE_HEIGHT/ Constants.SQUARE_DIM]
                [Constants.SCENE_WIDTH/ Constants.SQUARE_DIM];
        this.generateBoard();
    }

    /**
     * graphically and logically creates board of GameSquares
     */
    private void generateBoard(){
        for (int i = 0; i < this.gameSquareBoard.length; i++){
            for (int j = 0; j < this.gameSquareBoard[0].length; j++){
                this.gameSquareBoard[i][j] = new GameSquare(j* Constants.SQUARE_DIM,
                        i* Constants.SQUARE_DIM, this.pane);
            }
        }
    }

    /**
     * accessor for gameSquare board
     * @return gameSquareBoard
     */
    public GameSquare[][] getBoard(){
        return this.gameSquareBoard;
    }

    /**
     * accessor for tileBoard
     * @return tileBoard
     */
    public Tile[][] getTileBoard(){
        return this.tileBoard;
    }

    /**
     * highlights the square that a tile being dragged would go into
     * useful to show user where their move would go
     * @param event mouseDragged
     */
    private void highlightSquares(MouseEvent event){
        for (GameSquare[] gameSquares : this.gameSquareBoard) {
            for (int j = 0; j < this.gameSquareBoard[0].length; j++) {
                gameSquares[j].resetColor();
                if (event.getY() < Constants.SCENE_HEIGHT && event.getY() > 0
                        && event.getX() > 0 && event.getX() < Constants.SCENE_WIDTH) {
                    int col = (int) (event.getX() / Constants.SQUARE_DIM);
                    int row = (int) (event.getY() / Constants.SQUARE_DIM);
                    this.gameSquareBoard[row][col].changeGlow();
                }
            }
        }
    }

    /**
     * checks each row if there is a potential word
     * @param words Arraylist of words to be checked against bst
     * @return returns score
     */
    public int checkHorizWords(ArrayList<String> words){
        int score = 0;
        ArrayList<Tile> wordTiles = new ArrayList<>();
        for (Tile[] tiles : this.tileBoard) {
            String word = "";
            //for each row, looks for a tile that has an empty tile to the left
            // and sets it as first letter
            for (int j = this.tileBoard[0].length - 1; j > 0; j--) {
                if (tiles[j] != null && tiles[j - 1] == null) {
                    word = String.valueOf(tiles[j].letter);
                    wordTiles.add(tiles[j]);
                    //adds on additional letters to right of first letter
                    for (int k = j + 1; k < this.tileBoard[0].length; k++) {
                        if (tiles[k] != null) {
                            word = word + tiles[k].letter;
                            wordTiles.add(tiles[k]);
                        } else {
                            break;
                        }
                    }
                //same as above, edge case for left side of board
                } else if (j == 1 && tiles[0] != null) {
                    word = String.valueOf(tiles[0].letter);
                    wordTiles.add(tiles[0]);
                    for (int k = j; k < this.tileBoard[0].length; k++) {
                        if (tiles[k] != null) {
                            word = word + tiles[k].letter;
                            wordTiles.add(tiles[k]);
                        } else {
                            break;
                        }
                    }
                }
                //calculates score based on words found
                score = this.calculateScore(word, words, wordTiles, score);
            }
        }
        return score;
    }

    /**
     * Very similar to checkHorizWords, except for vertical words
     * @param words arraylist of words to be checked against bst
     * @return returns score
     */
    public int checkVertWords(ArrayList<String> words){
        int score = 0;
        ArrayList<Tile> wordTiles = new ArrayList<>();
        for (int j = 0; j < this.tileBoard[0].length; j++) {
            String word = "";
            //for each column, look for a tile with an empty square above and set as first tile
            for (int i = this.tileBoard.length - 1; i > 0; i--) {
                if (this.tileBoard[i][j] != null && this.tileBoard[i - 1][j] == null) {
                    word = String.valueOf(this.tileBoard[i][j].letter);
                    wordTiles.add(this.tileBoard[i][j]);
                    //add on tiles underneath to create potential word
                    for (int k = i + 1; k < this.tileBoard.length; k++) {
                        if (this.tileBoard[k][j] != null) {
                            word = word + this.tileBoard[k][j].letter;
                            wordTiles.add(this.tileBoard[k][j]);
                        } else {
                            break;
                        }
                    }
                //same as above, edge case of first letter on first row
                } else if (i == 1 && this.tileBoard[0][j] != null) {
                    word = String.valueOf(this.tileBoard[0][j].letter);
                    wordTiles.add(this.tileBoard[0][j]);
                    for (int k = i; k < this.tileBoard.length; k++) {
                        if (this.tileBoard[k][j] != null) {
                            word = word + this.tileBoard[k][j].letter;
                            wordTiles.add(this.tileBoard[k][j]);
                        } else {
                            break;
                        }
                    }
                }
                //calculate score for words found
                score = this.calculateScore(word, words, wordTiles, score);
            }
        }
        return score;
    }

    /**
     * ensures the same word isn't counted multiple times
     * if not for this method, DOG would be scored 3 times
     * because for each letter, checkHorizWords would set
     * D as the first letter and score it accordingly
     * @param word word that was detected
     * @param words arraylist of words to be checked against bst
     * @return boolean of whether not word already is detected
     */
    private boolean isNotDuplicate(String word, ArrayList<String> words){
        for (String existingWord : words) {
            if (word == existingWord) {
                return false;
            }
        }
        return true;
    }

    /**
     * ensures that at least one of the tiles in the word being
     * checked is "new", i.e. added on the turn. this ensures that
     * words already on the board that the player doesn't add to
     * do not count towards their score
     *
     * @param tiles ArrayList of tiles that make up each word
     * @return boolean of whether or not word is new
     */
    private boolean checkWordNew(ArrayList<Tile> tiles){
        for (Tile tile: tiles) {
            if(tile.isTemporary()){
                return true;
            }
        }
        return false;
    }

    /**
     * handles premium letter squares with color checking
     * calculates a "bonus" to be added to the original score
     * @param tile tile in word being checked
     * @return bonus addition
     */
    private int checkBonusLetters(Tile tile){
        GameSquare square = this.gameSquareBoard[tile.getRow()][tile.getCol()];
        if (square.getColor() == Constants.DL_COLOR){
            square.setToNormal();
            return tile.getScore();
        } else if (square.getColor() == Constants.TL_COLOR){
            square.setToNormal();
            return 2 * tile.getScore();
        }
        return 0;
    }

    /**
     * handles premium word squares with color checking
     * calculates a "bonus" multiplier that will act on original word score
     * @param tile tile in word being checked
     * @return multiplier
     */
    private int checkBonusWords(Tile tile){
        GameSquare square = this.gameSquareBoard[tile.getRow()][tile.getCol()];
        if (square.getColor() == Constants.DW_COLOR){
            square.setToNormal();
            return 2;
        } else if (square.getColor() == Constants.TW_COLOR){
            square.setToNormal();
            return 3;
        }
        return 1;
    }

    /**
     * scores each word that is detected
     * calls on both Bonus methods
     * clears the wordTiles array if
     * @param word word that was detected
     * @param words arraylist of words to be checked against bst
     * @param wordTiles tiles that make up detected word
     * @param score previous score
     * @return the calculated score
     */
    private int calculateScore(String word, ArrayList<String> words,
                               ArrayList<Tile> wordTiles, int score){
        if (word.length() > 1 && this.isNotDuplicate(word, words) &&
                this.checkWordNew(wordTiles)) {
            words.add(word);
            for (Tile tile: wordTiles) {
                score += tile.getScore() + this.checkBonusLetters(tile);
            }
            for (Tile tile: wordTiles){
                score = score * this.checkBonusWords(tile);
            }
        } else{
            wordTiles.clear();
        }
        return score;
    }
}
