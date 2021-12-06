/**
 * AVLTree
 *
 * An implementation of a AVL Tree with distinct integer keys and info
 *
 */

public class AVLTree {
	
	private IAVLNode root;
	
	/**
	 * public AVLTree()
	 * 
	 * AVLTree constructor, sets root field to a virtual node
	 */
	public AVLTree()
	{
		this.root = new AVLNode();
	}
	
	/**
	 * public AVLTree(IAVLNode node)
	 * 
	 * AVLTree constructor, creates a new tree from existing subtree and separates it from its parent
	 * 
	 * precondition: subtree is AVL tree, node is not null
	 */
	public AVLTree(IAVLNode node)
	{
		root = node;
		if (node.getParent() != null) { 
			IAVLNode virtual = new AVLNode();
			if (node.isLeftChild()) {
				node.getParent().setLeft(virtual);
			}
			else {
				node.getParent().setRight(virtual);
			}
			node.setParent(null);
		}
	}
	
  /**
   * public boolean empty()
   *
   * returns true if and only if the tree is empty
   */
  public boolean empty() {
    return !root.isRealNode();
  }

  /**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree otherwise,
	 * returns null
	 * 
	 * complexity is O(logn): calls a method that uses a binary search
	 */
	public String search(int k) {
		IAVLNode node = this.treePosition(this.root, k);
		if (node != null && node.getKey() == k) { //added null condition 
			return node.getValue();
		}
		return null;
	}

	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the AVL tree. the tree must remain
	 * valid (keep its invariants). returns the number of rebalancing operations, or
	 * 0 if no rebalancing operations were necessary. returns -1 if an item with key
	 * k already exists in the tree.
	 * 
	 * complexity is O(log n): using treePosition in O(log n), updateSizeUp in O(log n), 
	 * rebalance in O(log n) and other operations in O(1)
	 */
	public int insert(int k, String i) {
		IAVLNode newNode;
		IAVLNode parent;
		if (k < 0) { // key is not legal
			return -1;
		}
		newNode = new AVLNode(k, i);
		if (this.empty()) { // if the tree is empty, the node becomes the new root
			this.root = newNode;
			return 0;
		}
		parent = this.treePosition(this.root, k);
		int parentKey = parent.getKey();
		if (parentKey == k) { // key k already exists in the tree
			return -1;
		} else {
			newNode.setParent(parent);
			if (k < parentKey) {
				parent.setLeft(newNode);
			} else {
				parent.setRight(newNode);
			}
			updateSizeUp(parent);
			return Rebalance.rebalance(this, parent);
		}
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there; the tree
	 * must remain valid (keep its invariants). returns the number of rebalancing
	 * operations, or 0 if no rebalancing operations were needed. returns -1 if an
	 * item with key k was not found in the tree.
	 * 
	 * compelexity: O(log n): uses treePosition in O(log n) and helper delete functions in O(log n),
	 * rebalance in O(log n) and other operations in O(1)
	 */
	public int delete(int k) {
		IAVLNode node;
		IAVLNode parent; //node that will need rebalancing
		if (this.empty()) { //if the tree is empty than k is not a key in the tree
			return -1;
		}
		node = treePosition(this.root, k);
		parent = node.getParent();
		if (node.getKey() != k) { 	//k is not found in the tree
			return -1;
		} else if (node.isALeaf()) { //if the node is a leaf, removes it
			deleteLeaf(node);
			return Rebalance.rebalance(this, parent);
		} else if (!node.myGetLeft().isRealNode() || !node.myGetRight().isRealNode()) { 	//the node has one child
			deleteNodeWithOneChild(node);
			return Rebalance.rebalance(this, parent);
		} else { 	//the node has 2 children
			IAVLNode successor = successor(node);
			int successorKey = successor.getKey();
			String successorValue = successor.getValue();
			parent = successor.getParent();
			
			IAVLNode replacement = new AVLNode(successorKey, successorValue); //defining new node with characteristics of the successor
			IAVLNode nodeParent = node.getParent();
			replacement.setParent(nodeParent);
			if(nodeParent != null && nodeParent.isRealNode()) { //updating parent of new child
				if (node.isLeftChild()) {
					nodeParent.setLeft(replacement);
				} else {
					nodeParent.setRight(replacement);
				}
			} else {
				this.root = replacement;
			}
			IAVLNode leftChild = node.myGetLeft();
			IAVLNode rightChild = node.myGetRight();
			replacement.setLeft(leftChild);
			replacement.setRight(rightChild);
			if(leftChild.isRealNode()) { //updating left child of new parent
				leftChild.setParent(replacement);
			}
			if(rightChild.isRealNode()) { //updating right child of new parent
				rightChild.setParent(replacement);
			}

			Rebalance.updateHeight(replacement);
			Rebalance.updateSize(nodeParent);
			
			//deleting successor from tree
			if (successor.isALeaf()) { 
				deleteLeaf(successor);
			} else {
				deleteNodeWithOneChild(successor);
			}
			
			if(parent.getKey() == node.getKey()) {
				parent = replacement;
			}
			return Rebalance.rebalance(this, parent);
		}
	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree, or null if
	 * the tree is empty
	 * 
	 * complexity is: O(logn): uses minNode in O(log n)
	 */
	public String min() {
		if(this.empty()) {
			return null;
		}
		IAVLNode min = minNode(this.root);
		return min.getValue();
	}

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty
    */
   public String max()
   {
	   if (empty()) {
		   return null;
	   }
	   IAVLNode curr = root;
	   while (curr.myGetRight().isRealNode()) {
		   curr = curr.myGetRight();
	   }
	   return curr.getValue();
   }
   
   
  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
  public int[] keysToArray()
  {
        int n = size();
	  	int[] arr = new int[n];
	  	keysArray(arr, root, 0);
        return arr;
  }


  /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */
  public String[] infoToArray()
  {
        int n = size();
	  	String[] arr = new String[n];
	  	infoArray(arr, root, 0);
        return arr;
  }

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    *
    * precondition: none
    * postcondition: none
    */
   public int size()
   {
	   return root.getSize();
   }
   
     /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    *
    * precondition: none
    * postcondition: none
    */
   public IAVLNode getRoot()
   {
	   return root;
   }
     /**
    * public string split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	* precondition: search(x) != null (therefore tree is not empty)
    * postcondition: none
    * complexity is O(logn): joins logn subtrees at worst case and overall rank difference sums up to logn (according to what was taught in class)
    */   
   public AVLTree[] split(int x)
   {
	   IAVLNode curr = treePosition(root, x);
	   AVLTree larger = new AVLTree(curr.myGetRight());
	   AVLTree smaller = new AVLTree(curr.myGetLeft());
	   while (curr.getParent() != null) { 
		   IAVLNode parent = curr.getParent();
		   IAVLNode mergeNode = new AVLNode(parent.getKey(), parent.getValue());
		   if (curr.isRightChild()) {
			   AVLTree left = new AVLTree(parent.myGetLeft());
			   smaller.join(mergeNode, left);
		   }
		   else {
			   AVLTree right = new AVLTree(parent.myGetRight());
			   larger.join(mergeNode, right);
		   }
		   curr = parent;
	   }
	   AVLTree[] res = {smaller, larger};
	   return res; 
   }
   
   /**
    * public join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree.
    * Returns the complexity of the operation (rank difference between the tree and t + 1)
	* precondition: keys(x,t) < keys() or keys(x,t) > keys()
    * postcondition: none
    * 
    * complexity is O(|height() - t.height()| + 1): the method travels down to the merging point,
    * and then travels up (|height() - t.height()| + 1) times at most to rebalance the tree 
    */   
   public int join(IAVLNode x, AVLTree t)
   {
	   if (empty() && t.empty()) { //both trees are empty
		   root = x;
		   return 1;
	   }
	   if (empty()) { //original tree is empty
		   t.insert(x.getKey(), x.getValue());
		   root = t.getRoot();
		   return height() + 2;
	   }
	   if (t.empty()) { //t is empty
		   insert(x.getKey(), x.getValue());
		   return height() + 2;
	   }
	   int k = t.height();
	   int treeRank = height();
	   IAVLNode curr;
	   int key = x.getKey();
	   if ((key > t.getRoot().getKey()) && key < root.getKey()) { // t < x < original
		   if (k > treeRank) {	//t has the bigger height
			   curr = t.moveRight(treeRank, size());
			   setJoinNodeRight(x);
			   x.setLeft(curr);
			   root = t.getRoot();
		   }
		   else {	//original has the bigger height
			   curr = moveLeft(k, t.size());
			   t.setJoinNodeLeft(x);
			   x.setRight(curr);   
		   }
	   }
	   else {	// original < x < t
		   if (k > treeRank) {	//t has the bigger height
			   curr = t.moveLeft(treeRank, size());
			   setJoinNodeLeft(x);
			   x.setRight(curr);
			   root = t.getRoot();
		   }
		   else {	//original has the bigger height
			   curr = moveRight(k, t.size());
			   t.setJoinNodeRight(x);
			   x.setLeft(curr);
		   }
	   }
	   Rebalance.updateHeight(x);
	   Rebalance.updateSize(x);
	   IAVLNode parent = curr.getParent();
	   curr.setParent(x);
	   x.setParent(parent);
	   if (parent != null && parent.isRealNode()) {	//set parent
		   if (key < parent.getKey()) {
			   parent.setLeft(x);
		   }
		   else {
			   parent.setRight(x);
		   }
		   Rebalance.rebalance(this, parent);
	   }
	   else {
		   root = x;
	   }
	   return Math.abs(k - treeRank) + 1;
   }
   
   /**
    * private void keysArray(int[] arr, IAVLNode node, int left)
    * 
    * helper method to keysToArray()
    * updates the received array to contain sorted tree keys
    * 
    * Complexity is O(n): the recursion passes through each node once and inserts its key in the array
    */
   private void keysArray(int[] arr, IAVLNode node, int left)
   {
	   if (node.isRealNode()) {
		   int i = left + node.myGetLeft().getSize();
		   arr[i] = node.getKey();
		   keysArray(arr, node.myGetLeft(), left);
		   keysArray(arr, node.myGetRight(), i + 1);
	   }
   }
   
   /**
    * private void infoArray(String[] arr, IAVLNode node, int left)
    * 
    * helper method to infoToArray()
    * updates the received array to contain sorted tree values
    * 
    * Complexity is O(n): the recursion passes through each node once and inserts its value in the array
    */
   private void infoArray(String[] arr, IAVLNode node, int left)
   {
 	   if (node.isRealNode()) {
 		   int i = left + node.myGetLeft().getSize();
 		   arr[i] = node.getValue();
 		   infoArray(arr, node.myGetLeft(), left);
 		   infoArray(arr, node.myGetRight(), i + 1);
 	   }
   }
   
   /**
	 * private IAVLNode treePosition(IAVLNode root, int k)
	 * 
	 * looks for key k in subtree of the node root and returns the last node encountered
	 * 
	 * complexity is O(logn): binary search
	 */
	private IAVLNode treePosition(IAVLNode root, int k) {
		IAVLNode node = null;
		while (root.isRealNode()) {
			node = root;
			int key = node.getKey();
			if (key == k) {
				return root;
			} else {
				if (k < key) {
					root = root.myGetLeft();
				} else {
					root = root.myGetRight();
				}
			}
		}
		return node;
	}
   
	/**
	 * IAVLNode minNode(IAVLNode root)
	 * 
	 * find and return the node with the minimum key in a given subtree (given subtree's root)
	 * 
	 * precondition: root is a real node
	 * 
	 * complexity is O(logn): travels left through the height of the tree, in AVL height is log n.
	 */
	private IAVLNode minNode(IAVLNode root) { 
		while(root.myGetLeft().isRealNode()) {
			IAVLNode leftChild = root.myGetLeft();
			root = leftChild;
		}
		return root;
	}
	
	/**
	 * private IAVLNode successor(IAVLNode node)
	 * 
	 * return the successor of a given node in a tree
	 * 
	 * preconditions: node is not maximal
	 * 
	 * complexity O(log n): worst case is traveling the height of the tree, in AVL height is log n.
	 */
	private IAVLNode successor(IAVLNode node) {
		if (node.myGetRight().isRealNode()) {
			return minNode(node.myGetRight());
	
		} else {
			if (node.isLeftChild()) {
				return node.getParent();
			} else {
				while (node.isRightChild()) {
					node = node.getParent();
				}
				return node;
			}
		}
	}
	
	/**
	 * private void updateSizeUp(IAVLNode node)
	 * 
	 * update's node's size and ancestors' size until root
	 * 
	 * complexity: O(log n): worst case is height of the tree, in AVL height is O(log n)
	 */
	private void updateSizeUp(IAVLNode node) {
		while(node != null) {
			Rebalance.updateSize(node);
			node = node.getParent();
		}
	}
	
	/**
	 * private void deleteLeaf(IAVLNode node)
	 * 
	 * delete a leaf node from tree by setting parent's child as virtual node
	 * 
	 * preconditions: node is a leaf
	 * 
	 * complexity is O(log n): uses updateSizeUp in O(log n)
	 */
	private void deleteLeaf(IAVLNode node) {
		IAVLNode parent = node.getParent();
		if (node.isLeftChild()) {
			parent.setLeft(new AVLNode());
			updateSizeUp(parent);
		} else if (node.isRightChild()) {
			parent.setRight(new AVLNode());
			updateSizeUp(parent);
		} else { 	//node is a leaf and a root
			this.root = new AVLNode();
		}
	}
	
	/**
	 * private void deleteNodeWithOneChild(IAVLNode node)
	 * 
	 * delete a node that has one child by bypassing it
	 * 
	 * preconditions: one of the node's children is a real node
	 * 
	 * complexity: O(log n): uses updateSizeUp in O(log n)
	 */
	private void deleteNodeWithOneChild(IAVLNode node) {
		IAVLNode rightChild;
		IAVLNode leftChild;
		IAVLNode parent;
		
		if (!node.myGetLeft().isRealNode()) {	//the node has only right child
			rightChild = node.myGetRight();
			if (node.isLeftChild()) {
				parent = node.getParent();
				rightChild.setParent(parent);
				parent.setLeft(rightChild);
				updateSizeUp(parent);
			} else if (node.isRightChild()) {
				parent = node.getParent();
				rightChild.setParent(parent);
				parent.setRight(rightChild);
				updateSizeUp(parent);
			} else { 	//node is a root with one child
				rightChild.setParent(null);
				this.root = rightChild;
			}
		} else if (!node.myGetRight().isRealNode()) {	//the node has only left child
			leftChild = node.myGetLeft();
			if (node.isLeftChild()) {
				parent = node.getParent();
				leftChild.setParent(parent);
				parent.setLeft(leftChild);
				updateSizeUp(parent);
			} else if (node.isRightChild()) {
				parent = node.getParent();
				leftChild.setParent(parent);
				parent.setRight(leftChild);
				updateSizeUp(parent);
			} else {	//node is a root with one child
				leftChild.setParent(null);
				this.root = leftChild;
			}
		}
	}
	
	/**
	 * public int height()
	 * 
	 * returns the height of the tree
	 * 
	 * Complexity is O(1)
	 */
   public int height()
   {
	   return root.getHeight();
   }
   
   /**
    * public IAVLNode moveRight(int rank)
    * 
    * returns the first node in the right spine of the tree with height lower than/equals rank, updates size
    * 
    * precondition: tree is not empty
    * 
    * complexity is O(height() - rank): the method travels down until the merging point at rank (height() - rank)
    */
   public IAVLNode moveRight(int rank, int size) {
	   return moveRightRec(rank, size, getRoot());
   }
   public IAVLNode moveRightRec(int rank, int size, IAVLNode x)
   {
	   if (x.getHeight() <= rank) {
		   return x;
	   }
	   if (!x.myGetRight().isRealNode()) {
		   x.myGetRight().setParent(x);
	   }
	   x.setSize(x.getSize() + size + 1);
	   return moveRightRec(rank, size, x.myGetRight());
   }
   
   /**
    * public IAVLNode moveLeft(int rank)
    * 
    * returns the first node in the left spine of the tree with height lower than/equals rank, updates size
    * 
    * precondition: tree is not empty
    * 
    * complexity is O(height() - rank): the method travels down until the merging point at rank (height() - rank)
    */
   public IAVLNode moveLeft(int rank, int size) {
	   return moveLeftRec(rank, size, getRoot());
   }
   public IAVLNode moveLeftRec(int rank, int size, IAVLNode x)
   {
	   if (x.getHeight() <= rank) {
		   return x;
	   }
	   x.setSize(x.getSize() + size + 1);
	   if (!x.myGetLeft().isRealNode()) {
		   x.myGetLeft().setParent(x);
	   }
	   return moveLeftRec(rank, size, x.myGetLeft());
   }
   
   /**
    * public void setJoinNodeRight(IAVLNode node)
    * 
    * sets the right son of the node x
    * 
    * complexity is O(1): changing pointers only
    */
   public void setJoinNodeRight(IAVLNode node) {
	   if (!empty()) {
		   IAVLNode right = getRoot();
		   node.setRight(right);
		   right.setParent(node);
	   }
   }
   
   /**
    * public void setJoinNodeLeft(IAVLNode node)
    * 
    * sets the left son of the node x
    * 
    * complexity is O(1): changing pointers only
    */
   public void setJoinNodeLeft(IAVLNode node) {
	   if (!empty()) {
		   IAVLNode left = getRoot();
		   node.setLeft(left);
		   left.setParent(node);
	   }
   }
   
   
   /**
	 * 
	 * Rebalance
	 * 
	 * a class with utility functions to rebalance AVL trees 
	 *
	 */
	public static class Rebalance
	{
		
		/**
		 * private int updateHeight(IAVLNode node)
		 * 
		 * updates the height of a tree node and returns the height difference
		 * 
		 * preconditions: node is not virtual
		 * 
		 * complexity is O(1): involves only simple arithmetic operations
		 */
		private static int updateHeight(IAVLNode node)
		{
			int height = 1 + Math.max(node.myGetRight().getHeight(), node.myGetLeft().getHeight());
			int rankDiff = Math.abs(node.getHeight() - height);
			if (rankDiff != 0) {
				node.setHeight(height);
			}
			return rankDiff;
			
		}
		
		/**
		 * private void updateSize(IAVLNode node)
		 * 
		 * updates the size of a tree node
		 * 
		 * preconditions: node is not virtual
		 * 
		 * complexity is O(1): involves only simple arithmetic operations
		 */
		private static void updateSize(IAVLNode node)
		{
			int size = 1 + node.myGetRight().getSize() + node.myGetLeft().getSize();
			node.setSize(size);
		}
		
		/**
		 * private int rotateRight(IAVLNode node)
		 * 
		 * rotates the subtree to the right by making node.parent become node.right and fixing the BST accordingly
		 * returns the number of rebalancing operations that were taken
		 * 
		 * preconditions: node is not the tree root (has a parent), node is not virtual
		 * 
		 * complexity is O(1): involves limited pointer changing and calls for helper functions of O(1) complexity
		 */
		public static int rotateRight(AVLTree tree, IAVLNode node) 
		{
			IAVLNode parent = node.getParent();
			IAVLNode grandparent = parent.getParent();
			node.setParent(grandparent);
			if (grandparent == null || !grandparent.isRealNode()) { //if parent was root
				tree.root = node;
			}
			else if (node.getKey() > grandparent.getKey()) {//if right son
				grandparent.setRight(node);
			}
			else {//if left son
				grandparent.setLeft(node);
			}
			parent.setLeft(node.myGetRight());
			node.myGetRight().setParent(parent);
			node.setRight(parent);
			parent.setParent(node);
			int counter = 1;
			counter =+ updateHeight(parent);
			counter += updateHeight(node);
			updateSize(parent);
			updateSize(node);
			return counter;
		}
		
		/**
		 * public static int void rotateLeft(IAVLNode node)
		 * 
		 * rotates the subtree to the left by making node.parent become node.left and fixing the BST accordingly
		 * returns the number of rebalancing operations that were taken
		 * 
		 * preconditions: node is not the tree root (has a parent), node is not virtual
		 * 
		 * complexity is O(1): involves limited pointer changing and calls for helper functions of O(1) complexity
		 */
		public static int rotateLeft(AVLTree tree, IAVLNode node)
		{
			IAVLNode parent = node.getParent();
			IAVLNode grandparent = parent.getParent();
			node.setParent(grandparent);
			if (grandparent == null || !grandparent.isRealNode()) { //if parent was root
				tree.root = node;
			}
			else if (node.getKey() > grandparent.getKey()) { //is right son
				grandparent.setRight(node);
			}
			else {//is left son
				grandparent.setLeft(node);
			}
			parent.setRight(node.myGetLeft());
			node.myGetLeft().setParent(parent);
			node.setLeft(parent);
			parent.setParent(node);
			int counter = 1;
			counter += updateHeight(parent);
			counter += updateHeight(node);
			updateSize(parent);
			updateSize(node);
			return counter;
		}
		
		/**
		 * public static int rotateRL(IAVLNode node)
		 * 
		 * rotates the subtree to the right and then left
		 * returns the number of rebalancing operations that were taken
		 * 
		 * preconditions: node is not the tree root (has a parent), node is not virtual
		 * 
		 * complexity is O(1): calls helper methods rotateRight(node) then rotateLeft(node) of O(1) complexity
		 */
		public static int rotateRL(AVLTree tree, IAVLNode node) {
			int counter = 0;
			counter += rotateRight(tree, node);
			counter += rotateLeft(tree, node);
			return counter;
		}
		
		/**
		 * public static int rotateRL(IAVLNode node)
		 * 
		 * rotates the subtree to the left and then right
		 * returns the number of rebalancing operations that were taken
		 * 
		 * preconditions: node is not the tree root (has a parent), node is not virtual
		 * 
		 * complexity is O(1): calls helper methods rotateLeft(node) then rotateRight(node) of O(1) complexity
		 */
		public static int rotateLR(AVLTree tree, IAVLNode node) {
			int counter = 0;
			counter += rotateLeft(tree, node);
			counter += rotateRight(tree, node);
			return counter;
		}
				
		/**
		 * public static boolean balanced(IAVLNode)
		 * 
		 * returns true if a node is a balance node according to AVL standard
		 * 
		 * Complexity: O(1)
		 */
		private static boolean balanced(IAVLNode node) {
			int height = node.getHeight();
			int leftDiff = height - node.myGetLeft().getHeight();
			int rightDiff = height - node.myGetRight().getHeight();
			
			return ((leftDiff == 1 && rightDiff == 1) ||
					(leftDiff == 1 && rightDiff == 2) || 
					(leftDiff == 2 && rightDiff == 1));
		}
		
		/**
		 * private static boolean requiresPromote(int leftDiff, int rightDiff)
		 * 
		 * returns true if a node's rank should be promoted
		 * 
		 * preconditions: leftDiff is the rank difference between the node and it's left child
		 * rightDiff is the rank difference between the node and it's right child
		 *  
		 * Complexity is O(1)
		 */
		private static boolean requiresPromote(int leftDiff, int rightDiff) {
			return ((leftDiff == 1 && rightDiff == 0) || (leftDiff == 0 && rightDiff == 1));
		}
		
		/**
		 * private static boolean requiresDemote(int leftDiff, int rightDiff)
		 * 
		 * returns true if a node's rank should be demoted
		 * 
		 * preconditions: leftDiff is the rank difference between the node and it's left child
		 * rightDiff is the rank difference between the node and it's right child
		 *  
		 * Complexity is O(1)
		 */
		private static boolean requiresDemote(int leftDiff, int rightDiff) {
			return (leftDiff == 2 && rightDiff == 2);
		}
		
		/**
		 * private static boolean requiresRotateRight(int leftDiff, int rightDiff)
		 * 
		 * returns true if a node's rank should be rotated to the right once 
		 * 
		 * preconditions: leftDiff is the rank difference between the node and its left child
		 * rightDiff is the rank difference between the node and its right child
		 * leftChild is node.getLeft()
		 *  
		 * Complexity is O(1)
		 */
		private static boolean requiresRotateRight(int leftDiff, int rightDiff, IAVLNode leftChild) {
			if ((leftDiff == 0 && rightDiff == 2) || (leftDiff == 1 && rightDiff == 3)) {
				int childHeight = leftChild.getHeight();
				int childLeftDiff = childHeight - leftChild.myGetLeft().getHeight();
				int childRightDiff = childHeight - leftChild.myGetRight().getHeight();
				return ((leftDiff == 0 && rightDiff == 2 && childLeftDiff == 1 && childRightDiff == 2)
						|| (leftDiff == 0 && rightDiff == 2 && childLeftDiff == 1 && childRightDiff == 1) 
						|| (leftDiff == 1 && rightDiff == 3 && childLeftDiff == 1 && childRightDiff == 1)
						|| (leftDiff == 1 && rightDiff == 3 && childLeftDiff == 1 && childRightDiff == 2));
			}
			return false;
		}

		/**
		 * private static boolean requiresRotateLeft(int leftDiff, int rightDiff, IAVLNode rightChild)
		 * 
		 * returns true if a node's rank should be rotated to the left once 
		 * 
		 * preconditions: leftDiff is the rank difference between the node and it's left child
		 * rightDiff is the rank difference between the node and it's right child
		 * rightChild is node.getLeft()
		 *  
		 * Complexity is O(1)
		 */
		private static boolean requiresRotateLeft(int leftDiff, int rightDiff, IAVLNode rightChild) {
			if ((leftDiff == 2 && rightDiff == 0) || (leftDiff == 3 && rightDiff == 1)) {
				int childHeight = rightChild.getHeight();
				int childLeftDiff = childHeight - rightChild.myGetLeft().getHeight();
				int childRightDiff = childHeight - rightChild.myGetRight().getHeight();
				return ((leftDiff == 2 && rightDiff == 0 && childLeftDiff == 2 && childRightDiff == 1)
						|| (leftDiff == 2 && rightDiff == 0 && childLeftDiff == 1 && childRightDiff == 1) 
						|| (leftDiff == 3 && rightDiff == 1 && childLeftDiff == 1 && childRightDiff == 1)
						|| (leftDiff == 3 && rightDiff == 1 && childLeftDiff == 2 && childRightDiff == 1));
			}
			return false;
		}

		/**
		 * private static boolean requiresLR(int leftDiff, int rightDiff, IAVLNode leftChild)
		 * 
		 * returns true if a node's rank should be rotated to the left and then to the right (double rotation)
		 * 
		 * preconditions: leftDiff is the rank difference between the node and it's left child
		 * rightDiff is the rank difference between the node and it's right child
		 * leftChild is node.getLeft()
		 *  
		 * Complexity is O(1)
		 */
		private static boolean requiresRotateLR(int leftDiff, int rightDiff, IAVLNode leftChild) {
			if ((leftDiff == 0 && rightDiff == 2) || (leftDiff == 1 && rightDiff == 3)) {
				int childHeight = leftChild.getHeight();
				int childLeftDiff = childHeight - leftChild.myGetLeft().getHeight();
				int childRightDiff = childHeight - leftChild.myGetRight().getHeight();
				return ((leftDiff == 0 && rightDiff == 2 && childLeftDiff == 2 && childRightDiff == 1)
						|| (leftDiff == 1 && rightDiff == 3 && childLeftDiff == 2 && childRightDiff == 1));
			}
			return false;
		}

		/**
		 * private static boolean requiresRotateRL(int leftDiff, int rightDiff, IAVLNode rightChild)
		 * 
		 * preconditions: leftDiff is the rank difference between the node and it's left child
		 * rightDiff is the rank difference between the node and it's right child
		 * rightChild is node.getLeft()
		 * 
		 * Complexity is O(1)
		 */
		private static boolean requiresRotateRL(int leftDiff, int rightDiff, IAVLNode rightChild) {
			if ((leftDiff == 2 && rightDiff == 0) || (leftDiff == 3 && rightDiff == 1)) {
				int childHeight = rightChild.getHeight();
				int childLeftDiff = childHeight - rightChild.myGetLeft().getHeight();
				int childRightDiff = childHeight - rightChild.myGetRight().getHeight();
				return ((leftDiff == 2 && rightDiff == 0 && childLeftDiff == 1 && childRightDiff == 2)
						|| (leftDiff == 3 && rightDiff == 1 && childLeftDiff == 1 && childRightDiff == 2));
			}
			return false;
		}
		
		/**
		 * public static int rebalance(AVLTree tree, IAVLNode node)
		 * 
		 * rebalances the tree using rotations, promotes and demotes
		 * returns the number of rebalancing operations that were taken
		 * 
		 * Complexity is O(log n): calls rebalancing methods of O(1) while traveling upwards
		 * at worst case from a leaf to the root 
		 */
		public static int rebalance(AVLTree tree, IAVLNode node) {
			int balanceCount = 0;
			while (node != null && node.isRealNode() && !balanced(node)) {
				int height = node.getHeight();
				int leftDiff = height - node.myGetLeft().getHeight();
				int rightDiff = height - node.myGetRight().getHeight();
				IAVLNode leftChild = node.myGetLeft();
				IAVLNode rightChild = node.myGetRight();
				if (requiresPromote(leftDiff, rightDiff) || requiresDemote(leftDiff, rightDiff)) {
					balanceCount += updateHeight(node);
					node = node.getParent();
				} else if (requiresRotateRight(leftDiff, rightDiff, leftChild)) {
					balanceCount += rotateRight(tree, leftChild);
					node = node.getParent().getParent();
				} else if (requiresRotateLeft(leftDiff, rightDiff, rightChild)) {
					balanceCount += rotateLeft(tree, rightChild);
					node = node.getParent().getParent();
				} else if (requiresRotateLR(leftDiff, rightDiff, leftChild)) {
					balanceCount += rotateLR(tree, leftChild.myGetRight());
					node = node.getParent().getParent();
				} else if (requiresRotateRL(leftDiff, rightDiff, rightChild)) {
					balanceCount += rotateRL(tree, rightChild.myGetLeft());
					node = node.getParent().getParent();
				}
			}
			return balanceCount;
		}
	}
   
	/**
	   * public interface IAVLNode
	   * ! Do not delete or modify this - otherwise all tests will fail !
	   */
	public interface IAVLNode{	
		public int getKey(); //returns node's key (for virtual node return -1)
		public String getValue(); //returns node's value [info] (for virtual node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public IAVLNode myGetLeft(); //returns left child (virtual or real)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public IAVLNode myGetRight(); //returns right child (virtual or real)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
    	public void setHeight(int height); // sets the height of the node
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
    	public void setSize(int newSize); //sets the number of nodes in the subtree, including current node
    	public int getSize(); //Returns the node's size (0 for virtual nodes)
    	public boolean isALeaf(); // returns true if the node is a leaf (has no children)
		public boolean isLeftChild(); // returns true if node is a left child
		public boolean isRightChild(); // returns true if node is a right child
	}

   /**
   * public class AVLNode
   *
   * implements IAVLNode interface and represents a node in an AVL tree
   */
  public class AVLNode implements IAVLNode
  {
	  private int key;
	  private String value;
	  private IAVLNode right;
	  private IAVLNode left;
	  private int height;
	  private int size;
	  private IAVLNode parent;
	  
	  /**
	   * public AVLNode()
	   * 
	   * AVLNode constructor, creates a virtual node
	   * 
	   * Complexity: O(1)
	   */
	  public AVLNode()
	  {
		  key = -1;
		  value = null;
		  size = 0;
		  height = -1;
	  }
	  
	  /**
	   * public AVLNode(int key, String value)
	   * 
	   * AVLNode constructor, creates a real node with key and value fields set to the arguments received
	   * 
	   * Complexity: O(1)
	   */
	  public AVLNode(int key, String value)
	  {
		  this.key = key;
		  this.value = value;
		  right = new AVLNode();
		  left = new AVLNode();
		  size = 1;
		  height = 0;
	  }

	  /**
	   * public int getKey()
	   * 
	   * returns the node's key
	   * 
	   * Complexity: O(1)
	   */
	  public int getKey()
	  {
		  return key; 
		}
		
	  /**
	   * public String getValue()
	   * 
	   * returns the node's value
	   * 
	   * Complexity: O(1)
	   */
	  public String getValue()
	  {
		  return value;
	  }
	
	  /**
	   * public void setLeft(IAVLNode node)
	   * 
	   * sets the left child to node
	   * 
	   * Complexity: O(1)
	   */
	  public void setLeft(IAVLNode node)
	  {
		  left = node; 
	  }
	
	  /**
	   * public IAVLNode getLeft()
	   * 
	   * returns the node's left child
	   * if the left child is virtual, returns null
	   * 
	   * Complexity: O(1)
	   */
	  public IAVLNode getLeft()
	  {
		  if (left.isRealNode()) {
			  return left;
		  }
		  return null; 
	  }
	  
	  /**
	   * public IAVLNode myGetLeft()
	   * 
	   * returns the node's left child (virtual or not)
	   * 
	   * Complexity: O(1)
	   */
	  public IAVLNode myGetLeft()
	  {
		  return left;
	  }
	
	  /**
	   * public void setRight(IAVLNode node)
	   * 
	   * sets the right child to node
	   * 
	   * Complexity: O(1)
	   */
	  public void setRight(IAVLNode node)
	  {
		  right = node;
	  }
	  
	  /**
	   * public IAVLNode getRight()
	   * 
	   * returns the node's right child
	   * if the left child is virtual, returns null
	   * 
	   * Complexity: O(1)
	   */
	  public IAVLNode getRight()
	  {
		  if (right.isRealNode()) {
			  return right;
		  }
		  return null;
	  }
	  
	  /**
	   * public IAVLNode myGetRight()
	   * 
	   * returns the node's right child (virtual or not)
	   * 
	   * Complexity: O(1)
	   */
	  public IAVLNode myGetRight()
	  {
		  return right;
	  }
	  
	  /**
	   * public void setParent(IAVLNode node)
	   * 
	   * sets the parent to node
	   * 
	   * Complexity: O(1)
	   */
	  public void setParent(IAVLNode node)
	  {
		  parent = node;
	  }
	  
	  /**
	   * public IAVLNode getParent()
	   * 
	   * returns the node's parent
	   * 
	   * Complexity: O(1)
	   */
	  public IAVLNode getParent()
	  {
		  return parent;
	  }
	  
	  /**
		* public boolean isRealNode()
		* 
		* returns true if node is not virtual
		*/
	  public boolean isRealNode()
	  {
		  return height != -1;
	  }
	  
	  /**
		* public void setHeight(int newHeight)
		* 
		* updates the height of the node to NewHeight
		* 
		* Complexity: O(1)
		*/
	  public void setHeight(int newHeight)
	  {
		  height = newHeight;
	  }
	  
	  /**
		* public int getHeight()
		* 
		* returns node's height
		* 
		* Complexity: O(1)
		*/
	  public int getHeight()
	  {
		  return height;
	  }
	  
	  /**
		* public int getSize()
		* 
		* returns node's size
		* 
		* Complexity: O(1)
		*/
	  public int getSize()
	  {
		  return size;
	  }
	  
	  /**
		* public void setSize(int newSize)
		* 
		* updates the size of the node to newSize
		* 
		* Complexity: O(1)
		*/
	  public void setSize(int newSize)
	  {
		  size = newSize;
	  }    
	  
	  /**
		* public boolean isALeaf()
		* 
		* returns true if the node is a leaf: the node has only virtual children
		* 
		* Complexity: O(1)
		*/
	  public boolean isALeaf()
	  {
		  return (!this.myGetLeft().isRealNode() && !this.myGetRight().isRealNode());
	  }
	  
	  /**
		* public boolean isLeftChild()
		* 
		* returns true if node is a left child to its parent
		* 
		* Complexity: O(1)
		*/
	  public boolean isLeftChild()
	  {
		  IAVLNode parent = this.getParent();
		  if (parent != null && parent.isRealNode()) {
			  return(this.getKey() == this.getParent().myGetLeft().getKey());
		  }
		  return false;
	  }
	  
	  /** 
		* public boolean isRightChild()
		* 
		* returns true if node is a right child to its parent
		* 
		* Complexity: O(1)
		*/
	  public boolean isRightChild()
	  {
		  IAVLNode parent = this.getParent();
		  if (parent != null && parent.isRealNode()) {
			  return(this.getKey() == this.getParent().myGetRight().getKey());
		  }	
		  return false;
	  }
  }
}
