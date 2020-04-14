package treebrowser.data.json;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import prefuse.data.Node;
import prefuse.data.Tree;
import treebrowser.data.SourceData;

public class JsonWriter
{
    private static Log log = LogFactory.getLog(JsonWriter.class);

    private JSONObject resultObject = null;

    public JsonWriter()
    {
        resultObject = new JSONObject();
    }

    public static void main(String[] args)
    {
        JsonWriter writer = new JsonWriter();
        Tree tree = writer.buildTree();

        try
        {
            String result = writer.toJSON(tree);
            log.info(result);
        }
        catch(Exception e)
        {
            log.error(e);
        }

    }

    public Tree buildTree()
    {
        Tree tree = new Tree();

        tree.addColumn(SourceData.NAME, String.class);
        tree.addColumn(SourceData.CHILD_COUNT, Integer.TYPE);
        tree.addColumn("attribute_1", String.class);

        Node root = tree.addRoot();
        root.setString(SourceData.NAME, "root");
        root.setInt(SourceData.CHILD_COUNT, 5);
        root.setString("attribute_1", "Bal");

        Node childNode = tree.addChild(root);
        childNode.setString(SourceData.NAME, "child");
        childNode.setInt(SourceData.CHILD_COUNT, 3);
        childNode.setString("attribute_1", "Fiets");

        Node grandChild = tree.addChild(childNode);
        grandChild.setString(SourceData.NAME, "grandChild");
        grandChild.setInt(SourceData.CHILD_COUNT, 0);
        grandChild.setString("attribute_1", "Computer");

        return tree;
    }

    public String toJSON(Tree tree) throws JSONException
    {
        Node root = tree.getRoot();
        copyProperties(root, resultObject);

        int childCount = root.getChildCount();
        if (childCount > 0)
        {
            JSONArray array = new JSONArray();
            resultObject.put("children", array);

            for (int i = 0; i < childCount; i++)
            {
                Node child = root.getChild(i);
                String name = child.getString(SourceData.NAME);
                JSONObject jsonChild = new JSONObject();
                array.put(i, jsonChild);
                // resultObject.put(name, jsonChild);
                copyProperties(child, jsonChild);

                toJSON(root.getChild(i), jsonChild);
            }
        }

        return resultObject.toString(2);
    }

    private void copyProperties(Node node, JSONObject currentObject) throws JSONException
    {
        int columnCount = node.getColumnCount();

        for (int i=0; i < columnCount; i++)
        {
            String name = node.getColumnName(i);
            Object value = node.get(i);

            currentObject.put(name, value);
        }
    }

    // children should be added

    public void toJSON(Node node, JSONObject jsonObject) throws JSONException
    {
        int childCount = node.getChildCount();

        if (childCount > 0)
        {
            JSONArray array = new JSONArray();
            jsonObject.put("children", array);

            for (int i = 0; i < childCount; i++)
            {
                Node child = node.getChild(i);
                String name = child.getString(SourceData.NAME);
                JSONObject jsonChild = new JSONObject();
                array.put(i, jsonChild);

                // jsonObject.put(name, jsonChild);
                copyProperties(child, jsonChild);

                toJSON(node.getChild(i), jsonChild);
            }
        }
    }
}
