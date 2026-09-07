package com.wthxrsh.grwm.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record RecommendationRequest(
        String city,
        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
        Double latitude,
        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
        Double longitude,
        String stylePreference
) {

    @AssertTrue(message = "Provide either a city name or both latitude and longitude")
    public boolean isValidLocation() {
        boolean hasCoords = latitude != null && longitude != null;
        return city != null || hasCoords;
    }
}