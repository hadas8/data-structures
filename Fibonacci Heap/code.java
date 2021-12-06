/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap
{
	public static int CUTS = 0;
	public static int LINKS = 0;
	private HeapNode min;
	private HeapNode first;
	private int size;
	private int numOfRoots;
	private int markedNodes;
	
	/**
	 * public FibonacciHeap()
	 * 
	 * FibonacciHeap constructor, creates an empty heap
	 * 
	 * Complexity is O(1)
	 */
	public FibonacciHeap() {
		size = 0;
		numOfRoots = 0;
		min = null;
		markedNodes = 0;
		first = null;
	}
	
	/**
	 * public boolean isEmpty()
	 *
	 * precondition: none
	 * 
	 * The method returns true if and only if the heap is empty.
	 * 
	 * Complexity is O(1): a simple check
	 */
	public boolean isEmpty()
	{
	  	return getFirst() == null;
	}
			
	/**
	 * public HeapNode insert(int key)
	 *
	 * creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
	 * 
	 *  Complexity is O(1): inserts new node as tree with rank 0.
	 */
	public HeapNode insert(int key)
	{  
	   	HeapNode newNode = new HeapNode(key);
	   	if(isEmpty()) {
	   		setMin(newNode);
	   	}
	   	else {
	   		HeapNode next = getFirst();
	   		HeapNode prev = next.getPrev();
	   		insertRoots(prev, next, newNode);
	   		if(newNode.getKey() < getMin().getKey()) {
	   			setMin(newNode);
	   		}
	   	}
	   	setFirst(newNode);
	   	increaseNumOfRoots();
	   	increaseSize();
	   	return newNode;
	}

	/**
	 * public void deleteMin()
	 *
	 * deletes the node containing the minimum key.
	 *
	 * worst case complexity is O(n): rank of min is at most O(log n) + worst case for consolidate is O(log n)
	 */
	public void deleteMin()
	{
	   	HeapNode min;
	   	HeapNode prev;
	   	HeapNode next;
	   	HeapNode child;
	   	HeapNode nextSfterMin;
	   	int rank;
	   	if(getSize() <= 1) {	//if Heap is empty or has one node, clears the heap
	   		clear();
	   	}
	   	else {
	   		min = getMin();
	   		prev = min.getPrev();
	   		next = min.getNext();
	   		rank = min.getRank();
	   		child = min.getChild();
	   		if(rank == 0) {		//if the minimal node has no children, deletes it from the root list
	   			prev.setNext(next);
	   			next.setPrev(prev);
	   			nextSfterMin = next;
	   		}
	   		else {
	   			nextSfterMin = child;
	   			if(getNumOfRoots() > 1) {
	   				insertRoots(prev, next, child);
	   			}
	   			for (int i = 0; i < rank; i++) {
	   				child.setParent(null);
	   				if(child.isMarked()) {
	   					child.unmark();
	   					decreaseMarkedNodes();
	   				}
	   			}
	   		}
	   		if(getFirst().getKey() == min.getKey()) {
	   			setFirst(nextSfterMin);
	   		}
	   		setNumOfRoots(getNumOfRoots() + rank - 1);
	   		decreaseSize();
	   		consolidate();
	   	} 
	}

	/**
	 * public HeapNode findMin()
	 *
	 * returns the node of the heap whose key is minimal.
	 *  
	 *Complexity is O(1): the minimal node is saved as a FibonacciHeap field
	 */
	public HeapNode findMin()
	{
	   	return getMin();
	}  
    
    /**
     * public void meld (FibonacciHeap heap2)
     *
     * melds the heap with heap2
     * 
     * complexity is O(1): - uses insertRoots to change the relevant prev and next fields for the roots
     * 					   - updates heap fields in simple calculations.
     *
     */
     public void meld (FibonacciHeap heap2)
     {
    	 if (!heap2.isEmpty()) {
    		 if(this.isEmpty()) {
    			setFirst(heap2.getFirst());
    			setMin(heap2.getMin());
    		 }
    		 else {
    			 updateMin(heap2.getMin());
    	     	 HeapNode first = getFirst();
    	     	 HeapNode last = first.getPrev();
    	     	 HeapNode first2 = heap2.getFirst();
    	     	  
    	     	 insertRoots(last, first, first2);
    		 }
    	 }
    	 setMarkedMNodes(getMarkedNodes() + heap2.getMarkedNodes());
    	 setNumOfRoots(getNumOfRoots() + heap2.getNumOfRoots());
    	 setSize(getSize() + heap2.getSize());
     }

   /**
    * public int size()
    *
    * returns the number of elements in the heap
    * 
    * Complexity is O(1): size is saved as a FibonacciHeap field   
    */
    public int size()
    {
    	return getSize();
    }
    	
    /**
     * public int[] countersRep()
     *
     * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
     * 
     * complexity is O(n): going through list of roots, O(n) in worst case, and afterwards
     * going through all possible ranks, at most O(log n).
     */
     public int[] countersRep()
     {
    	 if (isEmpty()) {
    		 return new int[0];
    	 }
     	int maxRank = 0;
     	HeapNode root = getFirst();
     	int rank;
     	int[] arr;
     	int numOfRoots = getNumOfRoots();
     	for(int i = 0; i < numOfRoots; i++) {
     		rank = root.getRank();
     		if (rank > maxRank) {
     			maxRank = rank;
     		}
     		root = root.getNext();
     	}
     	arr = new int[maxRank+1];
     	for (int  i = 0; i < numOfRoots; i++) {
     		arr[root.getRank()]++;
     		root = root.getNext();
     	}
        return arr;
     }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
    * 
    * worst case complexity is O(n): calls decreaseKey in O(n) at worst case and deleteMin in O(n) at worst case    *
    */
    public void delete(HeapNode x) 
    {    
    	if (x.getKey() != getMin().getKey()) {
    		int delta = x.getKey() - getMin().getKey() + 1;
        	decreaseKey(x, delta);
    	}
    	deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading CUTS procedure should be applied if needed).
    * 
    * worst case complexity is O(n): calls the helper method makeRoot in O(1) and the method cascade
    * that operates in O(n) at worst case.
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	HeapNode parent = x.getParent();
    	x.setKey(x.getKey() - delta);
    	if (!x.isRoot()) {
    		if (x.getKey() < parent.getKey()) {
    			makeRoot(x);
            	cascade(parent);
    		}
    	}
    	updateMin(x);
    }
            
   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap.
    * 
    * Complexity is O(1): potential is calculated by arithmetic operation on int type fields
    */
    public int potential() 
    {    
    	return getNumOfRoots() + 2*getMarkedNodes();
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    * 
    * Complexity is O(1): the links number is a static field of this class
    */
    public static int totalLinks()
    {    
    	return LINKS;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which disconnects a subtree from its parent (during decreaseKey/delete methods).
    * 
    * Complexity is O(1): the cuts number is a static field of this class
    */
    public static int totalCuts()
    {    
    	return CUTS;
    }

  /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * 
    * Complexity is O(k(logk+degH)): for each of the k minimal nodes this method calls
    * the static method insertLevel in O(degH) and also calls deleteMin on a helper heap of O(k*degH) nodes
    * in O(log(k*degH)).
    * So for each node in the final array the complexity is O(degH + log(k*degH)) = O(logk + degH)
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
        int[] keyArr = new int[k];
        FibonacciHeap temp = new FibonacciHeap();
        HeapNode currMin = H.findMin();
        insertLevel(temp, currMin);
        int i = 0;
        while (i < k) {
        	currMin = temp.findMin();
            HeapNode orgNode = currMin.getPointer();
            temp.deleteMin();
            keyArr[i] = currMin.getKey();
            i++;
            insertLevel(temp, orgNode.getChild());
        }
        return keyArr;
    }
    
      /***************************/
     /*** getters and setters ***/
    /***************************/
    
    /**
     * public void setMin(HeapNode newMin)
     * 
     * sets the heap's minimal node to newMin
     * 
     * complexity: O(1)
     */
	public void setMin(HeapNode newMin) {
		this.min = newMin;
	}
	
	/**
	 * public HeapNode getMin()
	 * 
	 * returns the node with minimal key in the heap, null if the heap is empty
	 * 
	 * Complexity: O(1)
	 */
	public HeapNode getMin() {
		return this.min;
	}
	
	/**
	 * public void setFirst(newNode newFirst)
	 * 
	 * sets the first root (from the left) of the heap to newFirst
	 * 
	 * complexity: O(1)
	 * 
	 */
	public void setFirst(HeapNode newFirst) {
		this.first = newFirst;
	}
	
	/**
	 * public HeapNode getFirst()
	 * 
	 * returns the first root of the heap, null if the heap is empty
	 * 
	 * complexity: O(1)
	 * 
	 */
	public HeapNode getFirst() {
		return this.first;
	}
	
	/**
	 * public void setSize(int newSize)
	 * 
	 * set the heap's size to newSize
	 * 
	 * complexity: O(1)
	 * 
	 */
	public void setSize(int newSize) {
		this.size = newSize;
	}
	
	/**
	 * public void increaseSize()
	 * 
	 * increase the size of the heap by 1
	 * 
	 * complexity: O(1)
	 */
	public void increaseSize() {
		this.size++;
	}
	
	/**
	 * public void decreaseSize()
	 * 
	 * decrease the size of the heap by 1
	 * 
	 * complexity: O(1)
	 */
	public void decreaseSize() {
		this.size--;
	}
	
	/**
	 * public int getSize()
	 * 
	 * returns the size of the heap - the amount of HeapNodes in the heap
	 * 
	 * complexity: O(1)
	 */
	public int getSize() {
		return this.size;
	}
	
	/**
	 * public void setNumOfRoots(int newNum)
	 * 
	 * set the number of roots to newNum
	 */
	public void setNumOfRoots(int newNum) {
		this.numOfRoots = newNum;
	}
	
	/**
	 * public void increaseNumOfRoots()
	 * 
	 * increase number of roots by 1
	 * 
	 * complexity: O(1)
	 */
	public void increaseNumOfRoots() {
		this.numOfRoots++;
	}
	
	/**
	 * public void decreaseNumOfRoots()
	 * 
	 * decrease number of roots  by 1
	 * 
	 * complexity: O(1)
	 */
	public void decreaseNumOfRoot() {
		this.numOfRoots--;
	}
	
	/**
	 * public int getNumOf Roots()
	 * 
	 * returns the number of roots in the heap
	 * 
	 * complexity: O(1)
	 */
	public int getNumOfRoots() {
		return this.numOfRoots;
	}
	
	/**
	 * public void setMarkedNodes(int newMarked)
	 * 
	 * sets the amount of marked nodes in the heap to newMarked
	 * 
	 * complexity: O(1)
	 */
	public void setMarkedMNodes(int newMarked) {
		this.markedNodes = newMarked; 
	}
	
	/**
	 * public void increaseMarkedNodes()
	 * 
	 * increase the amount of marked nodes by 1
	 * 
	 * complexity: O(1)
	 */
	public void increaseMarkedNodes() {
		this.markedNodes++;
	}
	
	/**
	 * public void decreaseMarkedNodes()
	 * 
	 * decrease the amount of marked nodes by 1
	 * 
	 * complexity: O(1)
	 */
	public void decreaseMarkedNodes() {
		this.markedNodes--;
	}
	
	/**
	 * public int getMarkedNodes()
	 * 
	 * returns the amount of marked nodes in the heap
	 * 
	 * complexity: O(1)
	 */
	public int getMarkedNodes() {
		return this.markedNodes;
	}
    
    /**
     * private void markNode(HeapNode x)
     * 
     * marks the node x and updates markedNodes field accordingly
     * 
     * Complexity is O(1): calls HeapNode method mark() in O(1) time
     */
    private void markNode(HeapNode x) {
    	if (!x.isMarked()) {
    		x.mark();
    		increaseMarkedNodes();
    	}
    }
    
    /**
     * private void unmarkNode(HeapNode x)
     * 
     * removes the mark from node x and updates markedNodes field accordingly
     * 
     * Complexity is O(1): calls HeapNode method unmark() in O(1) time
     */
    private void unmarkNode(HeapNode x) {
    	if (x.isMarked()) {
    		x.unmark();
    		decreaseMarkedNodes();
    	}
    }
    
    /**
     * private void updateMin(HeapNode x)
     * 
     * updates the pointer to the minimum node if x.key is smaller the the current minimum
     * 
     * precondition: both nodes are legal roots in the heap
     * 
     * Complexity is O(1): changing one pointer
     */
    private void updateMin(HeapNode x)
    {
    	if (x.getKey() < getMin().getKey()) {
    		setMin(x);
    	}
    }
    
      /***********************************/
     /*** helper methods to deleteMin ***/
    /***********************************/
	
    /**
	 * private HeapNode link(HeapNode root1, HeapNode root2)
	 * 
	 * links two trees with the same rank to one tree
	 * returns the tree after the link
	 * 
	 * precondition: root1.getRank() == root2.getRank()
	 * 
	 * Complexity is O(1): changing a limited number of pointers
	 */
	private HeapNode link(HeapNode root1, HeapNode root2) {
		HeapNode prev;
		HeapNode next;
		HeapNode oldChild;
		HeapNode childPrev;
		HeapNode parent;
		HeapNode newChild;
		int rank;
		if (root1.getKey() < root2.getKey()) {
			parent = root1;
			newChild = root2;
		}
		else {
			parent = root2;
			newChild = root1;
		}
		prev = newChild.getPrev();
		next = newChild.getNext();
		rank = parent.getRank();
		prev.setNext(next);
		next.setPrev(prev);
		if(rank == 0) {
			newChild.setNext(newChild);
			newChild.setPrev(newChild);
		}
		else {
			oldChild = parent.getChild();
			childPrev = oldChild.getPrev();
			childPrev.setNext(newChild);
			oldChild.setPrev(newChild);
			newChild.setPrev(childPrev);
			newChild.setNext(oldChild);
		}
		newChild.setParent(parent);
		parent.setChild(newChild);
		parent.increaseRank();
		LINKS++;
		return parent;
	}
	
	/**
	 * private void clear()
	 * 
	 * a helper method that clears the heap: turning it into an empty heap
	 * 
	 * Complexity is O(1): changing the entries of fields int the heap
	 */
	private void clear() {
		setFirst(null);
		setMin(null);
		setNumOfRoots(0);
		setSize(0);
		setMarkedMNodes(0);
	}
	
	/**
	 * private void consolidate()
	 * 
	 * consolidates trees of same rank in the heap
	 * 
	 * postcondition: at most 1 tree from each rank in the heap
	 * 
	 * Worst case complexity is O(n): toBuckets in worst case O(n) + fromBuckets in O(log n)
	 */
	private void consolidate() {
		int maxRank = (int) Math.ceil(1.45 * Math.log(getSize() + 1) / Math.log(2)) + 1;
		HeapNode[] rankTrees = new HeapNode[maxRank];
		toBuckets(rankTrees);
		fromBuckets(rankTrees);
	}
	
	/**
	 * private void toBuckets(HeapNode[] rankTrees)
	 * 
	 * helper method for consolidate: going through root,
	 * and linking all trees of the same rank
	 * 
	 * precondition: rankTrees is array of null HeapNodes
	 * precondition: rankTrees.size = O(log n)
	 * 
	 * worst case complexity is O(n): worst case for a heap full of new nodes (n trees of rank 0),
	 * so the method iterates over n roots. 
	 */
	private void toBuckets(HeapNode[] rankTrees) {
		HeapNode root = getFirst();
		HeapNode bucketRoot;
		int rank;
		for(int i = 0; i < getNumOfRoots(); i++){
			bucketRoot = root;
			root = root.getNext();
			rank = bucketRoot.getRank();
			while(rankTrees[rank] != null) {
				bucketRoot = link(bucketRoot, rankTrees[rank]);
				rankTrees[rank] = null;
				rank++;
			}
			rankTrees[rank] = bucketRoot;
		}
	}
	
	/**
	 * private void fromBuckets(HeapNode[] rankTrees)
	 * 
	 * a helper method for consolidate: adding linked trees to heap ordered by rank
	 * 
	 * precondition: rankTrees.size = O(log n)
	 * 
	 * complexity is O(log n): the size of the buckets array
	 */
	private void fromBuckets(HeapNode[] rankTrees) {
		int newNumOfRoots = 0;
		int size = getSize();
		int markedNodes = getMarkedNodes();
		clear();
		for (HeapNode tree : rankTrees) {
			if(tree != null) {
				tree.setNext(tree); 
				tree.setPrev(tree);
				if(isEmpty()) {
					setFirst(tree);
					setMin(tree);
				}
				else {
					HeapNode next = getFirst();  
					HeapNode prev = next.getPrev();
					insertRoots(prev, next, tree);
					updateMin(tree);
				}
				newNumOfRoots++;
			}
		}
		setSize(size);
		setMarkedMNodes(markedNodes);
		setNumOfRoots(newNumOfRoots);
	}
	
	/**
	 * private void insertRoots(HeapNode prev, HeapNode next, HeapNode firstRoot)
	 * 
	 * a helper method: inserting a list of linked nodes (list may also contain 1 node)
	 * inside the list of roots by setting them between prev and next.
	 * 
	 * Complexity is O(1): changing a limited nmber of pointers
	 */
	private void insertRoots(HeapNode prev, HeapNode next, HeapNode firstRoot) {
		HeapNode lastRoot = firstRoot.getPrev();
		prev.setNext(firstRoot);
		firstRoot.setPrev(prev);
		lastRoot.setNext(next);
		next.setPrev(lastRoot); 
	}
	
	  /*************************************/
	 /*** helper methods to decreaseKey ***/
	/*************************************/
	
	/**
     * private void makeRoot(HeapNode x)
     * 
     * makes x a root in the heap
     * 
     * precondition: x is an existing node in the heap and not a root (has a parent)
     * 
     * Complexity is O(1): changing a limited number of pointers
     */
    private void makeRoot(HeapNode x)
    {
    	HeapNode parent = x.getParent();
    	parent.decreaseRank();
    	increaseNumOfRoots();
    	CUTS++;
    	if (!x.isOnlyChild()) {	//update parent
    		if (parent.getChild().getKey() == x.getKey()) {
    			parent.setChild(x.getNext());
    		}
    		x.getPrev().setNext(x.getNext()); //update former sibilings
    		x.getNext().setPrev(x.getPrev());
    	}
    	else {
    		parent.setChild(null);
    	}
    	x.setParent(null);
    	HeapNode prev = getFirst().getPrev(); //update current sibilings
    	prev.setNext(x);
    	getFirst().setPrev(x);
    	x.setPrev(prev);
    	x.setNext(getFirst());
    	setFirst(x);
    	unmarkNode(x);
    }
    
    /**
     * private void cascade(HeapNode x)
     * 
     *implements the cascading cuts algorithm under the decrease-key operation
     *
     *worst case complexity is O(n): the method passes through each node in the path between x
     *and its tree root while they are marked. At worst case it will go through the entire height
     *of the tree. In fibonacci heaps the height is not bounded so the tree height will be at worst case n,
     *and the WC complexity is O(n).
     *
     */
    private void cascade(HeapNode x)
    {
    	if (x.isRoot()) {
    		return;
    	}
    	if (!x.isMarked()) {
    		markNode(x);
    	}
    	else {
    		HeapNode parent = x.getParent();
        	makeRoot(x);
        	cascade(parent);
    	}
    }
	
      /*****************************/
	 /*** helper method to kMin ***/
    /*****************************/
	
    /**
	 * private static void insertLevel(FibonacciHeap C, HeapNode x)
	 * 
	 * helper method to kMin
	 * 
	 * inserts all sibilings of x into C, the helper heap
	 * 
	 * Complexity is O(degH), when H is a binomial heap that has x as a node in it:
	 * this method adds every sibiling x has in H, and the maximum number of sibilings in a binomial heap is degH
	 */
	 private static void insertLevel(FibonacciHeap C, HeapNode x) {
	  	if (x != null) {
	   		HeapNode child = x;
	   		int childPointerKey = child.getKey();
	   		HeapNode curr = C.insert(child.getKey());
	   		curr.setPointer(child);
	   		child = child.getNext();
	   		while (childPointerKey != child.getKey()) {
	   			curr = C.insert(child.getKey());
	   			curr.setPointer(child);
	   			child = child.getNext();
	   		}
	   	}
	 }

		
   /**
    * public class HeapNode
    * 
    * this class implements a node in a Fibonacci heap
    */
    public class HeapNode {

    	public int key;
    	private int rank;
    	private boolean marked;
    	private HeapNode child;
    	private HeapNode parent;
    	private HeapNode prev;
    	private HeapNode next;
    	private HeapNode nodePointer;
    	
    	/**
    	 * public HeapNode(int key)
    	 * 
    	 * HeapNode constructor, creates a node and sets its key to the key received
    	 * 
    	 * Complexity is O(1)
    	 */
    	public HeapNode(int key) {
    		this.key = key;
    		marked = false;
    		rank = 0;
    		next = this;
    		prev = this;
    	}
    	
    	/**
    	 * public HeapNode getPointer()
    	 * 
    	 * returns a pointer to a different node that was saved to this node
    	 * (used only for the purpose of the static method kMin)
    	 * 
    	 * Complexity is O(1)
    	 */
    	public HeapNode getPointer() {
    		return nodePointer;
    	}
    	
    	/**
    	 * public void setPointer(HeapNode x)
    	 * 
    	 * sets this node to have a pointer to a different node
    	 * (used only for the purpose of the static method kMin)
    	 * 
    	 * Complexity is O(1)
    	 */
    	public void setPointer(HeapNode x) {
    		nodePointer = x;
    	}
    	
    	/**
    	 * public int getKey()
    	 * 
    	 * returns the key of the node
    	 * 
    	 * Complexity is O(1)
    	 */
    	public int getKey() {
    		return this.key;
    	}

    	/**
    	 * public void setKey(int newKey)
    	 * 
    	 * sets the node's key to newKey
    	 * 
    	 * Complexity is O(1)
    	 */
    	public void setKey(int newKey) {
    		key = newKey;
    	}
    	
    	/**
    	 * public int getRank()
    	 * 
    	 * returns the node's rank
    	 * 
    	 * Complexity is O(1)
    	 */
    	public int getRank() {
    		return this.rank;
    	} 
    	
       	/**
    	 * public void setRank(int newRank)
    	 * 
    	 * sets the node's rank to newRank
    	 * 
    	 * Complexity is O(1)
    	 */
    	public void setRank(int newRank) {
    		rank = newRank;
    	}
    	
    	/**
    	 * public void increaseRank()
    	 * 
    	 * increases the node's rank by 1
    	 * 
    	 * Complexity is O(1)
    	 */
    	public void increaseRank() {
      		rank++;
      	}
    	
    	/**
    	 * public void decreaseRank()
    	 * 
    	 * decreases the node's rank by 1
    	 * 
    	 * Complexity is O(1)
    	 */
    	public void decreaseRank() {
    		rank--;
    	}
    	
    	/**
    	 * public boolean isMarked()
    	 * 
    	 * returns true iff the node is marked (had a child cut from it and isn't a root)
    	 * 
    	 * Complexity is O(1)
    	 */
    	public boolean isMarked() {
    		return marked;
    	}
    	    	
    	/**
    	 * public HeapNode getParent()
    	 * 
    	 * returns the parent of the node, null if there isn't one
    	 * 
    	 * Complexity is O(1)
    	 */
    	public HeapNode getParent() {
    		return parent;
    	}
    	
    	/**
    	 * public void setParent(HeapNode x)
    	 * 
    	 * sets the node's parent to x
    	 * 
    	 * Complexity is O(1)
    	 */
    	public void setParent(HeapNode x) {
    		parent = x;
    	}
    	
    	/**
    	 * public HeapNode getChild()
    	 * 
    	 * returns the child of the node, null if there isn't one
    	 * 
    	 * Complexity is O(1)
    	 */
    	public HeapNode getChild() {
    		return child;
    	}
    	
    	/**
    	 * public void setChild(HeapNode x)
    	 * 
    	 * sets the node's child to x
    	 * 
    	 * Complexity is O(1)
    	 */
    	public void setChild(HeapNode x) {
    		child = x;
    	}
    	
    	/**
    	 * public HeapNode getNext()
    	 * 
    	 * returns the next sibiling of the node
    	 * 
    	 * Complexity is O(1)
    	 */
    	public HeapNode getNext() {
    		return next;
    	}
    	
    	/**
    	 * public void setNext(HeapNode x)
    	 * 
    	 * sets the node's next sibiling to x
    	 * 
    	 * Complexity is O(1)
    	 */
    	public void setNext(HeapNode x) {
    		next = x;
    	}
    	
    	/**
    	 * public HeapNode getPrev()
    	 * 
    	 * returns the previous sibiling of the node
    	 * 
    	 * Complexity is O(1)
    	 */
    	public HeapNode getPrev() {
    		return prev;
    	}
    	
    	/**
    	 * public void setPrev(HeapNode x)
    	 * 
    	 * sets the node's previous sibiling to x
    	 * 
    	 * Complexity is O(1)
    	 */
    	public void setPrev(HeapNode x) {
    		prev = x;
    	}
    	
    	/**
    	 * public boolean isOnlyChild()
    	 * 
    	 * returns true iff the node has no sibilings
    	 * 
    	 * Complexity is O(1)
    	 */
    	public boolean isOnlyChild() {
    		return next.getKey() == key;
    	}
    	
    	/**
    	 * public void mark()
    	 * 
    	 * marks the node
    	 * 
    	 * Complexity is O(1)
    	 */
    	public void mark() {
    		marked = true;
    	}
    	
    	/**
    	 * public void unmark()
    	 * 
    	 * removes the mark from the node
    	 * 
    	 * Complexity is O(1)
    	 */
    	public void unmark() {
    		marked = false;
    	}
    	
    	/**
    	 * public boolean isRootk()
    	 * 
    	 * returns true iff the node is a root
    	 * 
    	 * Complexity is O(1)
    	 */
    	public boolean isRoot() {
    		return parent == null;
    	}    	
    }
}
