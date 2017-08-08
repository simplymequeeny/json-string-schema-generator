package org.simplymequeeny;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

public class JsonSchemaGenerator {

    private static final Logger LOGGER = Logger.getLogger(JsonSchemaGenerator.class.getName());
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String outputAsString(String json) throws IOException {
        return outputAsString(json, null);
    }

    public static void outputAsFile(String json, String filename) throws IOException {
        FileUtils.writeStringToFile(new File(filename), outputAsString(json), "utf8");
    }

    private static String outputAsString(String json, JsonNodeType type) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(json);
        StringBuilder output = new StringBuilder();

        if (type == null) {
            output.append("{\"type\": \"object\",");
        }
        else if (type == JsonNodeType.ARRAY) {
            output.append("{ \"type\": \"array\",");
            output.append("\"items\": {");
        }
        output.append("\"properties\": {");

        for (Iterator<String> iterator = jsonNode.fieldNames(); iterator.hasNext();) {
            String fieldName = iterator.next();
            LOGGER.info("processing " + fieldName + "...");

            if (jsonNode.get(fieldName).isArray()) {
                JsonNode node = jsonNode.get(fieldName).get(0);
                LOGGER.info(fieldName + " is an array with value of " + node.toString());
                output.append("\"" + fieldName + "\":");
                output.append(outputAsString(node.toString(), JsonNodeType.ARRAY));
                output.append(",");
            }
            else if (jsonNode.get(fieldName).isObject()) {
                JsonNode node = jsonNode.get(fieldName);
                output.append("\"" + fieldName + "\":");
                output.append(outputAsString(node.toString()));
                output.append(",");
            }
            else {
                output.append(convertNodeToStringSchemaNode(jsonNode, fieldName));
            }
        }

        if (type == JsonNodeType.ARRAY) output.append("}");

        output.append("}");
        output.append("}");

        LOGGER.info("generated schema = " + output.toString());
        return output.toString();
    }

    private static String convertNodeToStringSchemaNode(JsonNode jsonNode, String key) {
        StringBuilder result = new StringBuilder("\"" + key + "\": { \"type\": \"");

        switch (jsonNode.get(key).getNodeType()) {
            case ARRAY :
                result.append("array");
                break;
            case BOOLEAN:
                result.append("boolean");
                break;
            case NUMBER:
                result.append("number");
                break;
            case OBJECT:
                result.append("object");
                break;
            case STRING:
                result.append("string");
                break;
        }
        result.append("\" },");

        return result.toString();
    }
}
