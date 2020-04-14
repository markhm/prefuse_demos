package treebrowser.data.tree;

import prefuse.data.Tree;

public interface TreeSource
{
    public static final String NO_ROOT = "No root";

    public String getAbsRoot();

    /** Initial call to parse and build the tree.*/
	public Tree parse();

    /** Subsequent call to return the parsed tree.*/
	public Tree getTree();


}
