package org.simplymequeeny;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.apache.commons.io.FileUtils;

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

    public static String outputAsString(String json) throws IOException {
        return outputAsString(json, null);
    }

    public static void outputAsFile(String json, String filename) throws IOException {
        FileUtils.writeStringToFile(new File(filename), outputAsString(json), "utf8");
    }

    private static String outputAsString(String json, JsonNodeType type) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(json);
        StringBuilder output = new StringBuilder();
        output.append("{");

        for (Iterator<String> iterator = jsonNode.fieldNames(); iterator.hasNext();) {
            String fieldName = iterator.next();
            LOGGER.info("processing " + fieldName + "...");

            JsonNodeType nodeType = jsonNode.get(fieldName).getNodeType();

            if (map.get(fieldName) == nodeType) continue;
            else map.put(fieldName, nodeType);

            output.append(convertNodeToStringSchemaNode(jsonNode, nodeType, fieldName));
        }

        output.append("}");

        LOGGER.info("generated schema = " + output.toString());
        return output.toString();
    }

    private static String convertNodeToStringSchemaNode(
            JsonNode jsonNode, JsonNodeType nodeType, String key) throws IOException {
        StringBuilder result = new StringBuilder("\"" + key + "\": { \"type\": \"");

        LOGGER.info(key + " node type " + nodeType + " with value " + jsonNode.get(key));
        JsonNode node = null;
        switch (nodeType) {
            case ARRAY :
                node = jsonNode.get(key).get(0);
                LOGGER.info(key + " is an array with value of " + node.toString());
                result.append("array\", \"items\": { \"properties\":");
                result.append(outputAsString(node.toString(), JsonNodeType.ARRAY));
                result.append("}},");
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
                result.append(outputAsString(node.toString()));
                result.append("},");
                break;
            case STRING:
                result.append("string\" },");
                break;
        }

        return result.toString();
    }
}
