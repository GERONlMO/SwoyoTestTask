package org.geronimo.command.handler.server;

import org.geronimo.util.ResponseBuilder;

public class ExitHandler {
    public String handle() {
        System.exit(0);
        return ResponseBuilder.success("Сервер завершает работу");
    }
}
