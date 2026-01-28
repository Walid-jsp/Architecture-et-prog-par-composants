package org.example.traffic_counts.controller;

import org.example.traffic_counts.service.TrafficService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/traffic")
public class TrafficController {

    private final TrafficService trafficService;

    public TrafficController(TrafficService trafficService) {
        this.trafficService = trafficService;
    }

    // Retourne le nombre total de véhicules sur tous les capteurs
    @GetMapping("/total")
    public int getTotalTraffic() {
        return trafficService.getTotalTraffic();
    }

    // Retourne la liste des noms de capteurs disponibles
    @GetMapping("/sensors")
    public List<String> getSensors() {
        return trafficService.getSensorNames();
    }

    // Retourne le comptage pour un capteur précis (ex: /sensor/MON_TD1)
    @GetMapping("/sensor/{id}")
    public Integer getSensorCount(@PathVariable String id) {
        return trafficService.getTrafficForSensor(id);
    }
}