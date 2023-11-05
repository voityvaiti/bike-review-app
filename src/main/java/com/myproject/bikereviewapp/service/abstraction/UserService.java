package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.User;

import java.util.List;

public interface UserService {

    boolean exists(String username);

    List<User> getAll();

    User getByUsername(String name);

    User create(User user);

    void delete(Long id);
    
}
