package org.geronimo.command.handler.client;

import org.geronimo.model.User;
import org.geronimo.util.ResponseBuilder;
import io.netty.channel.Channel;
import java.util.Map;

public class ExitHandler {
    private final Map<Channel, User> sessions;

    public ExitHandler(Map<Channel, User> sessions) {
        this.sessions = sessions;
    }

    public String handle(Channel channel) {
        sessions.remove(channel);
        return ResponseBuilder.success("Сессия завершена");
    }
}