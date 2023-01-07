package org.yingye.scs.util;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class Parser {

    public static HashMap<?, ?> parseYamlToMap(File file) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        return yaml.loadAs(new FileInputStream(file), HashMap.class);
    }

}
