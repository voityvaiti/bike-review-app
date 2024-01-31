package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.MotorcycleRepository;
import com.myproject.bikereviewapp.repository.ReviewRepository;
import com.myproject.bikereviewapp.repository.UserRepository;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);


    private final ReviewRepository reviewRepository;

    private final MotorcycleRepository motorcycleRepository;

    private final UserRepository userRepository;



    @Override
    public Page<Review> getReviewsByMotorcycleId(Long id, Pageable pageable) {

        LOGGER.debug("Page request received: {}, by Motorcycle with ID: {}", pageable, id);

        Optional<Motorcycle> optionalMotorcycle = motorcycleRepository.findById(id);

        if(optionalMotorcycle.isEmpty()) {
            throw new EntityNotFoundException("Motorcycle with id" + id + "not found");
        }

        return reviewRepository.findAllByMotorcycle(optionalMotorcycle.get(), pageable);
    }

    @Override
    public Page<Review> getReviewsByUserId(Long id, Pageable pageable) {

        LOGGER.debug("Page request received: {}, by User with ID: {}", pageable, id);

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User with id" + id + "not found");
        }
        
        return reviewRepository.findAllByUser(optionalUser.get(), pageable);
    }

    @Override
    public Review create(Review review) {

        review.setPublicationDate(LocalDate.now());

        LOGGER.debug("Saving new Review: {}", review);

        return reviewRepository.save(review);
    }

    @Override
    public void delete(Long id) {

        LOGGER.debug("Removing Review with ID: {}", id);

        reviewRepository.deleteById(id);
    }
}
