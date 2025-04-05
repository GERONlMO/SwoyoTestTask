package org.geronimo.command.handler.server;

import org.geronimo.model.Topic;
import org.geronimo.util.DataSerializer;
import org.geronimo.util.ResponseBuilder;
import java.io.IOException;
import java.util.Map;

public class SaveDataHandler {
    private final Map<String, Topic> topics;

    public SaveDataHandler(Map<String, Topic> topics) {
        this.topics = topics;
    }

    public String handle(String filename) {
        try {
            DataSerializer.save(topics, filename);
            return ResponseBuilder.success("Данные сохранены в " + filename);
        } catch (IOException e) {
            return ResponseBuilder.error("Не удалось сохранить данные");
        }
    }
}
