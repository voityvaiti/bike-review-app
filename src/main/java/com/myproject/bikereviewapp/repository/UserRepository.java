package com.myproject.bikereviewapp.repository;

import com.myproject.bikereviewapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsUserByUsername(String username);

    List<User> findAllByOrderByIdAsc();

    Optional<User> findByUsername(String username);

}
