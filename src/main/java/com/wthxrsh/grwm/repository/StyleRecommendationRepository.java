package com.wthxrsh.grwm.repository;

import com.wthxrsh.grwm.model.StyleRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StyleRecommendationRepository extends JpaRepository<StyleRecommendation, Long> {

    List<StyleRecommendation> findByUserIdOrderByCreatedAtDesc(Long userId);
}