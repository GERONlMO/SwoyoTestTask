package org.geronimo.command.handler.client;

import org.geronimo.util.CommandParser;
import org.geronimo.util.ResponseBuilder;
import org.geronimo.model.User;
import io.netty.channel.Channel;
import java.util.Map;
import java.util.Optional;

public class LoginHandler {
    private final Map<Channel, User> sessions;

    public LoginHandler(Map<Channel, User> sessions) {
        this.sessions = sessions;
    }

    public String handle(Channel channel, String args) {
        Optional<String> username = CommandParser.getArgumentValue(args, "-u");
        if (!username.isPresent()) {
            return ResponseBuilder.error("Используйте формат: login -u=<username>");
        }
        sessions.put(channel, new User(username.get()));
        return ResponseBuilder.success("Авторизация прошла успешно");
    }
}