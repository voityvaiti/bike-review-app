package com.myproject.bikereviewapp.repository;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserIdAndMotorcycleId(Long userId, Long motorcycleId);

    Page<Review> findAllByMotorcycle(Motorcycle motorcycle, Pageable pageable);

    Page<Review> findAllByUser(User user, Pageable pageable);

    Optional<Review> findByUserIdAndMotorcycleId(Long userId, Long motorcycleId);

}
