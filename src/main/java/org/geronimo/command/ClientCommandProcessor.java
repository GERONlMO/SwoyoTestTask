package org.geronimo.command;

import org.geronimo.command.context.*;
import org.geronimo.command.handler.client.*;
import org.geronimo.util.*;
import org.geronimo.model.*;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientCommandProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ClientCommandProcessor.class);

    private final Map<String, Topic> topics;
    private final Map<Channel, User> sessions = new ConcurrentHashMap<>();
    private final Map<Channel, VoteCreationContext> voteCreationContexts = new ConcurrentHashMap<>();
    private final Map<Channel, VoteContext> voteContexts = new ConcurrentHashMap<>();

    private final LoginHandler loginHandler;
    private final CreateHandler createHandler;
    private final ViewHandler viewHandler;
    private final VoteHandler voteHandler;
    private final DeleteHandler deleteHandler;
    private final ExitHandler exitHandler;

    public ClientCommandProcessor(Map<String, Topic> topics) {
        this.topics = topics;
        this.loginHandler = new LoginHandler(sessions);
        this.createHandler = new CreateHandler(topics, voteCreationContexts);
        this.viewHandler = new ViewHandler(topics);
        this.voteHandler = new VoteHandler(voteContexts, topics);
        this.deleteHandler = new DeleteHandler(topics, sessions);
        this.exitHandler = new ExitHandler(sessions);
    }

    public String processCommand(Channel channel, String command) {
        try {
            if (voteContexts.containsKey(channel)) {
                return voteHandler.processVote(channel, command);
            }

            VoteCreationContext creationContext = voteCreationContexts.get(channel);
            if (creationContext != null) {
                return processVoteCreation(creationContext, command);
            }

            if (!sessions.containsKey(channel) && !command.startsWith("login")) {
                return ResponseBuilder.error("Сначала выполните вход (login)");
            }

            String[] parts = command.split(" ", 2);
            String cmdType = parts[0];
            String args = parts.length > 1 ? parts[1] : "";

            switch (cmdType) {
                case "login": return loginHandler.handle(channel, args);
                case "create": return createHandler.handle(channel, args, sessions.get(channel).getUsername());
                case "view": return viewHandler.handle(args);
                case "vote": return voteHandler.handleStartVote(channel, args);
                case "delete": return deleteHandler.handle(channel, args);
                case "exit": return exitHandler.handle(channel);
                default: return ResponseBuilder.error("Неизвестная команда");
            }
        } catch (Exception e) {
            logger.error("Ошибка обработки команды: {}", command, e);
            return ResponseBuilder.error("Некорректный формат команды");
        }
    }

    private String processVoteCreation(VoteCreationContext context, String input) {
        try {
            switch (context.step) {
                case 0:
                    context.name = input;
                    context.step++;
                    return ResponseBuilder.expect("Введите описание:");
                case 1:
                    context.description = input;
                    context.step++;
                    return ResponseBuilder.expect("Введите количество вариантов (минимум 2):");
                case 2:
                    context.optionCount = Integer.parseInt(input.trim());
                    if (context.optionCount < 2) {
                        voteCreationContexts.remove(context.channel);
                        return ResponseBuilder.error("Минимум 2 варианта");
                    }
                    context.step++;
                    return ResponseBuilder.expect("Введите варианты через запятую:");
                case 3:
                    context.options = Arrays.asList(input.split("\\s*,\\s*"));
                    if (context.options.size() != context.optionCount) {
                        voteCreationContexts.remove(context.channel);
                        return ResponseBuilder.error("Количество вариантов не совпадает");
                    }
                    Vote vote = new Vote(
                            context.name,
                            context.description,
                            context.options,
                            context.creator,
                            new HashMap<>()
                    );
                    context.topic.addVote(vote);
                    voteCreationContexts.remove(context.channel);
                    return ResponseBuilder.success("Голосование создано");
                default:
                    voteCreationContexts.remove(context.channel);
                    return ResponseBuilder.error("Неизвестный шаг");
            }
        } catch (NumberFormatException e) {
            voteCreationContexts.remove(context.channel);
            return ResponseBuilder.error("Введите число!");
        }
    }
}