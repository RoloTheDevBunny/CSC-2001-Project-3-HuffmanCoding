import java.util.NoSuchElementException;

/**
 * A min-heap of ints stored in an array, as presented in lecture.
 *
 * The heap is a complete binary tree kept in heap[1] .. heap[size];
 * heap[0] is never used. For the node at index i:
 *     left child  = 2 * i
 *     right child = 2 * i + 1
 *     parent      = i / 2
 *
 * Heap-order property: every parent is <= its children, so the
 * minimum is always at heap[1]. Duplicate values are allowed.
 *
 * Do not rename the class, the public methods, or change their
 * parameters/return types. You may add private helper methods.
 */
public class PriorityQueue {

    /** Number of values a new heap can hold before it has to grow. */
    public static final int DEFAULT_CAPACITY = 10;

    private HuffmanNode[] heap;   // heap[0] is unused; the root is heap[1]
    private int size;     // values live in heap[1] ... heap[size]

    /** Creates an empty heap with room for DEFAULT_CAPACITY values. */
    public PriorityQueue() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Creates an empty heap with room for capacity values before it grows.
     * (The array needs capacity + 1 slots, since index 0 is unused.)
     * throws IllegalArgumentException if capacity is less than 1
     */
    public PriorityQueue(int capacity) {
        // TODO: validate capacity, then create the array
        if (capacity < 1) {
            throw new IllegalArgumentException("Too small capacity");
        }
        heap = new HuffmanNode[capacity+1];
    }

    /**
     * Builds a heap from values bottom-up in O(n): copies them into
     * heap[1] .. heap[n] in the given order, then calls buildHeap().
     * values itself must not be changed. (Make the array exactly big
     * enough: n + 1 slots, but at least 2.)
     * throws IllegalArgumentException if values is null
     */
    public PriorityQueue(HuffmanNode[] values) {
        if (values == null) {
            throw new IllegalArgumentException("Input array is null");
        }

        heap = new HuffmanNode[Math.max(values.length + 1, 2)];
        size = values.length;
        System.arraycopy(values, 0, heap, 1, size);
        buildHeap();
    }

    /**
     * Adds value to the heap: puts it in the next open spot (index size + 1),
     * then percolates it up until its parent is no larger.
     * Grows the array first if it is full.
     */
    public void insert(HuffmanNode value) {
        // TODO
        if (heap.length <= size+1) {
            grow();
        }
        size++;
        heap[size] = value;
        percolateUp(size);
    }

    /**
     * Removes and returns the smallest value: the last leaf moves to the
     * root, then percolates down until it is no larger than its children.
     * throws NoSuchElementException if the heap is empty
     */
    public HuffmanNode removeMin() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        HuffmanNode smallest = heap[1];
        heap[1] = heap[size];
        size--;
        percolateDown(1);
        return smallest;
    }

    /**
     * Returns the smallest value without removing it.
     * throws NoSuchElementException if the heap is empty
     */
    public HuffmanNode peek() {
        // TODO
        if (size == 0) {
            throw new NoSuchElementException("Heap empty, no element to peek");
        }
        return heap[1];
    }

    /**
     * Returns the number of values in the heap.
     */
    public int size() {
        // TODO
        return size;
    }

    /**
     * Reports whether the heap has no values.
     * return true if the heap is empty
     */
    public boolean isEmpty() {
        // TODO
        return size == 0;
    }

    /**
     * Returns the heap's values in array order (heap[1] ... heap[size]) as a
     * new array of length size(). Changing the returned array must not
     * change the heap.
     * Example: after inserting 3, 5, 8, 10, 7, 12, 9 this returns
     * {3, 5, 8, 10, 7, 12, 9}.
     * (The insert order matches the heap array order in this example)
     */
    public HuffmanNode[] toArray() {
        // TODO
        HuffmanNode[] newArray = new HuffmanNode[size];
        System.arraycopy(heap, 1, newArray, 0, size);
        return newArray;
    }

    /**
     * Moves the value at index i up while it is smaller than its parent.
     * (Stop when it reaches the root or its parent is <= it.)
     */
    private void percolateUp(int i) {
        if (i <= 1) {
            return;
        }
        HuffmanNode parentVal = heap[i/2];
//        if (parentVal <= heap[i]) {
        if (parentVal.compareTo(heap[i]) <= 0) {
            return;
        }
        heap[i/2] = heap[i];
        heap[i] = parentVal;
        percolateUp(i/2);
    }

    /**
     * Moves the value at index i down, always swapping with the smaller
     * child (the LEFT child if both children are equal), until it is no
     * larger than its children or reaches a leaf.
     */
    private void percolateDown(int i) {
        while (2 * i <= size) {
            int child = 2 * i;

            // Select right when smaller, equal children keeps left
//            if (child + 1 <= size && heap[child + 1] < heap[child]) {
            if (child + 1 <= size && heap[child].compareTo(heap[child + 1]) > 0) {
                child++;
            }

//            if (heap[i] <= heap[child]) {
            if (heap[i].compareTo(heap[child]) >= 0) {
                return;
            }

            swap(i, child);
            i = child;
        }
    }

    /**
     * Exchanges heap[a] and heap[b].
     * a and b are the indices of the values to swap
     */
    private void swap(int a, int b) {
        HuffmanNode temp = heap[b];
        heap[b] = heap[a];
        heap[a] = temp;
    }

    /** Doubles the array's capacity, keeping every value at the same index. */
    private void grow() {
        HuffmanNode[] newArray = new HuffmanNode[heap.length*2];
        System.arraycopy(heap, 1, newArray, 1, size);
        heap = newArray;
    }

    /**
     * Bottom up heap construction.
     * Restores heap order over heap[1] .. heap[size] by percolating down
     * each non-leaf node, starting at the last one (index size / 2) and
     * working back to the root (index 1).
     */
    private void buildHeap() {
        for (int i = size / 2; i >= 1; i--) {
            percolateDown(i);
        }
    }

    /**
     * Returns a new array holding values in ascending order: builds a
     * PriorityQueue from values with the PriorityQueue(int[]) constructor, then calls
     * removeMin() repeatedly. values itself must not be changed.
     * throws IllegalArgumentException if values is null
     */
    public static HuffmanNode[] heapSort(HuffmanNode[] values) {
        PriorityQueue heap = new PriorityQueue(values);
        HuffmanNode[] sortedValues = new HuffmanNode[values.length];
        for (int i=0; !heap.isEmpty(); i++) {
            sortedValues[i] = heap.removeMin();
        }
        return sortedValues;
    }
}
