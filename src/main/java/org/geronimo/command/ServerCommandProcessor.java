package org.geronimo.command;

import org.geronimo.command.handler.server.ExitHandler;
import org.geronimo.command.handler.server.LoadDataHandler;
import org.geronimo.command.handler.server.SaveDataHandler;
import org.geronimo.model.Topic;
import org.geronimo.util.ResponseBuilder;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerCommandProcessor {
    private final Map<String, Topic> topics;
    private final SaveDataHandler saveHandler;
    private final LoadDataHandler loadHandler;
    private final ExitHandler exitHandler;
    private static final Logger logger = LoggerFactory.getLogger(ServerCommandProcessor.class);

    public ServerCommandProcessor(Map<String, Topic> topics) {
        this.topics = topics;
        this.saveHandler = new SaveDataHandler(topics);
        this.loadHandler = new LoadDataHandler(topics);
        this.exitHandler = new ExitHandler();
    }

    public String processCommand(String command) {
        logger.debug("Обработка серверной команды: {}", command);
        try {
            String[] parts = command.split(" ", 2);
            String cmd = parts[0];
            String arg = parts.length > 1 ? parts[1] : "";

            String result = switch (cmd) {
                case "save" -> saveHandler.handle(arg);
                case "load" -> loadHandler.handle(arg);
                case "exit" -> exitHandler.handle();
                default -> ResponseBuilder.error("Неизвестная команда");
            };

            logger.info("Команда '{}' выполнена: {}", command, result);
            return result;
        } catch (Exception e) {
            logger.error("Ошибка обработки команды '{}'", command, e);
            return ResponseBuilder.error("Внутренняя ошибка сервера");
        }
    }
}