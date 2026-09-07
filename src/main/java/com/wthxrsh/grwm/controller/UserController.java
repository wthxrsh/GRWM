package com.wthxrsh.grwm.controller;

import com.wthxrsh.grwm.dto.UpdateProfileRequest;
import com.wthxrsh.grwm.dto.UserProfileResponse;
import com.wthxrsh.grwm.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public UserProfileResponse getProfile() {
        return userService.getProfile(userService.currentUser().getUsername());
    }

    @PutMapping("/profile")
    public UserProfileResponse updateProfile(@RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(userService.currentUser().getUsername(), request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Long currentId = userService.currentUser().getId();
        if (!currentId.equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own account");
        }
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}