package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Review;

import java.util.List;

public interface ReviewService {

    List<Review> getReviewsByMotorcycleId(Long id);

    Review create(Review review);

    void delete(Long id);

}
