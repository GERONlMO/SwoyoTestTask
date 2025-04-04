package org.geronimo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Vote {
    private String name;
    private String description;
    private List<String> options;
    private Map<Integer, Integer> results = new HashMap<>();
    private String creator;

    @JsonCreator
    public Vote(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("options") List<String> options,
            @JsonProperty("creator") String creator
    ) {
        this.name = name;
        this.description = description;
        this.options = new ArrayList<>(options);
        this.creator = creator;
    }

    public synchronized void vote(int option) {
        results.compute(option, (k, v) -> (v == null) ? 1 : v + 1);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public Map<Integer, Integer> getResults() {
        return new HashMap<>(results);
    }

    public String getCreator() {
        return creator;
    }
}
