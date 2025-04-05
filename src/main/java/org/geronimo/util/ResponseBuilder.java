package org.geronimo.util;

import java.util.List;
import java.util.Map;

public class ResponseBuilder {
    public static String error(String message) {
        return "ОШИБКА: " + message;
    }

    public static String success(String message) {
        return "УСПЕХ: " + message;
    }

    public static String expect(String message) {
        return "ОЖИДАНИЕ: " + message;
    }

    public static String formatVoteResults(String name, String description,
                                           Map<Integer, Integer> results, List<String> options) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Тема: %s\nОписание: %s\nРезультаты:\n", name, description));
        results.forEach((optionId, count) ->
                sb.append(String.format("- %s: %d голосов\n", options.get(optionId), count))
        );
        return sb.toString();
    }
}