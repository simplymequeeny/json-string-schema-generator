[![Build Status](https://travis-ci.org/simplymequeeny/json-string-schema-generator.svg?branch=master)](https://travis-ci.org/simplymequeeny/json-string-schema-generator)
[![codecov](https://codecov.io/gh/simplymequeeny/json-string-schema-generator/branch/master/graph/badge.svg)](https://codecov.io/gh/simplymequeeny/json-string-schema-generator)

# json-string-schema-generator
Generates JSON Schema or POJO based from JSON data using JAVA

#### Motivation
I have been googling a lot to find a library in JAVA that would generate me a JSON Schema based on the JSON data I have.  But most of the libraries I found need to have a JAVA class in order to generate a JSON schema.  So I made a simple JSON Schema Generator based on JSON data.  If there is a library that already exists please let me know (via posting an Issue here)  and I will be happy to use it in the current project I am working on.  Thanks!

### Features
1. Generates JSON Schema (output as String or File)
1. Generates POJOs

#### Usage
Just call the static method JsonSchemaGenerator.outputAsString or JsonSchemaGenerator.outputAsFile
```java
public class GenerateSchema {
    public static void main(String[] args) throws IOException {
        String json = "{\"sectors\": [{\"times\":[{\"intensity\":30," +
                "\"start\":{\"hour\":8,\"minute\":30},\"end\":{\"hour\":17,\"minute\":0}}," +
                "{\"intensity\":10,\"start\":{\"hour\":17,\"minute\":5},\"end\":{\"hour\":23,\"minute\":55}}]," +
                "\"id\":\"dbea21eb-57b5-44c9-a953-f61816fd5876\"}]}";
        String result = JsonSchemaGenerator.outputAsString("Schedule", "this is a test", json);
            /* sample output
            {
              "title": "Schedule",
              "description": "this is a test",
              "type": "object",
              "properties": {
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
                              "properties": {
                                "hour": {
                                  "type": "number"
                                },
                                "minute": {
                                  "type": "number"
                                }
                              }
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
            }
             */

        // To generate JSON schema into a file
        JsonSchemaGenerator.outputAsFile("Schedule", "this is a test", json, "output-schema.json");
        
        // To generate POJO(s)
        JsonSchemaGenerator.outputAsPOJO("Schedule", "this is a test", json, "com.example", "generated-sources");
    }
}
```
#### Author
* [Quennie Teves](https://github.com/simplymequeeny) - initial work

#### Built With
1. [Java 8](https://www.oracle.com/java/index.html)
1. [Jackson Databind](https://github.com/FasterXML/jackson-databind)
1. [JSON Schema Validator](https://github.com/everit-org/json-schema)
1. [jsonschema2pojo](http://www.jsonschema2pojo.org/)

#### License
This project is licensed under the MIT License - see the [LICENSE](/LICENSE) file for details
