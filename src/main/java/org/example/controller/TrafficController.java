package org.example.controller;

import org.example.service.TrafficService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/traffic")
public class TrafficController {

    private final TrafficService trafficService;

    public TrafficController(TrafficService trafficService) {
        this.trafficService = trafficService;
    }

    @GetMapping("/total")
    public int getTotalTraffic() {
        return trafficService.getTotalTraffic();
    }

    @GetMapping("/sensors")
    public List<String> getSensors() {
        return trafficService.getSensorNames();
    }

    @GetMapping("/sensor/{id}")
    public Integer getSensorCount(@PathVariable String id) {
        return trafficService.getTrafficForSensor(id);
    }
}