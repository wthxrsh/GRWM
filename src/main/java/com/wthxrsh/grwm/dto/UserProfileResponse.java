package com.wthxrsh.grwm.dto;

import com.wthxrsh.grwm.model.User;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String city,
        String stylePreference,
        LocalDateTime createdAt
) {

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCity(),
                user.getStylePreference(),
                user.getCreatedAt()
        );
    }
}