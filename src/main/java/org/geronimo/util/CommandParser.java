package org.geronimo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Optional;

public class CommandParser {
    public static Optional<String> getArgumentValue(String args, String param) {
        Matcher m = Pattern.compile(param + "=([^\\s]+)").matcher(args);
        return m.find() ? Optional.of(m.group(1)) : Optional.empty();
    }

    public static Optional<String> getValueAfterFlag(String args, String flag) {
        Matcher m = Pattern.compile(flag + "\\s+(\\S+)").matcher(args);
        return m.find() ? Optional.of(m.group(1)) : Optional.empty();
    }
}