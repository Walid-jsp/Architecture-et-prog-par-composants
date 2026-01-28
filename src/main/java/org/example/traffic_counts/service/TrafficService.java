
package org.example.traffic_counts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.example.traffic_counts.models.TrafficSnapshot;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class TrafficService {

    private static final String API_URL = "http://data.mobility.brussels/traffic/api/counts/?request=live";
    // On utilise Jackson pour lire le JSON
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 1.Récupère les données brutes depuis l'API et les transforme en objets Java.
     */
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

    /**
     * 2.Retourne la liste de tous les noms de capteurs (ex: "MON_TD1").
     */
    public List<String> getSensorNames() {
        TrafficSnapshot snapshot = getSnapshot();
        if (snapshot == null || snapshot.data == null) {
            return new ArrayList<>();
        }
        // Les noms des capteurs sont les clés de la map "data"
        return new ArrayList<>(snapshot.data.keySet());
    }

    /**
     * 3.Calcule le nombre total de voitures sur tous les capteurs (fenêtre 1 minute).
     */
    public int getTotalTraffic() {
        TrafficSnapshot snapshot = getSnapshot();
        if (snapshot == null || snapshot.data == null) {
            return 0;
        }

        // On additionne le trafic de chaque capteur
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
            return null; // ou 0, selon ce que tu préfères
        }

        // On cherche le capteur spécifique
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