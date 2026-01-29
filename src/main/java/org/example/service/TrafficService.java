
package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.example.models.TrafficSnapshot;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrafficService {

    private static final String API_URL = "http://data.mobility.brussels/traffic/api/counts/?request=live";
    private final ObjectMapper mapper = new ObjectMapper();

    public TrafficSnapshot getSnapshot() {
        String json = queryApi();
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return mapper.readValue(json, TrafficSnapshot.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getSensorNames() {
        TrafficSnapshot snapshot = getSnapshot();
        if (snapshot == null || snapshot.data == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(snapshot.data.keySet());
    }

    public int getTotalTraffic() {
        TrafficSnapshot snapshot = getSnapshot();
        if (snapshot == null || snapshot.data == null) {
            return 0;
        }

        return snapshot.data.values().stream()
                .mapToInt(sensorData -> {
                    try {
                        if (sensorData.results != null &&
                                sensorData.results.timeWindows != null &&
                                sensorData.results.timeWindows.get("1m") != null &&
                                sensorData.results.timeWindows.get("1m").t1 != null &&
                                sensorData.results.timeWindows.get("1m").t1.count != null) {

                            return sensorData.results.timeWindows.get("1m").t1.count;
                        }
                        return 0;
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .sum();
    }

    public Integer getTrafficForSensor(String sensorId) {
        TrafficSnapshot snapshot = getSnapshot();
        if (snapshot == null || snapshot.data == null) {
            return null;
        }

        var sensorData = snapshot.data.get(sensorId);

        if (sensorData != null &&
                sensorData.results != null &&
                sensorData.results.timeWindows != null &&
                sensorData.results.timeWindows.get("1m") != null &&
                sensorData.results.timeWindows.get("1m").t1 != null) {

            return sensorData.results.timeWindows.get("1m").t1.count;
        }

        return null;
    }

    private String queryApi() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}