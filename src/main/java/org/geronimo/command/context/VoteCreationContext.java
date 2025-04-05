package org.geronimo.command.context;

import org.geronimo.model.Topic;
import io.netty.channel.Channel;

import java.util.List;

public class VoteCreationContext {
    public final Topic topic;
    public final String creator;
    public final Channel channel;
    public String name;
    public String description;
    public int optionCount;
    public List<String> options;
    public int step = 0;

    public VoteCreationContext(Topic topic, String creator, Channel channel) {
        this.topic = topic;
        this.creator = creator;
        this.channel = channel;
    }
}