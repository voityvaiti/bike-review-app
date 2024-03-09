package com.myproject.bikereviewapp.repository;

import com.myproject.bikereviewapp.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Optional<Reaction> findByReviewIdAndUserId(Long reviewId, Long userId);

}
