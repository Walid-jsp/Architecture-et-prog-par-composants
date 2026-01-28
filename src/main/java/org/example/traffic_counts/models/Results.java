package org.example.traffic_counts.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class Results {
    public Map<String, TimeWindow> timeWindows = new HashMap<>();

    @JsonAnySetter
    public void add(String key, TimeWindow value) {
        timeWindows.put(key, value);
    }
}
