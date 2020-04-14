package treebrowser.data.tree.filesystem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import prefuse.data.Node;
import prefuse.data.Tree;
import treebrowser.data.SourceData;
import treebrowser.data.tree.TreeSource;
import treebrowser.data.tree.TreeUtil;

import java.io.BufferedReader;
import java.io.FileReader;

public class JSONTreeSource implements TreeSource
{
    private static Log log = LogFactory.getLog(JSONTreeSource.class);

    private String fileName = null;

    private Tree tree = null;

    public JSONTreeSource(String fileName)
    {
        this.fileName = fileName;
    }

    public static void main(String[] args)
    {
        String fileName = "/Users/mark/git/prefuse_demos/demos/flare.json";

        JSONTreeSource source = new JSONTreeSource(fileName);

        Tree tree = source.parse();

        // TreeUtil.printTree(tree);
    }

    public Tree parse()
    {
        try
        {
            String jsonContent = loadJSONFile();

            // String content = FileLocatorUtils.readFileToString(file, "utf-8");

            tree = new Tree();

            tree.addColumn(SourceData.NAME, String.class);
            tree.addColumn(SourceData.VALUE, Integer.TYPE);
            tree.addColumn(SourceData.CHILD_COUNT, Integer.TYPE);
            tree.addColumn(SourceData.DOI, Double.TYPE);

            // Convert JSON string to JSONObject
            JSONObject jsonRoot = convertStringSource(jsonContent);

            Node root = tree.addRoot();
            String rootName = jsonRoot.getString("name");
            root.setString(SourceData.NAME, rootName);

            JSONArray children = jsonRoot.getJSONArray("children");
            processChildren(children, root);

            // buildTree(jsonRoot);

        }
        catch(Exception e)
        {
            log.error(e);
        }

        return tree;
    }

    public Tree getTree()
    {
        return tree;
    }

    private void processChildren(JSONArray array, Node pointer)
    {
        // per child
        for (int i=0; i < array.length(); i++)
        {
            JSONObject object = null;
            Node child = null;
            try
            {
                // get the child's name
                object = (JSONObject) array.get(i);
                String name = object.getString("name");
                // log.info("name = " + name);

                child = tree.addChild(pointer);
                child.setString(SourceData.NAME, name);
            }
            catch(JSONException e)
            {
                // this should never happen
                log.warn("Property 'name' not found for "+object);
            }

            boolean isLeaf = true;

            // Try to get the value field. If available, we're definitely dealing with a leaf.
            try
            {
                int value = object.getInt("value");
                child.setInt(SourceData.VALUE, value);
            }
            catch(JSONException jsonException)
            {
                log.warn("Property 'value' not found for "+object);
                isLeaf = false;
            }

            if (!isLeaf)
            {
                child.setString(SourceData.NAME, child.getString(SourceData.NAME)+".");
                JSONArray children = null;
                try
                {
                    children = object.getJSONArray("children");
                    if (children != null)
                    {
                        processChildren(children, child);
                    }
                } catch (JSONException jsonException)
                {
                    log.warn("Property 'children' not found for " + object);
                }
            }
        }
    }

    private void buildTree(JSONObject jsonObject)
    {

    }

    private JSONObject convertStringSource(String content)
    {
        JSONObject jsonObject = null;

        try
        {
            jsonObject = new JSONObject(content);
        }
        catch(Exception e)
        {
            log.error(e);
            System.exit(-1);
        }

        return jsonObject;
    }

    private String loadJSONFile()
    {
        StringBuilder sb = null;
        try
        {
            BufferedReader fileReader = new BufferedReader(new FileReader(fileName));

            sb = new StringBuilder();
            String line = fileReader.readLine();

            while (line != null)
            {
                sb.append(line);
                line = fileReader.readLine();
            }
        }
        catch(Exception e)
        {
            log.error(e);
        }

        return sb.toString();
    }


    public String getAbsRoot()
    {
        return "JSON root";
    }

}
