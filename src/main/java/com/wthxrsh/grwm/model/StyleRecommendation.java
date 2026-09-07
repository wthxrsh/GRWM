package com.wthxrsh.grwm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "style_recommendations")
public class StyleRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String city;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "feels_like")
    private Double feelsLike;

    @Column(name = "humidity")
    private Double humidity;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "weather_code")
    private Integer weatherCode;

    @Column(name = "weather_condition")
    private String weatherCondition;

    @Column(name = "style_preference")
    private String stylePreference;

    @Column(length = 2000)
    private String summary;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "style_recommendation_items", joinColumns = @JoinColumn(name = "recommendation_id"))
    @Column(name = "item")
    private List<String> recommendations = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}