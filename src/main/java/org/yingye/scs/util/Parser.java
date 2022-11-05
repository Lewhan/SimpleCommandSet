package org.yingye.scs.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class Parser {

    @SuppressWarnings("all")
    public static HashMap parseYamlToMap(File file) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        return yaml.loadAs(new FileInputStream(file), HashMap.class);
    }

    public static JsonNode parseYamlToJson(File file) throws FileNotFoundException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(mapper.writeValueAsString(parseYamlToMap(file)));
    }

    public static String parseYamlToJsonString(File file) throws FileNotFoundException, JsonProcessingException {
        return parseYamlToJson(file).toPrettyString();
    }

}
