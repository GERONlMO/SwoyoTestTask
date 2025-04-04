package org.geronimo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Topic {
    private String name;
    private Map<String, Vote> votes = new HashMap<>();
    private String creator;

    @JsonCreator
    public Topic(
            @JsonProperty("name") String name,
            @JsonProperty("creator") String creator
    ) {
        this.name = name;
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void addVote(Vote vote) {
        votes.put(vote.getName(), vote);
    }

    public Vote getVote(String voteName) {
        return votes.get(voteName);
    }

    public boolean removeVote(String voteName) {
        return votes.remove(voteName) != null;
    }

    public int getVotesCount() {
        return votes.size();
    }

    public String getCreator() {
        return creator;
    }

    public Collection<Vote> getAllVotes() {
        return votes.values();
    }
}
