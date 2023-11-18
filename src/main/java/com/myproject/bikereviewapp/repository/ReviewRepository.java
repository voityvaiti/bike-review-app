package com.myproject.bikereviewapp.repository;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByMotorcycle(Motorcycle motorcycle);

    Page<Review> findAllByMotorcycle(Motorcycle motorcycle, Pageable pageable);

    @Query("SELECT m.id as motorcycleId, AVG(r.rating) as averageRating FROM Review r JOIN r.motorcycle m GROUP BY m.id")
    List<Object[]> getMotorcycleIdToAvgRating();

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.motorcycle.id = :motorcycleId")
    Float getAvgRating(@Param("motorcycleId") Long motorcycleId);

}
