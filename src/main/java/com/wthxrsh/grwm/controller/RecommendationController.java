package com.wthxrsh.grwm.controller;

import com.wthxrsh.grwm.dto.RecommendationRequest;
import com.wthxrsh.grwm.dto.RecommendationResponse;
import com.wthxrsh.grwm.service.RecommendationService;
import com.wthxrsh.grwm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserService userService;

    public RecommendationController(RecommendationService recommendationService, UserService userService) {
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    @PostMapping("/suggest")
    public ResponseEntity<RecommendationResponse> suggest(@Valid @RequestBody RecommendationRequest request) {
        return ResponseEntity.ok(recommendationService.suggest(userService.currentUser(), request));
    }

    @GetMapping("/history")
    public List<RecommendationResponse> history() {
        return recommendationService.history(userService.currentUser());
    }

    @GetMapping("/{id}")
    public RecommendationResponse getById(@PathVariable Long id) {
        return recommendationService.getById(userService.currentUser(), id);
    }
}