#json-string-schema-generator
Generates JSON Schema based from JSON data using JAVA

#### Motivation
I have been googling a lot to find a library in JAVA that would generate me a JSON Schema based on the JSON data I have.  But the library I found need to have a JAVA class in order to generate a JSON schema.  So I made a simple JSON Schema Generator based on JSON data.  If there is a library that already exists please let me know (via posting an Issue here)  and I will be happy to use it in the current project I am working on.  Thanks!

#### Usage
Just call the static method JsonSchemaGenerator.outputAsString or JsonSchemaGenerator.outputAsFile
```java
public class GenerateSchema {
        public static void main(String[] args) throws IOException {
            String json = "{\"sectors\": [{\"times\":[{\"intensity\":30," +
                    "\"start\":{\"hour\":8,\"minute\":30},\"end\":{\"hour\":17,\"minute\":0}}," +
                    "{\"intensity\":10,\"start\":{\"hour\":17,\"minute\":5},\"end\":{\"hour\":23,\"minute\":55}}]," +
                    "\"id\":\"dbea21eb-57b5-44c9-a953-f61816fd5876\"}]}";
            String result = JsonSchemaGenerator.outputAsString(json);
            /* sample output
            {
              "sectors": {
                "type": "array",
                "items": {
                  "properties": {
                    "times": {
                      "type": "array",
                      "items": {
                        "properties": {
                          "intensity": {
                            "type": "number"
                          },
                          "start": {
                            "type": "object",
                            "properties": {
                              "hour": {
                                "type": "number"
                              },
                              "minute": {
                                "type": "number"
                              }
                            }
                          },
                          "end": {
                            "type": "object",
                            "properties": {}
                          }
                        }
                      }
                    },
                    "id": {
                      "type": "string"
                    }
                  }
                }
              }
            }
             */
            
            JsonSchemaGenerator.outputAsFile(json, "output-schema.json");
            // the above statement will generate the file on the directory 
            // where code was executed
        }
}
```
#### Author
* Quennie Teves - initial work 

#### Built With
1. Java 8
1. Jackson Databind 

#### License
This project is licensed under the MIT License - see the [LICENSE](/LICENSE) file for details