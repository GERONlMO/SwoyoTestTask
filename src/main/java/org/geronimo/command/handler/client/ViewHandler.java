package org.geronimo.command.handler.client;

import org.geronimo.model.Vote;
import org.geronimo.util.CommandParser;
import org.geronimo.util.ResponseBuilder;
import org.geronimo.model.Topic;
import java.util.Map;
import java.util.Optional;

public class ViewHandler {
    private final Map<String, Topic> topics;

    public ViewHandler(Map<String, Topic> topics) {
        this.topics = topics;
    }

    public String handle(String args) {
        if (args.contains("-t") && args.contains("-v")) {
            Optional<String> topicName = CommandParser.getArgumentValue(args, "-t");
            Optional<String> voteName = CommandParser.getArgumentValue(args, "-v");

            if (!topicName.isPresent() || !voteName.isPresent()) {
                return ResponseBuilder.error("Используйте формат: view -t=<topic> -v=<vote>");
            }

            Topic topic = topics.get(topicName.get());
            if (topic == null) return ResponseBuilder.error("Раздел не найден");

            Vote vote = topic.getVote(voteName.get());
            if (vote == null) return ResponseBuilder.error("Голосование не найдено");

            return ResponseBuilder.formatVoteResults(
                    vote.getName(),
                    vote.getDescription(),
                    vote.getResults(),
                    vote.getOptions()
            );
        } else if (args.contains("-t")) {
            Optional<String> topicName = CommandParser.getArgumentValue(args, "-t");
            if (!topicName.isPresent()) return ResponseBuilder.error("Укажите раздел: -t=<topic>");

            Topic topic = topics.get(topicName.get());
            if (topic == null) return ResponseBuilder.error("Раздел не найден");

            StringBuilder sb = new StringBuilder();
            topic.getAllVotes().forEach(v -> sb.append(v.getName()).append("\n"));
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            topics.forEach((name, topic) ->
                    sb.append(String.format("%s (голосований: %d)\n", name, topic.getVotesCount()))
            );
            return sb.toString();
        }
    }
}