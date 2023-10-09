package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.User;

public interface UserService {

    User findByUsername(String name);

    User create(User user);

    void delete(Long id);
    
}
