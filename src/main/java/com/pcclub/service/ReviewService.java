package com.pcclub.service;

import com.pcclub.model.Review;
import com.pcclub.model.ResourceType;
import com.pcclub.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsForResource(ResourceType type, Integer resourceId) {
        return reviewRepository.findByResourceTypeAndResourceId(type, resourceId);
    }

    public List<Review> getReviewsByUser(Integer userId) {
        return reviewRepository.findByUserId(userId);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
} 