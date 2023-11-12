package com.myproject.bikereviewapp.repository;

import com.myproject.bikereviewapp.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    List<Brand> findAllByOrderByIdAsc();

}
