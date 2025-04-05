package org.geronimo.command.handler.client;

import org.geronimo.command.context.VoteContext;
import org.geronimo.model.Topic;
import org.geronimo.util.CommandParser;
import org.geronimo.util.ResponseBuilder;
import org.geronimo.model.Vote;
import io.netty.channel.Channel;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VoteHandler {
    private final Map<Channel, VoteContext> voteContexts;
    private final Map<String, Topic> topics;

    public VoteHandler(Map<Channel, VoteContext> voteContexts, Map<String, Topic> topics) {
        this.voteContexts = voteContexts;
        this.topics = topics;
    }

    public String handleStartVote(Channel channel, String args) {
        Optional<String> topicName = CommandParser.getArgumentValue(args, "-t");
        Optional<String> voteName = CommandParser.getArgumentValue(args, "-v");

        if (!topicName.isPresent() || !voteName.isPresent()) {
            return ResponseBuilder.error("Неверный формат команды");
        }

        Topic topic = topics.get(topicName.get());
        if (topic == null) return ResponseBuilder.error("Раздел не найден");

        Vote vote = topic.getVote(voteName.get());
        if (vote == null) return ResponseBuilder.error("Голосование не найдено");

        voteContexts.put(channel, new VoteContext(vote, channel));

        String options = IntStream.range(0, vote.getOptions().size())
                .mapToObj(i -> (i + 1) + ". " + vote.getOptions().get(i))
                .collect(Collectors.joining("\n"));

        return ResponseBuilder.expect("Выберите вариант:\n" + options);
    }

    public String processVote(Channel channel, String input) {
        VoteContext context = voteContexts.get(channel);
        if (context == null) return ResponseBuilder.error("Голосование не начато");

        try {
            int choice = Integer.parseInt(input) - 1;
            context.vote.vote(choice);
            voteContexts.remove(channel);
            return ResponseBuilder.success("Голос учтен");
        } catch (Exception e) {
            return ResponseBuilder.error("Некорректный вариант");
        }
    }
}