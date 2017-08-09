package org.simplymequeeny;

import org.apache.commons.io.FileUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class JsonSchemaGeneratorTest {

    @Test
    public void shouldGenerateSchemaUsingSimpleObject() throws Exception {
        String json = "{\"start\":{\"hour\":17,\"minute\":5}}";
        String result = JsonSchemaGenerator.outputAsString("Schedule", "Test", json);
        Assert.assertTrue(isValid(json, result));
    }

    @Test
    public void shouldGenerateSchemaUsingArrayOfObjects() throws Exception {
        String json = "{\"sectors\": [{\"times\":[{\"intensity\":30," +
                "\"start\":{\"hour\":8,\"minute\":30},\"end\":{\"hour\":17,\"minute\":0}}," +
                "{\"intensity\":10,\"start\":{\"hour\":17,\"minute\":5},\"end\":{\"hour\":23,\"minute\":55}}]," +
                "\"id\":\"dbea21eb-57b5-44c9-a953-f61816fd5876\"}]}";
        String result = JsonSchemaGenerator.outputAsString("Schedule", "Test",json);
        Assert.assertTrue(isValid(json, result));
    }

    @Test
    public void shouldGenerateAValidSchema() throws Exception {
        String json = "{\"createdAt\":\"2017-07-19T16:31:26.843Z\"," +
                "\"sectors\":[{\"times\":[{\"intensity\":30,\"start\":{\"hour\":8,\"minute\":30}," +
                "\"end\":{\"hour\":17,\"minute\":0}},{\"intensity\":10,\"start\":{\"hour\":17,\"minute\":5}," +
                "\"end\":{\"hour\":23,\"minute\":55}}],\"id\":\"dbea21eb-57b5-44c9-a953-f61816fd5876\"}]," +
                "\"dayOfWeek\":\"0,6\",\"createdBy\":\"Admin\",\"name\":\"test weekend preset\"," +
                "\"client\":\"TestClient\",\"id\":\"83d6640a-6d80-487c-b92c-e4239e1ec6d5\"}";
        String result = JsonSchemaGenerator.outputAsString("Schedule", "Test",json);
        Assert.assertTrue(isValid(json, result));
    }

    @Test
    public void shouldWriteJsonSchemaToFile() throws Exception {
        String json = "{\"createdAt\":\"2017-07-19T16:31:26.843Z\"," +
                "\"sectors\":[{\"times\":[{\"intensity\":30,\"start\":{\"hour\":8,\"minute\":30}," +
                "\"end\":{\"hour\":17,\"minute\":0}},{\"intensity\":10,\"start\":{\"hour\":17,\"minute\":5}," +
                "\"end\":{\"hour\":23,\"minute\":55}}],\"id\":\"dbea21eb-57b5-44c9-a953-f61816fd5876\"}]," +
                "\"dayOfWeek\":\"0,6\",\"createdBy\":\"Admin\",\"name\":\"test weekend preset\"," +
                "\"client\":\"TestClient\",\"id\":\"83d6640a-6d80-487c-b92c-e4239e1ec6d5\"," +
                "\"state\":true, \"dateToday\": \"null\"}";
        String filename = "output-schema.json";
        JsonSchemaGenerator.outputAsFile("Schedule", "Test",json, filename);
        Assert.assertTrue(FileUtils.getFile(filename).exists());
    }

    @Test
    public void shouldGeneratePOJOs() throws IOException {
        String json = "{\"createdAt\":\"2017-07-19T16:31:26.843Z\"," +
                "\"sectors\":[{\"times\":[{\"intensity\":30,\"start\":{\"hour\":8,\"minute\":30}," +
                "\"end\":{\"hour\":17,\"minute\":0}},{\"intensity\":10,\"start\":{\"hour\":17,\"minute\":5}," +
                "\"end\":{\"hour\":23,\"minute\":55}}],\"id\":\"dbea21eb-57b5-44c9-a953-f61816fd5876\"}]," +
                "\"dayOfWeek\":\"0,6\",\"createdBy\":\"Admin\",\"name\":\"test weekend preset\"," +
                "\"client\":\"TestClient\",\"id\":\"83d6640a-6d80-487c-b92c-e4239e1ec6d5\"," +
                "\"state\":true, \"dateToday\": \"null\"}";

        String directory = "generated-sources";
        JsonSchemaGenerator.outputAsPOJO("Schedule", "Test", json,
                "com.example", directory);
        Assert.assertTrue("POJO(s) not generated",
                FileUtils.getFile(directory).list().length > 0);
        FileUtils.forceDeleteOnExit(new File(directory));
    }

    @Test
    public void shouldGeneratePOJOsIntoExistingDirectory() throws IOException {
        String json = "{\"createdAt\":\"2017-07-19T16:31:26.843Z\"," +
                "\"sectors\":[{\"times\":[{\"intensity\":30,\"start\":{\"hour\":8,\"minute\":30}," +
                "\"end\":{\"hour\":17,\"minute\":0}},{\"intensity\":10,\"start\":{\"hour\":17,\"minute\":5}," +
                "\"end\":{\"hour\":23,\"minute\":55}}],\"id\":\"dbea21eb-57b5-44c9-a953-f61816fd5876\"}]," +
                "\"dayOfWeek\":\"0,6\",\"createdBy\":\"Admin\",\"name\":\"test weekend preset\"," +
                "\"client\":\"TestClient\",\"id\":\"83d6640a-6d80-487c-b92c-e4239e1ec6d5\"," +
                "\"state\":true, \"dateToday\": \"null\"}";

        String directory = "generated-src";
        File dir = new File(directory);
        FileUtils.forceMkdir(dir);

        JsonSchemaGenerator.outputAsPOJO("Schedule", "Test", json,
                "com.example", directory);
        Assert.assertTrue("POJO(s) not generated",
                FileUtils.getFile(directory).list().length > 0);
        FileUtils.forceDeleteOnExit(dir);
    }

    private boolean isValid(String json, String result) {
        boolean isValid = true;

        JSONObject rawSchema = new JSONObject(new JSONTokener(result));
        Schema schema = SchemaLoader.load(rawSchema);

        try {
            schema.validate(new JSONObject(json));
        }
        catch (ValidationException e) {
            isValid = false;
        }

        return isValid;
    }
}
