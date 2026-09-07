package com.wthxrsh.grwm.dto;

import com.wthxrsh.grwm.model.StyleRecommendation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record RecommendationResponse(
        Long id,
        String city,
        Double latitude,
        Double longitude,
        Double temperature,
        Double feelsLike,
        Double humidity,
        Double windSpeed,
        Integer weatherCode,
        String weatherCondition,
        String stylePreference,
        String summary,
        List<String> recommendations,
        LocalDateTime createdAt
) {

    public static RecommendationResponse from(StyleRecommendation recommendation) {
        return new RecommendationResponse(
                recommendation.getId(),
                recommendation.getCity(),
                recommendation.getLatitude(),
                recommendation.getLongitude(),
                recommendation.getTemperature(),
                recommendation.getFeelsLike(),
                recommendation.getHumidity(),
                recommendation.getWindSpeed(),
                recommendation.getWeatherCode(),
                recommendation.getWeatherCondition(),
                recommendation.getStylePreference(),
                recommendation.getSummary(),
                recommendation.getRecommendations() == null
                        ? List.of()
                        : new ArrayList<>(recommendation.getRecommendations()),
                recommendation.getCreatedAt()
        );
    }
}