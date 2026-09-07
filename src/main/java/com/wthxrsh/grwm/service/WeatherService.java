package com.wthxrsh.grwm.service;

import tools.jackson.databind.JsonNode;
import com.wthxrsh.grwm.exception.ResourceNotFoundException;
import com.wthxrsh.grwm.exception.WeatherServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class WeatherService {

    private final RestClient weatherRestClient;
    private final RestClient geocodingRestClient;
    private final RestClient reverseGeocodingRestClient;

    public WeatherService(RestClient weatherRestClient,
                          RestClient geocodingRestClient,
                          RestClient reverseGeocodingRestClient) {
        this.weatherRestClient = weatherRestClient;
        this.geocodingRestClient = geocodingRestClient;
        this.reverseGeocodingRestClient = reverseGeocodingRestClient;
    }

    public record Location(String name, double latitude, double longitude) {
    }

    public record WeatherData(
            double temperature,
            double feelsLike,
            double humidity,
            double windSpeed,
            int weatherCode,
            String condition) {
    }

    public Location geocode(String cityName) {
        try {
            JsonNode body = geocodingRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/search")
                            .queryParam("name", cityName)
                            .queryParam("count", 1)
                            .build())
                    .retrieve()
                    .body(JsonNode.class);

            JsonNode results = body == null ? null : body.path("results");
            if (results == null || results.isMissingNode() || !results.isArray() || results.isEmpty()) {
                throw new ResourceNotFoundException("City not found: " + cityName);
            }
            JsonNode first = results.get(0);
            String name = first.path("name").asString();
            return new Location(
                    name == null || name.isBlank() ? cityName : name,
                    first.path("latitude").asDouble(),
                    first.path("longitude").asDouble());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherServiceException("Unable to resolve city '" + cityName + "'", e);
        }
    }

    public String reverseGeocode(double latitude, double longitude) {
        try {
            JsonNode body = reverseGeocodingRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/data/reverse-geocode-client")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("localityLanguage", "en")
                            .build())
                    .retrieve()
                    .body(JsonNode.class);
            if (body == null) {
                return null;
            }
            String city = body.path("city").asString();
            if (city == null || city.isBlank()) {
                city = body.path("locality").asString();
            }
            return (city == null || city.isBlank()) ? null : city;
        } catch (Exception e) {
            return null;
        }
    }

    public WeatherData fetch(Location location) {
        try {
            JsonNode body = weatherRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/forecast")
                            .queryParam("latitude", location.latitude())
                            .queryParam("longitude", location.longitude())
                            .queryParam("current",
                                    "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m")
                            .queryParam("timezone", "auto")
                            .build())
                    .retrieve()
                    .body(JsonNode.class);

            if (body == null) {
                throw new WeatherServiceException("Empty response from weather service");
            }

            JsonNode current = body.path("current");
            if (current.isMissingNode()) {
                throw new WeatherServiceException("Weather service returned no current data");
            }

            int weatherCode = current.path("weather_code").asInt(0);
            return new WeatherData(
                    current.path("temperature_2m").asDouble(),
                    current.path("apparent_temperature").asDouble(),
                    current.path("relative_humidity_2m").asDouble(),
                    current.path("wind_speed_10m").asDouble(),
                    weatherCode,
                    describeCode(weatherCode));
        } catch (WeatherServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherServiceException("Unable to fetch weather data", e);
        }
    }

    private static final Map<Integer, String> CODES = Map.ofEntries(
            Map.entry(0, "Clear sky"),
            Map.entry(1, "Mainly clear"),
            Map.entry(2, "Partly cloudy"),
            Map.entry(3, "Overcast"),
            Map.entry(45, "Fog"),
            Map.entry(48, "Depositing rime fog"),
            Map.entry(51, "Light drizzle"),
            Map.entry(53, "Drizzle"),
            Map.entry(55, "Heavy drizzle"),
            Map.entry(56, "Freezing drizzle"),
            Map.entry(57, "Freezing drizzle"),
            Map.entry(61, "Light rain"),
            Map.entry(63, "Rain"),
            Map.entry(65, "Heavy rain"),
            Map.entry(66, "Freezing rain"),
            Map.entry(67, "Freezing rain"),
            Map.entry(71, "Light snow"),
            Map.entry(73, "Snow"),
            Map.entry(75, "Heavy snow"),
            Map.entry(77, "Snow grains"),
            Map.entry(80, "Light rain showers"),
            Map.entry(81, "Rain showers"),
            Map.entry(82, "Heavy rain showers"),
            Map.entry(85, "Snow showers"),
            Map.entry(86, "Heavy snow showers"),
            Map.entry(95, "Thunderstorm"),
            Map.entry(96, "Thunderstorm with hail"),
            Map.entry(99, "Thunderstorm with heavy hail"));

    private String describeCode(int code) {
        return CODES.getOrDefault(code, "Unknown conditions");
    }
}