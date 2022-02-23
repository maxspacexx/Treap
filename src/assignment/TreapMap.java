package assignment;

import java.util.ArrayList;
import java.util.Iterator;

public class TreapMap<K extends Comparable<K>, V> implements Treap<K, V>
{
    private TreapNode root;
    private ArrayList<K> keySet;

    //testing only
    public TreapMap()
    {
        root = null;
        keySet = new ArrayList<>();
    }
    public TreapMap(TreapNode root)
    {
        this.root = root;
        keySet = new ArrayList<>();
    }

    private class TreapNode
    {
        TreapNode right;
        TreapNode left;
        K key;
        V val;
        int prio;

        public TreapNode(K key, V value)
        {
            right = null;
            left = null;
            this.key = key;
            this.val = value;
            prio = (int) (Math.random() * 65536);
        }
        public TreapNode(K key, V value, int prio)
        {
            this(key, value);
            this.prio = prio;
        }
        public TreapNode(TreapNode t)
        {
            if (t == null)
            {
                return;
            }
            right = t.right;
            left = t.left;
            key = t.key;
            val = t.val;
            prio = t.prio;
        }

        public TreapNode()
        {

        }

        public void set(TreapNode t)
        {
            if (t == null)
            {
                return;
            }
            right = t.right;
            left = t.left;
            key = t.key;
            val = t.val;
            prio = t.prio;
        }
        public String toString()
        {
            return "key: " + key.toString() + ", value: " + val.toString();
        }
    }

    @Override
    public V lookup(K key)
    {
        if (key == null)
        {
            return null;
        }
        TreapNode temp = lookupHelper(root, key);
        if(temp == null)
        {
            return null;
        }
        return temp.val;
    }

    //recursive search function
    public TreapNode lookupHelper(TreapNode node, K key)
    {
        //one possible base case
        if (node == null)
        {
            return null;
        }
        if (node.key.compareTo(key) > 0)
        {
            return lookupHelper(node.left, key);
        } else if (node.key.compareTo(key) < 0)
        {
            return lookupHelper(node.right, key);
        }
        else if (node.key.compareTo(key) == 0)
        {
            //second possible base case
            return node;
        }
        return null;
    }

    @Override
    public void insert(K key, V value)
    {
        if (key == null || value == null)
        {
            //System.out.println("Treap not modified.");
            return;
        }
        if (root == null)
        {
            //sets the root to the new pair if it doesn't exist
            root = new TreapNode(key, value);
        } else
        {
            //calls recursive implementation
            TreapNode newNode = new TreapNode(key, value);
            insertHelper(root, newNode);
        }
    }

    private void insertHelper(TreapNode node, TreapNode insert)
    {
        //base
        if (node == null)
        {
            return;
        }
        if (node.key.compareTo(insert.key) > 0)
        {
            //insert the node if null, or further down if not null
            if (node.left != null)
            {
                insertHelper(node.left, insert);
            } else
            {
                node.left = insert;
            }
            //rotate if necessary
            if (node.prio < node.left.prio)
            {
                node.set(rightRotate(node));
            }
        } else if (node.key.compareTo(insert.key) < 0)
        {
            //insert the node if null, or further down if not null
            if (node.right != null)
            {
                insertHelper(node.right, insert);
            } else
            {
                node.right = insert;
            }
            //rotate if necessary
            if (node.prio < node.right.prio)
            {
                node.set(leftRotate(node));
            }
        } else if (node.key.compareTo(insert.key) == 0)
        {
            //override existing node if needed
            node.val = insert.val;
        } else
        {
            //if an unknown error happened
            System.err.println("Unknown error.");
        }
    }


    private TreapNode rightRotate(TreapNode mid)
    {
        //rotate right
        TreapNode left = new TreapNode(mid.left);
        TreapNode temp = left.right;
        left.right = new TreapNode(mid);
        left.right.left = (temp);
        return left;
    }

    private TreapNode leftRotate(TreapNode mid)
    {
        //rotate left
        TreapNode right = new TreapNode(mid.right);
        TreapNode temp = right.left;
        right.left = new TreapNode(mid);
        right.left.right = (temp);
        return right;
    }

    @Override
    public V remove(K key)
    {
        //check if exists
        if(key == null)
        {
            return null;
        }
        TreapNode temp = lookupHelper(root, key);
        if (temp == null)
        {
            return null;
        }
        //call recursive method
        root = removeHelper(root, key);
        return temp.val;
    }

    private TreapNode removeHelper(TreapNode node, K keyToRemove)
    {
        //postorder traversal
        //base case
        if (node == null)
        {
            return null;
        }
        if (keyToRemove.compareTo(node.key) < 0)
        {
            //set the child here, so parent does not need to be passed down
            node.left = removeHelper(node.left, keyToRemove);
        } else if (keyToRemove.compareTo(node.key) > 0)
        {
            //set the child here, so parent does not need to be passed down
            node.right = removeHelper(node.right, keyToRemove);
        } else if (keyToRemove.compareTo(node.key) == 0)
        {
            if (node.left == null && node.right == null)
            {
                node = null;
            }
            else if (node.left == null)
            {
                node.set(node.right);
            } else if (node.right == null)
            {
                node.set(node.left);
            } else
            {
                if (node.left.prio >= node.right.prio)
                {
                    node.set(rightRotate(node));
                    //may need to do more rotations
                    node.right = removeHelper(node.right, keyToRemove);
                } else
                {
                    node.set(leftRotate(node));
                    node.left = removeHelper(node.left, keyToRemove);
                }
            }
        }
        return node;

    }

    @Override
    public Treap<K, V>[] split(K key)
    {
        Treap<K, V>[] treaps = new TreapMap[2];
        //check inputs
        if(key == null || root == null)
        {
            treaps[0] = null;
            treaps[1] = null;
            return treaps;
        }
        //remove key if it exists
        TreapNode temp = lookupHelper(root, key);
        remove(key);
        //split
        splitHelper(key, root.val);
        treaps[0] = new TreapMap<>(root.left);
        treaps[1] = new TreapMap<>(root.right);
        //re-insert if necessary
        if(temp != null)
        {
            treaps[1].insert(temp.key, temp.val);
        }
        return treaps;
    }

    private void splitHelper(K key, V value)
    {
        //essentially a copy of insert
        if (key == null || value == null)
        {
            System.out.println("Treap not modified.");
            return;
        }
        if (root == null)
        {
            root = new TreapNode(key, value, 65536);
        } else
        {
            TreapNode newNode = new TreapNode(key, value, 65536);
            insertHelper(root, newNode);
        }
    }

    @Override
    public void join(Treap<K, V> t)
    {
        //check input
        if(t == null)
        {
            return;
        }
        if((t.getClass() != this.getClass()))
        {
            return;
        }
        //check that root is not null
        if(root == null)
        {
            this.root = ((TreapMap<K, V>) t).root;
            return;
        }
        //check for correct key values
        boolean allKeysLess = true;
        boolean allKeysGt = true;
        for(K key: t)
        {
            for(K thiskey: this)
            {
                if(key.compareTo(thiskey) == 0)
                {
                    //System.out.println("hi");
                    return;
                }
                else if(key.compareTo(thiskey) < 0)
                {
                    allKeysGt = false;
                }
                else
                {
                    allKeysLess = false;
                }
            }
            if(!(allKeysLess || allKeysGt))
            {
                //System.out.println("hi");
                return;
            }
        }
        //create new temporary root
        TreapNode temp = new TreapNode();
        if(((TreapMap<K, V>) t).root.key.compareTo(this.root.key) < 0)
        {
            temp.left = ((TreapMap<K, V>) t).root;
            temp.right = this.root;
        }
        else
        {
            temp.right = ((TreapMap<K, V>) t).root;
            temp.left = this.root;
        }
        //remove temporary root to join
        root = joinHelper(temp);
    }

    public TreapNode joinHelper(TreapNode root)
    {
        //essentially a copy of remove
        if (root.left == null && root.right == null)
        {
            root = null;
        }
        else if (root.left == null)
        {
            root.set(root.right);
        } else if (root.right == null)
        {
            root.set(root.left);
        } else
        {
            if (root.left.prio >= root.right.prio)
            {
                root.set(rightRotate(root));
                //may need to do more rotations
                root.right = joinHelper(root.right);
            } else
            {
                root.set(leftRotate(root));
                root.left = joinHelper(root.left);
            }
        }
        return root;
    }


    public String toString()
    {
        //check for non-null root
        if (root == null)
        {
            return "";
        }
        return printPreorder(0, root, new StringBuilder());
    }

    private String printPreorder(int level, TreapNode node, StringBuilder tree)
    {
        if (node == null)
        {
            //comment out the next two lines if necessary
            //tree.append("\t".repeat(level));
            //tree.append("null\n");
            return "";
        }
        //insert indentation
        tree.append("\t".repeat(level));
        //append node data
        tree.append("[" + node.prio + "] <" + node.key.toString() + ", " + node.val.toString() + ">\n");
        if (node.left == null && node.right == null)
        {
            return tree.toString();
        }
        //recursive call
        printPreorder(level + 1, node.left, tree);
        printPreorder(level + 1, node.right, tree);
        return tree.toString();
    }

    @Override
    public Iterator<K> iterator()
    {
        //reset keySet in case treap was modified
        keySet = new ArrayList<>();
        iteratorHelper(this.root);
        return keySet.iterator();
    }

    private void iteratorHelper(TreapNode node)
    {
        if(node == null)
        {
            return;
        }
        if (node.left != null)
        {
            iteratorHelper(node.left);
        }
        keySet.add(node.key);
        if (node.right != null)
        {
            iteratorHelper(node.right);
        }
    }

    @Override
    public double balanceFactor() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /*private void testingHeapProperty(TreapNode node)
    {
        //Will print an error if the treap doesn't satisfy heap property, otherwise nothing
        if(node == null)
        {
            return;
        }
        if (node.right != null)
        {
            if (node.right.prio > node.prio)
            {
                System.err.println("Heap property false.");
            }
            testingHeapProperty(node.right);
        }
        if (node.left != null)
        {
            if (node.left.prio > node.prio)
            {
                System.err.println("Heap property false.");
            }
            testingHeapProperty(node.left);
        }
    }*/

    /*private boolean testingBSTProperty(TreapNode node, K min, K max)
    {
        if(node == null)
        {
            return true;
        }
        if(node.key.compareTo(min) < 0 || node.key.compareTo(max) > 0)
        {
            return false;
        }
        return testingBSTProperty(node.left, min, node.key) && testingBSTProperty(node.right, node.key, max);
    }*/



}
