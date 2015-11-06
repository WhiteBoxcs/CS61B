import java.util.Iterator;
import java.util.Stack;

/**
 * Implementation of a BST based String Set.
 * @author Josh Hug and <your name here>
 */
public class BSTStringSet implements SortedStringSet {
    /** Represents a single Node of the tree. */
    private static class Node {
        /** String stored in this Node. */
        private String s;
        /** Left child of this Node. */
        private Node left;
        /** Right child of this Node. */
        private Node right;

        /** Creates a Node containing SP. */
        public Node(String sp) {
            s = sp;
        }
    }

    /** Root node of the tree. */
    private Node root;

    /** Creates a new empty StringSet. */
    public BSTStringSet() {
        root = null;
    }

    /** Returns true if the String S is in the set. */
    public boolean contains(String s) {
        return contains(s, root);
    }

    /** Returns true if String S is in the subset rooted in P. */
    private boolean contains(String s, Node p) {
        if (p == null) {
            return false;
        }

        int cmp = s.compareTo(p.s);
        if (cmp < 0) {
            return contains(s, p.left);
        }
        if (cmp > 0) {
            return contains(s, p.right);
        }

        return true;
    }

    /** Inserts the string S if it is not already present.
     *  If it is already present, does nothing.
     */
    public void put(String s) {
        root = put(s, root);
    }

    /** Helper method for put. Returns a BST rooted in P,
      *  but with S added to this BST.
      */
    private Node put(String s, Node p) {
        if (p == null) {
            return new Node(s);
        }

        int cmp = s.compareTo(p.s);

        if (cmp < 0) {
            p.left = put(s, p.left);
        }
        if (cmp > 0) {
            p.right = put(s, p.right);
        }

        return p;
    }

    @Override
    public Iterator<String> iterator(String L, String U) {
        return new BoundedBSTIterator(L,U);
    }
    
    private class BoundedBSTIterator implements Iterator<String> {
        
        private Stack<Node> pos;
        private Node cur;
        private String U;
        private String L;

        public BoundedBSTIterator(String L, String U){
            this.L = L;
            this.U = U;
            this.pos = new Stack<Node>();
            cur = root;
        }
        
        
        @Override
        public boolean hasNext() {
            return !pos.isEmpty() || (cur != null && cur.s.compareTo(U) <= 0);
        }

        @Override
        public String next() {
            while(cur != null && cur.s.compareTo(L) >= 0){
                pos.push(cur);
                cur = cur.left;
            }
            
            Node retr = pos.pop();
            cur = retr.right;
            
            return retr.s;
        }

    }

    
    

    /** Prints the Set in increasing order. */
    public void printSet() {
        printInOrder(root);
        System.out.println();
    }

    /** Helper method that prints BST rooted in P. */
    private void printInOrder(Node p) {
        if (p == null) {
            return;
        }

        printInOrder(p.left);
        System.out.print(p.s + " ");
        printInOrder(p.right);
    }


}
