package org.geronimo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Topic {
    private String name;
    private Map<String, Vote> votes = new HashMap<>();
    private String creator;

    @JsonCreator
    public Topic(
            @JsonProperty("name") String name,
            @JsonProperty("creator") String creator,
            @JsonProperty("votes") Map<String, Vote> votes
    ) {
        this.name = name;
        this.creator = creator;
        this.votes = votes != null ? votes : new HashMap<>();
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

    @JsonIgnore
    public int getVotesCount() {
        return votes.size();
    }

    @JsonIgnore
    public Collection<Vote> getAllVotes() {
        return votes.values();
    }

    @JsonProperty("votes")
    public Map<String, Vote> getVotes() {
        return votes;
    }

    public String getName() {
        return name;
    }
}
