package treebrowser.data.tree;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import prefuse.data.*;
import prefuse.data.column.Column;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import treebrowser.data.SourceData;
import treebrowser.data.json.JsonWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class TreeUtil
{
	private static final Log log = LogFactory.getLog(TreeUtil.class);

	static NumberFormat formatter = new DecimalFormat("0");
	static Locale locale;
	static NumberFormat localeFormatter;

	static
	{
		locale = Locale.ENGLISH;
		localeFormatter = NumberFormat.getInstance(locale);
	}
	
	private TreeUtil()
	{
	}

	/**
     * @deprecated There is a method for this.
	 */
	public static int getNodeLevel(final Node node)
	{
		int levelCounter = 0;
		for (Node parent = node.getParent(); parent != null;)
			if (parent != null)
			{
				levelCounter++;
				parent = parent.getParent();
			}

		return levelCounter;
	}

	private static int called = 0;

	public static void printSchemaOnce(Schema schema)
    {
        if (called < 1)
        {
            log.info("Found the following colums in the Schema:");
            int numberOfColumns = schema.getColumnCount();
            for (int i = 0; i < numberOfColumns; i++)
            {
                log.info("[" + i + "] - " + schema.getColumnName(i));
            }
            called++;
        }
    }

    public static void printTupleWeights(final Tree tree)
    {
        for (Iterator iterator = tree.tuples(); iterator.hasNext(); )
        {
            Tuple tuple = (Tuple) iterator.next();
            log.info("found node "+tuple.getString(SourceData.FULL_NAME)+
                    " with weight: "+tuple.getString(SourceData.WEIGHT));
        }
    }

    public static void printTree(Tree tree)
    {
        logTree(tree);
    }

    public static void printTreeRootColumnValues(Tree tree)
    {
        Table nodeTable = tree.getNodeTable();

        Node rootNode = tree.getRoot();
        int numberOfColums = tree.getRoot().getColumnCount();

        log.info("**** Printing Root Tree Column values: ");
        for (int i=0; i < numberOfColums; i++)
        {
            Column column = nodeTable.getColumn(i);
            // column.
            log.info("["+i+"] "+rootNode.get(i));
        }
    }


    public static synchronized void logTreeStatistics(final Tree tree)
    {
        if (log.isInfoEnabled())
        {
            log.info("******************************************************************************");
            log.info("TREE STATISTICS");
            log.info("number of nodes: "+tree.getNodeCount());
            log.info("number of edges: "+tree.getEdgeCount());
            Node rootNode = tree.getRoot();
            if (rootNode.canGetInt(SourceData.WEIGHT))
            {
                log.info("root: " + rootNode + " weight: " + rootNode.getInt(SourceData.WEIGHT));
            }
            else
            {
                log.info("root: " + rootNode);
            }
            // printChildren(rootNode);
            log.info("******************************************************************************");
        }
    }


    /** Logs the contents of a Tree to analyse
     * @param tree
     */
    public static synchronized void logTree(final Tree tree)
    {
        if (log.isInfoEnabled())
        {
            log.info("-----------------------------------------------------");
            log.info("Analysing tree");
            log.info("number of nodes: "+tree.getNodeCount());
            log.info("number of edges: "+tree.getEdgeCount());
            Node rootNode = tree.getRoot();
            if (rootNode.canGetInt(SourceData.WEIGHT))
            {
                log.info("root: " + rootNode + " weight: " + rootNode.getInt(SourceData.WEIGHT));
            }
            else
            {
                log.info("root: " + rootNode);
            }
            printChildren(rootNode);
            log.info("-----------------------------------------------------");
        }
    }


    public static void printNodeList(final List<Node> nodeList)
    {
        for (int i=0; i < nodeList.size(); i++)
        {
            printNode(nodeList.get(i));
        }
    }

    public static void printNode(final Node node)
    {
        int numberOfChildren = node.getChildCount();
        log.info("weight: "+node.getInt(SourceData.WEIGHT)+" "+numberOfChildren);

        for(Iterator i = node.children(); i.hasNext(); )
        {
            printNode((Node)i.next());
        }
    }

    public static void printTupleDetails(final Tuple tuple)
    {
        if (log.isInfoEnabled())
        {
            log.info("********");
            log.info("Node name: "+tuple.get(SourceData.NAME));
            int numberOfColums = tuple.getColumnCount();
            for(int i=0; i < numberOfColums; i++)
            {
                if (!tuple.getColumnName(i).startsWith("_"))
                {
                    log.info("[" + i + "] - " + tuple.getColumnName(i) + ": '" + tuple.get(i)+"'");
                }
            }
        }
    }

    public static void printVisualItemDetails(final VisualItem visualItem)
    {
        log.info("********");
        log.info("Node name: "+visualItem.get(SourceData.NAME));
        int numberOfColums = visualItem.getColumnCount();
        for(int i=0; i < numberOfColums; i++)
        {
            log.info("["+i+"] - "+visualItem.getColumnName(i)+" with value: "+visualItem.get(i));
        }
    }


    private static void printChildren(final Node node)
    {
        int childCount = node.getChildCount();
        for (int i=0; i< childCount; i++)
        {
            Node childNode = node.getChild(i);
            if(node.canGetInt(SourceData.WEIGHT))
            {
                int nodeWeight = childNode.getInt(SourceData.WEIGHT);
                log.info("child[" + i + "] - weight: " + nodeWeight + " " + childNode);
            }
            else
            {
                log.info("child[" + i + "] " +childNode.getString(SourceData.NAME));
            }
            printChildren(childNode);
        }
    }

    public static void printTupleData(final Iterator<Tuple> iter)
    {
        int counter = 0;
        while (iter.hasNext())
        {
            Tuple tuple = iter.next();
            TreeUtil.printTupleDetails(tuple);
            counter++;
        }
        if (counter == 0) log.warn("No tuples to print.");
    }

    /**Verifies whether the tree doesn't contain a tuple with WEIGHT = 0, which cannot be visualized
     * (this results in some strange deadlock situation)
     * @param tree the tree to be verified
     */
    public static void verifyTreeForZeroWeight(final Tree tree)
    {
        if (!tree.isValidTree())
        {
            // log.info("tree not valid, who cares?");
            throw new RuntimeException("Tree not valid");
        }

        TupleSet tupleSet = tree.getNodes();
        for (Iterator<Tuple> iterator = tupleSet.tuples(); iterator.hasNext();)
        {
            Tuple tuple = iterator.next();
            if (tuple.getInt(SourceData.WEIGHT) == 0)
            {
                throw new RuntimeException("Cannot visualize dataset which contains tuple with WEIGHT = 0, please check: "+tuple);
            }
        }
    }

    public static void advancedTreeVerification(final Tree tree)
    {
        if (!tree.isValidTree())
        {
            log.info("tree not valid, who cares?");
            // throw new RuntimeException("Tree not valid");
        }
        // Node rootNode = tree.getRoot();
        // advancedNodeVerification(rootNode);
    }

    private static void advancedNodeVerification(final Node node)
    {
        int nodeWeight = node.getInt(SourceData.WEIGHT);
        int numberOfClasses = node.getInt(SourceData.NUMBER_OF_CLASSES);

        int summarizedChildWeight = 0;

        int numberOfChildren = node.getChildCount();

        for (int i = 0; i < numberOfChildren; i++)
        {
            Node childNode = node.getChild(i);
            int childWeight = childNode.getInt(SourceData.WEIGHT);
            int childClasses = childNode.getInt(SourceData.NUMBER_OF_CLASSES);
            summarizedChildWeight = summarizedChildWeight + childWeight + childClasses;
        }

        if (nodeWeight != (summarizedChildWeight + numberOfClasses))
        {
            log.info("nodeWeight for "+node.getString(SourceData.FULL_NAME)+
                    " ("+nodeWeight+") is unequal to sum of childWeights ("+summarizedChildWeight+
                    ") and number of classes ("+numberOfClasses+")");
        }

        // recurse into children
        for (int i = 0; i < numberOfChildren; i++)
        {
            advancedNodeVerification(node.getChild(i));
        }
    }

    private int calculateTotalWeight(final Tree tree)
    {
        int totalWeight = 0;
        for (Iterator iterator = tree.nodes(); iterator.hasNext();)
        {
            Node node = (Node) iterator.next();
            totalWeight = node.getInt(SourceData.WEIGHT);
        }
        return totalWeight;
    }

    /**
     * Set the correct weights in SourceData.WEIGHT for variable
     * @param variable
     */
    public static void setCorrectNodeWeights(Tree tree, String variable)
    {
        calculateBranchWeight(tree, tree.getRoot(), variable);
    }

    private static int calculateBranchWeight(Tree tree, Node branch, String variable)
    {
        int branchWeight = branch.getInt(variable);
        for(int i=0; i< branch.getChildCount(); i++)
        {
            branchWeight += calculateBranchWeight(tree, branch.getChild(i), variable);
        }
        branch.setInt(SourceData.WEIGHT, branchWeight);
        return branchWeight;
    }

    /** */
    public static void removeAllNodeBranchesWithZeroWeight(final Tree tree)
    {
        TupleSet tupleSet = tree.getNodes();

        for (Iterator<Tuple> iterator = tupleSet.tuples(); iterator.hasNext();)
        {
            Tuple tuple = iterator.next();
            // log.info("tuple is instance of:"+ tuple.getClass());
            if (tuple.getDouble(SourceData.WEIGHT) == 0)
            {
                // ||  tuple.getInt(CMSourceData.NUMBER_OF_CLASSES) == 0
                log.info("weight 0, removing: "+tuple.getString(SourceData.FULL_NAME));
                // log.info("tuple "+tuple.getString(CMSourceData.FULL_NAME)+" is a "+tuple.getClass());
                tree.removeNode(tuple.getRow());
            }
        }
    }

	public static void mergeEmptyNodesWithParent(Tree tree)
	{
		Node rootNode = tree.getRoot();
		processNode(tree, rootNode);
	}

    private static void processNode(final Tree tree, final Node processingNode)
    {
        for(int i=0; i < processingNode.getChildCount(); i++)
        {
            Node parentNode = processingNode.getParent();
            Node childNode = processingNode.getChild(i);
            if (parentNode != null && parentNode.getInt(SourceData.WEIGHT) == 0 && processingNode.getChildCount() == 1
                    && parentNode != null && parentNode.getParent() != null)  // parent cannot be root
            {
                mergeChildWithParent(tree, processingNode, childNode);
            }

            processNode(tree, childNode);
        }
    }

    private static void mergeChildWithParent(final Tree tree, final Node nodeToRemove, final Node childNode)
    {
        Node parentNode = nodeToRemove.getParent();

        if (parentNode != null)
        {
            System.out.println("tree is a "+tree.getClass());

            parentNode.setString(SourceData.NAME, parentNode.getString(SourceData.NAME) +"."+ nodeToRemove.getString(SourceData.NAME));
            parentNode.setString(SourceData.FULL_NAME, nodeToRemove.getString(SourceData.FULL_NAME));
            parentNode.setInt(SourceData.WEIGHT, nodeToRemove.getInt(SourceData.WEIGHT));
            tree.addChildEdge(parentNode, childNode);

            tree.removeEdge(tree.getEdge(nodeToRemove, childNode));
            tree.removeEdge(tree.getEdge(parentNode, nodeToRemove));

            log.info("nodeToRemove is a "+nodeToRemove.getClass());

            int rowToRemove = nodeToRemove.getRow();
            log.info("rowToRemove: "+rowToRemove);
            log.info("tuplecount = "+nodeToRemove.getTable().getTupleCount());
            Tuple tupleToRemove = nodeToRemove.getTable().getTuple(rowToRemove);
            log.info("tupleToRemove is a "+tupleToRemove.getClass());
            tree.removeTuple(tupleToRemove);
        }
    }

    public static void stopAndLogNode(final Node node, final String name)
    {
        if (node.getString(SourceData.NAME).equals(name))
        {
            if(log.isInfoEnabled())
            {
                log.info(node);
            }
        }
    }

    /**
     *
     * @param item
     * @return
     * @deprecated There is a method for this
     */
    public static List<NodeItem> getAncestors(final NodeItem item)
    {
        List<NodeItem> list = new Vector<NodeItem>(item.getDepth() + 1);

        NodeItem parentNode = (NodeItem) item.getParent();
        while (parentNode != null)
        {
            list.add(parentNode);
            parentNode = (NodeItem) parentNode.getParent();
        }
        return list;
    }

    public static void setRootValues(Tree tree)
    {
        Tuple rootNode = tree.getRoot();

        float kBytes = 0.0F;
        int amount = 0;
        for (Iterator i = tree.children(tree.getRoot()); i.hasNext();)
        {
            Node node = (Node) i.next();
            kBytes += node.getLong(SourceData.SUMMARIZED_KB);
            amount += node.getInt(SourceData.SUMMARIZED_NUMBER_OF_FILES);
        }

        long roundedKBytes = Math.round(kBytes);

        rootNode.setLong(SourceData.SUMMARIZED_KB, roundedKBytes);
        rootNode.setInt(SourceData.SUMMARIZED_NUMBER_OF_FILES, amount);
    }

    public static String[] getColumnArrayfromNo(Schema schema, boolean visual)
    {
//        if (visual)
//        {
//            int columnsWithUnderscore = 0;
//
//            for (int i = 0; i < schema.getColumnCount(); i++)
//            {
//                String columnName = schema.getColumnName(i);
//                if (columnName.startsWith("_"))
//                {
//                    columnsWithUnderscore++;
//                }
//            }
//        }
//        String[] result = null;
//        if (visual)
//        {
//            result = new String[columnsWithUnderscore];
//        }
//        else
//        {
//            result = new String[columnsWithUnderscore];
//        }

        String[] result = new String[schema.getColumnCount()];
        for(int i=0; i<schema.getColumnCount(); i++)
        {
            String columnName = schema.getColumnName(i);
            if(columnName.startsWith("_"))
            {
                result[i] = columnName;
            }
        }

        return result;
    }
    public static String[] getColumnArrayfrom(Schema schema)
    {
        String[] result = new String[schema.getColumnCount()];
        for(int i=0; i<schema.getColumnCount(); i++)
        {
            result[i] = schema.getColumnName(i);
        }

        return result;
    }

    public static int getMaxNodeDepth(Tree tree)
    {
        int maxDepth = 0;
        Node deepestNode = null;

        for (int i=0; i< tree.getNodeTable().getMaximumRow(); i++)
        {
            Node node = tree.getNode(i);
            int currentDepth = node.getDepth();
            if (currentDepth > maxDepth)
            {
                maxDepth = currentDepth;
                deepestNode = node;
            }
        }

        log.info("Deepest node is: "+deepestNode.get(SourceData.NAME));
        recurseToParents(deepestNode);

        return maxDepth;
    }

    public static Stack<Node> recurseToParents(Node node)
    {
        Stack<Node> result = new Stack<Node>();

        // log.info("Node("+node.getDepth()+") = "+node.get(SourceData.COLUMN_NAME));
        // we push the first node right in
        result.push(node);

        Node parent = node.getParent();
        while (parent != null)
        {
            // log.info("Node("+parent.getDepth()+") = "+parent.get(SourceData.COLUMN_NAME));
            result.push(parent);
            parent = parent.getParent();
        }
        return result;
    }

    public static void addStackToNewTree(Tree newTree, Stack<Node> nodeStack)
    {
        // Process all nodes in the stack
        Node currentNode = nodeStack.pop();
        while (currentNode != null)
        {
            // Next node to create as part of the new Tree,...
            if (currentNode.getParent() == null)
            {
                // if it is the root, we look for the root and copy the contents.
                Node newRootNode = newTree.getRoot();
                if (newRootNode == null) log.error("New root Node cannot be null ");

                if(!newRootNode.getString(SourceData.UUID_ID).equals(currentNode.getString(SourceData.UUID_ID)))
                {
                    copyNodeContents(currentNode, newRootNode);
                }
            }
            else
            {
                // if it is not a root Node, get the parent identifier
                Node parentNodeOldTree = currentNode.getParent();
                String UUID = parentNodeOldTree.getString(SourceData.UUID_ID);

                Iterator<Tuple> j = newTree.getNodes().tuples();
                while (j.hasNext())
                {
                    Tuple tuple = j.next();
                    // TreeUtil.printTupleDetails(tuple);

                    // The correct parent has the same UUID
                    if (tuple.getString(SourceData.UUID_ID).equals(UUID))
                    {
                        // found parent, now we look for the child
                        Node parentNode = newTree.getNode(tuple.getRow());
                        int childCount = parentNode.getChildCount();

                        boolean childAlreadyCreated = false;
                        for (int i=0; i< childCount; i++)
                        {
                            Node child = parentNode.getChild(i);
                            if (child.getString(SourceData.UUID_ID).equals(currentNode.getString(SourceData.UUID_ID)))
                            {
                                childAlreadyCreated = true;
                            }
                        }
                        if (!childAlreadyCreated)
                        {
                            // Add a child and copy the contents.
                            Node newChild = newTree.addChild(parentNode);
                            copyNodeContents(currentNode, newChild);

                        }
                    }
                }
            }
            if (nodeStack.size() > 0)
            {
                currentNode = nodeStack.pop();
            }
            else
            {
                currentNode = null;
            }
        }
    }

    private static void copyNodeContents(Node fromNode, Node toNode)
    {
        int fromCount = fromNode.getColumnCount();
        int toCount = toNode.getColumnCount();
        if (fromCount != toCount) throw new RuntimeException("Columns should be identical");

        for(int i=0; i < fromCount; i++)
        {
            Object object = fromNode.get(i);
            if (fromNode.getColumnName(i) != toNode.getColumnName(i))
            {
                throw new RuntimeException("Columns are not identical ("+fromNode.getColumnName(i)+"-"+toNode.getColumnName(i));
            }
            toNode.set(i, object);

            // throw new RuntimeException("Should probably clone objects");
        }
    }

    // --- Adding recursive child count as property

    public static void addRecursiveChildCount(Tree tree)
    {
        // long begin = System.currentTimeMillis();

        tree.addColumn(SourceData.REC_CHILD_COUNT, Integer.TYPE, 0);
        TreeUtil.setRecursivePropertyValues(tree, SourceData.CHILD_COUNT, SourceData.REC_CHILD_COUNT);

        // long end = System.currentTimeMillis();
        // log.info("Adding recursive child count took "+(end-begin)/1000+" seconds.");
    }

    /**Adds a recursively summarised property for every node in the tree. Column needs to have been added before.
     *
     * @param tree
     * @param property for which the Int values are to be summarised
     * @param summarisedProperty property field to store the summarised values
     */
    private static void setRecursivePropertyValues(final Tree tree, final String property, final String summarisedProperty)
    {
        Node root = tree.getRoot();

        recurseIntoBranch(tree, root, property, summarisedProperty);
    }

    private static int recurseIntoBranch(final Tree tree, final Node branch, final String property, final String summarisedProperty)
    {
        int summarisedValue = branch.getChildCount(); // branch.getInt(property);

        for(int i=0; i< branch.getChildCount(); i++)
        {
            summarisedValue += recurseIntoBranch(tree, branch.getChild(i), property, summarisedProperty);
        }
        branch.setInt(summarisedProperty, summarisedValue);

        return summarisedValue;
    }

    public static void addUUIDsTo(Tree tree)
    {
        // Add the UUID column if necessary.
        if (tree.getNodeTable().getColumn(SourceData.UUID_ID) == null)
        {
            tree.addColumn(SourceData.UUID_ID, String.class);
        }

        TupleSet tupleSet = tree.getNodes();
        Iterator<Tuple> i = tupleSet.tuples();
        while (i.hasNext())
        {
            Tuple tuple = i.next();
            tuple.set(SourceData.UUID_ID, UUID.randomUUID().toString());
        }
    }

    public static void writeTreeToJson(Tree tree, String fileName)
    {
        JsonWriter writer = new JsonWriter();
        try
        {
            String result = writer.toJSON(tree);
            FileWriter fileWriter = new FileWriter(new File(fileName));
            BufferedWriter buf = new BufferedWriter(fileWriter);

            buf.write(result);

            buf.flush();
            buf.close();;
        }
        catch(Exception e)
        {
            log.error(e);
        }
    }
}
