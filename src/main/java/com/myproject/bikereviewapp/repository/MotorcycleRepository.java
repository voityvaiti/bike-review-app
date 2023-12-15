package com.myproject.bikereviewapp.repository;

import com.myproject.bikereviewapp.entity.Motorcycle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MotorcycleRepository extends JpaRepository<Motorcycle, Long> {

    Page<Motorcycle> getAllByModelContainingIgnoreCaseOrBrandNameContainingIgnoreCase(String model, String brandName, Pageable pageable);

}
