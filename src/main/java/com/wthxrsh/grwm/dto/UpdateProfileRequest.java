package com.wthxrsh.grwm.dto;

public record UpdateProfileRequest(
        String firstName,
        String lastName,
        String city,
        String stylePreference,
        String password
) {
}