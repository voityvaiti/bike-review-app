package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Review;

import java.util.List;
import java.util.Map;

public interface ReviewService {

    List<Review> getReviewsByMotorcycleId(Long id);

    Map<Long, Float> getMotorcycleIdToAvgRating();

    Float getAvgRating(Long motorcycleId);

    Review create(Review review);

    void delete(Long id);

}
