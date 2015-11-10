
/**
 * Represents the general IntHeap class
 * @author william
 *
 */
public class IntHeap {
    
    private int length;
    private int[] store;

    /**
     * Creates a default heap.
     */
    public IntHeap(){
        this.length = 0;
        this.store = new int[4];
    }
    
    /**
     * Creates a heap using another array.
     * @param store the new STORE
     * @param length the subset length of the target array.
     */
    protected IntHeap(int[] store, int length){
        this.store = store;
        this.length = length;
        
        //Enforce maximallity condition on the store using maxHeap algorithm:
        
    }
    
    /**
     * Adds an element to the integer heap.
     * @param elem
     */
    public void add(int elem){
        if(length+1 > store.length)
            increaseStore();
        
        //Place the element at the next given position.
        store[length] = (elem);
        
        //Validate the structure.
        propagateUp(length);
        
        length++;
        
    }
    
    /**
     * Propagates an element up the heap.
     * @param pos the position of the element.
     */
    private void propagateUp(int pos) {
        //If the ordering property of the heap is wrong at the node indexed POS.
        if(parent(pos) != -1 && store[pos] > store[parent(pos)]){
            //XOR SWAP the elements (this notation reveals more about the swap then others might)
            store[pos]         = store[pos] ^ store[parent(pos)];
            store[parent(pos)] = store[pos] ^ store[parent(pos)];
            store[pos]         = store[pos] ^ store[parent(pos)];
            
            //Propagate upwards from the new position of the element.
            propagateUp(parent(pos));
        }
        
    }
    
    
    /**
     * Removes the most maximal element in worst case \Theta(log SIZE) time.
     * @return
     */
    public int removeMax(){
        int maximal = max();  

        //Swap the top element with the last element, and then decrease the length.
        store[0] = store[--length];
        
        //If there could possibly exist a violation to the heap relation
        if(length != 0)
            //Fix by propagation.
            propagateDown(0);

        return maximal;
    }
    
    /**
     * Propagates a possibly errorneous node down a heap.
     * @param i the index of the errorneous node.
     */
    private void propagateDown(int pos) {
        int left =  leftChild(pos);
        int right = rightChild(pos);
        
        //If the left node is the tree and there is an imbalance
        // Note: if the left node is not in the tree, the right is certainly not (by completeness).
        if(left < length && store[left] < store[pos]){
            int swapTarget = left;
            
            //Choose a suitable swap target amongst the children.
            if(right < length && store[left] < store[right])
                swapTarget = right;
            
            //Perform the swap.
            store[pos]         = store[pos] ^ store[swapTarget];
            store[swapTarget]  = store[pos] ^ store[swapTarget];
            store[pos]         = store[pos] ^ store[swapTarget];
            
            //Propagate down from the swap target (the new location of elem originally at POS).
            propagateDown(swapTarget);
        }
        
        
    }


    /**
     * Returns the max element.
     * @return
     */
    public int max(){
        if(!isEmpty())
            return store[0];
        else
            throw new IllegalStateException("Heap empty.");
    }
    
    /**
     * Returns whether or not the heap was empty.
     * @return
     */
    private boolean isEmpty() {
        return size() == 0;
    }

    
    private void increaseStore() {
        int[] temp = store;
        store = new int[store.length*2];
        System.arraycopy(temp, 0, store, 0, temp.length);
        
    }


    public int size() {
        return length;
    }

    /**
     * Heapifies an the ARRAY to element K.. 
     * @return The heap.
     */
    public static IntHeap heapify(int[] array, int k){
        IntHeap destructiveHeap = new IntHeap(array,k);
        
        return destructiveHeap;
    }

    /**
     * Gets the index for the parent of a node at index INDEX with 0 ordering.
     * @param index
     * @return
     */
    private static int parent(int index){
        if(index > 0)
            return (index -1)/2;
        else
            return -1;
    }
    
    /**
     * Gets the index for the left child of a node at index INDEX with 0-based indexing.
     * @param index
     * @return
     */
    private static int leftChild(int index){
        return 2*(index) + 1;
    }
    
    /**
     * Gets the index for the left child of a node at index INDEX with 0-based indexing.
     * @param index
     * @return
     */
    private static int rightChild(int index){
        return 2*(index) + 2;
    }
    
}
