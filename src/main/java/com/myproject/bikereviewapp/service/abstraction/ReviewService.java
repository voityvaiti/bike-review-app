package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Reaction;
import com.myproject.bikereviewapp.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReviewService {

    Review getById(Long id);

    Page<Review> getReviewsByMotorcycleId(Long id, Pageable pageable);

    Page<Review> getReviewsByUserId(Long id, Pageable pageable);

    Review getIfExistsUserReviewOnMotorcycle(Long userId, Long motorcycleId);

    Review create(Review review);

    void saveReaction(Reaction reaction);

    void delete(Long id);

    void deleteReviewIfOwnedByUser(Long reviewId, Long userId);

}
