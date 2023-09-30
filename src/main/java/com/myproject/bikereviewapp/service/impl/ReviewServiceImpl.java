package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.repository.ReviewRepository;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }


    @Override
    public Review create(Review review) {

        return reviewRepository.save(review);
    }

    @Override
    public void delete(Long id) {

        reviewRepository.deleteById(id);
    }
}
