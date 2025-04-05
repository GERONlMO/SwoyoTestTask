package org.geronimo.command.handler.client;

import org.geronimo.model.Vote;
import org.geronimo.util.CommandParser;
import org.geronimo.util.ResponseBuilder;
import org.geronimo.model.Topic;
import org.geronimo.model.User;
import io.netty.channel.Channel;
import java.util.Map;
import java.util.Optional;

public class DeleteHandler {
    private final Map<String, Topic> topics;
    private final Map<Channel, User> sessions;

    public DeleteHandler(Map<String, Topic> topics, Map<Channel, User> sessions) {
        this.topics = topics;
        this.sessions = sessions;
    }

    public String handle(Channel channel, String args) {
        User user = sessions.get(channel);
        Optional<String> topicName = CommandParser.getArgumentValue(args, "-t");
        Optional<String> voteName = CommandParser.getArgumentValue(args, "-v");

        if (!topicName.isPresent() || !voteName.isPresent()) {
            return ResponseBuilder.error("Используйте формат: delete -t=<topic> -v=<vote>");
        }

        Topic topic = topics.get(topicName.get());
        if (topic == null) return ResponseBuilder.error("Раздел не найден");

        Vote vote = topic.getVote(voteName.get());
        if (vote == null) return ResponseBuilder.error("Голосование не найдено");

        if (!vote.getCreator().equals(user.getUsername())) {
            return ResponseBuilder.error("Нет прав на удаление");
        }

        topic.removeVote(voteName.get());
        return ResponseBuilder.success("Голосование удалено");
    }
}