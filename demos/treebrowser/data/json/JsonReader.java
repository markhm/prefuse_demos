package treebrowser.data.json;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.*;
import java.util.Iterator;
import java.util.Set;

public class JsonReader
{
    private static Log log = LogFactory.getLog(JsonReader.class);


    // https://www.codevoila.com/post/65/java-json-tutorial-and-example-json-java-orgjson
    public JsonReader()
    {
    }

    public static void main(String[] args) throws Exception
    {
        File file = new File("data/d3js/msc2010_tree_pretty.json");
        BufferedReader fileReader = new BufferedReader(new FileReader(file));

        StringBuilder sb = new StringBuilder();

        String line = fileReader.readLine();

        while (line != null)
        {
            sb.append(line);
            line = fileReader.readLine();
        }

        // String content = FileLocatorUtils.readFileToString(file, "utf-8");

        // Convert JSON string to JSONObject
        JSONObject jsonObject = new JSONObject(sb.toString());

        printJSONObject(jsonObject);

        // System.out.println(jsonObject);

    }

    private void write(JSONObject jsonObject) throws IOException
    {
        JSONWriter writer = new JSONWriter(new BufferedWriter(new FileWriter(new File("output.json"))));
        // writer.
    }

    private static void printJSONObject(JSONObject jsonObject) throws JSONException
    {
        // keyset
        Set<String> keySet = jsonObject.keySet();
        Iterator<String> i = keySet.iterator();

        while (i.hasNext())
        {
            String key = i.next();
            log.info("Found key: "+key);
        }

        // names
        JSONArray jsonArray = jsonObject.names();

        int length = jsonArray.length();

        for (int j = 0; j< length; j++)
        {
            // JSONObject object = (JSONObject) jsonArray.get(j);

            String string = (String) jsonArray.get(j);

            log.info("Found string: "+string);
            // printJSONObject(object);
        }
    }

}
