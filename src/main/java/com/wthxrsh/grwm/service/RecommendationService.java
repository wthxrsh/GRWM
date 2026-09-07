package com.wthxrsh.grwm.service;

import com.wthxrsh.grwm.dto.RecommendationRequest;
import com.wthxrsh.grwm.dto.RecommendationResponse;
import com.wthxrsh.grwm.exception.ResourceNotFoundException;
import com.wthxrsh.grwm.model.StyleRecommendation;
import com.wthxrsh.grwm.model.User;
import com.wthxrsh.grwm.repository.StyleRecommendationRepository;
import com.wthxrsh.grwm.service.AIService.AdviceResult;
import com.wthxrsh.grwm.service.WeatherService.Location;
import com.wthxrsh.grwm.service.WeatherService.WeatherData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecommendationService {

    private final WeatherService weatherService;
    private final AIService aiService;
    private final StyleRecommendationRepository recommendationRepository;

    public RecommendationService(WeatherService weatherService,
                                 AIService aiService,
                                 StyleRecommendationRepository recommendationRepository) {
        this.weatherService = weatherService;
        this.aiService = aiService;
        this.recommendationRepository = recommendationRepository;
    }

    @Transactional
    public RecommendationResponse suggest(User user, RecommendationRequest request) {
        Location location;
        if (request.city() != null && !request.city().isBlank()) {
            location = weatherService.geocode(request.city());
        } else {
            String resolvedCity = weatherService.reverseGeocode(request.latitude(), request.longitude());
            location = new Location(resolvedCity, request.latitude(), request.longitude());
        }

        WeatherData weather = weatherService.fetch(location);
        String style = request.stylePreference() != null && !request.stylePreference().isBlank()
                ? request.stylePreference()
                : user.getStylePreference();
        AdviceResult advice = aiService.recommend(weather, style);

        StyleRecommendation recommendation = StyleRecommendation.builder()
                .user(user)
                .city(location.name())
                .latitude(location.latitude())
                .longitude(location.longitude())
                .temperature(weather.temperature())
                .feelsLike(weather.feelsLike())
                .humidity(weather.humidity())
                .windSpeed(weather.windSpeed())
                .weatherCode(weather.weatherCode())
                .weatherCondition(weather.condition())
                .stylePreference(style)
                .summary(advice.summary())
                .recommendations(advice.recommendations())
                .build();
        recommendationRepository.save(recommendation);

        return RecommendationResponse.from(recommendation);
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> history(User user) {
        return recommendationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(RecommendationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecommendationResponse getById(User user, Long id) {
        StyleRecommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found: " + id));
        if (!recommendation.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Recommendation not found: " + id);
        }
        return RecommendationResponse.from(recommendation);
    }
}