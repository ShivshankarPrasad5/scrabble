package indy;

/**
 * Nodes encode different valid words
 * Node class also handles the BST search algorithm
 */
public class Node {
    public String data;
    public Node left;
    public Node right;

    /**
     * constructor for node class
     * left and right initialized to null
     * @param data word encoded in node
     */
    public Node(String data){
        this.data = data;
        this.left = null;
        this.right = null;
    }

    /**
     * search method, also recursive!
     * @param word the word we are looking for
     * @return returns word if valid, null if invalid
     */
    public String search(String word){
        //compares to a node (first the root, as seen in WordTree)
        //if it is alphabetically behind this node, goes to left
        //else goes to right, continues recursively until word is found, if it exists
        if(this.data.compareTo(word) == 0){
            return this.data;
        } else if (this.data.compareTo(word) > 0){
            if(this.left != null){
                return this.left.search(word);
            }
        }else if (this.right != null){
            return this.right.search(word);
        }
        return null;
    }
}

