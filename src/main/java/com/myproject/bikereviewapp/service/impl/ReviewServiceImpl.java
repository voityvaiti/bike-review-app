package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.*;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.exceptionhandler.exception.UniquenessConstraintViolationException;
import com.myproject.bikereviewapp.repository.MotorcycleRepository;
import com.myproject.bikereviewapp.repository.ReactionRepository;
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
    private final ReactionRepository reactionRepository;

    private final MotorcycleRepository motorcycleRepository;

    private final UserRepository userRepository;


    @Override
    public Review getById(Long id) {

        LOGGER.debug("Looking for Review with ID: {}", id);

        return reviewRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Review with id " + id + " not found")
        );
    }

    @Override
    public Page<Review> getReviewsByMotorcycleId(Long id, Pageable pageable) {

        LOGGER.debug("Page request received: {}, by Motorcycle with ID: {}", pageable, id);

        Optional<Motorcycle> optionalMotorcycle = motorcycleRepository.findById(id);

        if (optionalMotorcycle.isEmpty()) {
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
    public Review getIfExistsUserReviewOnMotorcycle(Long userId, Long motorcycleId) {
        return reviewRepository.findByUserIdAndMotorcycleId(userId, motorcycleId).orElse(null);
    }

    @Override
    public Review create(Review review) {

        if (reviewRepository.existsByUserIdAndMotorcycleId(review.getUser().getId(), review.getMotorcycle().getId())) {

            LOGGER.warn("User with ID: {} already has Review on Motorcycle with ID: {}", review.getUser().getId(), review.getMotorcycle().getId());
            throw new UniquenessConstraintViolationException("You already wrote review on this motorcycle.");
        }

        review.setPublicationDate(LocalDate.now());

        LOGGER.debug("Saving new Review: {}", review);

        return reviewRepository.save(review);
    }

    @Override
    public void saveReaction(Reaction newReaction) {

        Optional<Reaction> optionalReaction = reactionRepository.findByReviewIdAndUserId(newReaction.getReview().getId(), newReaction.getUser().getId());

        if(optionalReaction.isPresent()) {
            Reaction reaction = optionalReaction.get();
            reaction.setLike(newReaction.isLike());

            reactionRepository.save(reaction);
        } else {
            reactionRepository.save(newReaction);
        }
    }

    @Override
    public void delete(Long id) {

        LOGGER.debug("Removing Review with ID: {}", id);

        reviewRepository.deleteById(id);
    }
}
