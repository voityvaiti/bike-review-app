package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Review;

public interface ReviewService {

    Review create(Review review);

    void delete(Long id);

}
