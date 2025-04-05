package org.geronimo.command.handler.client;

import org.geronimo.command.context.VoteCreationContext;
import org.geronimo.util.CommandParser;
import org.geronimo.util.ResponseBuilder;
import org.geronimo.model.Topic;
import io.netty.channel.Channel;
import java.util.*;

public class CreateHandler {
    private final Map<String, Topic> topics;
    private final Map<Channel, VoteCreationContext> voteCreationContexts;

    public CreateHandler(Map<String, Topic> topics, Map<Channel, VoteCreationContext> voteCreationContexts) {
        this.topics = topics;
        this.voteCreationContexts = voteCreationContexts;
    }

    public String handle(Channel channel, String command, String username) {
        if (command.startsWith("topic")) {
            Optional<String> topicName = CommandParser.getArgumentValue(command, "-n");
            if (!topicName.isPresent()) {
                return ResponseBuilder.error("Укажите название раздела: -n=<topic>");
            }
            if (topics.containsKey(topicName.get())) {
                return ResponseBuilder.error("Раздел уже существует");
            }
            topics.put(topicName.get(), new Topic(topicName.get(), username, new HashMap<>()));
            return ResponseBuilder.success("Раздел создан");
        } else if (command.startsWith("vote")) {
            Optional<String> topicName = CommandParser.getArgumentValue(command, "-t");
            if (!topicName.isPresent()) {
                return ResponseBuilder.error("Укажите раздел: -t=<topic>");
            }
            Topic topic = topics.get(topicName.get());
            if (topic == null) {
                return ResponseBuilder.error("Раздел не найден");
            }
            voteCreationContexts.put(channel, new VoteCreationContext(topic, username, channel));
            return ResponseBuilder.expect("Введите название голосования:");
        }
        return ResponseBuilder.error("Неизвестная подкоманда");
    }
}