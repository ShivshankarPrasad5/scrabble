package indy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * WordTree represents the BST used to check the validity of words
 * Contains a Node for each word
 */
public class WordTree {
    private Node root;
    private ArrayList<String> words;

    /**
     * constructor for BST wordTree
     * initializes instance variables
     */
    public WordTree() {
        this.words = new ArrayList<>();
        this.getWords();
        this.root = this.createTree(this.words, 0, this.words.size() - 1);
    }

    /**
     * reads text file of all valid Scrabble words into an arrayList
     */
    private void getWords() {
        File file = new File("/Users/shivshankarprasad/Desktop/cs15/src/indy/ScrabbleWords.txt");
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String string = sc.nextLine();
                this.words.add(string);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * creates BST from sorted list
     * recursively finds middle of list and splits
     * @param words arrayList of all valid words
     * @param start index of first word in list
     * @param end index of last word in list
     * @return returns the root node
     */
    private Node createTree(ArrayList<String> words, int start, int end){
        //ensures tree creation stops once all words are in tree
        if (start > end){
            return null;
        }
        //finds middle of list and creates a node
        int middle = (start + end) / 2;
        Node node = new Node(words.get(middle));

        //sets left and right of node as middle of new sublist to left and right
        node.left = this.createTree(words, start, middle - 1);
        node.right = this.createTree(words, middle + 1, end);
        return node;
    }

    /**
     * searches for a given word in BST
     * done recursively, actual code is in Node class
     * @param word the word we are searching for
     * @return returns word if valid, null if not valid
     */
    public String search(String word){
        return this.root.search(word);
    }

}
