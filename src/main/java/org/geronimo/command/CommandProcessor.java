package org.geronimo.command;

import org.geronimo.model.*;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.geronimo.util.DataSerializer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CommandProcessor.class);
    private final Map<String, Topic> topics;
    private final Map<Channel, User> sessions = new ConcurrentHashMap<>();
    private final Map<Channel, VoteCreationContext> voteCreationContexts = new ConcurrentHashMap<>();

    public CommandProcessor(Map<String, Topic> topics) {
        this.topics = topics;
    }

    public String process(Channel channel, String command) {
        logger.info("Обработка команды: {}", command);
        if (!sessions.containsKey(channel) && !command.startsWith("login")) {
            return "ОШИБКА: Сначала выполните вход (login)";
        }

        try {
            String[] parts = command.split(" ", 2);
            String cmdType = parts[0];
            String args = parts.length > 1 ? parts[1] : "";

            switch (cmdType) {
                case "login": return handleLogin(channel, args);
                case "create": return handleCreate(channel, args);
                case "view": return handleView(channel, args);
                case "vote": return handleVote(channel, args);
                case "delete": return handleDelete(channel, args);
                case "save": return handleSave(args);
                case "load": return handleLoad(args);
                case "exit": return handleExit(channel);
                default: return "ОШИБКА: Неизвестная команда";
            }
        } catch (Exception e) {
            logger.error("Ошибка обработки команды: {}", command, e);
            return "ОШИБКА: Некорректный формат команды";
        }
    }

    private String handleLogin(Channel channel, String args) {
        Matcher m = Pattern.compile("-u=(\\w+)").matcher(args);
        if (!m.find()) return "ОШИБКА: Используйте формат: login -u=<username>";
        sessions.put(channel, new User(m.group(1)));
        logger.info("Пользователь {} подключился", m.group(1));
        return "УСПЕХ: Авторизация прошла успешно";
    }

    private String handleCreate(Channel channel, String command) {
        User user = sessions.get(channel);
        VoteCreationContext context = voteCreationContexts.get(channel);

        if (context != null) {
            return processVoteCreationStep(context, command);
        }

        if (command.startsWith("topic")) {
            Matcher m = Pattern.compile("-n=(\\w+)").matcher(command);
            if (!m.find()) return "ОШИБКА: Укажите название раздела: -n=<topic>";
            String topicName = m.group(1);
            if (topics.containsKey(topicName)) return "ОШИБКА: Раздел уже существует";
            topics.put(topicName, new Topic(topicName, user.getUsername()));
            logger.info("Создан раздел: {}", topicName);
            return "УСПЕХ: Раздел создан";
        } else if (command.startsWith("vote")) {
            Matcher m = Pattern.compile("-t=(\\w+)").matcher(command);
            if (!m.find()) return "ОШИБКА: Укажите раздел: -t=<topic>";

            Topic topic = topics.get(m.group(1));
            if (topic == null) return "ОШИБКА: Раздел не найден";

            voteCreationContexts.put(channel, new VoteCreationContext(topic, user.getUsername(), channel));
            return "ОЖИДАНИЕ: Введите название голосования:";
        }
        return "ОШИБКА: Неизвестная подкоманда";
    }

    private String processVoteCreationStep(VoteCreationContext context, String input) {
        switch (context.step) {
            case 0:
                context.name = input;
                context.step++;
                return "ОЖИДАНИЕ: Введите описание:";
            case 1:
                context.description = input;
                context.step++;
                return "ОЖИДАНИЕ: Введите количество вариантов:";
            case 2:
                try {
                    context.optionCount = Integer.parseInt(input);
                    context.step++;
                    return "ОЖИДАНИЕ: Введите варианты (через запятую):";
                } catch (NumberFormatException e) {
                    return "ОШИБКА: Некорректное число";
                }
            case 3:
                context.options = Arrays.asList(input.split(","));
                Vote vote = new Vote(context.name, context.description, context.options, context.creator);
                context.topic.addVote(vote);
                voteCreationContexts.remove(context.channel);
                return "УСПЕХ: Голосование создано";
            default:
                voteCreationContexts.remove(context.channel);
                return "ОШИБКА: Неизвестный шаг";
        }
    }

    private String handleView(Channel channel, String args) {
        if (args.contains("-t") && args.contains("-v")) {
            Matcher m = Pattern.compile("-t=(\\w+) -v=(\\w+)").matcher(args);
            if (!m.find()) return "ОШИБКА: Неверный формат параметров";

            Topic topic = topics.get(m.group(1));
            if (topic == null) return "ОШИБКА: Раздел не найден";

            Vote vote = topic.getVote(m.group(2));
            if (vote == null) return "ОШИБКА: Голосование не найдено";

            StringBuilder results = new StringBuilder();
            vote.getResults().forEach((optionId, count) -> {
                String optionText = vote.getOptions().get(optionId);
                results.append(String.format("- %s: %d голосов\n", optionText, count));
            });

            return String.format(
                    "Тема: %s\nОписание: %s\nРезультаты:\n%s",
                    vote.getName(),
                    vote.getDescription(),
                    results.toString()
            );
        } else if (args.contains("-t")) {
            Matcher m = Pattern.compile("-t=(\\w+)").matcher(args);
            if (!m.find()) return "ОШИБКА: Укажите раздел: -t=<topic>";

            Topic topic = topics.get(m.group(1));
            if (topic == null) return "ОШИБКА: Раздел не найден";

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

    private String handleVote(Channel channel, String args) {
        Matcher m = Pattern.compile("-t=(\\w+) -v=(\\w+)").matcher(args);
        if (!m.find()) return "ОШИБКА: Используйте формат: vote -t=<topic> -v=<vote>";

        Topic topic = topics.get(m.group(1));
        if (topic == null) return "ОШИБКА: Раздел не найден";

        Vote vote = topic.getVote(m.group(2));
        if (vote == null) return "ОШИБКА: Голосование не найдено";

        channel.writeAndFlush("Доступные варианты:\n" + vote.getOptions() + "\nВведите номер варианта:");
        return "УСПЕХ: Голос учтен";
    }

    private String handleDelete(Channel channel, String args) {
        User user = sessions.get(channel);
        Matcher m = Pattern.compile("-i=(\\w+) -v=(\\w+)").matcher(args);
        if (!m.find()) return "ОШИБКА: Используйте формат: delete -t=<topic> -v=<vote>";

        Topic topic = topics.get(m.group(1));
        if (topic == null) return "ОШИБКА: Раздел не найден";

        Vote vote = topic.getVote(m.group(2));
        if (vote == null) return "ОШИБКА: Голосование не найдено";

        if (!vote.getCreator().equals(user.getUsername()))
            return "ОШИБКА: Нет прав на удаление";

        topic.removeVote(m.group(2));
        logger.info("Удалено голосование: {}", m.group(2));
        return "УСПЕХ: Голосование удалено";
    }

    private String handleSave(String args) {
        try {
            DataSerializer.save(topics, args);
            logger.info("Данные сохранены в файл: {}", args);
            return "УСПЕХ: Данные сохранены";
        } catch (Exception e) {
            logger.error("Ошибка сохранения", e);
            return "ОШИБКА: Не удалось сохранить данные";
        }
    }

    private String handleLoad(String args) {
        try {
            Map<String, Topic> loaded = DataSerializer.load(args);
            topics.clear();
            topics.putAll(loaded);
            logger.info("Данные загружены из файла: {}", args);
            return "УСПЕХ: Данные загружены";
        } catch (Exception e) {
            logger.error("Ошибка загрузки", e);
            return "ОШИБКА: Не удалось загрузить данные";
        }
    }

    private String handleExit(Channel channel) {
        sessions.remove(channel);
        logger.info("Пользователь отключен");
        return "УСПЕХ: Сессия завершена";
    }

    private static class VoteCreationContext {
        private final Topic topic;
        private final String creator;
        private final Channel channel;
        private String name;
        private String description;
        private int optionCount;
        private List<String> options;
        private int step = 0;

        public VoteCreationContext(Topic topic, String creator, Channel channel) {
            this.topic = topic;
            this.creator = creator;
            this.channel = channel;
        }
    }
}
