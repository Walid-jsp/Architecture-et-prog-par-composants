package org.example.models;

import java.util.HashMap;
import java.util.Map;

public class TrafficSnapshot {

    public String requestDate;
    public Map<String, SensorData> data = new HashMap<>();
}
