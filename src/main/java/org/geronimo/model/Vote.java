package org.geronimo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

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
            @JsonProperty("creator") String creator,
            @JsonProperty("results") Map<Integer, Integer> results
    ) {
        this.name = name;
        this.description = description;
        this.options = new ArrayList<>(options);
        this.creator = creator;
        this.results = results != null ? results : new HashMap<>();
    }

    public synchronized void vote(int option) {
        if (option < 0 || option >= options.size()) {
            throw new IllegalArgumentException("Неверный вариант");
        }
        results.put(option, results.getOrDefault(option, 0) + 1);
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

    @JsonProperty("results")
    public Map<Integer, Integer> getResults() {
        return new HashMap<>(results);
    }

    public String getCreator() {
        return creator;
    }
}
