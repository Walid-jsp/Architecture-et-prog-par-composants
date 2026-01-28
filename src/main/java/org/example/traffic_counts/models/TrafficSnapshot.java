package org.example.traffic_counts.models;

import java.util.HashMap;
import java.util.Map;

public class TrafficSnapshot {

    public String requestDate;
    public Map<String, SensorData> data = new HashMap<>();
}
