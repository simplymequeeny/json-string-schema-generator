package org.simplymequeeny;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.sun.codemodel.JCodeModel;
import org.apache.commons.io.FileUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsonschema2pojo.SchemaMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public final class JsonSchemaGenerator {

    private static final Logger LOGGER = Logger.getLogger(JsonSchemaGenerator.class.getName());
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Map<String, JsonNodeType> map = new HashMap<>();

    public static String outputAsString(String title, String description,
                                        String json) throws IOException {
        return cleanup(outputAsString(title, description, json, null));
    }

    public static void outputAsFile(String title, String description,
                                    String json, String filename) throws IOException {
        FileUtils.writeStringToFile(
                new File(filename),
                cleanup(outputAsString(title, description, json)),
                "utf8");
    }

    public static void outputAsPOJO(String title, String description,
                                    String json, String packageName,
                                    String outputDirectory) throws IOException {
        String schema = JsonSchemaGenerator.outputAsString(title, title, json);
        LOGGER.info("Generating POJO(s) ...");

        File fDirectory = new File(outputDirectory);
        if (!fDirectory.exists()) FileUtils.forceMkdir(fDirectory);

        JCodeModel codeModel = new JCodeModel();
        SchemaMapper mapper = new SchemaMapper();
        mapper.generate(codeModel, title, packageName, schema);
        codeModel.build(fDirectory);
        LOGGER.info("DONE.");
    }

    private static String outputAsString(String title, String description,
                                         String json, JsonNodeType type) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(json);
        StringBuilder output = new StringBuilder();


        if (type == null && jsonNode.getNodeType() != JsonNodeType.ARRAY) {
            output.append(
                    "{\"title\": \"" +
                            title + "\", \"description\": \"" +
                            description + "\", \"type\": \"object\", \"properties\": {");
        }

        if (type == null && jsonNode.getNodeType() == JsonNodeType.ARRAY) {
            output.append(
                    "{\"title\": \"" +
                            title + "\", \"description\": \"" +
                            description + "\", \"type\": \"array\", \"items\": [");
            boolean flag = false;
            for (int i=0; i< jsonNode.size(); i++) {
                flag = true;
                JsonNode node = jsonNode.get(i);
                output.append(outputAsString(null, null, node.toString(), JsonNodeType.ARRAY));
            }
            if (flag){
                output.replace( output.lastIndexOf(","), output.lastIndexOf(",")+1,"");
            }
            output.append("]");

        }

        if (type == JsonNodeType.ARRAY){

            LOGGER.info("processing items ...");


            output.append(convertNodeToStringSchemaNode(jsonNode, jsonNode.getNodeType(), null));

        }else {
            if (type != null)
                output.append("{");
            for (Iterator<String> iterator = jsonNode.fieldNames(); iterator.hasNext(); ) {
                String fieldName = iterator.next();
                LOGGER.info("processing " + fieldName + "...");

                JsonNodeType nodeType = jsonNode.get(fieldName).getNodeType();

                output.append(convertNodeToStringSchemaNode(jsonNode, nodeType, fieldName));
            }

            if (type == null) output.append("}");

            output.append("}");
        }
        LOGGER.info("generated schema = " + output.toString());
        return output.toString();
    }

    private static String convertNodeToStringSchemaNode(
            JsonNode jsonNode, JsonNodeType nodeType, String key) throws IOException {
        StringBuilder result = new StringBuilder();

        if (key != null) {
            result.append("\"" + key + "\": { \"type\": \"");
            LOGGER.info(key + " node type " + nodeType + " with value " + jsonNode.get(key));
        }else{
            result.append("{ \"type\": \"");
        }
        JsonNode node = null;
        switch (nodeType) {
            case ARRAY :

                result.append("array\", \"items\": [");
                boolean flag = false;
                for (int i=0; i< jsonNode.get(key).size(); i++) {
                    flag = true;
                    node = jsonNode.get(key).get(i);
                    LOGGER.info(key + " is an array with value of " + node.toString());
                    result.append(outputAsString(null, null, node.toString(), JsonNodeType.ARRAY));
                }
                if (flag){
                    result.replace( result.lastIndexOf(","), result.lastIndexOf(",")+1,"");
                }
                result.append("]},");
                break;
            case BOOLEAN:
                result.append("boolean\" },");
                break;
            case NUMBER:
                result.append("number\" },");
                break;
            case OBJECT:
                node = jsonNode.get(key);
                result.append("object\", \"properties\": ");
                result.append(outputAsString(null, null, node.toString(), JsonNodeType.OBJECT));
                result.append("},");
                break;
            case STRING:
                result.append("string\" },");
                break;
        }

        return result.toString();
    }

    private static String cleanup(String dirty) {
        JSONObject rawSchema = new JSONObject(new JSONTokener(dirty));
        Schema schema = SchemaLoader.load(rawSchema);
        return schema.toString();
    }
}
