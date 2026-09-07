package com.wthxrsh.grwm.controller;

import com.wthxrsh.grwm.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final WeatherService weatherService;

    public LocationController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/reverse")
    public ReverseGeocodeResponse reverse(@RequestParam double latitude, @RequestParam double longitude) {
        String city = weatherService.reverseGeocode(latitude, longitude);
        return new ReverseGeocodeResponse(city, latitude, longitude);
    }

    public record ReverseGeocodeResponse(String city, double latitude, double longitude) {
    }
}