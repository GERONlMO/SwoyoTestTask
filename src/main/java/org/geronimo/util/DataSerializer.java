package org.geronimo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.geronimo.model.Topic;

public class DataSerializer {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void save(Map<String, Topic> data, String filename) throws IOException {
        mapper.writeValue(new File(filename), data);
    }

    public static Map<String, Topic> load(String filename) throws IOException {
        return mapper.readValue(new File(filename),
                mapper.getTypeFactory().constructMapType(Map.class, String.class, Topic.class));
    }
}
