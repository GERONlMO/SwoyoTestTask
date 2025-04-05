package org.geronimo.command.handler.server;

import org.geronimo.model.Topic;
import org.geronimo.util.DataSerializer;
import org.geronimo.util.ResponseBuilder;
import java.io.IOException;
import java.util.Map;

public class LoadDataHandler {
    private final Map<String, Topic> topics;

    public LoadDataHandler(Map<String, Topic> topics) {
        this.topics = topics;
    }

    public String handle(String filename) {
        try {
            Map<String, Topic> loaded = DataSerializer.load(filename);
            topics.clear();
            topics.putAll(loaded);
            return ResponseBuilder.success("Данные загружены из " + filename);
        } catch (IOException e) {
            return ResponseBuilder.error("Не удалось загрузить данные");
        }
    }
}
