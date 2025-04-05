package org.geronimo.command.context;

import org.geronimo.model.Vote;
import io.netty.channel.Channel;

public class VoteContext {
    public final Vote vote;
    public final Channel channel;

    public VoteContext(Vote vote, Channel channel) {
        this.vote = vote;
        this.channel = channel;
    }
}