package treebrowser.data;

import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tree;

import java.util.UUID;

public class SourceData
{
    private static int INITIAL_NODE_VALUE = -1;

    public static final String ROOT_NODE_NAME = "root\n";

    public static final String NAME = "name";
    public static final String VALUE = "value";

// 	public static final String LABEL = "label";

    public static final String ROOT = "root";
    public static final String FULL_NAME = "full_name";
    public static final String WIRE_FRAME = "wire_frame";

    public static final String UUID_ID = "uuid";

    public static final String DOI = "doi";

    public static final String NUMBER_OF_CLASSES = "number_of_classes";

    public static final String DEPENDENCY_COUNT = "dependency_count";

    public static final String TREE_DEPTH = "tree_depth";
    public static final String CHILD_COUNT = "child_count";
    public static final String REC_CHILD_COUNT = "rec_child_count";

    public static final String WEIGHT = "weight";
    public static final String EXPERT_UI = "expert_ui";
    public static final String INCLUDE_GREEN = "include_green";

    public static final String HAS_CHILDREN = "has_children";
    public static final String NUMBER_OF_METHODS = "number_of_methods";
    public static final String SUMMARIZED_NUMBER_OF_FILES = "summarized_no_files";
    public static final String SUMMARIZED_KB = "summarized_kb";

    public static final String METRIC_CYCLOMATIC_COMPLEXITY = "metric_cyclomatic_complexity";
    public static final String METRIC_METHOD_SIZE = "metric_method_size_violation";
    public static final String METRIC_ILLEGAL_CATCH = "metric_illegal_catch";
    public static final String METRIC_CLASS_FAN_OUT = "metric_class_fan_out";
    public static final String METRIC_JDEPEND_PREFIX = "jdepend-metric_";
    public static final String CLASSCOUNT = "classcount";
    public static final String ABSTRACT_CLASSCOUNT = "abstract_classcount";
    public static final String AFFERENT_COUPLINGS = "afferent_couplings";
    public static final String EFFERENT_COUPLINGS = "efferent_couplings";
    public static final String ABSTRACTNESS = "abstractness";
    public static final String INSTABILITY = "instability";
    public static final String DISTANCE = "distance";
    public static final String DEPENDENCY_CYCLES = "dependency_cycles";
    public static final String COMPLEXITY_SUMMARIZED = "complexity_summarized";

    public SourceData()
    {
    }

    public static final Tree initializeTree(String rootNodeLabel)
    {
        Tree tree = new Tree();

        Table nodeTable = tree.getNodeTable();

        // initialize node properties
        // name is the key identifier
        nodeTable.addColumn(NAME, String.class);

        // a boolean that indicates whether the node is the root
        nodeTable.addColumn(ROOT, Boolean.TYPE, false);

        // Unique identifier
        nodeTable.addColumn(UUID_ID, String.class);

        // longer name
        nodeTable.addColumn(FULL_NAME, java.lang.String.class, "");

        // weight of the node, aggregated from the weight of the underlying nodes
        nodeTable.addColumn(WEIGHT, Double.TYPE);

        // nodeTable.addColumn(TREE_DEPTH, Integer.TYPE);

        nodeTable.addColumn(DOI, Double.TYPE);

        nodeTable.addColumn(NUMBER_OF_CLASSES, Integer.TYPE, 0);
        nodeTable.addColumn(SUMMARIZED_NUMBER_OF_FILES, Integer.TYPE);
        nodeTable.addColumn(SUMMARIZED_KB, Long.TYPE);

        // rootNode.getTable().addColumn(NUMBER_OF_METHODS, Integer.TYPE);

        // Deactivating a few to see what we can getProperty away with (probably will be hurting ComplexityMap)
        // nodeTable.addColumn(EXPERT_UI, Integer.TYPE);

        // initialise root node
        Node rootNode = tree.addRoot();
        rootNode.setString(NAME, ROOT_NODE_NAME + rootNodeLabel);
        rootNode.setBoolean(ROOT, true);

        return tree;
    }

    public static Node createNode(Tree tree, Node parent)
    {
        Node node = createNodeWithoutUUID(tree, parent);

        String uniqueID = UUID.randomUUID().toString();
        node.setString(UUID_ID, uniqueID);

        return node;
    }

    public static Node createNodeWithoutUUID(Tree tree, Node parent)
    {
        Node node = tree.addChild(parent);

        return node;
    }

}
