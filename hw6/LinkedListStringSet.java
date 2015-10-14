/** Implementation of StringSet using a LinkedList.
  * @author Josh Hug
  */
public class LinkedListStringSet implements StringSet {
    /** Node class for storing data. */
    private class Node {
        /** String stored by this node. */
        private String s;

        /** next node in the linked list. */
        private Node next;

        /** Creates a new node containing the given string SP. */
        public Node(String sp) {
            s = sp;
        }
    }

    /** The front of the linked list storing this set. This sentinel
      * node isn't truly necessary for this data structure, since it
      * only saves us a single null check, but hey, why not?
      */
    private Node front = new Node("dummy");

    /** Pointer to the end of the list. */
    private Node last = front;

    /** Returns true if the set contains S.
      *
      * I'm using iteration here, but I'd very strongly advise you to write
      * recursive code for your BST set. The code is much nicer and writing
      * recursive tree code is most of the point of this hw.
      */
    public boolean contains(String s) {
        for (Node p = front; p != null; p = p.next) {
            if (s.compareTo(p.s) == 0) {
                return true;
            }
        }

        return false;
    }

    /** Adds the string S to the set.
      *
      * If the string is already in the set, do nothing.
      */
    public void put(String s) {
        if (contains(s)) {
            return;
        }

        last.next = new Node(s);
        last = last.next;
    }
}
