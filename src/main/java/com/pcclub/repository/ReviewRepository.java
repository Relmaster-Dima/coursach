package com.pcclub.repository;

import com.pcclub.model.Review;
import com.pcclub.model.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByResourceTypeAndResourceId(ResourceType type, Integer resourceId);
    List<Review> findByUserId(Integer userId);
} 