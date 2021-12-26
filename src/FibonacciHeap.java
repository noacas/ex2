/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{

    private HeapNode minNode;
    private HeapNode first;
    private int size;
    private int trees;
    private int numMark;
    static int linksCounter;
    static int cutsCounter;

    FibonacciHeap() {
        minNode = null;
        first = null;
    }

    /**
     * public HeapNode getFirst()
     *
     * Returns the first node in heap, and null if heap is empty
     * Complexity O(1)
     */
    public HeapNode getFirst() {
        return first;
    }

    /**
     * public int getTrees()
     *
     * return number of trees in heap
     * Complexity O(1)
     */
    public int getTrees() {
        return trees;
    }

    /**
     * public int getNumMark()
     *
     * return number of marked nodes in heap
     * Complexity O(1)
     */
    public int getNumMark() {
        return numMark;
    }

    /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    * Complexity O(1)
    */
    public boolean isEmpty()
    {
    	return first == null;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    * Complexity O(1)
    */
    public HeapNode insert(int key)
    {
        return insert(key, null);
    }

    /**
     * public HeapNode insert(int key, HeapNode info)
     *
     * Creates a node (of type HeapNode) which contains the given key and given info, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     *
     * Returns the newly created node.
     * Complexity O(1)
     */
    public HeapNode insert(int key, HeapNode info)
    {
        size++;
        HeapNode node = new HeapNode(key, info);
        placeNodeFirst(node);
        if ((minNode == null) || (minNode.getKey() > key)) {
            minNode = node;
        }
        return node;
    }

    /**
     * private void placeNodeBefore(HeapNode node, HeapNode x)
     *
     * Place node (and its tree) before node X
     * Complexity O(1)
     */
    private void placeNodeBefore(HeapNode node, HeapNode x) {
        x.getPrev().setNext(node);
        node.setPrev(x.getPrev());
        node.setNext(x);
        x.setPrev(node);
    }

    /**
     * private void placeNodeFirst(HeapNode node)
     *
     * Place node (and its tree) as the first tree in heap
     * precondition: node is unmarked
     * Complexity O(1)
     */
    private void placeNodeFirst(HeapNode node) {
        if (first != null) {
            placeNodeBefore(node, first);
        }
        else {
            node.setNext(node);
            node.setPrev(node);
        }
        trees++;
        first = node;
    }

    /**
     * private void placeNodeAsFirstChild(HeapNode node, heapNode parent)
     *
     * Place node (and its tree) as the first child of parent
     * Complexity O(1)
     */
    private void placeNodeAsFirstChild(HeapNode node, HeapNode parent) {
        if (parent.getChild() == null) {
            node.setNext(node);
            node.setPrev(node);
        }
        else {
            placeNodeBefore(node, parent.getChild());
        }
        parent.setChild(node);
        node.setParent(parent);
        parent.setRank(parent.getRank() + 1);
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    * Also update size, numMark (unmark the children)
    * Consolidate heap in the end
    * Complexity: w.c. O(n) amortized O(log n)
    */
    public void deleteMin()
    {
        size--;
        trees--;
        // if only node in heap
        if (size == 0) {
            minNode = null;
            first = null;
            return;
        }
        if (minNode.getChild() != null) {
            HeapNode firstChild = minNode.getChild();
            HeapNode lastChild = minNode.getChild().getPrev();
            firstChild.setPrev(minNode.getPrev());
            firstChild.getPrev().setNext(firstChild);
            HeapNode cur = firstChild;
            do {
                if (cur.isMark()) {
                    cur.unMark();
                    numMark--;
                }
                cur.setParent(null);
                trees++;
                cur = cur.getNext();
            } while (cur != firstChild);
            lastChild.setNext(minNode.getNext());
            lastChild.getNext().setPrev(lastChild);
        }
        else {
            minNode.getPrev().setNext(minNode.getNext());
            minNode.getNext().setPrev(minNode.getPrev());
        }
        if (first == minNode) {
            first = minNode.getNext();
        }
        consolidate();
    }

    /**
     * private void consolidate()
     *
     * Perform consolidation after delete-min
     * Also updates linksCounter, minNode, trees
     * Complexity w.c. O(n) amortized O(1)
     */
    private void consolidate() {
        fromBuckets(toBuckets());
    }

    /**
     * private HeapNode[] toBuckets()
     *
     * Return heap roots in bucket and perform linking until we
     * only have at most one tree from each rank in the buckets
     * Complexity w.c. O(n) amortized O(1)
     */
    private HeapNode[] toBuckets() {
        int bucketsSize = (int) (Math.log(size) / Math.log(2) + 2);
        HeapNode[] buckets = new HeapNode[bucketsSize];
        first.getPrev().setNext(null);
        HeapNode cur = first;
        while (cur != null) {
            int rank = cur.getRank();
            HeapNode next = cur.getNext();
            while (buckets[rank] != null) {
                cur = link(cur, buckets[rank]);
                buckets[rank] = null;
                rank = cur.getRank();
            }
            buckets[rank] = cur;
            cur = next;
        }
        return buckets;
    }

    /**
     * private void fromBuckets(HeapNode[] buckets)
     *
     * using the buckets (created while consolidating),
     * re-construct the heap
     * Complexity O(log n)
     */
    private void fromBuckets(HeapNode[] buckets) {
        first = null;
        trees = 0;
        HeapNode lastAdded = null;
        for (HeapNode bucket : buckets) {
            if (bucket == null) {
                continue;
            }
            trees++;
            if (first == null) {
                first = bucket;
                minNode = bucket;
                lastAdded = bucket;
            } else {
                lastAdded.setNext(bucket);
                bucket.setPrev(lastAdded);
                lastAdded = bucket;
                if (bucket.getKey() < minNode.getKey()) {
                    minNode = bucket;
                }
            }
        }
        lastAdded.setNext(first);
        first.setPrev(lastAdded);
    }

    /**
     * private HeapNode link(HeapNode x, HeapNode y)
     *
     * link 2 trees
     */
    private HeapNode link(HeapNode x, HeapNode y) {
        linksCounter++;
        if (x.getKey() > y.getKey()) {
            HeapNode temp = x;
            x = y;
            y = temp;
        }
        placeNodeAsFirstChild(y, x);
        return x;
    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    * Complexity O(1)
    */
    public HeapNode findMin()
    {
    	if (minNode == null) {
            return null;
        }
        return minNode;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    * Complexity O(1)
    */
    public void meld(FibonacciHeap heap2)
    {
        if (heap2.isEmpty()){
            return;
        }
        HeapNode oldLast = first.getPrev();
        HeapNode newLast = heap2.first.getPrev();
    	oldLast.setNext(heap2.first);
        first.setPrev(newLast);
        newLast.setNext(first);
        heap2.first.setPrev(oldLast);
        size += heap2.size;
        trees += heap2.trees;
        numMark += heap2.numMark;
        if (heap2.minNode.getKey() < minNode.getKey()) {
            minNode = heap2.minNode;
        }
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    * Complexity O(1)
    */
    public int size()
    {
    	return size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
    * Complexity: O(n);
    */
    public int[] countersRep()
    {
        if (isEmpty()) {
            return new int[0];
        }
        int maxOrder = first.getPrev().getRank();
        int[] arr = new int[maxOrder + 1];
        HeapNode cur = first;
        do {
            int rank = cur.getRank();
            arr[rank]++;
            cur = cur.getNext();
        } while (cur != first);
        return arr;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    * Complexity: w.c O(n) amortized O(log n)
    */
    public void delete(HeapNode x) 
    {    
    	decreaseKey(x, Integer.MAX_VALUE);
        deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    * Complexity: w.c O(n) amortized O(1);
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.setKey(x.getKey() - delta);
        // check if new minNode
        if (x.getKey() < minNode.getKey()) {
            minNode = x;
        }
        // check if tree is not valid
        if ((x.getParent() != null) && (x.getParent().getKey() > x.getKey())) {
            cascadingCut(x, x.getParent());
        }

    }

    /**
     * private void cut(HeapNode son, HeapNode parent)
     *
     * Cutting son from parent and place it as first tree in heap
     * Complexity: O(1)
     */
    private void cut(HeapNode son, HeapNode parent){
        cutsCounter++;
        son.parent = null;
        if (son.isMark()) {
            son.unMark();
            numMark--;
        }
        parent.setRank(parent.getRank()-1);
        if (son.getNext() == son) {
            parent.setChild(null);
        }
        else {
            son.getPrev().setNext(son.getNext());
            son.getNext().setPrev(son.getPrev());
            if (parent.getChild() == son) {
                parent.setChild(son.getNext());
            }
        }
        placeNodeFirst(son);
    }

    /**
     * private void cascadingCut(HeapNode son, HeapNode parent)
     * Perform cascading cuts on tree until tree is valid
     * Complexity: w.c. O(log n) amortized O(1)
     */
    private void cascadingCut(HeapNode son, HeapNode parent)
    {
        cut(son, parent);
        // if parent is root
        if (parent.getParent() == null) {
            return;
        }
        if (parent.isMark()) {
            cascadingCut(parent, parent.getParent());
        }
        else{
            parent.mark();
            numMark++;
        }
    }

    /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap.
    * Complexity O(1)
    */
    public int potential() 
    {    
    	return trees + 2 * numMark;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    * Complexity O(1)
    */
    public static int totalLinks()
    {    
    	return linksCounter;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods).
    * Complexity O(1)
    */
    public static int totalCuts()
    {    
    	return cutsCounter;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H.
    * Complexity: O(k*deg(H))
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        int[] arr = new int[k];
        FibonacciHeap temp = new FibonacciHeap();
        temp.insert(H.minNode.getKey(), H.minNode);
        for (int i=0; i < k; i++) {
            HeapNode curMin = temp.findMin();
            arr[i] = curMin.getKey();
            HeapNode firstChild = curMin.getInfo().getChild();
            if (firstChild != null) {
                HeapNode cur = firstChild;
                do {
                    temp.insert(cur.getKey(), cur);
                    cur = cur.getNext();
                } while (cur != firstChild);
            }
            temp.deleteMin();
        }
        return arr;
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{

    	public int key;
        private int rank;
        private boolean mark;
        private HeapNode info;
        private HeapNode child;
        private HeapNode next;
        private HeapNode prev;
        private HeapNode parent;

    	public HeapNode(int key) {
    		this(key, null);
    	}

       public HeapNode(int key, HeapNode info) {
           this.key = key;
           mark = false;
           rank = 0;
           child = null;
           parent = null;
           next = null;
           prev = null;
           this.info = info;
       }

    	public int getKey() {
    		return this.key;
    	}

       public void setKey(int key) {
           this.key = key;
       }

       public int getRank() {
           return rank;
       }

       public void setRank(int rank) {
           this.rank = rank;
       }

       public boolean isMark() {
           return mark;
       }

       public void mark() {
           mark = true;
       }

       public void unMark() {
            mark = false;
       }

       public HeapNode getChild() {
           return child;
       }

       public void setChild(HeapNode child) {
           this.child = child;
       }

       public HeapNode getNext() {
           return next;
       }

       public void setNext(HeapNode next) {
           this.next = next;
       }

       public HeapNode getPrev() {
           return prev;
       }

       public void setPrev(HeapNode prev) {
           this.prev = prev;
       }

       public HeapNode getParent() {
           return parent;
       }

       public void setParent(HeapNode parent) {
           this.parent = parent;
       }

       public HeapNode getInfo() {
           return info;
       }

       public void setInfo(HeapNode info) {
           this.info = info;
       }
   }
}
