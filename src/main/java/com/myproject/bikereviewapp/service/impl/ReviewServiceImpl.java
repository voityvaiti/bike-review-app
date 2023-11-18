package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.MotorcycleRepository;
import com.myproject.bikereviewapp.repository.ReviewRepository;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    private final MotorcycleRepository motorcycleRepository;


    public ReviewServiceImpl(ReviewRepository reviewRepository, MotorcycleRepository motorcycleRepository) {
        this.reviewRepository = reviewRepository;
        this.motorcycleRepository = motorcycleRepository;
    }

    @Override
    public List<Review> getReviewsByMotorcycleId(Long id) {
        Optional<Motorcycle> optionalMotorcycle = motorcycleRepository.findById(id);

        if(optionalMotorcycle.isEmpty()) {
            throw new EntityNotFoundException("Motorcycle with id" + id + "not found");
        }

        return reviewRepository.findAllByMotorcycle(optionalMotorcycle.get());
    }

    @Override
    public Page<Review> getReviewsByMotorcycleId(Long id, Pageable pageable) {
        Optional<Motorcycle> optionalMotorcycle = motorcycleRepository.findById(id);

        if(optionalMotorcycle.isEmpty()) {
            throw new EntityNotFoundException("Motorcycle with id" + id + "not found");
        }

        return reviewRepository.findAllByMotorcycle(optionalMotorcycle.get(), pageable);
    }

    @Override
    public Map<Long, Float> getMotorcycleIdToAvgRating() {

        List<Object[]> motorcycleIdToAvgRatingObjects = reviewRepository.getMotorcycleIdToAvgRating();

        return motorcycleIdToAvgRatingObjects.stream().collect(Collectors.toMap(
                object -> (Long)object[0],
                object -> ((Double)object[1]).floatValue()
        ));
    }

    @Override
    public Float getAvgRating(Long motorcycleId) {
        return reviewRepository.getAvgRating(motorcycleId);
    }

    @Override
    public Review create(Review review) {

        review.setPublicationDate(LocalDate.now());
        return reviewRepository.save(review);
    }

    @Override
    public void delete(Long id) {

        reviewRepository.deleteById(id);
    }
}
