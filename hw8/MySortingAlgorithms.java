import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Distribution Sort adopted from Algorithms, 
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            k = Math.min(k,array.length);
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }        
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            this.sort(array,0,k);
        }
        
        public void sort(int[] array, int start, int end){
            end = Math.min(end,array.length);
            start = Math.max(start, 0);
            //This algorithm works as follows.
            //For all entries
            for(int i = start; i < end; i++){
                for(int j = i; j > start; j--){
                    if(array[j] < array[j-1]){
                        //XOR SWAP IF DIFFERENT
                        swap(array, j-1, j);
                    }
                    else
                        break;
                }
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            k = Math.min(k,array.length);
            /* Slower than insertion sort, 
             * since it searches the array for the next smallest integer.
             *  Whereas insertion sort actually 
             *  just swaps if and only if there is an inversion to an adjacent element.
             *  Both are intuitive. However, I think insertion sort should be aptly
             *  named "swap" sort. Selection sort derives its name from selecting
             *  the next best element with which to replace a given element;
             *  Both act to decrease inversions on each iteration.
             *  That is to invent a sorting algorithm is to do so in a manner
             *  such that there is a guarenteed negative net inversion count on
             *  every iterate. */
            
            //Loop over the array
            for(int i = 0; i < k; i++){
                //Then select an element and find the next smallest element
                int min = i;
                for(int j = i+1; j < k; j++) {
                    min = array[min] > array[j] ? //If the 'min' is not the minest.
                          min = j //set min to the smaller elem.
                        : min; //otherwise, min remains the same.
                }
                
                //Now put the smallest element where it should be!
                swap(array, min, i);
            }
            
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
      * method is easier to write than a recursive merge method.
      * Note: I'm only talking about the merge operation here,
      * not the entire algorithm, which is easier to do recursively.
      */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {     
            k = Math.min(k,array.length);
            mergeSort(array, 0, k, new int[k]);
        }

        // may want to add additional methods

        private void mergeSort(int[] array, int left, int right, int[] js) {
            int mid = (right + left)/2;
            if(right - left > 1){
                //Split left
                mergeSort(array,left,mid,js);
                //split right
                mergeSort(array,mid,right,js);
                //Combine into JS
                merge(array, left, mid, right, js);
                
                //Does this contradict the speed of the algorithm?
                System.arraycopy(js, left, array, left, right-left);
            }
            
            
        }

        private void merge(int[] array, int left, int mid, int right,
                int[] js) {
            
            //Iterate over two different indexing variables, and stop when they both reach the end.
            for(int l = left, r = mid; l < mid || r < right;){
                //If the right indexer is at its end or if it is not and the left element is less
                if((r == right) || (l < mid && array[l] < array[r]))
                {
                    //Use the left
                    js[l+r-mid] = array[l];
                    l++;
                }
                //If the left indexer is at its end or if it is not and the right element is less than or equal
                else if((l == mid) ||  (r < right && array[l] >= array[r])){
                    //use the right
                    js[l+r-mid] = array[r];
                    r++;
                }
            }
        }

        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Distribution Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class PositiveDistributionSort implements SortingAlgorithm {
        @Override
        /**
         * Assumes that array has all positive elements.
         */
        public void sort(int[] array, int k) {
            k = Math.min(k,array.length);
            //Do nothing for 0 sized lists
            if(k <= 1)
                return;
            
            int max = findMax(array, k);
            int[] counts = new int[max+1];
            
            for(int i = 0; i < k; i++)
                ++counts[array[i]]; //TLDR; ++var >>>>>>>> var++;

            //Perform filling procedure.
            int index = 0;
            for(int elem = 0; elem < max+1; elem++){
                int end = counts[elem]+index;
                if(end != index)
                    Arrays.fill(array, index, end, elem);
                index = end;
            }
            
            
        }
        
        /**
         * Finds the maximal element.
         * @param array The array to search
         * @param k Over which the array must be searched.
         * @return
         */
        private int findMax(int[] array, int k){
            return Arrays.stream(array)
                    .limit(k)
                    .max()
                    .getAsInt();
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Distribution Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            k = Math.min(k,array.length);
            
            
            IntHeap arrayHeap = IntHeap.heapify(array, k);
            for(int i = k-1; i >= 0; i--){
                array[i] = arrayHeap.removeMax();
            }
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }        
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        private static InsertionSort insertionSort = new InsertionSort();
        private static Random r = new Random();
        private static final int SAMPLE_COUNT = 3;

        @Override
        public void sort(int[] array, int k) {
            k = Math.min(k,array.length);
            quickSort(array,0,k);
        }
        
        private void quickSort(int[] array, int start, int end) {
            if(end - start > 5){
                int mid = partition(array,start,end);
                quickSort(array, start,mid);
                quickSort(array, mid+1,end);
            }
            else if(end - start > 1)
            {
                insertionSort.sort(array, start, end);
            }
        }

        /**
         * Performs the partition algorithm
         * returning the final position of the pivot.
         * @param array the array to partition
         * @param start the start INCLUSIVE
         * @param end the end EXCLUSIVE
         * @return the PIVOT position.
         */
        private int partition(int[] array, int start, int end){
            int i = start;
            int j = end-1;
            
            int pivot = selectPivot(array,start,end);

            //Now perform this process untill things are done.
            while( i < j){
                if(array[i] >= array[j] && (array[i] >= array[pivot] && array[j] <= array[pivot]))
                    pivot = quickSwap(array,i,j,pivot);
                
                if(i < pivot && array[i] <= array[pivot])
                    i++;
                if(j > pivot && array[j] >= array[pivot])
                    j--; 
            }
            
            return pivot;
        }
        
        /**
         * Selects a pivot between two indices
         * @param array the array in which the pivot resides
         * @param start the first index at which parsing begins (inclusive)
         * @param end the end (exclusive)
         * @return the pivot index
         */
        private int selectPivot(int[] array, int start, int end){
            int dist = end - start;
            
            if(dist < 1)
                return -1;
            else if(dist < 4)
                return end-1;
            else{
                //For not so simple case, take SAMPLE_COUNT random indices and find the middle most one.
                int[] samples = new int[] {start, (start+end-1)/2, end-1};
                
                double average = (array[start]
                        + array[(start+end-1)/2]
                                + array[end-1])/3;
                

                int median = samples[0];
                for(int i = 1; i < 3; i++){
                    if(Math.abs(array[samples[i]] - average) < Math.abs(array[median] - average))
                        median = samples[i];
                }
                return end-1;
                        
            }
        }

        /**
         * Quick swaps two elements and returns the new paivot position if changed.
         */
        private int quickSwap(int array[], int i, int j, int pivot){
            swap(array,i,j);
            if(i == pivot)
                pivot = j;
            
            else if(j == pivot)
                pivot = i;
            return pivot;
            
        }
    
        
        @Override
        public String toString() {
            return "Quicksort";
        }        
    

    }

    /*
     * Your LSD Sort implementation, treating ints
     * as a sequence of 8 bit characters. Or if you want 
     * to do less bit-hacking, you can treat them as strings
     * of decimal digits.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        /** For an example implementation, see Kevin Wayne and 
          * Bob Sedgewick's Algorithms textbook.
          *
          * http://algs4.cs.princeton.edu/51radix/LSD.java.html
          */
        public void sort(int[] a, int k) {
            k = Math.min(k,a.length);
            
            int[] workspace = new int[k];
            //Iterate over the possible bits.
            for(int selector =1; selector != 0 ; selector <<= 1){
                int zeroCount = 0;
                int oneCount = 0;
                for(int i =0; i < k; i++){
                    if((a[i] & selector) > 0)
                        oneCount++;
                    else
                        zeroCount++;
                }
                //If the list is already sorted, stop iterating over the selector.
                if(oneCount+zeroCount != k)
                    break;
                
                oneCount = zeroCount;
                zeroCount = 0;
                for(int i = 0; i < a.length; i++){
                    if((a[i] & selector) > 0)
                        workspace[oneCount++]=a[i];
                    else
                        workspace[zeroCount++] = a[i];
                }
                
                System.arraycopy(workspace, 0, a, 0, k);
                
                
            }
            
            
        }

        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * Tricky: Your MSD Sort implementation, treating ints
     * as a string of 8 bit characters.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        /** Provides an implementation of LSD.sort. 
          * From Kevin Wayne and Bob Sedgewick's Algorithms textbook.
          * http://algs4.cs.princeton.edu/51radix/LSD.java.html
          */
        public void sort(int[] a, int k) {
            int N = Math.min(a.length, k);
            int[] aux = new int[N];
            sort(a, 0, N-1, 0, aux);
        }

        public void sort(int[] a, int lo, int hi, int d, int[] aux) {
            /* Each int is 32 bits and are thus 4 byes wide.
               Each byte holds a value between 0 and 255.
               MASK is used to mask off 8 bits at a time */
            int BITS_PER_INT = 32;    
            int BITS_PER_BYTE = 8;  
            int R = 1 << BITS_PER_BYTE;   
            int MASK = R - 1;              

            // compute frequency counts (need R = 256)
            int[] count = new int[R+1];
            int mask = R - 1;   // 0xFF;
            int shift = BITS_PER_INT - BITS_PER_BYTE*d - BITS_PER_BYTE;
            for (int i = lo; i <= hi; i++) {
                int c = (a[i] >> shift) & mask;
                count[c + 1]++;
            }

            // transform counts to indicies
            for (int r = 0; r < R; r++)
                count[r+1] += count[r];

            for (int i = lo; i <= hi; i++) {
                int c = (a[i] >> shift) & mask;
                aux[count[c]++] = a[i];
            }

            // copy back
            for (int i = lo; i <= hi; i++) 
                a[i] = aux[i - lo];

            // no more bits
            if (d == 4) return;

            // recursively sort for each character
            if (count[0] > 0)
                sort(a, lo, lo + count[0] - 1, d+1, aux);
            for (int r = 0; r < R; r++)
                if (count[r+1] > count[r])
                    sort(a, lo + count[r], lo + count[r+1] - 1, d+1, aux);
        }

        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * XOR SWAP
     */
    public static void swap(int[] a, int i, int j) {
        if(a[i] != a[j]){
            a[i] = a[i] ^ a[j];
            a[j] = a[i] ^ a[j];
            a[i] = a[i] ^ a[j];
        }
    }

}


