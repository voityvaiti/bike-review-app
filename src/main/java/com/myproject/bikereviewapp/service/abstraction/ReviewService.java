package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ReviewService {

    Page<Review> getReviewsByMotorcycleId(Long id, Pageable pageable);

    Map<Long, Float> getMotorcycleIdToAvgRating();

    Float getAvgRating(Long motorcycleId);

    Review create(Review review);

    void delete(Long id);

}
